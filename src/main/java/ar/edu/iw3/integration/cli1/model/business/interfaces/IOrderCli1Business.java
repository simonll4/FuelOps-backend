package ar.edu.iw3.integration.cli1.model.business.interfaces;

import java.util.List;

import ar.edu.iw3.integration.cli1.model.OrderCli1;
import ar.edu.iw3.model.business.exceptions.BadRequestException;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

public interface IOrderCli1Business {

    public OrderCli1 load(String orderNumberCli1) throws NotFoundException, BusinessException;

    public List<OrderCli1> list() throws BusinessException;

    public OrderCli1 add(OrderCli1 order) throws FoundException, BusinessException;

    public OrderCli1 addExternal(String json) throws FoundException, BusinessException, BadRequestException;
}