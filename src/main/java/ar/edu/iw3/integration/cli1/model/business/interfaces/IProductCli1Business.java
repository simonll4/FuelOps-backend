package ar.edu.iw3.integration.cli1.model.business.interfaces;

import ar.edu.iw3.integration.cli1.model.ProductCli1;
import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface IProductCli1Business {

    public ProductCli1 load(String idCli1) throws NotFoundException, BusinessException;

    public List<ProductCli1> list() throws BusinessException;

    public Product map(ProductCli1 product) throws BusinessException;
}
