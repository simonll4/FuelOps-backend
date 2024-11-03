
package ar.edu.iw3.integration.cli1.model.business.implementations;

import java.util.List;
import java.util.Optional;
import ar.edu.iw3.integration.cli1.model.business.interfaces.*;
import ar.edu.iw3.integration.cli1.model.persistence.OrderCli1Repository;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.interfaces.*;
import ar.edu.iw3.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ar.edu.iw3.integration.cli1.model.OrderCli1;
import ar.edu.iw3.integration.cli1.model.OrderCli1JsonDeserializer;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderCli1Business implements IOrderCli1Business {

    @Autowired(required = false)
    private OrderCli1Repository orderDAO;

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

    @Autowired(required = false)
    private IOrderBusiness orderBaseBusiness;

    @Override
    public OrderCli1 add(OrderCli1 order) throws FoundException, BusinessException {
        try {
            orderBaseBusiness.load(order.getId());

            throw FoundException.builder().message("Se encontró la Orden id=" + order.getId()).build();

        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        if (orderDAO.findOneByOrderNumberCli1(order.getOrderNumberCli1()).isPresent()) {
            throw FoundException.builder().message("Se encontró la Orden numero=" + order.getOrderNumberCli1()).build();
        }

        try {
            return orderDAO.save(order);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Autowired
    private ICustomerCli1Business customerBusiness;

    @Autowired
    private ITruckCli1Business truckBusiness;

    @Autowired
    private IProductCli1Business productBusiness;

    @Autowired
    private IDriverCli1Business driverBusiness;

    @Override
    public OrderCli1 addExternal(String json) throws FoundException, BusinessException {
        ObjectMapper mapper = JsonUtils.getObjectMapper(OrderCli1.class, new OrderCli1JsonDeserializer(
                OrderCli1.class, driverBusiness, truckBusiness, customerBusiness, productBusiness), null);
        OrderCli1 order;
        try {
            order = mapper.readValue(json, OrderCli1.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        return add(order);
    }

    @Override
    public OrderCli1 cancelExternal(String orderNumberCli1) throws BusinessException {
        Optional<OrderCli1> orderFound = orderDAO.findOneByOrderNumberCli1(orderNumberCli1);
        if(orderFound.isPresent() && orderFound.get().getStatus().equals(Order.Status.ORDER_RECEIVED)) {
            orderFound.get().setStatus(Order.Status.ORDER_CANCELLED);
            orderDAO.save(orderFound.get());
            return orderFound.get();
        }
        else if (orderFound.isEmpty()){
            throw new BusinessException("No se encuentra la orden orderNumberCli1=" + orderNumberCli1);
        }
        throw new BusinessException("No se puede cancelar la orden, estado actual: " + orderFound.get().getStatus());
    }


}