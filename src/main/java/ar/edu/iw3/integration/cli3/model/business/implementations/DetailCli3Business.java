package ar.edu.iw3.integration.cli3.model.business.implementations;

import ar.edu.iw3.integration.cli3.model.business.interfaces.IDetailCli3Business;
import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.*;
import ar.edu.iw3.model.business.interfaces.IDetailBusiness;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.model.persistence.DetailRepository;
import ar.edu.iw3.websockets.wrappers.DetailWsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DetailCli3Business implements IDetailCli3Business {

    @Autowired
    private IOrderBusiness orderBusiness;

    @Autowired
    private IDetailBusiness detailBusiness;

    @Autowired
    private DetailRepository detailDAO;

    @Autowired
    private SimpMessagingTemplate wSock;

    @Override
    public void add(Detail detail) throws FoundException, BusinessException, NotFoundException {
        long currentTime = System.currentTimeMillis();
        Order orderFound = orderBusiness.load(detail.getOrder().getId());
        Optional<List<Detail>> detailsOptional = detailDAO.findByOrderId(detail.getOrder().getId());

        DetailWsWrapper detailWsWrapper = new DetailWsWrapper();
        detailWsWrapper.setTimeStamp(new Date(currentTime));
        detailWsWrapper.setAccumulatedMass(detail.getAccumulatedMass());
        detailWsWrapper.setDensity(detail.getDensity());
        detailWsWrapper.setTemperature(detail.getTemperature());
        detailWsWrapper.setFlowRate(detail.getFlowRate());

        if ((detailsOptional.isPresent() && !detailsOptional.get().isEmpty())) {
            Date lastTimeStamp = orderFound.getFuelingEndDate();
            if (checkFrequency(currentTime, lastTimeStamp)) {
                detail.setTimeStamp(new Date(currentTime));
                Detail savedDetail = detailBusiness.add(detail);
                orderFound.setFuelingEndDate(new Date(currentTime));
                orderBusiness.update(orderFound);

                detailWsWrapper.setId(savedDetail.getId());
                // Envío de detalle de carga a clientes (WebSocket)
                wSock.convertAndSend("/topic/details/order/" + detail.getOrder().getId(), detailWsWrapper);
            }
        } else {
            detail.setTimeStamp(new Date(currentTime));
            detailBusiness.add(detail);
            orderFound.setFuelingStartDate(new Date(currentTime));
            orderFound.setFuelingEndDate(new Date(currentTime));
            orderBusiness.update(orderFound);


            // Envío de detalle de carga a clientes (WebSocket)
            wSock.convertAndSend("/topic/details/order/" + detail.getOrder().getId(), detailWsWrapper);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// UTILIDADES  ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Value("${loading.details.saving.frequency}")
    private long SAVE_INTERVAL_MS;

    private boolean checkFrequency(long currentTime, Date lastTimeStamp) {
        return currentTime - lastTimeStamp.getTime() >= SAVE_INTERVAL_MS;
    }
}
