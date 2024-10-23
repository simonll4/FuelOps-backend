package ar.edu.iw3.integration.cli2.controllers;

import ar.edu.iw3.controllers.Constants;
import ar.edu.iw3.integration.cli2.model.business.implementaions.OrderCli2Business;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.URL_INTEGRATION_CLI2 + "/orders")
public class OrderCli2RestController {

    @Autowired
    OrderCli2Business orderCli2Business;

    @SneakyThrows
    @PostMapping(value = "/inicial-weighing", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> registerInitialWeighing(
            @RequestHeader("orderNumber") String orderNumber,
            @RequestHeader("initialWeight") float initialWeight) {
        Integer pass = orderCli2Business.registerInitialWeighing(orderNumber, initialWeight);
        return new ResponseEntity<>(pass.toString(), HttpStatus.OK);
    }

    @SneakyThrows
    @PostMapping("/final-weighing")
    public ResponseEntity<byte[]> registerFinalWeighing(
            @RequestHeader("orderNumber") String orderNumber,
            @RequestHeader("finalWeight") float initialWeight) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.pdf\"");
        byte[] pdfContent = orderCli2Business.registerFinalWeighing(orderNumber, initialWeight);
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

}
