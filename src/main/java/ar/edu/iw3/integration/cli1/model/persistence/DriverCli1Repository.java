package ar.edu.iw3.integration.cli1.model.persistence;

import ar.edu.iw3.integration.cli1.model.DriverCli1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DriverCli1Repository extends JpaRepository<DriverCli1, Long> {
    Optional<DriverCli1> findOneByIdCli1(String idCli1);

    Optional<DriverCli1> findByDocumentAndIdCli1NotAndCodCli1Temp(String document, String idCli1, boolean codCli1Temp);

    Optional<DriverCli1> findByDocument(String document);

    @Modifying
    @Query(value = "INSERT INTO cli1_drivers (id_driver, id_cli1, cod_cli1temp) VALUES (:idDriver, :idCli1, false)", nativeQuery = true)
    void insertDriverCli1(@Param("idDriver") Long idDriver, @Param("idCli1") String idCli1);



}
