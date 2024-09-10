package ar.edu.iw3.model.business;

import java.util.List;

import ar.edu.iw3.model.Supplier;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

public interface ISupplierBusiness {

    public List<Supplier> list() throws BusinessException;

    public Supplier load(long id) throws NotFoundException, BusinessException;

    public Supplier load(String supplier) throws NotFoundException, BusinessException;

    public Supplier add(Supplier supplier) throws FoundException, BusinessException;

    public Supplier update(Supplier supplier) throws NotFoundException,FoundException, BusinessException;

    public void delete(Supplier supplier) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;
}