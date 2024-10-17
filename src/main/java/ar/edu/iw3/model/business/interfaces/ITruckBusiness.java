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

    public Truck add(Truck truck) throws FoundException, BusinessException;

    public Truck update(Truck truck) throws NotFoundException, BusinessException, FoundException;

    public void delete(Truck truck) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;

    public Truck loadOrCreate(Truck truck) throws BusinessException;

    public Set<Tanker> processTankers(Truck truck) throws BusinessException;

}
