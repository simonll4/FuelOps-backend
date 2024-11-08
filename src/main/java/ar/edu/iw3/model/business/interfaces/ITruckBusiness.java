package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.Tanker;
import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;
import java.util.Set;

public interface ITruckBusiness {
    public List<Truck> list() throws BusinessException;

    public Truck load(long id) throws NotFoundException, BusinessException;

    public Truck load(String licensePlate) throws NotFoundException, BusinessException;

}
