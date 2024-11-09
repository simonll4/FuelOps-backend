package ar.edu.iw3.integration.cli1.model.business.interfaces;


import ar.edu.iw3.integration.cli1.model.DriverCli1;
import ar.edu.iw3.model.Driver;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface IDriverCli1Business {

    public Driver addExternal(DriverCli1 driver) throws FoundException, BusinessException, NotFoundException;

}
