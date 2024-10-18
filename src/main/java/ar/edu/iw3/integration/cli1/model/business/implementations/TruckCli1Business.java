package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.business.interfaces.ITruckCli1Business;
import ar.edu.iw3.model.Tanker;
import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.implementations.TankBusiness;
import ar.edu.iw3.model.business.implementations.TruckBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Service
@Slf4j
public class TruckCli1Business implements ITruckCli1Business {

    @Autowired
    private TruckBusiness baseTruckBusiness;

    @Override
    public Truck loadOrCreate(Truck truck) throws BusinessException {

        Optional<Truck> findTruck = Optional.empty();
        try {
            findTruck = Optional.ofNullable(baseTruckBusiness.load(truck.getLicensePlate()));
        } catch (NotFoundException ignored) {
            // If the truck is not found, we create it
        }

        if (findTruck.isEmpty()) {
            try {
                truck = baseTruckBusiness.add(truck);
            } catch (FoundException ignored) {
                // will not happen
            }

            truck.setTankers(baseTruckBusiness.processTankers(truck));
            return truck;
        }
        return findTruck.get();
    }


}
