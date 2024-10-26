package ar.edu.iw3.model.business.implementations;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.model.persistence.OrderRepository;
import ar.edu.iw3.util.PdfGenerator;
import com.itextpdf.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class OrderBusiness implements IOrderBusiness {

    @Autowired
    private OrderRepository orderDAO;

    @Override
    public List<Order> list() throws BusinessException {
        try {
            return orderDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Order load(long id) throws NotFoundException, BusinessException {
        Optional<Order> orderFound;
        try {
            orderFound = orderDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (orderFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra la Orden id= " + id).build();
        return orderFound.get();
    }

    @Override
    public Order add(Order order) throws FoundException, BusinessException {
        try {
            load(order.getId());
            throw FoundException.builder().message("Ya existe la Orden id= " + order.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            return orderDAO.save(order);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al Crear Nueva Orden").build();
        }
    }

    @Override
    public Order update(Order order) throws NotFoundException, BusinessException, FoundException {
        load(order.getId());
        try {
            return orderDAO.save(order);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al Actualizar Orden").build();
        }
    }

    @Override
    public void delete(Order order) throws NotFoundException, BusinessException {
        delete(order.getId());
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);
        try {
            orderDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Autowired
    DetailBusiness detailBusiness;


    @Override
    public byte[] generateConciliationPdf(Long idOrder) throws BusinessException, NotFoundException {
        Order orderFound = load(idOrder);

        float initialWeighing = orderFound.getInitialWeighing();
        float productLoaded = orderFound.getLastAccumulatedMass();
        float finalWeight = orderFound.getFinalWeighing();
        float netWeight = finalWeight - initialWeighing;
        float difference = netWeight - productLoaded;
        float avgTemperature = detailBusiness.calculateAverageTemperature(orderFound.getId());
        float avgDensity = detailBusiness.calculateAverageDensity(orderFound.getId());
        float avgFlow = detailBusiness.calculateAverageFlowRate(orderFound.getId());
        Product product = orderFound.getProduct();

        try {
            return PdfGenerator.generateFuelLoadingReconciliationReport(initialWeighing, finalWeight, productLoaded, netWeight, difference, avgTemperature, avgDensity, avgFlow, product);
        } catch (DocumentException | IOException e) {
            log.error("Error generando el PDF: {}", e.getMessage(), e);
            throw BusinessException.builder().message("Error al generar el reporte PDF").ex(e).build();
        }
    }

    @Override
    public Map<String, Object> getConciliationJson(Long idOrder) throws BusinessException, NotFoundException {
        Order orderFound = load(idOrder);

        float initialWeighing = orderFound.getInitialWeighing();
        float productLoaded = orderFound.getLastAccumulatedMass();
        float finalWeight = orderFound.getFinalWeighing();
        float netWeight = finalWeight - initialWeighing;
        float difference = netWeight - productLoaded;
        float avgTemperature = detailBusiness.calculateAverageTemperature(orderFound.getId());
        float avgDensity = detailBusiness.calculateAverageDensity(orderFound.getId());
        float avgFlow = detailBusiness.calculateAverageFlowRate(orderFound.getId());
        Product product = orderFound.getProduct();

        Map<String, Object> conciliationData = new HashMap<>();
        conciliationData.put("initialWeighing", initialWeighing);
        conciliationData.put("finalWeighing", finalWeight);
        conciliationData.put("accumulatedMass", productLoaded);
        conciliationData.put("netWeight", netWeight);
        conciliationData.put("differenceWeight", difference);
        conciliationData.put("averageTemperature", avgTemperature);
        conciliationData.put("averageDensity", avgDensity);
        conciliationData.put("averageFlowRate", avgFlow);
        conciliationData.put("product", product != null ? product.getProduct() : "Unknown");
        return conciliationData;
    }

}