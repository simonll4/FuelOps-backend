package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.TruckCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.ITruckCli1Business;
import ar.edu.iw3.integration.cli1.model.persistence.TruckCli1Repository;
import ar.edu.iw3.integration.cli1.util.MapperEntity;
import ar.edu.iw3.model.Tanker;
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
import java.util.Set;


@Service
@Slf4j
public class TruckCli1Business implements ITruckCli1Business {

    @Autowired
    private TruckCli1Repository truckDAO;

    @Autowired
    private TruckBusiness baseTruckBusiness;
    @Autowired
    private TankerCli1Business tankerCli1Business;


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

    @Autowired
    private MapperEntity mapperEntity;

    @Override
    public TruckCli1 add(TruckCli1 truck) throws FoundException, BusinessException, NotFoundException {

        // Si el camion recibido ya existe en la base de datos, se actualiza
        Optional<TruckCli1> findTruck = truckDAO.findOneByIdCli1(truck.getIdCli1());
        if (findTruck.isPresent()) {
            // Actualizamos los valores en caso de cambios
            findTruck.get().setDescription(truck.getDescription());
            findTruck.get().setTankers(truck.getTankers());

            // Procesado del cisternado
            Set<Tanker> newTankers = baseTruckBusiness.processTankers(findTruck.get());
            findTruck.get().setTankers(newTankers);

            baseTruckBusiness.update(findTruck.get());
            return load(truck.getIdCli1());
        }

        // Si el camion ya existe en base de datos, se mapea
        try {
            Truck baseTruck = baseTruckBusiness.load(truck.getLicensePlate());
            mapperEntity.map(truck, baseTruck);
            return truck;
        } catch (NotFoundException ignored) {
            // log.trace(e.getMessage(), e);
        }

        try {
            TruckCli1 newTruck = truckDAO.save(truck);
            newTruck.setTankers(baseTruckBusiness.processTankers(truck));
            return newTruck;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }
}
