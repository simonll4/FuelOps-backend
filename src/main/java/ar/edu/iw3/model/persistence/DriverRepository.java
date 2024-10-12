package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByDocument(String document);

    Optional<Driver> findByDocumentAndIdNot(String document, Long id);
}
