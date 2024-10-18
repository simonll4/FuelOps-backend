package ar.edu.iw3.integration.cli3.model.business;

import ar.edu.iw3.events.AlarmEvent;
import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.exceptions.UnProcessableException;
import ar.edu.iw3.model.business.implementations.DetailBusiness;
import ar.edu.iw3.model.business.implementations.OrderBusiness;
import ar.edu.iw3.model.persistence.DetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DetailCli3Business implements IDetailCli3Business {

    @Autowired
    private OrderBusiness orderBusiness;

    @Autowired
    private DetailBusiness detailBusiness;

    @Autowired
    private DetailRepository detailDAO;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    @Override
    public void receiveDetails(Detail detail) throws NotFoundException, BusinessException, FoundException, UnProcessableException {
        Order orderFound = orderBusiness.load(detail.getOrder().getId());

        if (orderFound.getStatus() != Order.Status.REGISTERED_INITIAL_WEIGHING) {
            throw new UnProcessableException("Estado de orden no válido");
        }
        if (detail.getFlowRate() < 0) {
            throw new UnProcessableException("Caudal no válido");
        }
        if (detail.getAccumulatedMass() < orderFound.getLastAccumulatedMass()) {
            throw new UnProcessableException("Masa acumulada no válida");
        }
        if (detail.getTemperature() > orderFound.getProduct().getTemperature()) {
            if (orderFound.isAlarmAccepted()) {
                orderFound.setAlarmAccepted(false);
                orderBusiness.update(orderFound);
                applicationEventPublisher.publishEvent(new AlarmEvent(detail, AlarmEvent.TypeEvent.TEMPERATURE_EXCEEDED));
            }
        }

        // Actualizacion de cabecera de orden
        orderFound.setLastTimeStamp(new Date(System.currentTimeMillis()));
        orderFound.setLastAccumulatedMass(detail.getAccumulatedMass());
        orderFound.setLastDensity(detail.getDensity());
        orderFound.setLastTemperature(detail.getTemperature());
        orderFound.setLastFlowRate(detail.getFlowRate());
        orderBusiness.update(orderFound);

        // Gurdardado de detalle en db
        saveDetails(orderFound, detail);
    }

    private void saveDetails(Order orderFound, Detail detail) throws FoundException, BusinessException, NotFoundException {
        long currentTime = System.currentTimeMillis();
        Optional<List<Detail>> detailsOptional = detailDAO.findByOrderId(orderFound.getId());
        if ((detailsOptional.isPresent() && !detailsOptional.get().isEmpty())) {
            Date lastTimeStamp = orderFound.getFuelingEndDate();
            if (checkFrequency(currentTime, lastTimeStamp)) {
                detail.setTimeStamp(new Date(currentTime));
                detailBusiness.add(detail);
                orderFound.setFuelingEndDate(new Date(System.currentTimeMillis()));
                orderBusiness.update(orderFound);
            }
        } else {
            detail.setTimeStamp(new Date(currentTime));
            detailBusiness.add(detail);
            orderFound.setFuelingStartDate(new Date(System.currentTimeMillis()));
            orderFound.setFuelingEndDate(new Date(System.currentTimeMillis()));
            orderBusiness.update(orderFound);
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// UTILIDADES  ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    // todo dar la posibildidad de cambiar la frecuencia de guardado
    private static final long SAVE_INTERVAL_MS = 5000; // Frecuencia de guardado (5 segundos)

    private boolean checkFrequency(long currentTime, Date lastTimeStamp) {
        return currentTime - lastTimeStamp.getTime() >= SAVE_INTERVAL_MS;
    }
}
