package ar.edu.iw3.integration.cli2.model.business.interfaces;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.ConflictException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

public interface IOrderCli2Business {

    Order registerInitialWeighing(String orderNumber, float initialWeight) throws BusinessException, NotFoundException, FoundException, ConflictException;

    byte[] registerFinalWeighing(String orderNumber, float finalWeight) throws BusinessException, NotFoundException, FoundException, ConflictException;
}
