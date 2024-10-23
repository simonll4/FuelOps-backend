package ar.edu.iw3.integration.cli3.model.business.interfaces;

import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.business.exceptions.*;

public interface IDetailCli3Business {

    void add(Detail detail) throws FoundException, BusinessException, NotFoundException;
}
