package ar.edu.iw3.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class DetailEvent extends ApplicationEvent{
    public enum TypeEvent {
        SAVE_DETAIL
    }

    public DetailEvent(Object source, TypeEvent typeEvent) {
        super(source);
        this.typeEvent = typeEvent;
    }

    private TypeEvent typeEvent;
    private Object extraData;
}