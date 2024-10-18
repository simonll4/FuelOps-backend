package ar.edu.iw3.integration.cli3.model.business;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.ConflictException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.persistence.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Slf4j
@Service
public class OrderCli3Business implements IOrderCli3Business {

    @Autowired
    private OrderRepository orderDAO;

    @Override
    public Order validatePassword(int password) throws NotFoundException, BusinessException, ConflictException {
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
    public void closeOrder(Long orderId) throws BusinessException, NotFoundException, ConflictException {
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
        orderDAO.save(order.get());
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
