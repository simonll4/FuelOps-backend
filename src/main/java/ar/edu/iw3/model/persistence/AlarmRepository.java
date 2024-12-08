package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    Optional<Alarm> findByStatusAndOrder_Id(Alarm.Status status, Long orderId);

    Optional<List<Alarm>> findByStatusAndOrder_Status(Alarm.Status status, Order.Status orderStatus);

    Optional<Page<Alarm>> findAllByOrder(Order order, Pageable pageable);
}
