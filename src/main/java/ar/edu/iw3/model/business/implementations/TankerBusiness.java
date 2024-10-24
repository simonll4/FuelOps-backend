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

    @Override
    public Tanker add(Tanker tanker) throws FoundException, BusinessException {
        try {
            load(tanker.getId());
            throw FoundException.builder().message("Ya existe el Tanque id= " + tanker.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            load(tanker.getLicense());
            throw FoundException.builder().message("Ya existe el Tanque con Patente " + tanker.getLicense()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            return tankDAO.save(tanker);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al Crear Nuevo Tanque").build();
        }
    }

    @Override
    public Tanker update(Tanker tanker) throws NotFoundException, BusinessException, FoundException {
        load(tanker.getId());

        Optional<Tanker> tankFound;
        try {
            tankFound = tankDAO.findByLicenseAndIdNot(tanker.getLicense(), tanker.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (tankFound.isPresent()) {
            throw FoundException.builder().message("Ya Existe un Tanque con Patente =" + tanker.getLicense()).build();
        }

        try {
            return tankDAO.save(tanker);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al Actualizar Camion").build();
        }
    }

    @Override
    public void delete(Tanker tanker) throws NotFoundException, BusinessException {
        delete(tanker.getId());
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);

        try {
            tankDAO.deleteById(id);
        } catch (Exception e){
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Tanker loadOrCreate(Tanker tanker) throws BusinessException {
        Optional<Tanker> findTanker;

        try {
            findTanker = tankDAO.findByLicense(tanker.getLicense());
            return findTanker.orElseGet(() -> tankDAO.save(tanker));

        } catch (Exception e) {
            throw BusinessException.builder().ex(e).build();
        }
    }
}