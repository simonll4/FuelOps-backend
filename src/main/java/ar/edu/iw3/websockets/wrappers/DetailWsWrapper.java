package ar.edu.iw3.websockets.wrappers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
public class DetailWsWrapper {

    private long id;
    private Date timeStamp;
    private float accumulatedMass;
    private float density;
    private float temperature;
    private float flowRate;

}