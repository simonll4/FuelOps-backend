package ar.edu.iw3.integration.cli1.model.persistence;

import ar.edu.iw3.integration.cli1.model.CustomerCli1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerCli1Repository extends JpaRepository<CustomerCli1, Long> {

    Optional<CustomerCli1> findOneByIdCli1(String idCli1);

    @Modifying
    @Query(value = "INSERT INTO cli1_customers (id_customer, id_cli1, cod_cli1temp) VALUES (:idCustomer, :idCli1, false)", nativeQuery = true)
    void insertCustomerCli1(@Param("idCustomer") Long idCustomer, @Param("idCli1") String idCli1);
}
