package ar.edu.iw3.integration.cli3.controllers;

import ar.edu.iw3.controllers.Constants;
import ar.edu.iw3.integration.cli3.OrderCli3SlimV1JsonSerializer;
import ar.edu.iw3.integration.cli3.model.business.interfaces.IOrderCli3Business;
import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.util.JsonUtils;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.URL_INTEGRATION_CLI3 + "/orders")
public class OrderCli3RestController {

    @Autowired
    private IOrderCli3Business orderCli3Business;

    @Autowired
    private IOrderCli3Business orderBusiness;

    // Endpoint para validar password y obtener id de la orden y preset de carga
    @SneakyThrows
    @PostMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validatePassword(@RequestHeader("Password") Integer password) {
        Order order = orderCli3Business.validatePassword(password);
        StdSerializer<Order> ser = new OrderCli3SlimV1JsonSerializer(Order.class, false);
        String result = JsonUtils.getObjectMapper(Order.class, ser, null).writeValueAsString(order);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Endpoint para cerrar la orden
    @SneakyThrows
    @PostMapping("/close")
    public ResponseEntity<?> closeOrder(@RequestHeader("OrderId") Long orderId) {
        orderCli3Business.closeOrder(orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Endpoint para recibir datos de carga
    @SneakyThrows
    @PostMapping("/detail")
    public ResponseEntity<?> receiveLoadData(@RequestBody Detail detail) {
        orderBusiness.receiveDetails(detail);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}