package ar.edu.iw3.auth.model.serializers;

import ar.edu.iw3.auth.model.Role;
import ar.edu.iw3.auth.model.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class UserSlimV1JsonSerializer extends StdSerializer<User> {

    public UserSlimV1JsonSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("username", user.getUsername());
        jsonGenerator.writeStringField("email", user.getEmail());
        jsonGenerator.writeArrayFieldStart("roles");
        for (Role role : user.getRoles()) {
            jsonGenerator.writeString(role.getName());
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
