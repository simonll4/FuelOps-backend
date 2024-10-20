package ar.edu.iw3.model.business.implementations;

import ar.edu.iw3.events.AlarmEvent;
import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.exceptions.UnProcessableException;
import ar.edu.iw3.model.business.interfaces.IDetailBusiness;
import ar.edu.iw3.model.persistence.DetailRepository;
import ar.edu.iw3.model.persistence.OrderRepository;
import ar.edu.iw3.util.EmailBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DetailBusiness implements IDetailBusiness {

    @Autowired
    private DetailRepository detailDAO;


    @Override
    public Detail load(long id) throws NotFoundException, BusinessException {
        Optional<Detail> detailFound;

        try {
            detailFound = detailDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (detailFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Detalle id= " + id).build();
        return detailFound.get();

    }

    @Override
    public Detail add(Detail detail) throws FoundException, BusinessException {
        try {
            load(detail.getId());
            throw FoundException.builder().message("Ya existe el detalle id = " + detail.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            return detailDAO.save(detail);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al Crear Nuevo Detalle").build();
        }

    }

    // lista todos los detalles de una orden
    @Override
    public List<Detail> listByOrder(long idOrder) throws NotFoundException, BusinessException {
        Optional<List<Detail>> detailsFound;

        try {
            detailsFound = detailDAO.findByOrderId(idOrder);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (detailsFound.isEmpty())
            throw NotFoundException.builder().message("Orden sin detalles = " + idOrder).build();
        return detailsFound.get();
    }

    public Float calculateAverageTemperature(Long orderId) {
        Double avgTemp = detailDAO.findAverageTemperatureByOrderId(orderId);
        return avgTemp != null ? avgTemp.floatValue() : 0.0f;
    }

    public Float calculateAverageDensity(Long orderId) {
        Double avgDensity = detailDAO.findAverageDensityByOrderId(orderId);
        return avgDensity != null ? avgDensity.floatValue() : 0.0f;
    }

    public Float calculateAverageFlowRate(Long orderId) {
        Double avgFlow = detailDAO.findAverageFlowRateByOrderId(orderId);
        return avgFlow != null ? avgFlow.floatValue() : 0.0f;
    }

}
