package ar.edu.iw3.model.business.implementations;

import ar.edu.iw3.model.Driver;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IDriverBusiness;
import ar.edu.iw3.model.persistence.DriverRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DriverBusiness implements IDriverBusiness {

    @Autowired
    private DriverRepository driverDAO;

    @Override
    public List<Driver> list() throws BusinessException {
        try {
            return driverDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Driver load(long id) throws NotFoundException, BusinessException {
        Optional<Driver> driverFound;

        try {
            driverFound = driverDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (driverFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Chofer id= " + id).build();
        return driverFound.get();
    }

    @Override
    public Driver load(String document) throws NotFoundException, BusinessException {
        Optional<Driver> driverFound;

        try {
            driverFound = driverDAO.findByDocument(document);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (driverFound.isEmpty())
            throw NotFoundException.builder().message("No se Encuentra el Chofer con DNI " + document).build();

        return driverFound.get();
    }

    @Override
    public Driver add(Driver driver) throws FoundException, BusinessException {
        try {
            load(driver.getId());
            throw FoundException.builder().message("Ya existe el Chofer id= " + driver.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            load(driver.getDocument());
            throw FoundException.builder().message("Ya existe el Chofer con DNI " + driver.getDocument()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            return driverDAO.save(driver);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al Crear Nuevo Chofer").build();
        }
    }

    @Override
    public Driver update(Driver driver) throws NotFoundException, BusinessException, FoundException {
        load(driver.getId());

        Optional<Driver> driverFound;
        try {
            driverFound = driverDAO.findByDocumentAndIdNot(driver.getDocument(),driver.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (driverFound.isPresent()) {
            throw FoundException.builder().message("Ya Existe un Chofer con DNI =" + driver.getDocument()).build();
        }

        try {
            return driverDAO.save(driver);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al Actualizar Chofer").build();
        }
    }

    @Override
    public void delete(Driver driver) throws NotFoundException, BusinessException {
        delete(driver.getId());
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);

        try {
            driverDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }


}
