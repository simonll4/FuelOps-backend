package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.TruckCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.ITruckCli1Business;
import ar.edu.iw3.integration.cli1.model.persistence.TruckCli1Repository;
import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.implementations.TruckBusiness;
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

    @Autowired
    private TruckBusiness baseTruckBusiness;


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
    public TruckCli1 add(TruckCli1 truck) throws FoundException, BusinessException {
        try {
            baseTruckBusiness.load(truck.getId());
            throw FoundException.builder().message("Se encontró el camion id=" + truck.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        if (truckDAO.findOneByIdCli1(truck.getIdCli1()).isPresent()) {
            throw FoundException.builder().message("Se encontró el camion idCli1=" + truck.getIdCli1()).build();
        }

        try {
            return truckDAO.save(truck);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public TruckCli1 loadOrCreate(TruckCli1 truck) throws BusinessException {

        Optional<Truck> findTruck = Optional.empty();
        try {
            findTruck = Optional.ofNullable(baseTruckBusiness.load(truck.getLicensePlate()));
        } catch (NotFoundException ignored) {
            // If the truck is not found, we create it
        }

        if (findTruck.isEmpty()) {
            try {
                truck = add(truck);
            } catch (FoundException ignored) {
                // will not happen
            }

            truck.setTankers(baseTruckBusiness.processTankers(truck));
            return truck;
        }
        return (TruckCli1) findTruck.get();
    }
}
