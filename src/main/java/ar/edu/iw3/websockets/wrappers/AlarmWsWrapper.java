package ar.edu.iw3.websockets.wrappers;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class AlarmWsWrapper {
    private long id;
    private long orderId;
    private String alertMessage;
    private Date timestamp;
}
