package ar.edu.iw3.integration.cli1.model.persistence;

import ar.edu.iw3.integration.cli1.model.TankerCli1;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TankerCli1Repository extends JpaRepository<TankerCli1, Long> {

    Optional<TankerCli1> findOneByIdCli1(String idCli1);
}
