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
    private ICustomerBusiness customerBaseBusiness;

    @Autowired
    private MapperEntity mapperEntity;

    @Override
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

        } catch (NotFoundException ignored) {


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