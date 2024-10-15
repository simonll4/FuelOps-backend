package ar.edu.iw3.model.business.implementations;

import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IDetailBusiness;
import ar.edu.iw3.model.persistence.DetailRepository;
import ar.edu.iw3.model.persistence.OrderRepository;
import ar.edu.iw3.util.EmailBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DetailBusiness implements IDetailBusiness {

    // IoC
    @Autowired
    private DetailRepository detailDAO;
    @Autowired
    private OrderRepository orderRepository;

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

        // todo validar que el detail traiga todos los campos antes de guardar (donde hacer eso?)

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

    @Autowired
    private OrderBusiness orderBusiness;

    @Autowired
    private EmailBusiness emailBusiness;

    @Override
    public void receiveDetails(Detail detail) throws NotFoundException, BusinessException, FoundException {
        Order orderFound = orderBusiness.load(detail.getOrder().getId());

        // todo crear excepciones personalizadas para estos casos?
        if (detail.getFlowRate() < 0) {
            throw new BusinessException("Caudal no válido");
        }
        if (detail.getAccumulatedMass() < orderFound.getLastAccumulatedMass()) {
            throw new BusinessException("Masa acumulada no válida");
        }

        checkOrderStatus(orderFound);

        if (detail.getTemperature() > temperaturaUmbral) {
            if (orderFound.isAlarmAccepted()) {
                orderFound.setAlarmAccepted(false);
                orderBusiness.update(orderFound);
                // todo el envio del mail deberia ser en segundo plano, se tarda una banda
                emailBusiness.sendSimpleMessage("simon.llamosas44@gmail.com", "EMERGENCIA: Temperatura por encima de lo normal",
                        String.format("TEMEPERTURA: ", detail.getTemperature()));
            }
            System.out.println("Guardar alerta en db ");
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

    @Override
    public void saveDetails(Order orderFound, Detail detail) throws FoundException, BusinessException, NotFoundException {
        long currentTime = System.currentTimeMillis();
        Optional<List<Detail>> detailsOptional = detailDAO.findByOrderId(orderFound.getId());
        if ((detailsOptional.isPresent() && !detailsOptional.get().isEmpty())) {
            Date lastTimeStamp = orderFound.getFuelingEndDate();
            if (checkFrequency(currentTime, lastTimeStamp)) {
                detail.setTimeStamp(new Date(currentTime));
                add(detail);
                orderFound.setFuelingEndDate(new Date(System.currentTimeMillis()));
                orderBusiness.update(orderFound);
            } else {
                throw BusinessException.builder().message("Detalle no guardado").build();
            }
        } else {
            detail.setTimeStamp(new Date(currentTime));
            add(detail);
            orderFound.setFuelingStartDate(new Date(System.currentTimeMillis()));
            orderFound.setFuelingEndDate(new Date(System.currentTimeMillis()));
            orderBusiness.update(orderFound);
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// UTILIDADES  ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    // todo donde definir temperatura umbral? por orden o por producto?
    private float temperaturaUmbral = 40.0F;

    private void checkOrderStatus(Order order) throws BusinessException {
        if (order.getStatus() != Order.Status.REGISTERED_INITIAL_WEIGHING) {
            throw new BusinessException("Estado de orden no válido");
        }
    }

    // todo dar la posibildidad de cambiar la frecuencia de guardado
    private static final long SAVE_INTERVAL_MS = 5000; // Frecuencia de guardado (5 segundos)

    private boolean checkFrequency(long currentTime, Date lastTimeStamp) {
        return currentTime - lastTimeStamp.getTime() >= SAVE_INTERVAL_MS;
    }
}
