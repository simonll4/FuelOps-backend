package ar.edu.iw3.integration.cli1.util;

import ar.edu.iw3.integration.cli1.model.*;
import ar.edu.iw3.integration.cli1.model.persistence.*;
import ar.edu.iw3.model.*;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.implementations.ProductBusiness;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@Slf4j
public class MapperEntity {

    @Autowired
    private TruckCli1Repository truckDAO;

    @Autowired
    private TankerCli1Repository tankerDAO;

    @Autowired
    private DriverCli1Repository driverDAO;

    @Autowired
    private CustomerCli1Repository customerDAO;

    @Autowired
    private ProductCli1Repository productDAO;

    @Autowired
    private ProductBusiness productBaseBusiness;

//    @Transactional
//    public Product map(ProductCli1 product) throws BusinessException, NotFoundException {
//
//        Product findProduct;
//        findProduct = productBaseBusiness.load(product.getProduct());
//        try {
//            product.setId(findProduct.getId());
//            product.setProduct(findProduct.getProduct());
//            product.setDescription(findProduct.getDescription());
//            product.setThresholdTemperature(findProduct.getThresholdTemperature());
//            productDAO.insertProductCli1(findProduct.getId(), product.getIdCli1());
//            return findProduct;
//        } catch (DataIntegrityViolationException e) {
//            throw BusinessException.builder().ex(e).build();
//        }
//    }

    @Transactional
    public void map(TruckCli1 truckCli1, Truck truck) throws BusinessException {
        if (truckDAO.findOneByIdCli1(truckCli1.getIdCli1()).isEmpty()) { // Comprobacion de que no exista el registro
            try {
                truckDAO.insertTruckCli1(truck.getId(), truckCli1.getIdCli1());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw BusinessException.builder().ex(e).build();
            }
        }
    }

    @Transactional
    public void map(TankerCli1 tankerCli1, Tanker tanker) throws BusinessException {
        if (tankerDAO.findOneByIdCli1(tankerCli1.getIdCli1()).isEmpty()) { // Comprobacion de que no exista el registro
            try {
                tankerDAO.insertTankerCli1(tanker.getId(), tankerCli1.getIdCli1());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw BusinessException.builder().ex(e).build();
            }
        }
    }

    @Transactional
    public void map(DriverCli1 driverCli1, Driver driver) throws BusinessException {
        if (driverDAO.findOneByIdCli1(driverCli1.getIdCli1()).isEmpty()) { // Comprobacion de que no exista el registro
            try {
                driverDAO.insertDriverCli1(driver.getId(), driverCli1.getIdCli1());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw BusinessException.builder().ex(e).build();
            }
        }
    }

    @Transactional
    public void map(CustomerCli1 customerCli1, Customer customer) throws BusinessException {
        if (customerDAO.findOneByIdCli1(customerCli1.getIdCli1()).isEmpty()) { // Comprobacion de que no exista el registro
            try {
                customerDAO.insertCustomerCli1(customer.getId(), customerCli1.getIdCli1());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw BusinessException.builder().ex(e).build();
            }
        }
    }
}
