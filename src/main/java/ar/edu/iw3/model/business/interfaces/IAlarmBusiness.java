package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface IAlarmBusiness {

    public List<Alarm> list() throws BusinessException;

    public Alarm load(long id) throws NotFoundException, BusinessException;

    //public Alarm load(String businessName) throws NotFoundException, BusinessException;

    public Alarm add(Alarm alarm) throws FoundException, BusinessException;

    Alarm update(Alarm alarm) throws NotFoundException, BusinessException;

    Boolean isAlarmAccepted(Long orderId) throws BusinessException;

    List<Alarm> pendingReview() throws NotFoundException;

    // public Alarm update(Alarm alarm) throws NotFoundException, BusinessException, FoundException;

    //public void delete(Alarm alarm) throws NotFoundException, BusinessException;

    //public void delete(long id) throws NotFoundException, BusinessException;

}
