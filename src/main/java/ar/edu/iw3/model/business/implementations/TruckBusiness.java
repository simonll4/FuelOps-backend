package ar.edu.iw3.model.business.implementations;

import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.ITruckBusiness;
import ar.edu.iw3.model.persistence.TruckRepository;
import ar.edu.iw3.model.Tanker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class TruckBusiness implements ITruckBusiness {

    @Autowired
    private TruckRepository truckDAO;

    @Autowired
    private TankerBusiness tankerBusiness;

    @Override
    public List<Truck> list() throws BusinessException {
        try {
            return truckDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Truck load(long id) throws NotFoundException, BusinessException {
        Optional<Truck> truckFound;

        try {
            truckFound = truckDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (truckFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Camion id= " + id).build();
        return truckFound.get();
    }

    @Override
    public Truck load(String licensePlate) throws NotFoundException, BusinessException {
        Optional<Truck> truckFound;

        try {
            truckFound = truckDAO.findByLicensePlate(licensePlate);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (truckFound.isEmpty())
            throw NotFoundException.builder().message("No se Encuentra el Camion con Patente " + licensePlate).build();

        return truckFound.get();
    }

}
