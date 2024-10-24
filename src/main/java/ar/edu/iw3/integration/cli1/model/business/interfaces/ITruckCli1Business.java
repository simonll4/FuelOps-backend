package ar.edu.iw3.integration.cli1.model.business.interfaces;

import ar.edu.iw3.integration.cli1.model.OrderCli1;
import ar.edu.iw3.integration.cli1.model.TruckCli1;
import ar.edu.iw3.model.Tanker;
import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.BadRequestException;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;
import java.util.Set;

public interface ITruckCli1Business {

    public TruckCli1 load(String idCli1) throws NotFoundException, BusinessException;

    public List<TruckCli1> list() throws BusinessException;

    public TruckCli1 add(TruckCli1 truck) throws FoundException, BusinessException;

    public TruckCli1 loadOrCreate(TruckCli1 truck) throws BusinessException;


}
