package ar.edu.iw3.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @Schema(hidden = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty(message = "El atributo 'product' no puede estar vacío")
    @Column(length = 100, unique = true)
    private String product;

    private String description;

    private float thresholdTemperature;

    @Column(columnDefinition = "tinyint default 1")
    private boolean stock = true;

    @Override
    public String toString() {
        return String.format("id=%s, product=%s, descripcion=%s", this.getId(), this.getProduct(), this.getDescription());
    }

}
