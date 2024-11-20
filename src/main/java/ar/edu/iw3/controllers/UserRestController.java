package ar.edu.iw3.controllers;

import ar.edu.iw3.Constants;
import ar.edu.iw3.auth.model.User;
import ar.edu.iw3.model.business.interfaces.IUserBusiness;
import ar.edu.iw3.util.StandartResponse;
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
import org.springframework.web.bind.annotation.*;


@Tag(name = "2. Usuarios", description = "Operaciones con usuarios")
@RestController
@RequestMapping(Constants.URL_USERS)
public class UserRestController extends BaseRestController {

    @Autowired
    private IUserBusiness userBusiness;


    @Operation(operationId = "list_internal_users", summary = "Listar usuarios", description = "Lista todos los usuarios internos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios internos", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
    })
    @SneakyThrows
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        return new ResponseEntity<>(userBusiness.list(), HttpStatus.OK);
    }

    @Operation(operationId = "load_internal_user", summary = "Cargar usuario", description = "Carga un usuario interno por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario interno cargado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "Usuario interno no encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @Parameter(in = ParameterIn.PATH, name = "id", description = "Id del usuario a cargar", required = true)
    @SneakyThrows
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadUser(@PathVariable Long id) {
        return new ResponseEntity<>(userBusiness.load(id), HttpStatus.OK);
    }

    @Operation(operationId = "load_internal_user_by_username", summary = "Cargar usuario por username", description = "Carga un usuario interno por username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario interno cargado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "Usuario interno no encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @Parameter(in = ParameterIn.PATH, name = "user", description = "Nombre del usuario a cargar", required = true)
    @SneakyThrows
    @GetMapping(value = "/name/{user}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadUser(@PathVariable String user) {
        return new ResponseEntity<>(userBusiness.load(user), HttpStatus.OK);
    }

    @Operation(operationId = "add_internal_user", summary = "Agregar usuario", description = "Agrega un usuario interno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario interno agregado"),
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
        User response = userBusiness.add(user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location", Constants.URL_USERS + "/" + response.getId());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }

    @Operation(operationId = "update_internal_user", summary = "Actualizar usuario", description = "Actualiza un usuario interno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario interno actualizado"),
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

    @Operation(operationId = "delete_internal_user", summary = "Eliminar usuario", description = "Elimina un usuario interno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario interno eliminado"),
    })
    @Parameter(in = ParameterIn.PATH, name = "id", description = "Id del usuario a eliminar", required = true)
    @SneakyThrows
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable Long id) {
        userBusiness.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
