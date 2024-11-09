package ar.edu.iw3.model.business.implementations;

import ar.edu.iw3.auth.IUserBusiness;
import ar.edu.iw3.auth.User;
import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.ConflictException;
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

    @Autowired
    DetailBusiness detailBusiness;

    @Autowired
    AlarmBusiness alarmBusiness;

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
    public Order update(Order order) throws NotFoundException, BusinessException {
        load(order.getId());
        try {
            return orderDAO.save(order);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al Actualizar Orden").build();
        }
    }

    @Autowired
    IUserBusiness userBusiness;

    // todo reutilizar codigo entre este metodo y el de abajo
    @Override
    public Order acknowledgeAlarm(Alarm alarm, User user) throws BusinessException, NotFoundException, ConflictException {
        Alarm alarmFound = alarmBusiness.load(alarm.getId());
        Order orderFound = load(alarmFound.getOrder().getId());
        User userFound = userBusiness.load(user.getUsername());

        if (alarmFound.getStatus() != Alarm.Status.PENDING_REVIEW) {
            throw ConflictException.builder().message("La alarma ya fue manejada").build();
        }
        if (orderFound.getStatus() != Order.Status.REGISTERED_INITIAL_WEIGHING) {
            throw ConflictException.builder().message("La orden no se encuentra en estado de carga").build();
        }
        if (!(alarm.getObservation() == null || alarm.getObservation().isEmpty())) {
            alarmFound.setObservation(alarm.getObservation());
        }
        alarmFound.setStatus(Alarm.Status.ACKNOWLEDGED);
        alarmFound.setUser(userFound);
        alarmBusiness.update(alarmFound);
        return update(orderFound);
    }

    @Override
    public Order confirmIssueAlarm(Alarm alarm, User user) throws BusinessException, NotFoundException, ConflictException {
        Alarm alarmFound = alarmBusiness.load(alarm.getId());
        Order orderFound = load(alarmFound.getOrder().getId());
        User userFound = userBusiness.load(user.getUsername());

        if (alarmFound.getStatus() != Alarm.Status.PENDING_REVIEW) {
            throw ConflictException.builder().message("La alarma ya fue manejada").build();
        }
        if (orderFound.getStatus() != Order.Status.REGISTERED_INITIAL_WEIGHING) {
            throw ConflictException.builder().message("La orden no se encuentra en estado de carga").build();
        }
        if (!(alarm.getObservation() == null || alarm.getObservation().isEmpty())) {
            alarmFound.setObservation(alarm.getObservation());
        }
        alarmFound.setStatus(Alarm.Status.CONFIRMED_ISSUE);
        alarmFound.setUser(userFound);
        alarmBusiness.update(alarmFound);
        return update(orderFound);
    }

    @Override
    public byte[] getConciliationPdf(Long idOrder) throws BusinessException, NotFoundException {
        Optional<Order> orderFound;
        try {
            orderFound = orderDAO.findByIdAndStatus(idOrder, Order.Status.REGISTERED_FINAL_WEIGHING);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("").build();
        }
        if (orderFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra la Orden id= " + idOrder).build();

        float initialWeighing = orderFound.get().getInitialWeighing();
        float productLoaded = orderFound.get().getLastAccumulatedMass();
        float finalWeight = orderFound.get().getFinalWeighing();
        float netWeight = finalWeight - initialWeighing;
        float difference = netWeight - productLoaded;
        float avgTemperature = detailBusiness.calculateAverageTemperature(orderFound.get().getId());
        float avgDensity = detailBusiness.calculateAverageDensity(orderFound.get().getId());
        float avgFlow = detailBusiness.calculateAverageFlowRate(orderFound.get().getId());
        Product product = orderFound.get().getProduct();

        try {
            return PdfGenerator.generateFuelLoadingReconciliationReport(initialWeighing, finalWeight, productLoaded, netWeight, difference, avgTemperature, avgDensity, avgFlow, product);
        } catch (DocumentException | IOException e) {
            log.error("Error generando el PDF: {}", e.getMessage(), e);
            throw BusinessException.builder().message("Error al generar el reporte PDF").ex(e).build();
        }
    }

    @Override
    public Map<String, Object> getConciliationJson(Long idOrder) throws BusinessException, NotFoundException{
        Optional<Order> orderFound;
        try {
            orderFound = orderDAO.findByIdAndStatus(idOrder, Order.Status.REGISTERED_FINAL_WEIGHING);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("").build();
        }
        if (orderFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra la Orden id= " + idOrder).build();

        float initialWeighing = orderFound.get().getInitialWeighing();
        float productLoaded = orderFound.get().getLastAccumulatedMass();
        float finalWeight = orderFound.get().getFinalWeighing();
        float netWeight = finalWeight - initialWeighing;
        float difference = netWeight - productLoaded;
        float avgTemperature = detailBusiness.calculateAverageTemperature(orderFound.get().getId());
        float avgDensity = detailBusiness.calculateAverageDensity(orderFound.get().getId());
        float avgFlow = detailBusiness.calculateAverageFlowRate(orderFound.get().getId());
        Product product = orderFound.get().getProduct();

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