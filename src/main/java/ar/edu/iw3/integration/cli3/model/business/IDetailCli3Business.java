package ar.edu.iw3.integration.cli3.model.business;

import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.exceptions.UnProcessableException;

public interface IDetailCli3Business {

    public void receiveDetails(Detail detail) throws NotFoundException, BusinessException, FoundException, UnProcessableException;

}
