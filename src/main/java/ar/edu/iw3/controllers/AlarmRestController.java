package ar.edu.iw3.controllers;

import ar.edu.iw3.Constants;
import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.interfaces.IAlarmBusiness;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.model.serializers.AlarmSlimV1JsonSerializer;
import ar.edu.iw3.util.FieldValidator;
import ar.edu.iw3.util.JsonUtils;
import ar.edu.iw3.util.PaginationInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(description = "API Interna para Gestionar Alarmas", name = "Alarms")
@RestController
@RequestMapping(Constants.URL_ALARMS)
public class AlarmRestController extends BaseRestController {

    @Autowired
    private IAlarmBusiness alarmBusiness;

    @Autowired
    private IOrderBusiness orderBusiness;


    /* ENPOINT PARA OBTENER UNA LISTA DE ALARMAS CORRESPONDIENTES A UNA ORDEN (PAGINABLE) */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @SneakyThrows
    public ResponseEntity<?> getAllAlarms(@RequestParam("idOrder") Long idOrder,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size,
                                          @RequestParam(value = "sort", defaultValue = "timeStamp,desc") String sort) {

        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            String sortField = sortParams[0].trim();
            String sortDirection = (sortParams.length > 1 ? sortParams[1].trim().toLowerCase() : "desc"); // Dirección predeterminada
            Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

            // Validar el campo de ordenación
            if (FieldValidator.isValidField(Alarm.class, sortField)) {
                throw new IllegalArgumentException("El campo de ordenación '" + sortField + "' no es válido para la entidad Alarm");
            }

            pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        } else {
            pageable = PageRequest.of(page, size);
        }

        Order order = orderBusiness.load(idOrder);
        Page<Alarm> alarms = alarmBusiness.getAllAlarmsByOrder(order, pageable);
        StdSerializer<Alarm> alarmSerializer = new AlarmSlimV1JsonSerializer(Alarm.class, false);
        ObjectMapper mapper = JsonUtils.getObjectMapper(Alarm.class, alarmSerializer, null);

        // Convertir cada alarma a JSON y agregarla al resultado
        List<Object> serializedAlarms = alarms.getContent().stream()
                .map(alarm -> {
                    try {
                        return mapper.valueToTree(alarm);  // Serializa a JsonNode directamente
                    } catch (Exception e) {
                        throw new RuntimeException("Error al serializar el objeto Alarm", e);
                    }
                }).toList();

        // Crear un objeto de información de paginación
        PaginationInfo paginationInfo = new PaginationInfo(
                alarms.getPageable(),
                alarms.getTotalPages(),
                alarms.getTotalElements(),
                alarms.getNumber(),
                alarms.getSize(),
                alarms.getNumberOfElements()
        );

        // Crear la respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("alarms", serializedAlarms);
        response.put("pagination", paginationInfo);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}