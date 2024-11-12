package ar.edu.iw3.integration.cli1.model.persistence;

import java.util.Optional;
import ar.edu.iw3.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iw3.integration.cli1.model.OrderCli1;

@Repository
public interface OrderCli1Repository extends JpaRepository<OrderCli1, Long> {

    Optional<OrderCli1> findOneByOrderNumberCli1(String oderNumberCli1);

    Optional<OrderCli1> findByTruck_idAndStatus(Long idTruck, Order.Status status);
}
