package ar.edu.iw3.integration.cli2.model.business;

import ar.edu.iw3.integration.cli1.model.OrderCli1;
import ar.edu.iw3.integration.cli1.model.persistence.OrderCli1Respository;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.ConflictException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.implementations.DetailBusiness;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.util.ActivationPasswordGenerator;
import ar.edu.iw3.util.PdfGenerator;
import com.itextpdf.text.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Service
public class OrderCli2Business {

    private static final Logger log = LoggerFactory.getLogger(OrderCli2Business.class);
    @Autowired
    private OrderCli1Respository orderCli1Respository;

    @Autowired
    private IOrderBusiness orderBusiness;

    @Autowired
    private DetailBusiness detailBusiness;

    @Autowired
    private PdfGenerator pdfGenerator;

    public Integer registerInitialWeighing(String orderNumber, float initialWeight) throws BusinessException, NotFoundException, FoundException, ConflictException {
        Optional<OrderCli1> orderFound;

        try {
            orderFound = orderCli1Respository.findOneByOrderNumberCli1(orderNumber);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (orderFound.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra la orden con el número " + orderNumber).build();
        }
        if (orderFound.get().getStatus() != Order.Status.ORDER_RECEIVED) {
            throw new ConflictException("Estado de orden no válido");
        }

        Integer password;
        do {
            password = ActivationPasswordGenerator.generateActivationPassword();
        } while (orderCli1Respository.findByActivatePassword(password).isPresent());

        orderFound.get().setActivatePassword(password);
        orderFound.get().setInitialWeighing(initialWeight);
        orderFound.get().setInitialWeighingDate(new Date(System.currentTimeMillis()));
        orderFound.get().setStatus(Order.Status.REGISTERED_INITIAL_WEIGHING);
        orderBusiness.update(orderFound.get());
        return password;
    }

    public byte[] registerFinalWeighing(String orderNumber, float finalWeight) throws BusinessException, NotFoundException, FoundException, ConflictException {
        Optional<OrderCli1> orderFound;

        try {
            orderFound = orderCli1Respository.findOneByOrderNumberCli1(orderNumber);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (orderFound.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra la orden con el número " + orderNumber).build();
        }
        if (orderFound.get().getStatus() != Order.Status.ORDER_CLOSED) {
            throw new ConflictException("Estado de orden no válido");
        }

        Order order = orderFound.get();
        order.setFinalWeighing(finalWeight);
        order.setFinalWeighingDate(new Date(System.currentTimeMillis()));
        order.setStatus(Order.Status.REGISTERED_FINAL_WEIGHING);
        orderBusiness.update(order);

        float initialWeighing = order.getInitialWeighing();
        float productLoaded = order.getLastAccumulatedMass();
        float netWeight = finalWeight - initialWeighing;
        float difference = netWeight - productLoaded;
        float avgTemperature = detailBusiness.calculateAverageTemperature(order.getId());
        float avgDensity = detailBusiness.calculateAverageDensity(order.getId());
        float avgFlow = detailBusiness.calculateAverageFlowRate(order.getId());
        Product product = order.getProduct();

        try {
            return pdfGenerator.generateFuelLoadingReconciliationReport(
                    initialWeighing,
                    finalWeight,
                    productLoaded,
                    netWeight,
                    difference,
                    avgTemperature,
                    avgDensity,
                    avgFlow,
                    product
            );
        } catch (DocumentException | IOException e) {
            log.error("Error generando el PDF: " + e.getMessage(), e);
            throw BusinessException.builder().message("Error al generar el reporte PDF").ex(e).build();
        }
    }

}