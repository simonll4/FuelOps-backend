package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.auth.User;
import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.ConflictException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;
import java.util.Map;

public interface IOrderBusiness {

    public List<Order> list() throws BusinessException;

    public Order load(long id) throws NotFoundException, BusinessException;

    public Order update(Order order) throws NotFoundException, BusinessException, FoundException;

    public Order acknowledgeAlarm(Alarm alarm, User user) throws BusinessException, NotFoundException, ConflictException;

    public Order confirmIssueAlarm(Alarm alarm, User user) throws BusinessException, NotFoundException, ConflictException;

    public byte[] generateConciliationPdf(Long orderNumber) throws BusinessException, NotFoundException;

    public Map<String, Object> getConciliationJson(Long idOrder) throws BusinessException, NotFoundException;

}
