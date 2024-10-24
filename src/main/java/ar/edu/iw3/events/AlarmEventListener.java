package ar.edu.iw3.events;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.implementations.AlarmBusiness;
import ar.edu.iw3.util.EmailBusiness;
import ar.edu.iw3.websockets.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private SimpMessagingTemplate wSock;

    @Value("${mail.temperature.exceeded.send.to}")
    private String to;

    private void handleTemperatureExceeded(Detail detail) {

        Date now = new Date(System.currentTimeMillis());

        // Create notification object
        Notification notification = new Notification();
        notification.setAlertMessage("Temperature exceeded for order " + detail.getOrder().getId());
        notification.setDetail(detail);
        notification.setTimestamp(now);
        try {
            wSock.convertAndSend("/topic/alarms", notification);
        } catch (Exception e) {
            log.error("Failed to send alert notification", e);
        }

        // Guardado de alerta en db
        Alarm alarm = new Alarm();
        alarm.setOrder(detail.getOrder());
        alarm.setTimeStamp(now);
        alarm.setTemperature(detail.getTemperature());
        alarm.setStatus(Alarm.Status.PENDING_REVIEW);

        try {
            alarmBusiness.add(alarm);
        } catch (BusinessException | FoundException e) {
            log.error(e.getMessage(), e);
        }

        // Armado de mail de alerta
        String subject = "Temperatura Excedida Orden Nro " + detail.getOrder().getId();
        String mensaje = String.format(
                """
                        ALERTA: Temperatura Excedida en la Orden Nro %s

                        Detalles de la Alerta:
                        ---------------------------------
                        Orden ID: %s
                        Fecha/Hora del Evento: %s
                        Temperatura Registrada: %.2f °C
                        Masa Acumulada: %.2f kg
                        Densidad: %.2f kg/m³
                        Caudal: %.2f Kg/h
                        ---------------------------------

                        Descripción: La temperatura del combustible ha superado el umbral establecido. \
                        Por favor, revise esta alerta lo antes posible para evitar inconvenientes.

                        Atentamente,
                        Sistema de Monitoreo de Carga de Combustible""",
                detail.getOrder().getId(),
                detail.getOrder().getId(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now),
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
