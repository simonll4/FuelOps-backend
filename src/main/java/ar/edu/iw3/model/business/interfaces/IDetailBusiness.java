package ar.edu.iw3.model.business.interfaces;

import  ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface IDetailBusiness {

    //todo public List<Detail> list() throws BusinessException;

    public List<Detail> listByOrder(long idOrder) throws NotFoundException, BusinessException;

    public Detail load(long id) throws NotFoundException, BusinessException;

    public Detail add(Detail detail) throws FoundException, BusinessException;

    public void receiveDetails(Detail detail) throws NotFoundException, BusinessException, FoundException;

    //public void saveDetails( Order orderFound,Detail detail) throws FoundException, BusinessException, NotFoundException;

    // todo public Detail update(Detail detail) throws NotFoundException, BusinessException, FoundException;

    // todo public void delete(Detail detail) throws NotFoundException, BusinessException;

    // todo public void delete(long id) throws NotFoundException, BusinessException;

}