package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.Customer;
import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface ICustomerBusiness {
    public List<Customer> list() throws BusinessException;

    public Customer load(long id) throws NotFoundException, BusinessException;

    public Customer load(String businessName) throws NotFoundException, BusinessException;

}
