package ar.edu.iw3.integration.cli3;

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
    public void serialize(Order order, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {

        gen.writeStartObject();
        gen.writeNumberField("id", order.getId());
        gen.writeNumberField("password", order.getActivatePassword());
        gen.writeNumberField("preset", order.getPreset());
        gen.writeEndObject();

    }
}
