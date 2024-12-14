package ar.edu.iw3.model.serializers;

import ar.edu.iw3.model.Product;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class ProductSlimV1JsonSerializer extends StdSerializer<Product> {
    public ProductSlimV1JsonSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    @Override
    public void serialize(Product product, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject(); // Inicia el objeto JSON

        jsonGenerator.writeNumberField("id", product.getId()); // Serializa el campo id
        jsonGenerator.writeStringField("product", product.getProduct()); // Serializa el campo product (suponiendo que es un String)
        jsonGenerator.writeStringField("description", product.getDescription()); // Serializa el campo description
        jsonGenerator.writeNumberField("thresholdTemperature", product.getThresholdTemperature()); // Serializa el campo thresholdTemperature
        jsonGenerator.writeNumberField("density", product.getDensity()); // Serializa el campo density
        jsonGenerator.writeBooleanField("stock", product.isStock()); // Serializa el campo stock

        jsonGenerator.writeEndObject(); // Finaliza el objeto JSON
    }
}
