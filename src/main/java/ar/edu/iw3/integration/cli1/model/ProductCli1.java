package ar.edu.iw3.integration.cli1.model;

import ar.edu.iw3.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cli1_products")
@PrimaryKeyJoinColumn(name = "id_product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ProductCli1 extends Product {

    @Column(nullable = false, unique = true)
    private String idCli1;

    private boolean codCli1Temp=false;
}
