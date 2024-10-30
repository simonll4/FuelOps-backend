package ar.edu.iw3.integration.cli1.controllers;

import ar.edu.iw3.Constants;
import ar.edu.iw3.util.StandartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.iw3.controllers.BaseRestController;
import ar.edu.iw3.integration.cli1.model.OrderCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.IOrderCli1Business;

@RestController
@RequestMapping(Constants.URL_INTEGRATION_CLI1 + "/orders")
@Tag(description = "API para Gestionar Ordenes desde Sistema Externo Administracion", name = "Cli1/Order")
public class OrderCli1RestController extends BaseRestController {

    @Autowired
    private IOrderCli1Business orderBusiness;

    @Operation(
            operationId = "add-external-order",
            summary = "Registra orden de carga",
            description = "Registra los datos de una orden de carga externa.")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pesaje registrado exitosamente.", headers = {
                    @Header(name = "Order-IdCli1", description = "Numero de orden", schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "403", description = "No posee autorización para consumir este servicio", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "No se encuentra la orden para el camion informado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @PostMapping(value = "/b2b")
    public ResponseEntity<?> addExternal(HttpEntity<String> httpEntity) {
        OrderCli1 response = orderBusiness.addExternal(httpEntity.getBody());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Order-IdCli1", response.getOrderNumberCli1());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }

}