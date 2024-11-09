package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.ProductCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.IProductCli1Business;
import ar.edu.iw3.integration.cli1.model.persistence.ProductCli1Repository;
import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.implementations.ProductBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductCli1Business implements IProductCli1Business {

    @Autowired
    private ProductCli1Repository productDAO;

    @Autowired
    private ProductBusiness productBaseBusiness;

    // se puede llegar a implementar para que el sistema externo busque un producto por su idCli1
    @Override
    public ProductCli1 load(String idCli1) throws NotFoundException, BusinessException {
        Optional<ProductCli1> r;
        try {
            r = productDAO.findOneByIdCli1(idCli1);
        } catch (Exception e) {
            ProductCli1Business.log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (r.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el producto idCli1=" + idCli1).build();
        }
        return r.get();
    }

    // se puede llegar a implementar para que el sistema externo liste todos los productos
    @Override
    public List<ProductCli1> list() throws BusinessException {
        try {
            return productDAO.findAll();
        } catch (Exception e) {
            ProductCli1Business.log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Product loadExternal(ProductCli1 product) throws BusinessException, NotFoundException, FoundException {
        Optional<ProductCli1> productCli1;

        // si el producto recibido tiene un id externo temporal, se verifica si el producto existe
        productCli1 = productDAO.findProductCli1ByProduct(product.getProduct());
        if (productCli1.isPresent() && product.isCodCli1Temp()) {
            return productCli1.get();
        }
        // si el producto recibido tiene un id externo no temporal, y si esta temporal en db lo reemplazamos
        if (productCli1.isPresent() && !product.isCodCli1Temp() && productCli1.get().isCodCli1Temp()) {
            productCli1.get().setIdCli1(product.getIdCli1());
            productCli1.get().setCodCli1Temp(false);
            return productDAO.save(productCli1.get());
        }

        // si el Producto recibido ya existe en la db con otro id externo no temporal, se lanza una excepcion
        try {
            productCli1 = productDAO.findByProductAndIdCli1NotAndCodCli1Temp(product.getProduct(), product.getIdCli1(), product.isCodCli1Temp());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (productCli1.isPresent()) {
            throw FoundException.builder().message("Ya existe un producto con el nombre:" + product.getProduct()).build();
        }

        // Si el producto recibido existe en la entidad producto externa con idCli1, se retorna el producto
        productCli1 = productDAO.findOneByIdCli1(product.getIdCli1());
        if (productCli1.isPresent()) {
            return productCli1.get();
        }

        // si se recibe un producto con codigo externo no existente en db, se mapea al producto base
        Product findProduct;
        findProduct = productBaseBusiness.load(product.getProduct());
        try {
            product.setId(findProduct.getId());
            product.setProduct(findProduct.getProduct());
            product.setDescription(findProduct.getDescription());
            product.setThresholdTemperature(findProduct.getThresholdTemperature());
            productDAO.insertProductCli1(findProduct.getId(), product.getIdCli1(), product.isCodCli1Temp());
            return findProduct;
        } catch (DataIntegrityViolationException e) {
            throw BusinessException.builder().message("Error al mapear producto").build();
        }
    }
}