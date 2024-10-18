package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.business.interfaces.ICustomerCli1Business;
import ar.edu.iw3.model.Customer;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.ICustomerBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CustomerCli1Business implements ICustomerCli1Business {

    @Autowired
    private ICustomerBusiness baseCustomerBusiness;

    @Override
    public Customer loadOrCreate(Customer customer) throws BusinessException {
        Optional<Customer> findCustomer = Optional.empty();
        try {
            findCustomer = Optional.ofNullable(baseCustomerBusiness.load(customer.getBusinessName()));

        } catch (NotFoundException ignored) {
            // If the customer is not found, we create it
        }

        if (findCustomer.isEmpty()) {
            try {
                return baseCustomerBusiness.add(customer);
            } catch (FoundException ignored) {
                // will not happen
            }
        }
        return findCustomer.get();
    }
}