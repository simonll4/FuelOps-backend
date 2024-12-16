package ar.edu.iw3.controllers;

import ar.edu.iw3.Constants;
import ar.edu.iw3.auth.model.User;
import ar.edu.iw3.model.business.interfaces.IUserBusiness;
import ar.edu.iw3.model.serializers.UserSlimV1JsonSerializer;
import ar.edu.iw3.util.JsonUtils;
import ar.edu.iw3.util.StandartResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "2. Usuarios", description = "Operaciones con usuarios")
@RestController
@RequestMapping(Constants.URL_USERS)
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserRestController extends BaseRestController {

    @Autowired
    private IUserBusiness userBusiness;

    @Autowired
    private PasswordEncoder pEncoder;


    @Operation(operationId = "list_users", summary = "Listar usuarios", description = "Lista todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
    })
    @SneakyThrows
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {

        List<User> users = userBusiness.list();

        StdSerializer<User> userSerializer = new UserSlimV1JsonSerializer(User.class, false);
        ObjectMapper mapper = JsonUtils.getObjectMapper(User.class, userSerializer, null);

        List<Object> serializedUsers = users.stream()
                .map(user -> {
                    try {
                        return mapper.valueToTree(user);
                    } catch (Exception e) {
                        throw new RuntimeException("Error al serializar el objeto User", e);
                    }
                }).toList();

        return new ResponseEntity<>(serializedUsers, HttpStatus.OK);
    }


    @Operation(operationId = "load_internal_user", summary = "Cargar usuario", description = "Carga un usuario por id")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "Id del usuario a cargar", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario cargado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadUser(@PathVariable Long id) {
        return new ResponseEntity<>(userBusiness.load(id), HttpStatus.OK);
    }


    @Operation(operationId = "load_user_by_username", summary = "Cargar usuario por username", description = "Carga un usuario por username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario cargado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @Parameter(in = ParameterIn.PATH, name = "user", description = "Nombre del usuario a cargar", required = true)
    @SneakyThrows
    @GetMapping(value = "/name/{user}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadUser(@PathVariable String user) {
        return new ResponseEntity<>(userBusiness.load(user), HttpStatus.OK);
    }


    @Operation(operationId = "add_user", summary = "Agregar usuario", description = "Agrega un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario agregado"),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Objeto JSON que representa los datos del usuario",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = User.class)
            )
    )
    @SneakyThrows
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> add(@Valid @RequestBody User user) {

        user.setPassword(pEncoder.encode(user.getPassword()));
        User response = userBusiness.add(user);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location", Constants.URL_USERS + "/" + response.getId());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }


    @Operation(operationId = "update_user", summary = "Actualizar usuario", description = "Actualiza un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Objeto JSON que representa los datos del usuario",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = User.class)
            )
    )
    @SneakyThrows
    @PutMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@Valid @RequestBody User user) {
        userBusiness.update(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(operationId = "delete_user", summary = "Eliminar usuario", description = "Elimina un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado"),
    })
    @Parameter(in = ParameterIn.PATH, name = "id", description = "Id del usuario a eliminar", required = true)
    @SneakyThrows
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable Long id) {
        userBusiness.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}