package ar.edu.iw3.model.serializers;

import ar.edu.iw3.auth.model.Role;
import ar.edu.iw3.auth.model.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Set;

public class UserSlimV1JsonSerializer extends StdSerializer<User> {

    public UserSlimV1JsonSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject(); // Inicia el objeto JSON
        jsonGenerator.writeNumberField("id", user.getId()); // Serializa el campo id
        jsonGenerator.writeStringField("email", user.getEmail()); // Serializa el campo email
        jsonGenerator.writeStringField("username", user.getUsername()); // Serializa el campo username
        jsonGenerator.writeBooleanField("enabled", user.isEnabled()); // Serializa el campo enabled

        // Array de roles
        Set<Role> roles = user.getRoles();
        jsonGenerator.writeArrayFieldStart("roles");
        for (Role role : roles) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("id", String.valueOf(role.getId()));
            jsonGenerator.writeStringField("name", role.getName());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject(); // Finaliza el objeto JSON
    }
}
