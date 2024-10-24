package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Tanker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TankerRepository extends JpaRepository<Tanker, Long> {
    Optional<Tanker> findByLicense(String license);

    Optional<Tanker> findByLicenseAndIdNot(String license, Long id);
}
