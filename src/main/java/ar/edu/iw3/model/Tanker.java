package ar.edu.iw3.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tankers")
@Inheritance(strategy = InheritanceType.JOINED)
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
// todo forma de cheqear la validez de los datos
//@NotBlank(message = "El atributo 'patente' es obligatorio")
//@Pattern(regexp = "[A-Z]{2}[0-9]{3}[A-Z]{2}", message = "El atributo 'patente' tiene un mal formato")
//private String patente;
//
//@Min(value = 1, message = "El atributo 'cisternadoLitros' tiene que ser mayor a cero")
//private double cisternadoLitros;
//
