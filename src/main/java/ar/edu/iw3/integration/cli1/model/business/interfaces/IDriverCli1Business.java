package ar.edu.iw3.integration.cli1.model.business.interfaces;

import ar.edu.iw3.integration.cli1.model.DriverCli1;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface IDriverCli1Business {

    public DriverCli1 load(String idCli1) throws NotFoundException, BusinessException;

    public List<DriverCli1> list() throws BusinessException;

<<<<<<< Updated upstream
    public DriverCli1 add(DriverCli1 driver) throws FoundException, BusinessException;

    public Driver loadOrCreate(DriverCli1 driver) throws BusinessException, NotFoundException;
=======
    public DriverCli1 add(DriverCli1 driver) throws FoundException, BusinessException, NotFoundException;

>>>>>>> Stashed changes
}
