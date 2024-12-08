package ar.edu.iw3.controllers;

import ar.edu.iw3.Constants;
import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.interfaces.IDetailBusiness;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.model.serializers.DetailSlimV1JsonSerializer;
import ar.edu.iw3.util.FieldValidator;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(Constants.URL_DETAILS)
public class DetailRestController extends BaseRestController {

    @Autowired
    private IDetailBusiness detailBusiness;

    @Autowired
    private IOrderBusiness orderBusiness;


    /* ENPOINT PARA OBTENER UNA LISTA DE DETALLES CORRESPONDIENTES A UNA ORDEN (PAGINABLE) */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @SneakyThrows
    public ResponseEntity<?> getAllAlarms(@RequestParam Long idOrder,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size,
                                          @RequestParam(value = "sort", required = false, defaultValue = "timeStamp,desc") String sort) {

        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            String sortField = sortParams[0].trim();
            String sortDirection = (sortParams.length > 1 ? sortParams[1].trim().toLowerCase() : "desc"); // Dirección predeterminada
            Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

            // Validar el campo de ordenación
            if (FieldValidator.isValidField(Detail.class, sortField)) {
                throw new IllegalArgumentException("El campo de ordenación '" + sortField + "' no es válido para la entidad Alarm");
            }

            pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        } else {
            pageable = PageRequest.of(page, size);
        }

        Order order = orderBusiness.load(idOrder);
        Page<Detail> details = detailBusiness.listByOrder(order, pageable);
        StdSerializer<Detail> detailSerializer = new DetailSlimV1JsonSerializer(Detail.class, false);
        ObjectMapper mapper = JsonUtils.getObjectMapper(Detail.class, detailSerializer, null);

        // Convertir cada detalle a JSON y agregarla al resultado
        List<Object> serializedDetails = details.getContent().stream()
                .map(detail -> {
                    try {
                        return mapper.valueToTree(detail);  // Serializa a JsonNode directamente
                    } catch (Exception e) {
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
        response.put("details", serializedDetails);
        response.put("pagination", paginationInfo);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}