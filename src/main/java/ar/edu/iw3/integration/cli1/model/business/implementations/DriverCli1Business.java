package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.CustomerCli1;
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
import java.util.Objects;
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

        // Escenario 1: Conductor existe con el mismo idCli1, se actualiza si hay cambios
        foundDriver = driverDAO.findOneByIdCli1(driver.getIdCli1());
        if (foundDriver.isPresent()) {
            return updateDriverData(foundDriver.get(), driver, false);
        }

        // Escenario 2 y 3: Buscar driver por document
        foundDriver = driverDAO.findByDocument(driver.getDocument());
        if (foundDriver.isPresent()) {
            DriverCli1 existingDriver = foundDriver.get();

            // Escenario 2: Cliente enviado con id temporal, pero existe un id fijo
            if (!existingDriver.isCodCli1Temp() && driver.isCodCli1Temp()) {
                driver.setIdCli1(existingDriver.getIdCli1());
                driver.setId(existingDriver.getId());
                driver.setCodCli1Temp(false);
                return updateDriverData(existingDriver, driver, false);
            }

            // Escenario 3: Existe con id temporal y llega con id fijo
            if (existingDriver.isCodCli1Temp() && !driver.isCodCli1Temp()) {
                existingDriver.setIdCli1(driver.getIdCli1());
                existingDriver.setCodCli1Temp(false);
                return updateDriverData(existingDriver, driver, true);
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
