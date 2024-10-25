package ar.edu.iw3.integration.cli1.model;

import ar.edu.iw3.model.Driver;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cli1_drivers")
@PrimaryKeyJoinColumn(name = "id_driver")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class DriverCli1 extends Driver {

    @Column(nullable = false, unique = true)
    private String idCli1;

    private boolean codCli1Temp=false;
}
