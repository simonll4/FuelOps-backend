package ar.edu.iw3.model.serializers;

import ar.edu.iw3.auth.model.Role;
import ar.edu.iw3.auth.model.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

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

        // Transforma el Set<Rol> a un String separado por comas
        Set<Role> roles = user.getRoles();
        String roleString = roles.stream()
                .map(Role::getName) // Asume que Rol tiene un método getName()
                .collect(Collectors.joining(","));
        jsonGenerator.writeStringField("roles", roleString); // Incluye los roles como un String

        jsonGenerator.writeEndObject(); // Finaliza el objeto JSON
    }
}
