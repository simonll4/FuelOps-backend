package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.CustomerCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.ICustomerCli1Business;
import ar.edu.iw3.integration.cli1.model.persistence.CustomerCli1Repository;
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

    @Autowired
    private ICustomerBusiness baseCustomerBusiness;

    @Autowired
    private Mapper mapper;

    @Override
<<<<<<< Updated upstream
    public CustomerCli1 add(CustomerCli1 customer) throws FoundException, BusinessException {
        try {
            Customer baseCustomer = baseCustomerBusiness.load(customer.getBusinessName());
            mapper.map(customer, baseCustomer);
            throw FoundException.builder().message("Se encontró el cliente id=" + customer.getId()).build();
=======
    public CustomerCli1 add(CustomerCli1 customer) throws BusinessException, NotFoundException, FoundException {

        // Si el cliente recibido ya existe en la base de datos base, se actualiza
        Optional<CustomerCli1> findCustomer = customerDAO.findOneByIdCli1(customer.getIdCli1());
        if (findCustomer.isPresent()) {
            // Actualizamos los valores en caso de que hayan cambiado
            findCustomer.get().setBusinessName(customer.getBusinessName());
            findCustomer.get().setEmail(customer.getEmail());
            customerBaseBusiness.update(findCustomer.get());
            return load(customer.getIdCli1());
        }

        // Si el cliente recibido ya existe en la base de datos base, se mapea
        try {
            Customer customerBase = customerBaseBusiness.load(customer.getBusinessName());
            mapperEntity.map(customer, customerBase);
            return customer;
>>>>>>> Stashed changes
        } catch (NotFoundException ignored) {

<<<<<<< Updated upstream
        }
        if (customerDAO.findOneByIdCli1(customer.getIdCli1()).isPresent()) {
            throw FoundException.builder().message("Se encontró el cliente idCli1=" + customer.getIdCli1()).build();
        }
=======
        // En caso de no existir, se agrega
>>>>>>> Stashed changes
        try {
            return customerDAO.save(customer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

<<<<<<< Updated upstream

    @Override
    public Customer loadOrCreate(CustomerCli1 customer) throws BusinessException, NotFoundException {
        Optional<Customer> findCustomer = Optional.empty();
        try {
            findCustomer = Optional.ofNullable(baseCustomerBusiness.load(customer.getBusinessName()));

        } catch (NotFoundException ignored) {
            // If the customer is not found, we create it
        }

        if (findCustomer.isEmpty()) {
            try {
                return baseCustomerBusiness.load(add(customer).getId());
            } catch (FoundException ignored) {
                // will not happen
            }
        }
        mapper.map(customer, findCustomer.get());
        return findCustomer.get();

    }
=======
>>>>>>> Stashed changes
}