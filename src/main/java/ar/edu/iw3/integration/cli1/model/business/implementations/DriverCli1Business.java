package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.DriverCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.IDriverCli1Business;
import ar.edu.iw3.integration.cli1.model.persistence.DriverCli1Repository;
import ar.edu.iw3.integration.cli1.util.MapperEntity;
import ar.edu.iw3.model.Driver;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.implementations.DriverBusiness;
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

    @Autowired
    private DriverBusiness driverBaseBusiness;

    @Autowired
    private MapperEntity mapperEntity;

    @Override
    public DriverCli1 add(DriverCli1 driver) throws FoundException, BusinessException, NotFoundException {

        // Si el conductor recibido ya existe en la base de datos, se actualiza
        Optional<DriverCli1> findDriver = driverDAO.findOneByIdCli1(driver.getIdCli1());
        if (findDriver.isPresent()) {
            // Actualizamos los valores en caso de modificaciones
            findDriver.get().setName(driver.getName());
            findDriver.get().setLastName(driver.getLastName());
            driverBaseBusiness.update(findDriver.get());
            return load(driver.getIdCli1());
        }

        // Si el conductor recibido ya existe en la base de datos base, se mapea
        try {
            Driver baseDriver = driverBaseBusiness.load(driver.getDocument());
            mapperEntity.map(driver, baseDriver);
            return driver;

        } catch (NotFoundException ignored) {

        }

        try {
            return driverDAO.save(driver);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }
}
