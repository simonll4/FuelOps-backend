package ar.edu.iw3.model.business.implementations;

import ar.edu.iw3.model.Customer;
import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.ICustomerBusiness;
import ar.edu.iw3.model.persistence.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CustomerBusiness implements ICustomerBusiness {

    @Autowired
    private CustomerRepository customerDAO;

    @Override
    public List<Customer> list() throws BusinessException {
        try {
            return customerDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Customer load(long id) throws NotFoundException, BusinessException {
        Optional<Customer> customerFound;

        try {
            customerFound = customerDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (customerFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Cliente id= " + id).build();
        return customerFound.get();
    }

    @Override
    public Customer load(String businessName) throws NotFoundException, BusinessException {
        Optional<Customer> customerFound;

        try {
            customerFound = customerDAO.findByBusinessName(businessName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (customerFound.isEmpty())
            throw NotFoundException.builder().message("No se Encuentra el Cliente " + businessName).build();

        return customerFound.get();
    }

    @Override
    public Customer add(Customer customer) throws FoundException, BusinessException {
        try {
            load(customer.getId());
            throw FoundException.builder().message("Ya existe el Cliente id= " + customer.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            load(customer.getBusinessName());
            throw FoundException.builder().message("Ya existe el Cliente " + customer.getBusinessName()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            return customerDAO.save(customer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al Crear Nuevo Producto").build();
        }
    }

    @Override
    public Customer update(Customer customer) throws NotFoundException, BusinessException, FoundException {
        load(customer.getId());

        Optional<Customer> customerFound;
        try {
            customerFound = customerDAO.findByBusinessNameAndIdNot(customer.getBusinessName(), customer.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (customerFound.isPresent()) {
            throw FoundException.builder().message("Ya Existe un Cliente con el Nombre =" + customer.getBusinessName()).build();
        }

        try {
            return customerDAO.save(customer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al Actualizar Producto").build();
        }
    }

    @Override
    public void delete(Customer customer) throws NotFoundException, BusinessException {
        delete(customer.getId());
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);

        try {
            customerDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Customer loadOrCreate(Customer customer) throws BusinessException {
        Optional<Customer> findCustomer;
        try {
            findCustomer = customerDAO.findByBusinessName(customer.getBusinessName());
            return findCustomer.orElseGet(() -> customerDAO.save(customer));
        }catch (Exception e){
            throw BusinessException.builder().ex(e).build();
        }
    }
}
