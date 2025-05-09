package ar.edu.iw3.model.serializers;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.Tanker;
import ar.edu.iw3.util.OrderUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class OrderSlimV1JsonSerializer extends StdSerializer<Order> {

    public OrderSlimV1JsonSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    // serealizer: id,patenete camion, nombre cliente, fecha recepcion, fecha estimada, estado, estado de alarmas
    @Override
    public void serialize(Order order, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        // Campo ID
        jsonGenerator.writeNumberField("id", order.getId());

        // Objeto "truck"
        jsonGenerator.writeObjectFieldStart("truck");
        jsonGenerator.writeStringField("licensePlate", order.getTruck().getLicensePlate());
        jsonGenerator.writeEndObject();

        // Objeto "customer"
        jsonGenerator.writeObjectFieldStart("customer");
        jsonGenerator.writeStringField("businessName", order.getCustomer().getBusinessName());
        jsonGenerator.writeEndObject();

        // Estado de alarmas
        String alarmStatus = OrderUtils.getAlarmStatus(order);
        jsonGenerator.writeObjectFieldStart("alarmStatus");
        jsonGenerator.writeStringField("state", alarmStatus);
        jsonGenerator.writeEndObject();

        // Campo de fechas y estado
        jsonGenerator.writeStringField("receptionDate", order.getExternalReceptionDate().toString());
        jsonGenerator.writeStringField("estimatedDate", order.getEstimatedDate().toString());
        jsonGenerator.writeStringField("status", order.getStatus().toString());

        jsonGenerator.writeEndObject();
    }

    // serealizer: id,patente camion,preset, nombre cliente, fecha recepcion, fecha estimada, fecha pesaje inicial y final,
    // fecha inicio y fin de carga


    public void serializeOrderDetail(Order order, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartObject();

        // Campo ID
        jsonGenerator.writeNumberField("id", order.getId());

        // Status
        jsonGenerator.writeStringField("status", order.getStatus().toString());

        // Objeto "truck"
        jsonGenerator.writeObjectFieldStart("truck");
        jsonGenerator.writeStringField("licensePlate", order.getTruck().getLicensePlate());
        // Lista de tankers
        jsonGenerator.writeArrayFieldStart("tankers");
        for (Tanker tanker : order.getTruck().getTankers()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("capacityLiters", tanker.getCapacity_liters());
            jsonGenerator.writeStringField("licensePlate", tanker.getLicense());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();

        // Objeto "customer"
        jsonGenerator.writeObjectFieldStart("customer");
        jsonGenerator.writeStringField("businessName", order.getCustomer().getBusinessName());
        jsonGenerator.writeEndObject();

        // Obejto "driver"
        jsonGenerator.writeObjectFieldStart("driver");
        jsonGenerator.writeStringField("name", order.getDriver().getName());
        jsonGenerator.writeStringField("lastName", order.getDriver().getLastName());
        jsonGenerator.writeStringField("dni", order.getDriver().getDocument());
        jsonGenerator.writeEndObject();

        // Objeto "product"
        jsonGenerator.writeObjectFieldStart("product");
        jsonGenerator.writeStringField("product", order.getProduct().getProduct());
        jsonGenerator.writeStringField("thresholdTemperature", String.valueOf(order.getProduct().getThresholdTemperature()));
        jsonGenerator.writeStringField("density", String.valueOf(order.getProduct().getDensity()));
        jsonGenerator.writeEndObject();

        // Preset
        jsonGenerator.writeNumberField("preset", order.getPreset());

        // Campo de fechas
        jsonGenerator.writeStringField("receptionDate", order.getExternalReceptionDate().toString());
        jsonGenerator.writeStringField("estimatedDate", order.getEstimatedDate().toString());

        // Validaciones para fechas opcionales
        jsonGenerator.writeFieldName("initialWeighingDate");
        if (order.getInitialWeighingDate() != null) {
            jsonGenerator.writeString(order.getInitialWeighingDate().toString());
        } else {
            jsonGenerator.writeNull();
        }

        jsonGenerator.writeFieldName("fuelingStartDate");
        if (order.getFuelingStartDate() != null) {
            jsonGenerator.writeString(order.getFuelingStartDate().toString());
        } else {
            jsonGenerator.writeNull();
        }

        jsonGenerator.writeFieldName("fuelingEndDate");
        if (order.getFuelingEndDate() != null) {
            jsonGenerator.writeString(order.getFuelingEndDate().toString());
        } else {
            jsonGenerator.writeNull();
        }

        jsonGenerator.writeFieldName("finalWeighingDate");
        if (order.getFinalWeighingDate() != null) {
            jsonGenerator.writeString(order.getFinalWeighingDate().toString());
        } else {
            jsonGenerator.writeNull();
        }

        jsonGenerator.writeStringField("lastAccumulatedMass", String.valueOf(order.getLastAccumulatedMass()));

        jsonGenerator.writeEndObject();
    }
}