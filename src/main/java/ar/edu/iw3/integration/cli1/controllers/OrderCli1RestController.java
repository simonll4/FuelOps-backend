package ar.edu.iw3.integration.cli1.controllers;

import ar.edu.iw3.Constants;
import ar.edu.iw3.model.Detail;
import ar.edu.iw3.util.StandartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ar.edu.iw3.controllers.BaseRestController;
import ar.edu.iw3.integration.cli1.model.OrderCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.IOrderCli1Business;

@Tag(description = "API para Gestionar Ordenes desde Sistema Externo Administracion", name = "Cli1/Order")
@RestController
@RequestMapping(Constants.URL_INTEGRATION_CLI1 + "/orders")
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLI1')")
public class OrderCli1RestController extends BaseRestController {

    @Autowired
    private IOrderCli1Business orderCli1Business;

    @Operation(
            operationId = "list-external-orders",
            summary = "Lista ordenes de carga",
            description = "Lista las ordenes de carga externas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ordenes de carga.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = OrderCli1.class))}),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        return new ResponseEntity<>(orderCli1Business.list(), HttpStatus.OK);
    }

    @Operation(
            operationId = "load-external-order",
            summary = "Carga orden de carga",
            description = "Carga los datos de una orden de carga externa.")
    @Parameter(in = ParameterIn.HEADER, name = "orderNumberCli1", schema = @Schema(type = "String"), required = true, description = "Número de orden de carga externa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden de carga cargada exitosamente.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = OrderCli1.class))}),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @GetMapping(value = "/{orderNumberCli1}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadByCode(@PathVariable(value = "orderNumberCli1") String orderNumberCli1) {
        return new ResponseEntity<>(orderCli1Business.load(orderNumberCli1), HttpStatus.OK);
    }

    @Operation(
            operationId = "add-external-order",
            summary = "Registra orden de carga",
            description = "Registra los datos de una orden de carga externa.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Objeto JSON que representa los datos de la orden de carga",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            example = """
            {
                "order_number": "String",
                "truck": {
                    "id": "String",
                    "license_plate": "String",
                    "description": "String",
                    "tanks": [
                        {
                            "id": "String",
                            "capacity": 0,
                            "license_plate": "String"
                        }
                    ]
                },
                "driver": {
                    "id": "String",
                    "name": "String",
                    "last_name": "String",
                    "document": "String"
                },
                "customer": {
                    "id": "String",
                    "business_name": "String",
                    "email": "String"
                },
                "product": {
                    "product": "String",
                    "description": "String"
                },
                "estimated_date": "String",
                "preset": 0
            }
            """
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Carga registrada exitosamente.", headers = {
                    @Header(name = "Location", description = "Ubicacion orden", schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "302", description = "Ya existe una orden con el codigo externo", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "302", description = "Ya existe una orden para el camion idExterno", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @PostMapping(value = "/b2b")
    public ResponseEntity<?> addExternal(HttpEntity<String> httpEntity) {
        OrderCli1 response = orderCli1Business.addExternal(httpEntity.getBody());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location", Constants.URL_INTEGRATION_CLI1 + "/orders/" + response.getOrderNumberCli1());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }

    @Operation(
            operationId = "cancel-external-order",
            summary = "Cancela orden de carga",
            description = "Cancela una orden de carga externa.")
    @Parameter(in = ParameterIn.HEADER, name = "order", schema = @Schema(type = "String"), required = true, description = "Número de orden de carga externa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden de carga cancelada exitosamente.", headers = {
                    @Header(name = "Location", description = "Ubicacion orden", schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "400", description = "Parametros insuficientes, entidades no enviadas", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @PostMapping(value = "/cancel")
    public ResponseEntity<?> cancelExternal(@RequestHeader("orderId") String orderNumberCli1) {
        OrderCli1 response = orderCli1Business.cancelExternal(orderNumberCli1);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", Constants.URL_INTEGRATION_CLI1 + "/orders/" + response.getOrderNumberCli1());
        return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
    }

}