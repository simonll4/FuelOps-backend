package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByActivatePassword(int activatePassword);

}