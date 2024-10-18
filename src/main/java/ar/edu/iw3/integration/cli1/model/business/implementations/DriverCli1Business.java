package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.business.interfaces.IDriverCli1Business;
import ar.edu.iw3.model.Driver;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.implementations.DriverBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class DriverCli1Business implements IDriverCli1Business {

    @Autowired
    private DriverBusiness baseDriverBusiness;

    @Override
    public Driver loadOrCreate(Driver driver) throws BusinessException {
        Optional<Driver> findDriver = Optional.empty();
        try{
            findDriver = Optional.ofNullable(baseDriverBusiness.load(driver.getDocument()));
        } catch (NotFoundException ignored) {
            // If the driver is not found, we create it
        }
        if (findDriver.isEmpty()) {
            try {
                return baseDriverBusiness.add(driver);
            } catch (FoundException ignored) {
                // will not happen
            }
        }
        return findDriver.get();
    }
}
