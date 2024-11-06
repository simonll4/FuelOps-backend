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
import java.util.Objects;
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

        // si el Cliente recibido ya existe en la db con otro id externo no temporal, se lanza una excepcion
        try {
           foundCustomer = customerDAO.findByBusinessNameAndIdCli1NotAndCodCli1Temp(customer.getBusinessName(), customer.getIdCli1(), customer.isCodCli1Temp());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (foundCustomer.isPresent()) {
            throw FoundException.builder().message("Ya existe un cliente con razon social:" + customer.getBusinessName()).build();
        }

        // Escenario 1: Cliente existe con el mismo idCli1, se actualiza si hay cambios
        foundCustomer = customerDAO.findOneByIdCli1(customer.getIdCli1());
        if (foundCustomer.isPresent()) {
            return updateCustomerData(foundCustomer.get(), customer, false);
        }

        // Escenario 2 y 3: Buscar cliente por businessName
        foundCustomer = customerDAO.findByBusinessName(customer.getBusinessName());
        if (foundCustomer.isPresent()) {
            CustomerCli1 existingCustomer = foundCustomer.get();

            // Escenario 2: Cliente enviado con id temporal, pero existe un id fijo
            if (!existingCustomer.isCodCli1Temp() && customer.isCodCli1Temp()) {
                customer.setIdCli1(existingCustomer.getIdCli1());
                customer.setId(existingCustomer.getId());
                customer.setCodCli1Temp(false);
                return updateCustomerData(existingCustomer, customer, false);
            }

            // Escenario 3: Existe con id temporal y llega con id fijo
            if (existingCustomer.isCodCli1Temp() && !customer.isCodCli1Temp()) {
                existingCustomer.setIdCli1(customer.getIdCli1());
                existingCustomer.setCodCli1Temp(false);
                return updateCustomerData(existingCustomer, customer, true);
            }
        }

        // En caso de no existir, se agrega
        try {
            return customerDAO.save(customer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }


    }

    // Funcion auxiliar para actualizar datos del customer si han cambiado
    private CustomerCli1 updateCustomerData(CustomerCli1 foundCustomer, CustomerCli1 newCustomer, boolean updated) {

        if (!Objects.equals(foundCustomer.getBusinessName(), newCustomer.getBusinessName())) {
            foundCustomer.setBusinessName(newCustomer.getBusinessName());
            updated = true;
        }
        if (!Objects.equals(foundCustomer.getEmail(), newCustomer.getEmail())) {
            foundCustomer.setEmail(newCustomer.getEmail());
            updated = true;
        }
        return updated ? customerDAO.save(foundCustomer) : foundCustomer;
    }
}



