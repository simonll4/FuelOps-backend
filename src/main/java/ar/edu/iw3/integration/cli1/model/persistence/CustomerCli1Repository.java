package ar.edu.iw3.integration.cli1.model.persistence;

import ar.edu.iw3.integration.cli1.model.CustomerCli1;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerCli1Repository extends JpaRepository<CustomerCli1, Long> {

    Optional<CustomerCli1> findOneByIdCli1(String idCli1);
}
