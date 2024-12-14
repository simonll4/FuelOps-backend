package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByActivatePassword(int activatePassword);

    Optional<Order> findByTruck_LicensePlateAndStatus(String licensePlate, Order.Status status);

    Optional<Order> findByIdAndStatus(long id, Order.Status status);

    @Query("SELECT o FROM Order o WHERE (:statuses IS NULL OR o.status IN :statuses)")
    Page<Order> findByStatuses(@Param("statuses") List<String> statuses, Pageable pageable);
}