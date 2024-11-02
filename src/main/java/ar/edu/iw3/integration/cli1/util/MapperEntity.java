package ar.edu.iw3.integration.cli1.util;

import ar.edu.iw3.integration.cli1.model.*;
import ar.edu.iw3.integration.cli1.model.persistence.*;
import ar.edu.iw3.model.*;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MapperEntity {

    @Autowired
    private TruckCli1Repository truckDAO;

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

    @Autowired
    private TankerCli1Repository tankerDAO;

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

    @Autowired
    private DriverCli1Repository driverDAO;

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

    @Autowired
    private CustomerCli1Repository customerDAO;

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
