package ar.edu.iw3.schedules;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataGraphScheduler {

    @Autowired
    private SimpMessagingTemplate wSock;

    @Scheduled(fixedDelay = 5, initialDelay = 1, timeUnit = TimeUnit.SECONDS)
    public void genDataAndPublish() {
        try {
            String[] meses = "Enero,Febrero,Marzo,Abril,Mayo,Junio,Julio,Agosto,Sptiembre,Octubre,Noviembre,Diciembre"
                    .split(",");
            List<LabelValue> valores = Arrays.stream(meses).map(mes -> {
                return new LabelValue(mes, ((int) (Math.random() * 100)));
            }).collect(Collectors.toList());
            wSock.convertAndSend("/topic/graph/data", valores);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}

@Getter
@Setter
@AllArgsConstructor
class LabelValue {
    private String label;
    private double value;
}