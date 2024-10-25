package ar.edu.iw3.integration.cli1.model;

import ar.edu.iw3.model.Truck;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cli1_trucks")
@PrimaryKeyJoinColumn(name = "id_truck")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class TruckCli1 extends Truck {

    @Column(nullable = false, unique = true)
    private String idCli1;

    private boolean codCli1Temp=false;
}
