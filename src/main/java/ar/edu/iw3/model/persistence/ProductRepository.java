package ar.edu.iw3.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iw3.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
