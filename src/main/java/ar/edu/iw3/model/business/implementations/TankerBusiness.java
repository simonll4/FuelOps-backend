package ar.edu.iw3.model.business.implementations;

import ar.edu.iw3.model.Tanker;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.ITankBusiness;
import ar.edu.iw3.model.persistence.TankerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TankerBusiness implements ITankBusiness {

    @Autowired
    private TankerRepository tankDAO;

    @Override
    public List<Tanker> list() throws BusinessException {
        try {
            return tankDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Tanker load(long id) throws NotFoundException, BusinessException {
        Optional<Tanker> tankFound;

        try {
            tankFound = tankDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (tankFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Tanque id= " + id).build();
        return tankFound.get();
    }


    @Override
    public Tanker load(String license) throws NotFoundException, BusinessException {
        Optional<Tanker> tankFound;

        try {
            tankFound = tankDAO.findByLicense(license);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (tankFound.isEmpty())
            throw NotFoundException.builder().message("No se Encuentra el Tanque con Patente " + license).build();

        return tankFound.get();
    }
}