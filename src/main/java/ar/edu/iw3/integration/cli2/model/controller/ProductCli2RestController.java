package ar.edu.iw3.integration.cli2.model.controller;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.edu.iw3.controllers.BaseRestController;
import ar.edu.iw3.controllers.Constants;
import ar.edu.iw3.integration.cli2.model.ProductCli2;
import ar.edu.iw3.integration.cli2.model.ProductCli2SlimV1JsonSerializer;
import ar.edu.iw3.integration.cli2.model.business.IProductCli2Business;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.util.IStandartResponseBusiness;
import ar.edu.iw3.util.JsonUtiles;

@RestController
@RequestMapping(Constants.URL_INTEGRATION_CLI2 + "/products")
@Profile({"cli2", "mysqldev", "mysqlprod"})
public class ProductCli2RestController extends BaseRestController {

    @Autowired
    private IProductCli2Business productBusiness;

    @Autowired
    private IStandartResponseBusiness response;

    @GetMapping(value = "/list-expired", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listExpired(
            @RequestParam(name = "since", required = false, defaultValue = "1970-01-01 00:00:00") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date since,
            @RequestParam(name = "slim", required = false, defaultValue = "v0") String slimVersion) {
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(since);
            if (c.get(Calendar.YEAR) == 1970) {
                since = new Date();
            }
            StdSerializer<ProductCli2> ser = null;
            if (slimVersion.equalsIgnoreCase("v1")) {
                ser = new ProductCli2SlimV1JsonSerializer(ProductCli2.class, false);
            } else {
                return new ResponseEntity<>(productBusiness.listExpired(since), HttpStatus.OK);
            }
            String result = JsonUtiles.getObjectMapper(ProductCli2.class, ser, null)
                    .writeValueAsString(productBusiness.listExpired(since));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (BusinessException | JsonProcessingException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
