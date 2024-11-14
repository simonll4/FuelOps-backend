package ar.edu.iw3.integration.cli3.model.serializers;

import ar.edu.iw3.model.Order;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class OrderCli3SlimV1JsonSerializer extends StdSerializer<Order> {

    public OrderCli3SlimV1JsonSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    @Override
    public void serialize(Order order, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", order.getId());
        jsonGenerator.writeNumberField("preset", order.getPreset());
        jsonGenerator.writeEndObject();
    }
}
