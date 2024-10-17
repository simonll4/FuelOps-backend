package ar.edu.iw3.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 100, unique = true)
    private String product;

    private String description;

    private float temperature;

    @Override
    public String toString() {
        return String.format("id=%s, product=%s, descripcion=%s", this.getId(), this.getProduct(), this.getDescription());
    }

}
