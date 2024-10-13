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

        // todo revisar: asi como esta devuelve una order con id distinto a 1
//        Optional<Order> orderFound;
//        try {
//            orderFound = orderDAO.findByIdNot(order.getId());
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            throw BusinessException.builder().ex(e).build();
//        }
//
//        if (orderFound.isPresent()) {
//            throw FoundException.builder().message("Ya Existe una Orden con el Id =" + order.getId()).build();
//        }

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
        if (order.get().getStatus() != Order.Status.PESAJE_INICIAL_REGISTRADO) {
            throw new BusinessException("Estado de la orden no válido");
        }

        return order.get();
    }

    @Override
    public void saveLastDetails(Detail detail) throws NotFoundException, BusinessException {
        Optional<Order> order;

        try {
            order = orderDAO.findById(detail.getOrder().getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al recuperarr orden", e);
        }

        if (order.isEmpty()) {
            throw new NotFoundException("Orden no econtrada");
        }

        // si la orden no esta en estado PESAJE_INICIAL_REGISTRADO rechazar la activación
        if (order.get().getStatus() != Order.Status.PESAJE_INICIAL_REGISTRADO) {
            throw new BusinessException("Estado de orden no válido");
        }

        // chequeo de que los datos del detalle sean validos
        if ((detail.getFlowRate() > 0) && (detail.getAccumulatedMass() >= order.get().getLastAccumulatedMass())) {
            // actualizacion de cabecera de orden
            order.get().setLastTimeStamp(new Date(System.currentTimeMillis()));
            order.get().setLastAccumulatedMass(detail.getAccumulatedMass());
            order.get().setLastDensity(detail.getDensity());
            order.get().setLastTemperature(detail.getTemperature());
            order.get().setLastFlowRate(detail.getFlowRate());
            orderDAO.save(order.get());
        }
        // todo tirar excepcion personalizada si no se cumple la condicion??
        else {
            throw new BusinessException("Datos de detalle no validos");
        }

    }

//    public Order closeOrder(Long orderId) {
//        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
//        order.setEstado(3); // Estado "Cerrada"
//        return orderRepository.save(order);
//    }

}



