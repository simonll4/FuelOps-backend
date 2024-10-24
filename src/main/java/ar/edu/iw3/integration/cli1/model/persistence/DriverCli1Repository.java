package ar.edu.iw3.integration.cli1.model.persistence;

import ar.edu.iw3.integration.cli1.model.DriverCli1;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverCli1Repository extends JpaRepository<DriverCli1, Long> {
    Optional<DriverCli1> findOneByIdCli1(String idCli1);
}
