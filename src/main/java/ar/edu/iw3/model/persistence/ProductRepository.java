package ar.edu.iw3.model.persistence;

import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ar.edu.iw3.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // JPA Query Methods https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html

    Optional<Product> findByProduct(String product);

    Optional<Product> findByProductAndIdNot(String product, Long id);

}
