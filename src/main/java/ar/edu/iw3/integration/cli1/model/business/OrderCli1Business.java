package ar.edu.iw3.integration.cli1.model.business;

import java.util.List;
import java.util.Optional;

import ar.edu.iw3.model.business.implementations.TankBusiness;
import ar.edu.iw3.model.business.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.iw3.integration.cli1.model.OrderCli1;
import ar.edu.iw3.integration.cli1.model.OrderCli1JsonDeserializer;
import ar.edu.iw3.integration.cli1.model.persistence.OrderCli1Respository;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.util.JsonUtiles;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderCli1Business implements IOrderCli1Business {

    @Autowired(required = false)
    private OrderCli1Respository orderDAO;


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
            throw FoundException.builder().message("Se encontró el Producto id=" + order.getId()).build();
        } catch (NotFoundException e) {
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
    private ICustomerBusiness customerBusiness;

    @Autowired
    private ITruckBusiness truckBusiness;

    @Autowired
    private TankBusiness tankBusiness;

    @Autowired
    private IProductBusiness productBusiness;

    @Autowired
    private IDriverBusiness driverBusiness;


    @Override
    public OrderCli1 addExternal(String json) throws FoundException, BusinessException {
        ObjectMapper mapper = JsonUtiles.getObjectMapper(OrderCli1.class,
                new OrderCli1JsonDeserializer(OrderCli1.class,driverBusiness,truckBusiness,customerBusiness,productBusiness,tankBusiness),null);
        OrderCli1 order = null;
        try {
            order = mapper.readValue(json, OrderCli1.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        return add(order);
    }
}