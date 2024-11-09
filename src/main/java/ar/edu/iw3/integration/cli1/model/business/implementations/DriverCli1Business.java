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

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class DriverCli1Business implements IDriverCli1Business {

    @Autowired
    private DriverCli1Repository driverDAO;

    @Override
    public DriverCli1 addExternal(DriverCli1 driver) throws FoundException, BusinessException, NotFoundException {
        Optional<DriverCli1> foundDriver;

        // Escenario 1: Conductor recibido con codCli1Temp y en db esta con codCli1Temp
        foundDriver = driverDAO.findByDocument(driver.getDocument());
        if (foundDriver.isPresent() && foundDriver.get().isCodCli1Temp() && driver.isCodCli1Temp()) {
            driver.setId(foundDriver.get().getId());
            return updateDriverData(foundDriver.get(), driver, false);
        }

        // si el conductor recibido ya existe en la db con otro id externo no temporal, se lanza una excepcion
        try {
            foundDriver = driverDAO.findByDocumentAndIdCli1NotAndCodCli1Temp(driver.getDocument(), driver.getIdCli1(), driver.isCodCli1Temp());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (foundDriver.isPresent()) {
            throw FoundException.builder().message("Ya existe un conductor con DNI" + driver.getDocument()).build();
        }

        // Escenario 2: Conductor ya existe con el mismo idCli1, se actualiza si hay cambios
        foundDriver = driverDAO.findOneByIdCli1(driver.getIdCli1());
        if (foundDriver.isPresent()) {
            return updateDriverData(foundDriver.get(), driver, false);
        }

        foundDriver = driverDAO.findByDocument(driver.getDocument());
        if (foundDriver.isPresent()) {

            // Escenario 3: Conductor llega con codCli1Temp, pero existe un id fijo
            if (!foundDriver.get().isCodCli1Temp() && driver.isCodCli1Temp()) {
                driver.setIdCli1(foundDriver.get().getIdCli1());
                driver.setId(foundDriver.get().getId());
                driver.setCodCli1Temp(false);
                return updateDriverData(foundDriver.get(), driver, false);
            }

            // Escenario 4: Conductor con codCli1Temp y llega con id fijo
            if (foundDriver.get().isCodCli1Temp() && !driver.isCodCli1Temp()) {
                foundDriver.get().setIdCli1(driver.getIdCli1());
                foundDriver.get().setCodCli1Temp(false);
                return updateDriverData(foundDriver.get(), driver, true);
            }
        }

        // En caso de no existir, se agrega
        try {
            return driverDAO.save(driver);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    // Funcion auxiliar para actualizar datos del driver si han cambiado
    private DriverCli1 updateDriverData(DriverCli1 foundDriver, DriverCli1 newDriver, boolean updated) {

        if (!Objects.equals(foundDriver.getName(), newDriver.getName())) {
            foundDriver.setName(newDriver.getName());
            updated = true;
        }
        if (!Objects.equals(foundDriver.getLastName(), newDriver.getLastName())) {
            foundDriver.setLastName(newDriver.getLastName());
            updated = true;
        }
        return updated ? driverDAO.save(foundDriver) : foundDriver;
    }

}
