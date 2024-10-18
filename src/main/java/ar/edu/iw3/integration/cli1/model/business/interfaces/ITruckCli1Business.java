package ar.edu.iw3.integration.cli1.model.business.interfaces;

import ar.edu.iw3.model.Tanker;
import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.BusinessException;

import java.util.Set;

public interface ITruckCli1Business {
    public Truck loadOrCreate(Truck truck) throws BusinessException;


}
