package ar.edu.iw3.model;

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

    @Column(nullable = false)
    private long capacity_liters;

    @Column(nullable = false)
    private String license ;
}
