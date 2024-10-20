package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;
import java.util.Map;

public interface IOrderBusiness {

    public List<Order> list() throws BusinessException;

    public Order load(long id) throws NotFoundException, BusinessException;

    public Order add(Order order) throws FoundException, BusinessException;

    public Order update(Order order) throws NotFoundException, BusinessException, FoundException;

    public void delete(Order order) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;

    public byte[] generateConciliationPdf(Long orderNumber) throws BusinessException, NotFoundException;

    public Map<String, Object> getConciliationJson(Long idOrder) throws BusinessException, NotFoundException;

}
