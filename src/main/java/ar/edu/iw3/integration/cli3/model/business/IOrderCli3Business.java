package ar.edu.iw3.integration.cli3.model.business;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.ConflictException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

public interface IOrderCli3Business {

    public Order validatePassword(int password) throws NotFoundException, BusinessException, ConflictException;

    public void closeOrder(Long orderId) throws BusinessException, NotFoundException, ConflictException;
}
