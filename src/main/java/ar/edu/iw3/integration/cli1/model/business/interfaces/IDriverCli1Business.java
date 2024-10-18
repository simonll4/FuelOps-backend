package ar.edu.iw3.integration.cli1.model.business.interfaces;

import ar.edu.iw3.model.Driver;
import ar.edu.iw3.model.business.exceptions.BusinessException;

public interface IDriverCli1Business {
    public Driver loadOrCreate(Driver driver) throws BusinessException;
}
