package ar.edu.iw3.integration.cli1.controllers;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.iw3.controllers.BaseRestController;
import ar.edu.iw3.controllers.Constants;
import ar.edu.iw3.integration.cli1.model.OrderCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.IOrderCli1Business;

@RestController
@RequestMapping(Constants.URL_INTEGRATION_CLI1 + "/orders")
public class OrderCli1RestController extends BaseRestController {

    @Autowired
    private IOrderCli1Business orderBusiness;

    /*@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        try {
            return new ResponseEntity<>(productBusiness.list(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{codCli1}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadByCode(@PathVariable("codCli1") String codCli1) {
        try {
            return new ResponseEntity<>(productBusiness.load(codCli1), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "")
    public ResponseEntity<?> add(@RequestBody OrderCli1 product) {
        try {
            OrderCli1 response = productBusiness.add(product);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_INTEGRATION_CLI1 + "/products/" + response.getCodCli1());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
        }
    }*/

    @SneakyThrows
    @PostMapping(value = "/b2b")
    public ResponseEntity<?> addExternal(HttpEntity<String> httpEntity) {
        OrderCli1 response = orderBusiness.addExternal(httpEntity.getBody());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", Constants.URL_INTEGRATION_CLI1 + "/orders/" + response.getOrderNumberCli1());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }

}