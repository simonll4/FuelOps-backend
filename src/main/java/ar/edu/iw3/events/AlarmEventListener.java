package ar.edu.iw3.events;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.implementations.AlarmBusiness;
import ar.edu.iw3.util.EmailBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class AlarmEventListener implements ApplicationListener<AlarmEvent> {

    @Override
    public void onApplicationEvent(AlarmEvent event) {
        if (event.getTypeEvent().equals(AlarmEvent.TypeEvent.TEMPERATURE_EXCEEDED) && event.getSource() instanceof Detail) {
            handleTemperatureExceeded((Detail) event.getSource());
        }
    }

    @Autowired
    private EmailBusiness emailBusiness;

    @Autowired
    private AlarmBusiness alarmBusiness;

    @Value("${mail.temperature.exceeded.send.to}")
    private String to;

    private void handleTemperatureExceeded(Detail detail) {

        // guardado en db
        Alarm alarm = new Alarm();
        alarm.setOrder(detail.getOrder());
        alarm.setTimeStamp(new Date(System.currentTimeMillis()));
        alarm.setTemperature(detail.getTemperature());
        alarm.setStatus(Alarm.Status.PENDING_REVIEW);
        try {
            alarmBusiness.add(alarm);
        } catch (BusinessException | FoundException e) {
            log.error(e.getMessage(), e);
        }

        String subject = "Temperatura Excedida Orden Nro " + detail.getOrder().getId();
        String mensaje = String.format("El combustible abastecido en la orden %s, supero el umbral de temperatura al tener %.2f\u00B0C"
                , detail.getOrder().getId(), detail.getTemperature());

        log.info("Enviando mensaje '{}'", mensaje);
        try {
            emailBusiness.sendSimpleMessage(to, subject, mensaje);
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
        }
    }
}
