package ar.edu.iw3.integration.cli1.model.persistence;

import ar.edu.iw3.integration.cli1.model.TankerCli1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TankerCli1Repository extends JpaRepository<TankerCli1, Long> {

    Optional<TankerCli1> findOneByIdCli1(String idCli1);

    @Modifying
    @Query(value = "INSERT INTO cli1_tankers (id_tanker, id_cli1, cod_cli1temp) VALUES (:idTanker, :idCli1, false)", nativeQuery = true)
    void insertTankerCli1(@Param("idTanker") Long idTanker, @Param("idCli1") String idCli1);
}
