package ar.edu.iw3.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tankers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Tanker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_truck", nullable = false)
    @JsonIgnoreProperties("tanks")
    private Truck truck;

    @Column(nullable = false)
    private long capacity_liters;

    @Column(nullable = false)
    private String license ;
}
