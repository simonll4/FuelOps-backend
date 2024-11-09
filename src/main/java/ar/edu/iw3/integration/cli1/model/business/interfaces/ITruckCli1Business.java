package ar.edu.iw3.integration.cli1.model.business.interfaces;

import ar.edu.iw3.integration.cli1.model.TruckCli1;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;


public interface ITruckCli1Business {

    public TruckCli1 load(String idCli1) throws NotFoundException, BusinessException;

    public TruckCli1 addExternal(TruckCli1 truck) throws FoundException, BusinessException, NotFoundException;
}
