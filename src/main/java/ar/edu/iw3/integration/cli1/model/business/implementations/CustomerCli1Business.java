package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.CustomerCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.ICustomerCli1Business;
import ar.edu.iw3.integration.cli1.model.persistence.CustomerCli1Repository;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class CustomerCli1Business implements ICustomerCli1Business {

    @Autowired
    private CustomerCli1Repository customerDAO;

    @Override
    public CustomerCli1 addExternal(CustomerCli1 customer) throws BusinessException, NotFoundException, FoundException {
        Optional<CustomerCli1> foundCustomer;

        // Escenario 1: Cliente recibido con codCli1Temp y en db esta con codCli1Temp
        foundCustomer = customerDAO.findByBusinessName(customer.getBusinessName());
        if (foundCustomer.isPresent() && foundCustomer.get().isCodCli1Temp() && customer.isCodCli1Temp()) {
            customer.setId(foundCustomer.get().getId());
            return updateCustomerData(foundCustomer.get(), customer, false);
        }

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

        // Escenario 2: Cliente ya existe con el mismo idCli1, se actualiza si hay cambios
        foundCustomer = customerDAO.findOneByIdCli1(customer.getIdCli1());
        if (foundCustomer.isPresent()) {
            return updateCustomerData(foundCustomer.get(), customer, false);
        }

        foundCustomer = customerDAO.findByBusinessName(customer.getBusinessName());
        if (foundCustomer.isPresent()) {

            // Escenario 3: Cliente llega con codCli1Temp, pero existe un id fijo
            if (!foundCustomer.get().isCodCli1Temp() && customer.isCodCli1Temp()) {
                customer.setIdCli1(foundCustomer.get().getIdCli1());
                customer.setId(foundCustomer.get().getId());
                customer.setCodCli1Temp(false);
                return updateCustomerData(foundCustomer.get(), customer, false);
            }

            // Escenario 4: Existe con codCli1Temp y llega con id fijo
            if (foundCustomer.get().isCodCli1Temp() && !customer.isCodCli1Temp()) {
                foundCustomer.get().setIdCli1(customer.getIdCli1());
                foundCustomer.get().setCodCli1Temp(false);
                return updateCustomerData(foundCustomer.get(), customer, true);
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