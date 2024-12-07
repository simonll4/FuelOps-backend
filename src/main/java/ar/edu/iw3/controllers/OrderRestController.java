package ar.edu.iw3.controllers;

import ar.edu.iw3.Constants;
import ar.edu.iw3.auth.model.User;
import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.util.StandartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

import java.util.Map;

@Tag(description = "API Interna para Gestionar Ordenes", name = "Order")
@RestController
@RequestMapping(Constants.URL_ORDERS)
public class OrderRestController extends BaseRestController {

    @Autowired
    private IOrderBusiness orderBusiness;

    // todo get all orders con paginacion, adaptar el metodo list de IOrderBusiness
    //  fechas mas recientes primero y dar la posbilidad de filtrar por estado
    // serealizer: id,patenete camion, nombre cliente, fecha recepcion, fecha estimada, estado
    // objeto estado alarma 3 boleanos que dicen si hay alguna alarma con ese estado para la orden
    // devolver informacion de paginacion

    // todo get order by id
    // serealizer: id,patente camion,preset, nombre cliente, fecha recepcion, fecha estimada, fecha pesaje inicial y final,
    // fecha inicio y fin de carga

    @Operation(
            operationId = "get-conciliation",
            summary = "Obtener conciliacion de orden de carga finalizada",
            description = "Devuelve la oreden de carga en formato JSON(Default) o PDF.")
    @Parameter(in = ParameterIn.QUERY,
            name = "idOrder", schema = @Schema(type = "Long"),
            required = true, description = "Identificador de la orden.")
    @Parameter(
            in = ParameterIn.HEADER,
            name = HttpHeaders.ACCEPT,
            schema = @Schema(type = "string", allowableValues = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PDF_VALUE}),
            description = "Tipo de respuesta. Puede ser 'application/pdf' (por defecto) o 'application/json'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conciliación obtenida con éxito en el formato especificado.",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(type = "object",
                                    example = """
                                            {
                                              "initialWeighing": 0.0,
                                              "finalWeighing": 0.0,
                                              "accumulatedMass": 0.0,
                                              "netWeight": 0.0,
                                              "differenceWeight": 0.0,
                                              "averageTemperature": 0.0,
                                              "averageDensity": 0.0,
                                              "averageFlowRate": 0.0,
                                              "product": "String"
                                            }
                                            """)),
                            @Content(mediaType = "application/pdf")
                    }
            ),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))})
    })
    @GetMapping("/conciliation/{idOrder}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')  or hasRole('ROLE_CLI1') or hasRole('ROLE_CLI2') or hasRole('ROLE_CLI3')")
    @SneakyThrows
    public ResponseEntity<?> getConciliationReport(@PathVariable("idOrder") Long idOrder,
                                                   @RequestHeader(value = HttpHeaders.ACCEPT,
                                                           defaultValue = MediaType.APPLICATION_PDF_VALUE)
                                                   String acceptHeader) {
        // Respuesta en JSON
        if (acceptHeader.equals(MediaType.APPLICATION_JSON_VALUE)) {
            Map<String, Object> conciliationData = orderBusiness.getConciliationJson(idOrder);
            return new ResponseEntity<>(conciliationData, HttpStatus.OK);
        }
        // Respuesta en PDF
        byte[] pdfContent = orderBusiness.getConciliationPdf(idOrder);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"fuel-conciliation.pdf\"");
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }


    @Operation(
            operationId = "acknowledge-alarm",
            summary = "Reconoce una alarma",
            description = "Acepta una alarma y guarda la informacion del usuario responsable.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Objeto JSON que representa los datos de la alarma",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Alarm.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Alarma aceptada exitosamente.", headers = {
                    @io.swagger.v3.oas.annotations.headers.Header(name = "Location", description = "Ubicacion de la orden", schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "409", description = "La alarma ya fue aceptada.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))})
    })
    @SneakyThrows
    @PostMapping("/acknowledge-alarm")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    public ResponseEntity<?> acknowledgeAlarm(@RequestBody Alarm alarm) {
        User user = getUserLogged();
        Order order = orderBusiness.acknowledgeAlarm(alarm, user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location", Constants.URL_ORDERS + "/orders/acknowledge-alarm/" + order.getId());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }

    @Operation(
            operationId = "issue-alarm",
            summary = "Marca como problematica a una alarma",
            description = "Emite una alarma y guarda la informacion del usuario responsable.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Objeto JSON que representa los datos de la alarma",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Alarm.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Alarma establecida con problemas.", headers = {
                    @io.swagger.v3.oas.annotations.headers.Header(name = "Location", description = "Ubicacion de la orden", schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "409", description = "La alarma ya fue emitida.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))})
    })
    @SneakyThrows
    @PostMapping("/issue-alarm")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    public ResponseEntity<?> confirmIssueAlarm(@RequestBody Alarm alarm) {
        User user = getUserLogged();
        Order order = orderBusiness.confirmIssueAlarm(alarm, user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location", Constants.URL_ORDERS + "/orders/issue-alarm/" + order.getId());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }
}