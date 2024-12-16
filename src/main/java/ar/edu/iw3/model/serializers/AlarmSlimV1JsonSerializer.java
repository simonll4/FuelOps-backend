package ar.edu.iw3.model.serializers;

import ar.edu.iw3.model.Alarm;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class AlarmSlimV1JsonSerializer extends StdSerializer<Alarm> {
    public AlarmSlimV1JsonSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    // serealizer: id, estado alarma, timestamp, usuario, temperatura
    @Override
    public void serialize(Alarm alarm, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        // Campo ID
        jsonGenerator.writeNumberField("id", alarm.getId());

        // order id
       // jsonGenerator.writeNumberField("orderId", alarm.getOrder().getId());

        // Campo estado
        jsonGenerator.writeStringField("status", alarm.getStatus().toString());

        // Campo timestamp
        jsonGenerator.writeStringField("timeStamp", alarm.getTimeStamp().toString());

        // Campo usuario
        String user = alarm.getUser() == null ? null : alarm.getUser().getUsername();
        jsonGenerator.writeStringField("user", user);

        // Campo observaciones
        String observations = alarm.getObservation() == null ? null : alarm.getObservation();
        jsonGenerator.writeStringField("observations", observations);

        // Campo temperatura
        jsonGenerator.writeNumberField("temperature", alarm.getTemperature());

        jsonGenerator.writeEndObject();
    }
}
