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

import java.text.SimpleDateFormat;
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

        // guardado de alerta en db
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

        // Envio de mail de alerta
        String subject = "Temperatura Excedida Orden Nro " + detail.getOrder().getId();
        String mensaje = String.format(
                "ALERTA: Temperatura Excedida en la Orden Nro %s\n\n" +
                        "Detalles de la Alerta:\n" +
                        "---------------------------------\n" +
                        "Orden ID: %s\n" +
                        "Fecha/Hora del Evento: %s\n" +
                        "Temperatura Registrada: %.2f °C\n" +
                        "Masa Acumulada: %.2f kg\n" +
                        "Densidad: %.2f kg/m³\n" +
                        "Caudal: %.2f Kg/h\n" +
                        "---------------------------------\n\n" +
                        "Descripción: La temperatura del combustible ha superado el umbral establecido. " +
                        "Por favor, revise esta alerta lo antes posible para evitar inconvenientes.\n\n" +
                        "Atentamente,\n" +
                        "Sistema de Monitoreo de Carga de Combustible",
                detail.getOrder().getId(),
                detail.getOrder().getId(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())),
                detail.getTemperature(),
                detail.getAccumulatedMass(),
                detail.getDensity(),
                detail.getFlowRate()
        );

        log.info("Enviando mensaje '{}'", mensaje);
        try {
            emailBusiness.sendSimpleMessage(to, subject, mensaje);
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
        }

    }
}
