package ar.edu.iw3.integration.cli1.model.business.interfaces;

import java.util.List;

import ar.edu.iw3.integration.cli1.model.OrderCli1;
import ar.edu.iw3.model.business.exceptions.*;

public interface IOrderCli1Business {

    public OrderCli1 load(String orderNumberCli1) throws NotFoundException, BusinessException;

    public List<OrderCli1> list() throws BusinessException;

    public OrderCli1 add(OrderCli1 order) throws FoundException, BusinessException;

    public OrderCli1 addExternal(String json) throws FoundException, BusinessException, BadRequestException, UnProcessableException;

    public OrderCli1 cancelExternal(String orderNumberCli1) throws BusinessException;
}