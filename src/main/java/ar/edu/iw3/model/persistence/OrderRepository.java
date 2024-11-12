package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByActivatePassword(int activatePassword);

    Optional<Order> findByTruck_LicensePlateAndStatus(String licensePlate, Order.Status status);

    Optional<Order> findByIdAndStatus(long id, Order.Status status);
}