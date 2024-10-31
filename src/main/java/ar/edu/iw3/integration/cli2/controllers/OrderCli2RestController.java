package ar.edu.iw3.integration.cli2.controllers;

import ar.edu.iw3.Constants;
import ar.edu.iw3.integration.cli2.model.business.implementaions.OrderCli2Business;
import ar.edu.iw3.model.Order;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(description = "API para Gestionar Ordenes desde Sistema Externo Balanza", name = "Cli2/Order")
@RestController
@RequestMapping(Constants.URL_INTEGRATION_CLI2 + "/orders")
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLI2')")
public class OrderCli2RestController {

    @Autowired
    OrderCli2Business orderCli2Business;

    @Operation(
            operationId = "initial-weighing",
            summary = "Registrar pesaje inicial",
            description = "Registra el pesaje inicial de una orden mediante la patente del camion.")
    @Parameter(in = ParameterIn.HEADER, name = "License-Plate", schema = @Schema(type = "String"), required = true, description = "Patente de camion.")
    @Parameter(in = ParameterIn.HEADER, name = "Initial-Weight", schema = @Schema(type = "Float"), required = true, description = "Peso inicial.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pesaje registrado exitosamente.", headers = {
                    @Header(name = "Order-Id", description = "Numero de orden", schema = @Schema(type = "string"))}),
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
    @PostMapping(value = "/initial-weighing", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> registerInitialWeighing(
            @RequestHeader("License-Plate") String licensePlate,
            @RequestHeader("Initial-Weight") float initialWeight) {
        Order order = orderCli2Business.registerInitialWeighing(licensePlate, initialWeight);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Order-Id", String.valueOf(order.getId()));
        return new ResponseEntity<>(order.getActivatePassword().toString(), responseHeaders, HttpStatus.OK);
    }


    @Operation(
            operationId = "final-weighing",
            summary = "Registrar pesaje final",
            description = "Registra el pesaje final de una orden mediante la patente del camion.")
    @Parameter(in = ParameterIn.HEADER, name = "License-Plate", schema = @Schema(type = "String"), required = true, description = "Patente de camion.")
    @Parameter(in = ParameterIn.HEADER, name = "Final-Weight", schema = @Schema(type = "Float"), required = true, description = "Peso inicial.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pesaje registrado exitosamente.", headers = {
                    @Header(name = "Order-Id", description = "Numero de orden", schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "403", description = "No posee autorización para consumir este servicio", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "No se encuentra la orden para el camion informado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @PostMapping("/final-weighing")
    public ResponseEntity<byte[]> registerFinalWeighing(
            @RequestHeader("License-Plate") String licensePlate,
            @RequestHeader("Final-Weight") float initialWeight) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"conciliation-report.pdf\"");
        byte[] pdfContent = orderCli2Business.registerFinalWeighing(licensePlate, initialWeight);
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

}