package ar.edu.iw3.integration.cli1.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iw3.integration.cli1.model.OrderCli1;

@Repository
public interface OrderCli1Respository extends JpaRepository<OrderCli1, Long>{

    Optional<OrderCli1> findOneByOrderNumberCli1(String oderNumberCli1);

    Optional<OrderCli1> findByActivatePassword(Integer activatePassword);

}
