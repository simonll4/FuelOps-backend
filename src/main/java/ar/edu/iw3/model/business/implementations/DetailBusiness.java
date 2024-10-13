package ar.edu.iw3.model.business.implementations;

import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IDetailBusiness;
import ar.edu.iw3.model.persistence.DetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DetailBusiness implements IDetailBusiness {

    // IoC
    @Autowired
    private DetailRepository detailDAO;

    @Override
    public Detail load(long id) throws NotFoundException, BusinessException {
        Optional<Detail> detailFound;

        try {
            detailFound = detailDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (detailFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Detalle id= " + id).build();
        return detailFound.get();

    }

    @Override
    public Detail add(Detail detail) throws FoundException, BusinessException {
        try {
            load(detail.getId());
            throw FoundException.builder().message("Ya existe el detalle id = " + detail.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        // todo validar que el detail traiga todos los campos antes de guardar (donde hacer eso?)

        try {
            return detailDAO.save(detail);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al Crear Nuevo Detalle").build();
        }

    }

    // lista todos los detalles de una orden
    @Override
    public List<Detail> listByOrder(long idOrder) throws NotFoundException, BusinessException {
        Optional<List<Detail>> detailsFound;

        try {
            detailsFound = detailDAO.findByOrderId(idOrder);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (detailsFound.isEmpty())
            throw NotFoundException.builder().message("Orden sin detalles = " + idOrder).build();
        return detailsFound.get();
    }

    @Autowired
    private OrderBusiness orderBusiness;

    @Override
    public Detail ReceiveDetails(Detail detail) throws FoundException, NotFoundException, BusinessException {
        checkTemperature(detail);

        // lanza un BusinessException si hay datos invalidos
        orderBusiness.saveLastDetails(detail);

        // guarda el detalle en la base de datos de acuerdo a la frecuencia de guardado
        long currentTime = System.currentTimeMillis();
        if (checkFrequency(currentTime)) {
            detail.setTimeStamp(new Date(currentTime));
            return add(detail);
        } else {
            // todo aca quizas poner una exception para decir que el detail no se guardo en la db
            return null;
        }
    }

    /////////////////////////////////////////////////////////////////
    ////////////////////////// UTILIDADES  //////////////////////////
    /////////////////////////////////////////////////////////////////

    private static final double MIN_TEMPERATURE = 10.0;
    private static final double MAX_TEMPERATURE = 35.0;

    private void checkTemperature(Detail detail) {
        double temperature = detail.getTemperature();
        if (temperature < MIN_TEMPERATURE || temperature > MAX_TEMPERATURE) {
            System.out.println("Warning: Temperature out of range! " + temperature + "°C");
        }
    }

    private long lastSaveTime = 0;
    private static final long SAVE_INTERVAL_MS = 5000; // Frecuencia de guardado (5 segundos)

    private boolean checkFrequency(long currentTime) {
        if (currentTime - lastSaveTime >= SAVE_INTERVAL_MS) {
            lastSaveTime = currentTime;
            return true;
        }
        return false;
    }

}
