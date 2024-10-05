package ar.edu.iw3.integration.cli1.model.business;

import java.util.List;

import ar.edu.iw3.integration.cli1.model.ProductCli1;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

public interface IProductCli1Business {

    public ProductCli1 load(String codCli1) throws NotFoundException, BusinessException;

    public List<ProductCli1> list() throws BusinessException;

    public ProductCli1 add(ProductCli1 product) throws FoundException, BusinessException;

    public ProductCli1 addExternal(String json) throws FoundException, BusinessException;
}