package ar.edu.iw3.integration.cli1.model.persistence;

import ar.edu.iw3.integration.cli1.model.TruckCli1;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TruckCli1Repository extends JpaRepository<TruckCli1, Long> {

    Optional<TruckCli1> findOneByIdCli1(String idCli1);
}
