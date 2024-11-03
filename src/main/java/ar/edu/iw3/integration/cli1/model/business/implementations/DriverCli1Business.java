package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.DriverCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.IDriverCli1Business;
import ar.edu.iw3.integration.cli1.model.persistence.DriverCli1Repository;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DriverCli1Business implements IDriverCli1Business {

    @Autowired
    private DriverCli1Repository driverDAO;

    @Override
    public DriverCli1 load(String idCli1) throws NotFoundException, BusinessException {
        Optional<DriverCli1> r;
        try {
            r = driverDAO.findOneByIdCli1(idCli1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (r.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el conductor idCli1=" + idCli1).build();
        }
        return r.get();
    }

    @Override
    public List<DriverCli1> list() throws BusinessException {
        try {
            return driverDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public DriverCli1 addExternal(DriverCli1 driver) throws FoundException, BusinessException, NotFoundException {
        Optional<DriverCli1> foundDriver;

        // si el conductor recibido ya existe en la db con otro id externo, se lanza una excepcion
        try {
            foundDriver = driverDAO.findByDocumentAndIdCli1Not(driver.getDocument(), driver.getIdCli1());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (foundDriver.isPresent()) {
            throw FoundException.builder().message("Ya existe un conductor con DNI" + driver.getDocument()).build();
        }

        // si el conductor recibido ya existe en la db con el mismo id externo, y se actualiza si hay camabios
        foundDriver = driverDAO.findOneByIdCli1(driver.getIdCli1());
        if (foundDriver.isPresent()) {
            if (driver.equals(foundDriver.get())) {
                return foundDriver.get();
            }
            foundDriver.get().setName(driver.getName());
            foundDriver.get().setLastName(driver.getLastName());
            return driverDAO.save(foundDriver.get());
        }

        // si el conductor recibido no existe en la db, se guarda
        try {
            return driverDAO.save(driver);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

}
