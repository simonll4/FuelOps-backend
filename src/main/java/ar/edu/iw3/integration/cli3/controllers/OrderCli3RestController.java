package ar.edu.iw3.integration.cli3.controllers;

import ar.edu.iw3.controllers.Constants;
import ar.edu.iw3.integration.cli3.model.business.IDetailCli3Business;
import ar.edu.iw3.integration.cli3.model.business.IOrderCli3Business;
import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.Order;
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

    // Endpoint para validar password y obtener id de la orden y preset de carga
    // todo pasar password por header?
    // todo hacer serealizador
    @SneakyThrows
    @PostMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validatePassword(@RequestBody String password) {
        Order order = orderCli3Business.validatePassword(Integer.parseInt(password));
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    // Endpoint para cerrar la orden
    // todo pasar numero de orden por header?
    @SneakyThrows
    @PostMapping("/close")
    public ResponseEntity<?> closeOrder(@RequestBody Long orderId) {
        orderCli3Business.closeOrder(orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    private IDetailCli3Business detailBusiness;

    // Endpoint para recibir datos de carga
    @SneakyThrows
    @PostMapping("/detail")
    public ResponseEntity<?> receiveLoadData(@RequestBody Detail detail) {
        detailBusiness.receiveDetails(detail);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
