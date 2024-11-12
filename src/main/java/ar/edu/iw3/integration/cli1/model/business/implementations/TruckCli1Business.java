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

import java.util.Objects;
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
    public TruckCli1 addExternal(TruckCli1 truck) throws FoundException, BusinessException, NotFoundException {
        Optional<TruckCli1> foundTruck;

        // Escenario 1: Camion recibido con codCli1Temp y en db esta con codCli1Temp
        foundTruck = truckDAO.findByLicensePlate(truck.getLicensePlate());
        if (foundTruck.isPresent() && foundTruck.get().isCodCli1Temp() && truck.isCodCli1Temp()) {
            truck.setId(foundTruck.get().getId());
            return updateTruckData(foundTruck.get(), truck, false);
        }

        // si el camion recibido ya existe en la db con otro id externo no temporal, se lanza una excepcion
        try {
            foundTruck = truckDAO.findOneByLicensePlateAndIdCli1NotAndCodCli1Temp(truck.getLicensePlate(), truck.getIdCli1(), truck.isCodCli1Temp());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (foundTruck.isPresent()) {
            throw FoundException.builder().message("Ya existe un camion con Patente" + truck.getLicensePlate()).build();
        }

        // Escenario 2: Camion ya existe con el mismo idCli1, se actualiza si hay cambios
        foundTruck = truckDAO.findOneByIdCli1(truck.getIdCli1());
        if (foundTruck.isPresent()) {
            return updateTruckData(foundTruck.get(), truck, false);
        }

        foundTruck = truckDAO.findByLicensePlate(truck.getLicensePlate());
        if (foundTruck.isPresent()) {

            // Escenario 3: Camion llega con codCli1Temp, pero existe un id fijo
            if (!foundTruck.get().isCodCli1Temp() && truck.isCodCli1Temp()) {
                truck.setIdCli1(foundTruck.get().getIdCli1());
                truck.setId(foundTruck.get().getId());
                truck.setCodCli1Temp(false);
                return updateTruckData(foundTruck.get(), truck, false);
            }

            // Escenario 4: Camion con codCli1Temp y llega con id fijo
            if (foundTruck.get().isCodCli1Temp() && !truck.isCodCli1Temp()) {
                foundTruck.get().setIdCli1(truck.getIdCli1());
                foundTruck.get().setCodCli1Temp(false);
                return updateTruckData(foundTruck.get(), truck, true);
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

    // Funcion auxiliar para actualizar datos del camión y tankers si han cambiado
    private TruckCli1 updateTruckData(TruckCli1 existingTruck, TruckCli1 newTruck, boolean updated) {

        // Actualizar campos si es necesario
        if (!Objects.equals(existingTruck.getLicensePlate(), newTruck.getLicensePlate())) {
            existingTruck.setLicensePlate(newTruck.getLicensePlate());
            updated = true;
        }
        if (!Objects.equals(existingTruck.getDescription(), newTruck.getDescription())) {
            existingTruck.setDescription(newTruck.getDescription());
            updated = true;
        }

        // Procesar tankers
        if (!existingTruck.getTankers().equals(newTruck.getTankers())) {
            existingTruck.getTankers().clear();
            truckDAO.save(existingTruck);
            for (Tanker tanker : newTruck.getTankers()) {
                tanker.setTruck(existingTruck);
                existingTruck.getTankers().add(tanker);
            }
            updated = true;
        }
        return updated ? truckDAO.save(existingTruck) : existingTruck;
    }
}
