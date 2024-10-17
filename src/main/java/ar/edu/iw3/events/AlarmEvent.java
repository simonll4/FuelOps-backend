package ar.edu.iw3.events;

import org.springframework.context.ApplicationEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmEvent extends ApplicationEvent {

    public enum TypeEvent {
        TEMPERATURE_EXCEEDED
    }

    public AlarmEvent(Object source, TypeEvent typeEvent) {
        super(source);
        this.typeEvent = typeEvent;
    }

    private TypeEvent typeEvent;
    private Object extraData;
}
