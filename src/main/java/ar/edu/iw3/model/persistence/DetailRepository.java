package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Detail;
import ar.edu.iw3.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DetailRepository extends JpaRepository<Detail, Long> {

    Optional<List<Detail>> findByOrderId(Long idOrder);

    Optional<Page<Detail>> findAllByOrder(Order order, Pageable pageable);

    @Query("SELECT AVG(d.temperature) FROM Detail d WHERE d.order.id = :orderId")
    Double findAverageTemperatureByOrderId(Long orderId);

    @Query("SELECT AVG(d.density) FROM Detail d WHERE d.order.id = :orderId")
    Double findAverageDensityByOrderId(Long orderId);

    @Query("SELECT AVG(d.flowRate) FROM Detail d WHERE d.order.id = :orderId")
    Double findAverageFlowRateByOrderId(Long orderId);

}

