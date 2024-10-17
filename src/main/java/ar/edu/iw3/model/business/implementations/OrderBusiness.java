package ar.edu.iw3.model.business.implementations;

import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.model.persistence.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Order validatePassword(int password) throws NotFoundException, BusinessException {
        Optional<Order> order;

        // Lógica para validar la contraseña de activación
        try {
            order = orderDAO.findByActivatePassword(password);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al recuperar orden", e);
        }

        if (order.isEmpty()) {
            throw new NotFoundException("Orden no econtrada");
        }

        // si la orden no esta en estado PESAJE_INICIAL_REGISTRADO rechazar la activación
        checkOrderStatus(order.get());
        return order.get();
    }

    @Override
    public Order closeOrder(Long orderId) throws BusinessException, NotFoundException {
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
        return orderDAO.save(order.get());
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// UTILIDADES  ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkOrderStatus(Order order) throws BusinessException {
        if (order.getStatus() != Order.Status.REGISTERED_INITIAL_WEIGHING) {
            throw new BusinessException("Estado de orden no válido");
        }
    }

}

