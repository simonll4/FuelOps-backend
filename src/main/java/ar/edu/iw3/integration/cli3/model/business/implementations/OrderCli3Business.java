package ar.edu.iw3.integration.cli3.model.business.implementations;

import ar.edu.iw3.events.AlarmEvent;
import ar.edu.iw3.events.DetailEvent;
import ar.edu.iw3.integration.cli3.model.business.interfaces.IOrderCli3Business;
import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.*;
import ar.edu.iw3.model.business.implementations.AlarmBusiness;
import ar.edu.iw3.model.business.implementations.OrderBusiness;
import ar.edu.iw3.model.persistence.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class OrderCli3Business implements IOrderCli3Business {

    @Autowired
    private OrderRepository orderDAO;

    @Autowired
    private OrderBusiness orderBusiness;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private AlarmBusiness alarmBusiness;

    @Override
    public Order validatePassword(int password) throws NotFoundException, BusinessException, ConflictException {
        Optional<Order> order;

        try {
            order = orderDAO.findByActivatePassword(password);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al recuperar orden", e);
        }

        if (order.isEmpty()) {
            throw new NotFoundException("Orden no econtrada");
        }
        checkOrderStatus(order.get());
        return order.get();
    }

    @Override
    public Order receiveDetails(Detail detail) throws NotFoundException, BusinessException, UnProcessableException, ConflictException {
        Order orderFound = orderBusiness.load(detail.getOrder().getId());

        // Validaciones de negocio
        if (orderFound.getStatus() != Order.Status.REGISTERED_INITIAL_WEIGHING) {
            throw new ConflictException("Estado de orden no válido");
        }
        if (detail.getFlowRate() < 0) {
            throw new UnProcessableException("Caudal no válido");
        }
        if (detail.getAccumulatedMass() < orderFound.getLastAccumulatedMass()) {
            throw new UnProcessableException("Masa acumulada no válida");
        }
        if (detail.getTemperature() > orderFound.getProduct().getThresholdTemperature()) {
            if (!alarmBusiness.isAlarmAccepted(orderFound.getId())) {
                applicationEventPublisher.publishEvent(new AlarmEvent(detail, AlarmEvent.TypeEvent.TEMPERATURE_EXCEEDED));
            }
        }

        // Actualizacion de cabecera de orden
        orderFound.setLastTimeStamp(new Date(System.currentTimeMillis()));
        orderFound.setLastAccumulatedMass(detail.getAccumulatedMass());
        orderFound.setLastDensity(detail.getDensity());
        orderFound.setLastTemperature(detail.getTemperature());
        orderFound.setLastFlowRate(detail.getFlowRate());
        orderDAO.save(orderFound);

        // Evento para manejar el almacenamiento de detalle
        applicationEventPublisher.publishEvent(new DetailEvent(detail, DetailEvent.TypeEvent.SAVE_DETAIL));

        return orderFound;
    }

    @Override
    public Order closeOrder(Long orderId) throws BusinessException, NotFoundException, ConflictException {
        Optional<Order> order;
        try {
            order = orderDAO.findById(orderId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al recuperar orden", e);
        }
        if (order.isEmpty()) {
            throw new NotFoundException("Orden no econtrada");
        }
        checkOrderStatus(order.get());
        order.get().setStatus(Order.Status.ORDER_CLOSED);
        order.get().setActivatePassword(null);
        return orderDAO.save(order.get());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// UTILIDADES  ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkOrderStatus(Order order) throws ConflictException {
        if (order.getStatus() != Order.Status.REGISTERED_INITIAL_WEIGHING) {
            throw new ConflictException("Estado de orden no válido");
        }
    }
}
