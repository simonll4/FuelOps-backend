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

}
