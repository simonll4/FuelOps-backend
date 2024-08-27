package ar.edu.iw3.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.persistence.ProductRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductBusiness implements IProductBusiness {

	// IoC
	@Autowired
	private ProductRepository productDAO;
	
	@Override
	public Product load(long id) throws NotFoundException, BusinessException {
		Optional<Product> r;
		
		try {
			r=productDAO.findById(id);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw BusinessException.builder().ex(e).build();
		}
		if(r.isEmpty())
			throw NotFoundException.builder().message("No se encuentra el Producto id="+id).build();
		
		return r.get();
		
		//return productDAO.findById(id).get();
	}
	
	@Override
	public Product load(String product) throws NotFoundException, BusinessException {
		Optional<Product> r;
		
		try {
			r=productDAO.findByProduct(product);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw BusinessException.builder().ex(e).build();
		}
		if(r.isEmpty())
			throw NotFoundException.builder().message("No se encuentra el Producto denominado "+product).build();
		
		return r.get();
	}

	
	@Override
	public Product add(Product product) throws FoundException, BusinessException {
		
		try {
			load(product.getId());
			throw FoundException.builder().message("Se encontró el producto id="+product.getId()).build();
		} catch (NotFoundException e) {
			// log.trace(e.getMessage(), e);
		}
		
		try {
			load(product.getProduct());
			throw FoundException.builder().message("Se encontró el producto "+product.getProduct()).build();
		} catch (NotFoundException e) {}
		
		try {
			return productDAO.save(product);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw BusinessException.builder().ex(e).build();
		}
		
	}

	
	@Override
	public List<Product> list() throws BusinessException {
		try {
			return productDAO.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw BusinessException.builder().ex(e).build();
		}
	}


// 1 Arroz 189 true
// 2 Leche 50  true

// 1 Leche 190 true <-- esto no puede ocurrir!!!!!!


	@Override
	public Product update(Product product) throws NotFoundException, BusinessException {
		load(product.getId());
		//load(product.getProduct()); CHacer todo para que esto funcione!!!!
		try {
			return productDAO.save(product);
		} catch (Exception e) {

			log.error(e.getMessage(),e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public void delete(Product product) throws NotFoundException, BusinessException {
		delete(product.getId());

	}

	@Override
	public void delete(long id) throws NotFoundException, BusinessException {
		load(id);
		try {
			productDAO.deleteById(id);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw BusinessException.builder().ex(e).build();
		}

	}

}
