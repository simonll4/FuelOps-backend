package ar.edu.iw3.integration.cli1.model.business.implementations;

import ar.edu.iw3.integration.cli1.model.ProductCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.IProductCli1Business;
import ar.edu.iw3.integration.cli1.model.persistence.ProductCli1Repository;
import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.implementations.ProductBusiness;
import jakarta.transaction.Transactional;
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

    @Override
    public List<ProductCli1> list() throws BusinessException {
        try {
            return productDAO.findAll();
        } catch (Exception e) {
            ProductCli1Business.log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Autowired
    private ProductBusiness baseProductBusiness;

    @Override
    @Transactional
    public Product map(ProductCli1 product) throws BusinessException {
        Product findProduct;
        try {
            findProduct = baseProductBusiness.load(product.getProduct());
        } catch (NotFoundException e) {
            throw BusinessException.builder().message("No existe el producto " + product.getProduct()).build();
        }

        if (productDAO.findOneByIdCli1(product.getIdCli1()).isPresent()) {
            try {
                return load(product.getIdCli1());
            } catch (NotFoundException ignore) {

            }
        }

        try {
            product.setId(findProduct.getId());
            product.setProduct(findProduct.getProduct());
            product.setDescription(findProduct.getDescription());
            product.setTemperature(findProduct.getTemperature());
            productDAO.insertProductCli1(findProduct.getId(), product.getIdCli1());
            return findProduct;
        } catch (DataIntegrityViolationException e) {
            // Manejo específico de la violación de la integridad de los datos
            throw BusinessException.builder().message("Error al insertar en la base de datos").ex(e).build();
        } catch (Exception e) {
            // Manejo general de excepciones
            throw BusinessException.builder().ex(e).build();
        }

    }
}
