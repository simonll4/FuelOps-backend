package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.TruckCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.ITruckCli1Business;
import ar.edu.iw3.integration.cli1.model.persistence.TruckCli1Repository;
import ar.edu.iw3.model.Tanker;
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
public class TruckCli1Business implements ITruckCli1Business {

    @Autowired
    private TruckCli1Repository truckDAO;

    @Override
    public TruckCli1 load(String idCli1) throws NotFoundException, BusinessException {
        Optional<TruckCli1> r;
        try {
            r = truckDAO.findOneByIdCli1(idCli1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (r.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el camion idCli1=" + idCli1).build();
        }
        return r.get();
    }

    @Override
    public List<TruckCli1> list() throws BusinessException {
        try {
            return truckDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public TruckCli1 addExternal(TruckCli1 truck) throws FoundException, BusinessException, NotFoundException {

        Optional<TruckCli1> foundTruck;
        try {
            foundTruck = truckDAO.findOneByLicensePlateAndIdCli1Not(truck.getLicensePlate(), truck.getIdCli1());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (foundTruck.isPresent()) {
            throw FoundException.builder().message("Ya existe un camion con Patente" + truck.getLicensePlate()).build();
        }

        //si existe el camion, chequeamos si hay cambios y actualizamos
        foundTruck = truckDAO.findOneByIdCli1(truck.getIdCli1());
        if (foundTruck.isPresent()) {
            TruckCli1 existingTruck = foundTruck.get();
            if (!existingTruck.getTankers().equals(truck.getTankers())) {
                existingTruck.getTankers().clear();
                truckDAO.save(existingTruck);
                for (Tanker tanker : truck.getTankers()) {
                    tanker.setTruck(existingTruck);
                    existingTruck.getTankers().add(tanker);
                }
                truckDAO.save(existingTruck);
                return load(truck.getIdCli1());
            } else {
                return existingTruck;
            }
        }

        // Guardar un nuevo camión si no existe
        try {
            for (Tanker tanker : truck.getTankers()) {
                tanker.setTruck(truck);
            }
            truckDAO.save(truck);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        return load(truck.getIdCli1());
    }
}
