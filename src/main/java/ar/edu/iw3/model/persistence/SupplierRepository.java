package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findOneBySupplier(String supplier);

    Optional<Supplier> findOneBySupplierAndIdNot(String supplier, long id);
}
