package ar.edu.iw3.integration.cli1.model.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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
import ar.edu.iw3.integration.cli1.model.business.IOrderCli1Business;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.util.IStandartResponseBusiness;

@RestController
@RequestMapping(Constants.URL_INTEGRATION_CLI1 + "/orders")
@Profile({ "cli1", "mysqldev"})
public class OrderCli1RestController extends BaseRestController {

    @Autowired
    private IOrderCli1Business orderBusiness;

    @Autowired
    private IStandartResponseBusiness response;

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

    @PostMapping(value = "/b2b")
    public ResponseEntity<?> addExternal(HttpEntity<String> httpEntity) {
        try {
            OrderCli1 response = orderBusiness.addExternal(httpEntity.getBody());
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_INTEGRATION_CLI1 + "/orders/" + response.getOrderNumberCli1());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
        }
    }


}