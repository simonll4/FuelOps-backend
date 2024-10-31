package ar.edu.iw3.model.business.interfaces;

import  ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface IDetailBusiness {

    public Detail load(long id) throws NotFoundException, BusinessException;

    public List<Detail> listByOrder(long idOrder) throws NotFoundException, BusinessException;

    Detail add(Detail detail) throws FoundException, BusinessException;

}