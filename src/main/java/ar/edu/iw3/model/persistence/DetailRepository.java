package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface DetailRepository extends JpaRepository<Detail, Long> {

    Optional<List<Detail>> findByOrderId(Long idOrder);

}

