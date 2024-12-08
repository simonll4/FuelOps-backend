package ar.edu.iw3.auth.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import ar.edu.iw3.auth.model.serializers.UserSlimV1JsonSerializer;
import ar.edu.iw3.auth.util.UserSlimV1Response;
import ar.edu.iw3.util.JsonUtils;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import ar.edu.iw3.auth.model.User;
import ar.edu.iw3.auth.custom.CustomAuthenticationManager;
import ar.edu.iw3.auth.filters.AuthConstants;
import ar.edu.iw3.controllers.BaseRestController;
import ar.edu.iw3.Constants;
import ar.edu.iw3.util.IStandartResponseBusiness;

@Tag(name = "1. Auth", description = "Autenticación de usuarios")
@RestController
public class AuthRestController extends BaseRestController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private IStandartResponseBusiness response;

    @Autowired
    private PasswordEncoder pEncoder;

    @PostMapping(value = Constants.URL_EXTERNAL_LOGIN, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> loginExternalOnlyToken(@RequestParam String username, @RequestParam String password) {
        Authentication auth = null;

        try {
            auth = authManager.authenticate(((CustomAuthenticationManager) authManager).authWrap(username, password));
        } catch (AuthenticationServiceException e0) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e0, e0.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(response.build(HttpStatus.UNAUTHORIZED, e, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        }

        // en principal tenemos la version simplificada de User
        User user = (User) auth.getPrincipal();
        String token = JWT.create().withSubject(user.getUsername())
                .withClaim("internalid", user.getId())
                .withClaim("roles", new ArrayList<String>(user.getAuthoritiesStr()))
                .withClaim("email", user.getEmail())
                .withClaim("version", "1.0.0")
                .withExpiresAt(new Date(System.currentTimeMillis() + AuthConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(AuthConstants.SECRET.getBytes()));
        return new ResponseEntity<String>(token, HttpStatus.OK);
    }


    @Operation(operationId = "login_internal", summary = "Login interno", description = "Login interno de la aplicacion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario logueado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
    })
    @SneakyThrows
    @PostMapping(value = Constants.URL_INTERNAL_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginInternal(@RequestBody User user) {
        Authentication auth = null;

        try {
            auth = authManager.authenticate(((CustomAuthenticationManager) authManager).authWrap(user.getUsername(), user.getPassword()));
        } catch (AuthenticationServiceException e0) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e0, e0.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(response.build(HttpStatus.UNAUTHORIZED, e, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        }

        // en principal tenemos la version simplificada de User
        User newUser = (User) auth.getPrincipal();
        String token = JWT.create().withSubject(newUser.getUsername())
                .withClaim("internalid", newUser.getId())
                .withClaim("roles", new ArrayList<String>(newUser.getAuthoritiesStr()))
                .withClaim("email", newUser.getEmail())
                .withClaim("version", "1.0.0")
                .withExpiresAt(new Date(System.currentTimeMillis() + AuthConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(AuthConstants.SECRET.getBytes()));

        // Crear la respuesta simplificada del usuario
        UserSlimV1Response userResponse = new UserSlimV1Response(
                newUser.getUsername(),
                newUser.getEmail(),
                newUser.getAuthoritiesStr()
        );

        // Devolver el token y el usuario simplificado en un Map
        Map<String, Object> response = Map.of(
                "token", token,
                "user", userResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(operationId = "validate_token", summary = "Validar token", description = "Valida el token del usuario logueado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario validado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
    })
    @SneakyThrows
    @GetMapping(value = Constants.URL_TOKEN_VALIDATE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateToken() {
        User user = getUserLogged();
        StdSerializer<User> ser = new UserSlimV1JsonSerializer(User.class, false);
        String result = JsonUtils.getObjectMapper(User.class, ser, null).writeValueAsString(user);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Hidden
    @GetMapping(value = "/demo/encodepass", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> encodepass(@RequestParam String password) {
        try {
            return new ResponseEntity<String>(pEncoder.encode(password), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}