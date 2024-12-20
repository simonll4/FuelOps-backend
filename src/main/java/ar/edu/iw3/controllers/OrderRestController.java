package ar.edu.iw3.controllers;

import ar.edu.iw3.Constants;
import ar.edu.iw3.auth.model.User;
import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.interfaces.IAlarmBusiness;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.model.serializers.OrderSlimV1JsonSerializer;
import ar.edu.iw3.util.FieldValidator;
import ar.edu.iw3.util.JsonUtils;
import ar.edu.iw3.util.PaginationInfo;
import ar.edu.iw3.util.StandartResponse;
import com.fasterxml.jackson.core.JsonGenerator;
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
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(description = "API Interna para Gestionar Ordenes", name = "Order")
@RestController
@RequestMapping(Constants.URL_ORDERS)
public class OrderRestController extends BaseRestController {

    @Autowired
    private IOrderBusiness orderBusiness;

    @Autowired
    private IAlarmBusiness alarmBusiness;


    /* ENPOINT PARA OBTENER UNA LISTA DE ORDENES (PAGINABLE) */
    @Operation(
            operationId = "get-all-orders",
            summary = "Obtener todas las ordenes de carga",
            description = "Devuelve todas las ordenes de carga en formato JSON.")
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer"), required = true, description = "Número de página.")
    @Parameter(in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "integer"), required = true, description = "Cantidad de elementos por página.")
    @Parameter(in = ParameterIn.QUERY, name = "filter", schema = @Schema(type = "string"), description = "Filtrado por estado")
    @Parameter(in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), description = "Campo y dirección de ordenamiento (ejemplo: 'externalReceptionDate,desc').")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ordenes obtenidas con éxito.",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(type = "object",
                                    example = """
                                            {
                                              "content": [
                                                {
                                                  "id": 0,
                                                  "truck": {
                                                    "licensePlate": "String"
                                                  },
                                                  "customer": {
                                                    "businessName": "String"
                                                  },
                                                  "receptionDate": "String",
                                                  "estimatedDate": "String",
                                                  "status": "String"
                                                }
                                              ],
                                              "pagination": {
                                                "pageable": {
                                                  "pageNumber": 0,
                                                  "pageSize": 0,
                                                  "sort": {
                                                    "sorted": false,
                                                    "unsorted": true,
                                                    "empty": true
                                                  }
                                                },
                                                "totalPages": 0,
                                                "totalElements": 0,
                                                "number": 0,
                                                "size": 0,
                                                "numberOfElements": 0
                                              }
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
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @SneakyThrows
    public ResponseEntity<?> getAll(@RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                    @RequestParam(value = "filter", required = false) String filter,
                                    @RequestParam(value = "sort", defaultValue = "externalReceptionDate,desc") String sort) {

        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            String sortField = sortParams[0].trim();
            String sortDirection = (sortParams.length > 1 ? sortParams[1].trim().toLowerCase() : "desc"); // Dirección predeterminada
            Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

            // Validar el campo de ordenación
            if (FieldValidator.isValidField(Order.class, sortField)) {
                throw new IllegalArgumentException("El campo de ordenación '" + sortField + "' no es válido para la entidad Order");
            }
            pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        } else {
            pageable = PageRequest.of(page, size);
        }

        final List<String> statusFilters = (filter != null && !filter.isEmpty())
                ? List.of(filter.split(","))
                : null;

        Page<Order> orders = orderBusiness.list(pageable, statusFilters);

        StdSerializer<Order> orderSerializer = new OrderSlimV1JsonSerializer(Order.class, false);
        ObjectMapper mapper = JsonUtils.getObjectMapper(Order.class, orderSerializer, null);

        List<Object> serializedOrders = orders.getContent().stream()
                .map(order -> {
                    try {
                        return mapper.valueToTree(order);
                    } catch (Exception e) {
                        throw new RuntimeException("Error al serializar el objeto Order", e);
                    }
                }).toList();

        PaginationInfo paginationInfo = new PaginationInfo(
                orders.getPageable(),
                orders.getTotalPages(),
                orders.getTotalElements(),
                orders.getNumber(),
                orders.getSize(),
                orders.getNumberOfElements()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("orders", serializedOrders);
        response.put("pagination", paginationInfo);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /* ENPOINT PARA OBTENER EL DETALLE DE UNA ORDEN POR SU ID */
    @Operation(
            operationId = "get-order-by-id",
            summary = "Obtener una orden de carga por su ID",
            description = "Devuelve una orden de carga en formato JSON.")
    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "Long"), required = true, description = "Identificador de la orden.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden obtenida con éxito.",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(type = "object",
                                    example = """
                                            {
                                              "id": 0,
                                              "truck": {
                                                "licensePlate": "String"
                                              },
                                              "customer": {
                                                "businessName": "String"
                                              },
                                              "preset": 0,
                                              "receptionDate": "String",
                                              "estimatedDate": "String",
                                              "initialWeighingDate": "String",
                                              "finalWeighingDate": "String"
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
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @SneakyThrows
    public ResponseEntity<?> getOrderById(@PathVariable("id") Long id) {
        Order order = orderBusiness.load(id);
        OrderSlimV1JsonSerializer orderSerializer = new OrderSlimV1JsonSerializer(Order.class, false);
        ObjectMapper mapper = JsonUtils.getObjectMapper(Order.class, orderSerializer, null);
        try {
            // Crear un generador de JSON
            StringWriter writer = new StringWriter();
            JsonGenerator jsonGenerator = mapper.getFactory().createGenerator(writer);

            // Llamar al method de serialización para el detalle
            orderSerializer.serializeOrderDetail(order, jsonGenerator);

            // Cerrar el generador y obtener el resultado
            jsonGenerator.close();
            String serializedOrder = writer.toString();

            return new ResponseEntity<>(serializedOrder, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException("Error al serializar el objeto Order", e);
        }
    }

    /* ENPOINT PARA OBTENER LA CONCILIACION DE UNA ORDEN (PDF/JSON) */
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


    @SneakyThrows
    @PostMapping("/set-alarm-status")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    public ResponseEntity<?> setAlarmStatus(@RequestBody Alarm alarm, @RequestParam Alarm.Status newStatus) {
        User user = getUserLogged();
        Order order = alarmBusiness.setAlarmStatus(alarm, user, newStatus);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location", Constants.URL_ORDERS + "/orders/set-alarm-status/" + order.getId());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }

//    /* ENPOINT PARA RECONOCER UNA ALARMA DURANTE LA CARGA*/
//    @Operation(
//            operationId = "acknowledge-alarm",
//            summary = "Reconoce una alarma",
//            description = "Acepta una alarma y guarda la informacion del usuario responsable.")
//    @io.swagger.v3.oas.annotations.parameters.RequestBody(
//            required = true,
//            description = "Objeto JSON que representa los datos de la alarma",
//            content = @Content(
//                    mediaType = MediaType.APPLICATION_JSON_VALUE,
//                    schema = @Schema(implementation = Alarm.class)
//            )
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Alarma aceptada exitosamente.", headers = {
//                    @io.swagger.v3.oas.annotations.headers.Header(name = "Location", description = "Ubicacion de la orden", schema = @Schema(type = "string"))}),
//            @ApiResponse(responseCode = "401", description = "Autenticación requerida.", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso.", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "404", description = "Recurso no encontrado.", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "409", description = "La alarma ya fue aceptada.", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "500", description = "Error interno.", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))})
//    })
//    @SneakyThrows
//    @PostMapping("/acknowledge-alarm")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
//    public ResponseEntity<?> acknowledgeAlarm(@RequestBody Alarm alarm) {
//        User user = getUserLogged();
//        Order order = orderBusiness.acknowledgeAlarm(alarm, user);
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set("Location", Constants.URL_ORDERS + "/orders/acknowledge-alarm/" + order.getId());
//        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
//    }


//    /* ENDPOINT PARA AVISAR PROBLEMA EN ALARMA DURANTE LA CARGA*/
//    @Operation(
//            operationId = "issue-alarm",
//            summary = "Marca como problematica a una alarma",
//            description = "Emite una alarma y guarda la informacion del usuario responsable.")
//    @io.swagger.v3.oas.annotations.parameters.RequestBody(
//            required = true,
//            description = "Objeto JSON que representa los datos de la alarma",
//            content = @Content(
//                    mediaType = MediaType.APPLICATION_JSON_VALUE,
//                    schema = @Schema(implementation = Alarm.class)
//            )
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Alarma establecida con problemas.", headers = {
//                    @io.swagger.v3.oas.annotations.headers.Header(name = "Location", description = "Ubicacion de la orden", schema = @Schema(type = "string"))}),
//            @ApiResponse(responseCode = "401", description = "Autenticación requerida.", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso.", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "404", description = "Recurso no encontrado.", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "409", description = "La alarma ya fue emitida.", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "500", description = "Error interno.", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))})
//    })
//    @SneakyThrows
//    @PostMapping("/issue-alarm")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
//    public ResponseEntity<?> confirmIssueAlarm(@RequestBody Alarm alarm) {
//        User user = getUserLogged();
//        Order order = orderBusiness.confirmIssueAlarm(alarm, user);
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set("Location", Constants.URL_ORDERS + "/orders/issue-alarm/" + order.getId());
//        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
//    }
}