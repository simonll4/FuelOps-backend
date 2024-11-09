package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.TankerCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.ITankerCli1Business;
import ar.edu.iw3.integration.cli1.model.persistence.TankerCli1Repository;
import ar.edu.iw3.model.Tanker;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.implementations.TankerBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TankerCli1Business implements ITankerCli1Business {

    // todo borrar si no se usa

    @Autowired
    private TankerCli1Repository tankerDAO;

    @Autowired
    private TankerBusiness baseTankerBusiness;
//    @Override
//    public TankerCli1 add(TankerCli1 tanker) throws FoundException, BusinessException {
//        try {
//            Tanker baseTanker = baseTankerBusiness.load(tanker.getLicense());
//            mapperEntity.map(tanker, baseTanker);
//            throw FoundException.builder().message("Se encontró el cisterna con id=" + tanker.getId()).build();
//        } catch (NotFoundException ignored) {
//
//        }
//        if (tankerDAO.findOneByIdCli1(tanker.getIdCli1()).isPresent()) {
//            throw FoundException.builder().message("Se encontró el cisterna con idCli1=" + tanker.getIdCli1()).build();
//        }
//
//        try {
//            return tankerDAO.save(tanker);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            throw BusinessException.builder().ex(e).build();
//        }
//    }
}