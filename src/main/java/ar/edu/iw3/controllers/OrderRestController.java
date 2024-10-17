package ar.edu.iw3.controllers;

import ar.edu.iw3.controllers.constants.Constants;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IDetailBusiness;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.util.IStandartResponseBusiness;
import ar.edu.iw3.model.Detail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.URL_ORDERS)
public class OrderRestController extends BaseRestController {

    @Autowired
    private IOrderBusiness orderBusiness;

    @Autowired
    private IStandartResponseBusiness response;

    // Endpoint para validar password y obtener id de la orden y preset de carga
    // todo pasar password por header?
    @PostMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validatePassword(@RequestBody String password) {
        try {
            Order order = orderBusiness.validatePassword(Integer.parseInt(password));
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Autowired
    private IDetailBusiness detailBusiness;

    // Endpoint para recibir datos de carga
    @PostMapping("/detail")
    public ResponseEntity<?> receiveLoadData(@RequestBody Detail detail) {
        try {
            detailBusiness.receiveDetails(detail);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para cerrar la orden
    // todo pasar numero de orden por header?
    @PostMapping("/close")
    public ResponseEntity<?> closeOrder(@RequestBody Long orderId) {
        try {
            Order order = orderBusiness.closeOrder(orderId);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

}
