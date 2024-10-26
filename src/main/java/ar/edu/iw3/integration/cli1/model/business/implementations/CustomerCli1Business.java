package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.CustomerCli1;
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
    public CustomerCli1 add(CustomerCli1 customer) throws FoundException, BusinessException {

        try {
            Customer customerBase = customerBaseBusiness.load(customer.getBusinessName());
            mapperEntity.map(customer, customerBase);
            throw FoundException.builder().message("Se encontró el cliente id=" + customer.getId()).build();
        } catch (NotFoundException ignored) {
        }

        if (customerDAO.findOneByIdCli1(customer.getIdCli1()).isPresent()) {
            throw FoundException.builder().message("Se encontró el cliente idCli1=" + customer.getIdCli1()).build();
        }

        try {
            return customerDAO.save(customer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Customer loadOrCreate(CustomerCli1 customer) throws BusinessException, NotFoundException {
        Optional<Customer> findCustomer = Optional.empty();

        // todo logica repetida
        try {
            findCustomer = Optional.ofNullable(customerBaseBusiness.load(customer.getBusinessName()));
        } catch (NotFoundException ignored) {
        }

        if (findCustomer.isEmpty()) {
            try {
                //return customerBaseBusiness.load(add(customer).getId());
                return add(customer);
            } catch (FoundException ignored) {
            }
        }
        mapperEntity.map(customer, findCustomer.get());
        return findCustomer.get();
    }

}