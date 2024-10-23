package ar.edu.iw3.integration.cli3.model.business.interfaces;

import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.*;

public interface IOrderCli3Business {

    public Order validatePassword(int password) throws NotFoundException, BusinessException, ConflictException;

    public void receiveDetails(Detail detail) throws NotFoundException, BusinessException, FoundException, UnProcessableException, ConflictException;

    public void closeOrder(Long orderId) throws BusinessException, NotFoundException, ConflictException;
}
