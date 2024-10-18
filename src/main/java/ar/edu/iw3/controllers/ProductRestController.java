package ar.edu.iw3.controllers;

import ar.edu.iw3.controllers.constants.Constants;
import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.Category;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.ICategoryBusiness;
import ar.edu.iw3.model.business.interfaces.IProductBusiness;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ar.edu.iw3.util.IStandartResponseBusiness;

@RestController
@RequestMapping(Constants.URL_PRODUCTS)
public class ProductRestController extends BaseRestController {

    @Autowired
    private IProductBusiness productBusiness;

    @Autowired
    private IStandartResponseBusiness response;

    @SneakyThrows
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        return new ResponseEntity<>(productBusiness.list(), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> loadProduct(@PathVariable long id) {
        return new ResponseEntity<>(productBusiness.load(id), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping(value = "/by_name/{product}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadProduct(@PathVariable String product) {
        return new ResponseEntity<>(productBusiness.load(product), HttpStatus.OK);
    }

    @SneakyThrows
    @PostMapping(value = "")
    public ResponseEntity<?> addProduct(@Valid @RequestBody Product product) {
        Product response = productBusiness.add(product);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", Constants.URL_PRODUCTS + "/" + response.getId());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }

    @SneakyThrows
    @PutMapping(value = "")
    public ResponseEntity<?> updateProduct(@RequestBody Product product) {
        productBusiness.update(product);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable long id) {
        productBusiness.delete(id);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @Autowired
    private ICategoryBusiness categoryBusiness;

    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listCategories() {
        try {
            return new ResponseEntity<>(categoryBusiness.list(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/categories/{id}")
    public ResponseEntity<?> loadCategory(@PathVariable long id) {
        try {
            return new ResponseEntity<>(categoryBusiness.load(id), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/categories/by_name/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadCategory(@PathVariable String category) {
        try {
            return new ResponseEntity<>(categoryBusiness.load(category), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/categories")
    public ResponseEntity<?> addCategory(@RequestBody Category category) {
        try {
            Category response = categoryBusiness.add(category);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_PRODUCTS + "/categories/" + response.getId());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
        }
    }

    @PutMapping(value = "/categories")
    public ResponseEntity<?> updateCategory(@RequestBody Category category) {
        try {
            categoryBusiness.update(category);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
        }
    }

    @DeleteMapping(value = "/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable long id) {
        try {
            categoryBusiness.delete(id);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

}
