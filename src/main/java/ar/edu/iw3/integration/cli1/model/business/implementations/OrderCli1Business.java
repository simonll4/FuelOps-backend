package ar.edu.iw3.integration.cli1.model.business.implementations;

import java.util.List;
import java.util.Optional;
import ar.edu.iw3.integration.cli1.model.business.interfaces.*;
import ar.edu.iw3.integration.cli1.model.persistence.OrderCli1Repository;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.*;
import ar.edu.iw3.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ar.edu.iw3.integration.cli1.model.OrderCli1;
import ar.edu.iw3.integration.cli1.model.deserializers.OrderCli1JsonDeserializer;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class OrderCli1Business implements IOrderCli1Business {

    @Autowired
    private OrderCli1Repository orderDAO;

    @Autowired
    private ICustomerCli1Business customerBusiness;

    @Autowired
    private ITruckCli1Business truckBusiness;

    @Autowired
    private IProductCli1Business productBusiness;

    @Autowired
    private IDriverCli1Business driverBusiness;

    @Override
    public OrderCli1 load(String orderNumberCli1) throws NotFoundException, BusinessException {
        Optional<OrderCli1> r;
        try {
            r = orderDAO.findOneByOrderNumberCli1(orderNumberCli1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (r.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra la orden orderNumberCli1=" + orderNumberCli1).build();
        }
        return r.get();
    }

    @Override
    public List<OrderCli1> list() throws BusinessException {
        try {
            return orderDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public OrderCli1 add(OrderCli1 order) throws FoundException, BusinessException {
        Optional<OrderCli1> orderFound;

        orderFound = orderDAO.findOneByOrderNumberCli1(order.getOrderNumberCli1());
        if (orderFound.isPresent()) {
            throw FoundException.builder().message("Ya existe una orden con el número " + order.getOrderNumberCli1()).build();
        }
        orderFound = orderDAO.findByTruck_idAndStatus(order.getTruck().getId(), Order.Status.ORDER_RECEIVED);
        if (orderFound.isPresent()) {
            throw FoundException.builder().message("Ya existe una orden para el camion id=" + order.getTruck().getId()).build();
        }

        try {
            return orderDAO.save(order);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public OrderCli1 addExternal(String json) throws FoundException, BusinessException, BadRequestException, UnProcessableException {
        ObjectMapper mapper = JsonUtils.getObjectMapper(OrderCli1.class, new OrderCli1JsonDeserializer(
                OrderCli1.class, driverBusiness, truckBusiness, customerBusiness, productBusiness), null);
        OrderCli1 order;

        try {
            order = mapper.readValue(json, OrderCli1.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw UnProcessableException.builder().message("El formato JSON es incorrecto").build();
        }

        // Seteo de orderNumberCli1 tmp
        if (order.getOrderNumberCli1() == null || order.getOrderNumberCli1().isBlank()) {
            String codeCli1Temp = order.getPreset() + System.currentTimeMillis() + "";
            order.setOrderNumberCli1(codeCli1Temp);
            order.setCodCli1Temp(true);
        }

        return add(order);
    }

    @Override
    public OrderCli1 cancelExternal(String orderNumberCli1) throws BusinessException {
        Optional<OrderCli1> orderFound = orderDAO.findOneByOrderNumberCli1(orderNumberCli1);
        if (orderFound.isPresent() && orderFound.get().getStatus().equals(Order.Status.ORDER_RECEIVED)) {
            orderFound.get().setStatus(Order.Status.ORDER_CANCELLED);
            orderDAO.save(orderFound.get());
            return orderFound.get();
        } else if (orderFound.isEmpty()) {
            throw new BusinessException("No se encuentra la orden orderNumberCli1=" + orderNumberCli1);
        }
        throw new BusinessException("No se puede cancelar la orden, estado actual: " + orderFound.get().getStatus());
    }

}