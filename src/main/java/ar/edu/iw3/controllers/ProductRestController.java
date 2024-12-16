package ar.edu.iw3.controllers;

import ar.edu.iw3.Constants;
import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.interfaces.IProductBusiness;
import ar.edu.iw3.model.serializers.ProductSlimV1JsonSerializer;
import ar.edu.iw3.util.JsonUtils;
import ar.edu.iw3.util.StandartResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.edu.iw3.util.IStandartResponseBusiness;

import java.util.List;

@Tag(description = "API para Gestionar Productos", name = "Product")
@RestController
@RequestMapping(Constants.URL_PRODUCTS)
public class ProductRestController extends BaseRestController {

    @Autowired
    private IProductBusiness productBusiness;

    @Autowired
    private IStandartResponseBusiness response;


    @Operation(operationId = "list-internal-products", summary = "Listar productos", description = "Lista todos los productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {

        List<Product> products = productBusiness.list();

        StdSerializer<Product> productSerializer = new ProductSlimV1JsonSerializer(Product.class, false);
        ObjectMapper mapper = JsonUtils.getObjectMapper(Product.class, productSerializer, null);

        List<Object> serializedProducts = products.stream()
                .map(product -> {
                    try {
                        return mapper.valueToTree(product);
                    } catch (Exception e) {
                        throw new RuntimeException("Error al serializar el objeto Product", e);
                    }
                }).toList();

        return new ResponseEntity<>(serializedProducts, HttpStatus.OK);
    }



    @Operation(operationId = "load-internal-product", summary = "Cargar producto", description = "Carga los datos de un producto")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "Identificador del producto", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto cargado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> loadProduct(@PathVariable long id) {
        return new ResponseEntity<>(productBusiness.load(id), HttpStatus.OK);
    }


    @Operation(operationId = "load-internal-product-by-name", summary = "Cargar producto por nombre", description = "Carga los datos de un producto por nombre")
    @Parameter(in = ParameterIn.PATH, name = "product", description = "Nombre del producto", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto cargado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),

    })
    @SneakyThrows
    @GetMapping(value = "/by_name/{product}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadProduct(@PathVariable String product) {
        return new ResponseEntity<>(productBusiness.load(product), HttpStatus.OK);
    }


    @Operation(operationId = "add-internal-product", summary = "Agregar producto", description = "Agrega un producto")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Objeto JSON que representa los datos del producto",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Product.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto agregado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @PostMapping(value = "")
    public ResponseEntity<?> addProduct(@Valid @RequestBody Product product) {
        Product response = productBusiness.add(product);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", Constants.URL_PRODUCTS + "/" + response.getId());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }


    @Operation(operationId = "update-internal-product", summary = "Actualizar producto", description = "Actualiza un producto")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Objeto JSON que representa los datos del producto",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Product.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @PutMapping(value = "")
    public ResponseEntity<?> updateProduct(@RequestBody Product product) {
        productBusiness.update(product);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(operationId = "delete-internal-product", summary = "Eliminar producto", description = "Elimina un producto")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "Identificador del producto", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
    })
    @SneakyThrows
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable long id) {
        productBusiness.delete(id);
        return new ResponseEntity<String>(HttpStatus.OK);
    }


