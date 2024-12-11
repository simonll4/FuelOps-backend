package ar.edu.iw3.model.serializers;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Detail;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class DetailSlimV1JsonSerializer extends StdSerializer<Detail> {
    public DetailSlimV1JsonSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    // serealizer: id, acummulated mass, density, flow, temperature, timestamp
    @Override
    public void serialize(Detail detail, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        // Campo ID
        jsonGenerator.writeNumberField("id", detail.getId());

        // Campo acummulated mass
        jsonGenerator.writeNumberField("accumulatedMass", detail.getAccumulatedMass());

        // Campo density
        jsonGenerator.writeNumberField("density", detail.getDensity());

        // Campo flow
        jsonGenerator.writeNumberField("flowRate", detail.getFlowRate());

        // Campo temperature
        jsonGenerator.writeNumberField("temperature", detail.getTemperature());

        // Campo timestamp
        jsonGenerator.writeStringField("timeStamp", detail.getTimeStamp().toString());

        jsonGenerator.writeEndObject();
    }
}
