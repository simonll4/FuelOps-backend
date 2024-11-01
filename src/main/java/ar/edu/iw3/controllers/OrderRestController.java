package ar.edu.iw3.controllers;

import ar.edu.iw3.Constants;
import ar.edu.iw3.auth.User;
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
            description = "Tipo de respuesta. Puede ser 'application/json' (por defecto) o 'application/pdf'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conciliación obtenida con éxito en el formato especificado.",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(type = "object",
                                    example = """
                                            {
                                              "initialWeighing": "float",
                                              "finalWeighing": "float",
                                              "accumulatedMass": "float",
                                              "netWeight": "float",
                                              "differenceWeight": "float",
                                              "averageTemperature": "float",
                                              "averageDensity": "float",
                                              "averageFlowRate": "float",
                                              "product": "string"
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
    @SneakyThrows
    @GetMapping("/conciliation")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')  or hasRole('ROLE_CLI1') or hasRole('ROLE_CLI2') or hasRole('ROLE_CLI3')")
    public ResponseEntity<?> registerFinalWeighing(@RequestParam("idOrder") Long idOrder, // todo cambiar a @PathVariable
                                                   @RequestHeader(value = HttpHeaders.ACCEPT,
                                                           defaultValue = MediaType.APPLICATION_JSON_VALUE)
                                                   String acceptHeader) {
        // Respuesta en JSON
        Map<String, Object> conciliationData = orderBusiness.getConciliationJson(idOrder);
        if (acceptHeader.equals(MediaType.APPLICATION_JSON_VALUE)) {
            return new ResponseEntity<>(conciliationData, HttpStatus.OK);
        }
        // Respuesta en PDF
        byte[] pdfContent = orderBusiness.generateConciliationPdf(idOrder);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"fuel-conciliation.pdf\"");
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }


    //todo armar doc
    @SneakyThrows
    @PostMapping("/acknowledge-alarm")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> acknowledgeAlarm(@RequestBody Alarm alarm) {
        User user = getUserLogged();
        Order order = orderBusiness.acknowledgeAlarm(alarm, user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location", Constants.URL_ORDERS + "/orders/acknowledge-alarm/" + order.getId());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }

    // todo armar doc
    @SneakyThrows
    @PostMapping("/issue-alarm")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> confirmIssueAlarm(@RequestBody Alarm alarm) {
        User user = getUserLogged();
        Order order = orderBusiness.confirmIssueAlarm(alarm, user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location", Constants.URL_ORDERS + "/orders/issue-alarm/" + order.getId());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }

}