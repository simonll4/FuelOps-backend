package ar.edu.iw3.integration.cli1.model.business.interfaces;

import ar.edu.iw3.integration.cli1.model.TankerCli1;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ITankerCli1Business {
    public TankerCli1 load(String idCli1) throws NotFoundException, BusinessException;

    public List<TankerCli1> list() throws BusinessException;

    public TankerCli1 add(TankerCli1 tanker) throws FoundException, BusinessException;

}
