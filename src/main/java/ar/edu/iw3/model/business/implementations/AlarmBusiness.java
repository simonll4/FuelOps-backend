package ar.edu.iw3.model.business.implementations;

import ar.edu.iw3.auth.model.User;
import ar.edu.iw3.auth.model.business.interfaces.IUserAuthBusiness;
import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.ConflictException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IAlarmBusiness;
import ar.edu.iw3.model.persistence.AlarmRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AlarmBusiness implements IAlarmBusiness {

    @Autowired
    private AlarmRepository alarmDAO;

    @Autowired
    private OrderBusiness orderBusiness;

    @Autowired
    IUserAuthBusiness userBusiness;

    @Override
    public List<Alarm> list() throws BusinessException {
        try {
            return alarmDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Alarm load(long id) throws NotFoundException, BusinessException {
        Optional<Alarm> alarmFound;
        try {
            alarmFound = alarmDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (alarmFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra la alarma id= " + id).build();
        return alarmFound.get();
    }

    public Alarm load(long orderId, Alarm.Status status) throws NotFoundException, BusinessException {
        Optional<Alarm> alarmFound;
        try {
            alarmFound = alarmDAO.findByOrder_IdAndStatus(orderId, status);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (alarmFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra la alarma para la orden id= " + orderId + " con status " + status).build();
        return alarmFound.get();
    }


    @Override
    public Alarm add(Alarm alarm) throws FoundException, BusinessException {

        try {
            load(alarm.getId());
            throw FoundException.builder().message("Ya existe la Alarma id = " + alarm.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }
        try {
            load(alarm.getId());
            throw FoundException.builder().message("Ya existe la Alarma = " + alarm.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }
        try {
            return alarmDAO.save(alarm);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Alarm update(Alarm alarm) throws NotFoundException, BusinessException {
        load(alarm.getId());
        try {
            return alarmDAO.save(alarm);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Boolean isAlarmAccepted(Long orderId) {
        return alarmDAO.findByStatusAndOrder_Id(Alarm.Status.PENDING_REVIEW, orderId).isPresent();
    }

    @Override
    public List<Alarm> pendingReview() throws NotFoundException {
        Optional<List<Alarm>> alarm = alarmDAO.findByStatusAndOrder_Status(Alarm.Status.PENDING_REVIEW, Order.Status.REGISTERED_INITIAL_WEIGHING);
        if (alarm.isEmpty()) {
            throw new NotFoundException("No alarm found with status PENDING_REVIEW");
        }
        return alarm.get();
    }

    @Override
    public Page<Alarm> getAllAlarmsByOrder(Order order, Pageable pageable) throws NotFoundException, BusinessException {
        Optional<Page<Alarm>> alarms;

        try {
            alarms = alarmDAO.findAllByOrder(order, pageable);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (alarms.isEmpty()) {
            throw new NotFoundException("No alarms found for order id = " + order.getId());
        }

        return alarms.orElseGet(Page::empty);
    }

    @Override
    public Order setAlarmStatus(Alarm alarm, User user, Alarm.Status newStatus) throws BusinessException, NotFoundException, ConflictException {
        Alarm alarmFound = load(alarm.getId());
        Order orderFound = orderBusiness.load(alarmFound.getOrder().getId());

        User userFound = userBusiness.load(user.getUsername());

        if (alarmFound.getStatus() != Alarm.Status.PENDING_REVIEW) {
            throw ConflictException.builder().message("La alarma ya fue manejada").build();
        }
        if (orderFound.getStatus() != Order.Status.REGISTERED_INITIAL_WEIGHING) {
            throw ConflictException.builder().message("La orden no se encuentra en estado de carga").build();
        }

        if (newStatus != Alarm.Status.ACKNOWLEDGED && newStatus != Alarm.Status.CONFIRMED_ISSUE) {
            throw BusinessException.builder().message("El estado proporcionado no es válido").build();
        }

        if (!(alarm.getObservation() == null || alarm.getObservation().isEmpty())) {
            alarmFound.setObservation(alarm.getObservation());
        }

        alarmFound.setStatus(newStatus);
        alarmFound.setUser(userFound);
        update(alarmFound);

        return orderBusiness.update(orderFound);
    }


}