package ar.edu.iw3.integration.cli2.model.business.implementaions;

import ar.edu.iw3.integration.cli2.model.business.interfaces.IOrderCli2Business;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.implementations.DetailBusiness;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.model.persistence.OrderRepository;
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
public class OrderCli2Business implements IOrderCli2Business {

    private static final Logger log = LoggerFactory.getLogger(OrderCli2Business.class);
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IOrderBusiness orderBusiness;

    @Autowired
    private DetailBusiness detailBusiness;


    @Override
    public Order registerInitialWeighing(String licensePlate, float initialWeight) throws BusinessException, NotFoundException, FoundException {
        Optional<Order> orderFound;

        try {
            orderFound = orderRepository.findByTruck_LicensePlateAndStatus(licensePlate, Order.Status.ORDER_RECEIVED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (orderFound.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra orden para cargar en camion con patente " + licensePlate).build();
        }
        // todo esto se puede sacar
//        if (orderFound.get().getStatus() != Order.Status.ORDER_RECEIVED) {
//            throw new ConflictException("Estado de orden no válido");
//        }

        int password;
        do {
            password = Integer.parseInt(ActivationPasswordGenerator.generateActivationPassword());
        } while (orderRepository.findByActivatePassword(password).isPresent());

        orderFound.get().setActivatePassword(password);
        orderFound.get().setInitialWeighing(initialWeight);
        orderFound.get().setInitialWeighingDate(new Date(System.currentTimeMillis()));
        orderFound.get().setStatus(Order.Status.REGISTERED_INITIAL_WEIGHING);
        orderBusiness.update(orderFound.get());
        return orderFound.get();
    }

    @Override
    public byte[] registerFinalWeighing(String licensePlate, float finalWeight) throws BusinessException, NotFoundException, FoundException {
        Optional<Order> orderFound;

        try {
            orderFound = orderRepository.findByTruck_LicensePlateAndStatus(licensePlate, Order.Status.ORDER_CLOSED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (orderFound.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra orden para camion con patente " + licensePlate).build();
        }
        // todo esto se puede sacar
//        if (orderFound.get().getStatus() != Order.Status.ORDER_CLOSED) {
//            throw new ConflictException("Estado de orden no válido");
//        }

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
            return PdfGenerator.generateFuelLoadingReconciliationReport(
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
            log.error("Error generando el PDF: {}", e.getMessage(), e);
            throw BusinessException.builder().message("Error al generar el reporte PDF").ex(e).build();
        }
    }

}