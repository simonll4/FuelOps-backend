package ar.edu.iw3.integration.cli1.model.business.interfaces;

import ar.edu.iw3.integration.cli1.model.CustomerCli1;
import ar.edu.iw3.model.Customer;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface ICustomerCli1Business {

    public CustomerCli1 load(String idCli1) throws NotFoundException, BusinessException;

    public List<CustomerCli1> list() throws BusinessException;

    public CustomerCli1 add(CustomerCli1 customer) throws FoundException, BusinessException;

    public Customer loadOrCreate(CustomerCli1 customer) throws BusinessException, NotFoundException;

}
