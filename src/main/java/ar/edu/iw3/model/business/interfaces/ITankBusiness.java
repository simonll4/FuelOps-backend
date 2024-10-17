package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.Tanker;
import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface ITankBusiness {
    public List<Tanker> list() throws BusinessException;

    public Tanker load(long id) throws NotFoundException, BusinessException;

    public Tanker load(String license) throws NotFoundException, BusinessException;

    public Tanker add(Tanker tank) throws FoundException, BusinessException;

    public Tanker update(Tanker tank) throws NotFoundException, BusinessException, FoundException;

    public void delete(Tanker tank) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;

    public Tanker loadOrCreate(Tanker tank) throws BusinessException;
}
