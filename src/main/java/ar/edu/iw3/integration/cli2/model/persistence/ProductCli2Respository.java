package ar.edu.iw3.integration.cli2.model.persistence;

import java.util.Date;
import java.util.List;

import ar.edu.iw3.integration.cli2.model.ProductCli2SlimView;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iw3.integration.cli2.model.ProductCli2;

@Repository
public interface ProductCli2Respository extends JpaRepository<ProductCli2, Long> {
	public List<ProductCli2> findByExpirationDateBeforeOrderByExpirationDateDesc(Date expirationDate);

	public List<ProductCli2SlimView> findByOrderByPriceDesc() throws BusinessException;
}

