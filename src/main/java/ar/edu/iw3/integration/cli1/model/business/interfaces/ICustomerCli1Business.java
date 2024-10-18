package ar.edu.iw3.integration.cli1.model.business.interfaces;

import ar.edu.iw3.model.Customer;
import ar.edu.iw3.model.business.exceptions.BusinessException;

public interface ICustomerCli1Business {
    public Customer loadOrCreate(Customer customer) throws BusinessException;
}
