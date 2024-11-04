package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.Driver;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface IDriverBusiness {
    public List<Driver> list() throws BusinessException;

    public Driver load(long id) throws NotFoundException, BusinessException;

    public Driver load(String document) throws NotFoundException, BusinessException;

}
