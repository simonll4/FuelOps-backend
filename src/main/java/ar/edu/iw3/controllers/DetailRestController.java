package ar.edu.iw3.controllers;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.interfaces.IDetailBusiness;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.model.serializers.AlarmSlimV1JsonSerializer;
import ar.edu.iw3.model.serializers.DetailSlimV1JsonSerializer;
import ar.edu.iw3.util.JsonUtils;
import ar.edu.iw3.util.PaginationInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DetailRestController extends BaseRestController {

    // todo verificar funcionamiento
    // serealizer: id, acummulated mass, density, flow, temperature, timestamp
    // devoler infomacion de paginacion


    @Autowired
    private IDetailBusiness detailBusiness;

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

        Page<Detail> details = detailBusiness.listByOrder(order, pageable);

        StdSerializer<Detail> detailSerializer = new DetailSlimV1JsonSerializer(Detail.class, false);
        ObjectMapper mapper = JsonUtils.getObjectMapper(Detail.class, detailSerializer, null);

        // Convertir cada alarma a JSON y agregarla al resultado
        List<String> serializedDetails = details.getContent().stream()
                .map(detail -> {
                    try {
                        return mapper.writeValueAsString(detail);
                    } catch (IOException e) {
                        throw new RuntimeException("Error al serializar el objeto Detail", e);
                    }
                }).toList();

        // Crear un objeto de información de paginación
        PaginationInfo paginationInfo = new PaginationInfo(
                details.getPageable(),
                details.getTotalPages(),
                details.getTotalElements(),
                details.getNumber(),
                details.getSize(),
                details.getNumberOfElements()
        );

        // Crear la respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("content", serializedDetails);
        response.put("pagination", paginationInfo);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
