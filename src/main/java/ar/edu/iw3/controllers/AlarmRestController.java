package ar.edu.iw3.controllers;

import ar.edu.iw3.Constants;
import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.interfaces.IAlarmBusiness;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.model.serializers.AlarmSlimV1JsonSerializer;
import ar.edu.iw3.model.serializers.OrderSlimV1JsonSerializer;
import ar.edu.iw3.util.JsonUtils;
import ar.edu.iw3.util.PaginationInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(description = "API Interna para Gestionar Alarmas", name = "Alarms")
@RestController
@RequestMapping(Constants.URL_ALARMS)
public class AlarmRestController extends BaseRestController {

    // todo verificar funcionamiento

    @Autowired
    private IAlarmBusiness alarmBusiness;

    @Autowired
    private IOrderBusiness orderBusiness;

    @GetMapping(value = "", params = {"idOrder","page","size"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @SneakyThrows
    public ResponseEntity<?> getAllAlarms(@RequestParam Long idOrder, @RequestParam("page") int page,
                                          @RequestParam("size") int size,
                                          @RequestParam(value = "sort", required = false, defaultValue = "timeStamp,desc") String sort) {
        Pageable pageable;

        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String sortField = sortParams[0];
                String sortDirection = sortParams[1].toLowerCase();

                Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
            } else {
                throw new IllegalArgumentException("El parámetro 'sort' debe tener el formato 'campo,direccion' (ejemplo: 'externalReceptionDate,desc')");
            }
        } else {
            pageable = PageRequest.of(page, size);
        }

        Order order = orderBusiness.load(idOrder);

        Page<Alarm> alarms = alarmBusiness.getAllAlarmsByOrder(order, pageable);

        StdSerializer<Alarm> alarmSerializer = new AlarmSlimV1JsonSerializer(Alarm.class, false);
        ObjectMapper mapper = JsonUtils.getObjectMapper(Alarm.class, alarmSerializer, null);

        // Convertir cada alarma a JSON y agregarla al resultado
        List<String> serializedAlarms = alarms.getContent().stream()
                .map(alarm -> {
                    try {
                        return mapper.writeValueAsString(alarm);
                    } catch (IOException e) {
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
        response.put("content", serializedAlarms);
        response.put("pagination", paginationInfo);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
