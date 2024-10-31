package ar.edu.iw3.websockets.wrappers;

import ar.edu.iw3.model.Detail;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AlarmWsWrapper {
    private long id;
    private String alertMessage;
    private Detail detail;
    private Date timestamp;
}