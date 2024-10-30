package ar.edu.iw3.integration.cli3.controllers;

import ar.edu.iw3.controllers.Constants;
import ar.edu.iw3.integration.cli3.OrderCli3SlimV1JsonSerializer;
import ar.edu.iw3.integration.cli3.model.business.interfaces.IOrderCli3Business;
import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.util.JsonUtils;
import ar.edu.iw3.util.StandartResponse;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.URL_INTEGRATION_CLI3 + "/orders")
@Tag(description = "API para Gestionar Ordenes desde Sistema Externo de Carga de Combustible", name = "Cli3/Order")
public class OrderCli3RestController {

    @Autowired
    private IOrderCli3Business orderCli3Business;

    @Operation(
            operationId = "validate-active-password",
            summary = "Recibe la contraseña de activación de una orden",
            description = "Valida la contraseña de activacion para obtener el ID de la orden y preset de carga.")
    @Parameter(in = ParameterIn.HEADER, name = "Password", schema = @Schema(type = "Integer"), required = true, description = "Password de activacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden encontrada.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(type = "object",
                            example = """
                                    {
                                      "id": "int",
                                      "password": "int",
                                      "preset": "float"
                                    }
                                    """))
            }),
            @ApiResponse(responseCode = "403", description = "No posee autorización para consumir este servicio", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "No se encuentra la orden para el identificador informado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "409", description = "La orden no posse un estado valido.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @PostMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validatePassword(@RequestHeader("Password") Integer password) {
        Order order = orderCli3Business.validatePassword(password);
        StdSerializer<Order> ser = new OrderCli3SlimV1JsonSerializer(Order.class, false);
        String result = JsonUtils.getObjectMapper(Order.class, ser, null).writeValueAsString(order);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Operation(
            operationId = "close-order",
            summary = "Cerrar orden de carga.",
            description = "Cierra una orden identificada por su ID.")
    @Parameter(in = ParameterIn.HEADER, name = "OrderId", schema = @Schema(type = "Long"), required = true, description = "Identificador de orden.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden cerrada exitosamente.", headers = {
                    @Header(name = "Order-Id", description = "Numero de orden", schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "403", description = "No posee autorización para consumir este servicio", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "No se encuentra la orden para el identificador informado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "409", description = "La orden no posse un estado valido.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @PostMapping("/close")
    public ResponseEntity<?> closeOrder(@RequestHeader("OrderId") Long orderId) {
        Order order = orderCli3Business.closeOrder(orderId);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Order-Id", String.valueOf(order.getId()));
        return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
    }


    @Operation(
            operationId = "receive-load-data",
            summary = "Recibir datos de carga",
            description = "Recibe y almacena detalles de carga para una orden específica.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Objeto JSON que representa los detalles de carga",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Detail.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles de carga recibidos correctamente.", headers = {
                    @Header(name = "Order-Id", description = "Numero de orden", schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "403", description = "No posee autorización para consumir este servicio.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "409", description = "La orden no posse un estado valido.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @PostMapping("/detail")
    public ResponseEntity<?> receiveLoadData(@RequestBody Detail detail) {
        Order order = orderCli3Business.receiveDetails(detail);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Order-Id", String.valueOf(order.getId()));
        return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
    }

}