//    @Operation(operationId = "list-internal-categories", summary = "Listar categorías", description = "Lista todas las categorías")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Lista de categorías", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))}),
//            @ApiResponse(responseCode = "401", description = "Autenticación requerida", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "500", description = "Error interno", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//    })
//    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> listCategories() {
//        try {
//            return new ResponseEntity<>(categoryBusiness.list(), HttpStatus.OK);
//        } catch (BusinessException e) {
//            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//
//    @Operation(operationId = "load-internal-category", summary = "Cargar categoría", description = "Carga los datos de una categoría")
//    @Parameter(in = ParameterIn.PATH, name = "id", description = "Identificador de la categoría", required = true)
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Categoría cargada", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))}),
//            @ApiResponse(responseCode = "401", description = "Autenticación requerida", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "500", description = "Error interno", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//    })
//    @GetMapping(value = "/categories/{id}")
//    public ResponseEntity<?> loadCategory(@PathVariable long id) {
//        try {
//            return new ResponseEntity<>(categoryBusiness.load(id), HttpStatus.OK);
//        } catch (BusinessException e) {
//            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        } catch (NotFoundException e) {
//            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
//        }
//    }
//
//
//    @Operation(operationId = "load-internal-category-by-name", summary = "Cargar categoría por nombre", description = "Carga los datos de una categoría por nombre")
//    @Parameter(in = ParameterIn.PATH, name = "category", description = "Nombre de la categoría", required = true)
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Categoría cargada", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))}),
//            @ApiResponse(responseCode = "401", description = "Autenticación requerida", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "500", description = "Error interno", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//    })
//    @GetMapping(value = "/categories/by_name/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> loadCategory(@PathVariable String category) {
//        try {
//            return new ResponseEntity<>(categoryBusiness.load(category), HttpStatus.OK);
//        } catch (BusinessException e) {
//            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        } catch (NotFoundException e) {
//            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @Operation(operationId = "add-internal-category", summary = "Agregar categoría", description = "Agrega una categoría")
//    @io.swagger.v3.oas.annotations.parameters.RequestBody(
//            required = true,
//            description = "Objeto JSON que representa los datos de la categoría",
//            content = @Content(
//                    mediaType = MediaType.APPLICATION_JSON_VALUE,
//                    schema = @Schema(implementation = Category.class)
//            )
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Categoría agregada", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))}),
//            @ApiResponse(responseCode = "401", description = "Autenticación requerida", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "500", description = "Error interno", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//    })
//    @PostMapping(value = "/categories")
//    public ResponseEntity<?> addCategory(@RequestBody Category category) {
//        try {
//            Category response = categoryBusiness.add(category);
//            HttpHeaders responseHeaders = new HttpHeaders();
//            responseHeaders.set("location", Constants.URL_PRODUCTS + "/categories/" + response.getId());
//            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
//        } catch (BusinessException e) {
//            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        } catch (FoundException e) {
//            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
//        }
//    }
//
//    @Operation(operationId = "update-internal-category", summary = "Actualizar categoría", description = "Actualiza una categoría")
//    @io.swagger.v3.oas.annotations.parameters.RequestBody(
//            required = true,
//            description = "Objeto JSON que representa los datos de la categoría",
//            content = @Content(
//                    mediaType = MediaType.APPLICATION_JSON_VALUE,
//                    schema = @Schema(implementation = Category.class)
//            )
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Categoría actualizada"),
//            @ApiResponse(responseCode = "401", description = "Autenticación requerida", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "500", description = "Error interno", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//    })
//    @PutMapping(value = "/categories")
//    public ResponseEntity<?> updateCategory(@RequestBody Category category) {
//        try {
//            categoryBusiness.update(category);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (BusinessException e) {
//            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        } catch (NotFoundException e) {
//            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
//        } catch (FoundException e) {
//            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
//        }
//    }
//
//
//    @Operation(operationId = "delete-internal-category", summary = "Eliminar categoría", description = "Elimina una categoría")
//    @Parameter(in = ParameterIn.PATH, name = "id", description = "Identificador de la categoría", required = true)
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Categoría eliminada"),
//            @ApiResponse(responseCode = "401", description = "Autenticación requerida", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "403", description = "Permisos insuficientes para acceder al recurso", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//            @ApiResponse(responseCode = "500", description = "Error interno", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))}),
//    })
//    @DeleteMapping(value = "/categories/{id}")
//    public ResponseEntity<?> deleteCategory(@PathVariable long id) {
//        try {
//            categoryBusiness.delete(id);
//            return new ResponseEntity<String>(HttpStatus.OK);
//        } catch (BusinessException e) {
//            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        } catch (NotFoundException e) {
//            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
//        }
//    }

}
