package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.CustomerCli1;
import ar.edu.iw3.integration.cli1.model.DriverCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.ICustomerCli1Business;
import ar.edu.iw3.integration.cli1.model.persistence.CustomerCli1Repository;
import ar.edu.iw3.integration.cli1.util.MapperEntity;
import ar.edu.iw3.model.Customer;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.ICustomerBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CustomerCli1Business implements ICustomerCli1Business {

    @Autowired
    private CustomerCli1Repository customerDAO;

    @Autowired
    private ICustomerBusiness customerBaseBusiness;

    @Autowired
    private MapperEntity mapperEntity;

    @Override
    public CustomerCli1 load(String idCli1) throws NotFoundException, BusinessException {
        Optional<CustomerCli1> r;
        try {
            r = customerDAO.findOneByIdCli1(idCli1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (r.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el camion idCli1=" + idCli1).build();
        }
        return r.get();
    }

    @Override
    public List<CustomerCli1> list() throws BusinessException {
        try {
            return customerDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public CustomerCli1 addExternal(CustomerCli1 customer) throws BusinessException, NotFoundException, FoundException {
        Optional<CustomerCli1> foundCustomer;

        // si el Cliente recibido ya existe en la db con otro id externo, se lanza una excepcion
        try {
           foundCustomer = customerDAO.findByBusinessNameAndIdCli1Not(customer.getBusinessName(), customer.getIdCli1());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (foundCustomer.isPresent()) {
            throw FoundException.builder().message("Ya existe un cliente con razon social:" + customer.getBusinessName()).build();
        }

        // si el cliente recibido ya existe en la db con el mismo id externo,se actualiza si hay camabios
        foundCustomer = customerDAO.findOneByIdCli1(customer.getIdCli1());
        if (foundCustomer.isPresent()) {
            if (customer.equals(foundCustomer.get())) {
                return foundCustomer.get();
            }
            // Actualizamos los valores en caso de que hayan cambiado
            foundCustomer.get().setBusinessName(customer.getBusinessName());
            foundCustomer.get().setEmail(customer.getEmail());
            return customerDAO.save(foundCustomer.get());
        }

        // En caso de no existir, se agrega
        try {
            return customerDAO.save(customer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

}