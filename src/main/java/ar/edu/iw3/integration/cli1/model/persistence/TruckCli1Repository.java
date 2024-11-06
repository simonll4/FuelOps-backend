package ar.edu.iw3.integration.cli1.model.persistence;

import ar.edu.iw3.integration.cli1.model.TruckCli1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TruckCli1Repository extends JpaRepository<TruckCli1, Long> {

    Optional<TruckCli1> findOneByIdCli1(String idCli1);

    Optional<TruckCli1> findOneByLicensePlateAndIdCli1NotAndCodCli1Temp(String licensePlate, String idCli1, boolean codCli1Temp);
    
    Optional<TruckCli1> findByLicensePlate(String licensePlate);

    @Modifying
    @Query(value = "INSERT INTO cli1_trucks (id_truck, id_cli1, cod_cli1temp) VALUES (:idTruck, :idCli1, false)", nativeQuery = true)
    void insertTruckCli1(@Param("idTruck") Long idTruck, @Param("idCli1") String idCli1);
}
