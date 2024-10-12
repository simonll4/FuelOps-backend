package ar.edu.iw3.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "orders")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {

    public enum Status {
        ORDEN_RECIBIDA,
        PESAJE_INICIAL_REGISTRADO,
        ORDEN_CERRADA,
        PESAJE_FINAL_REGISTRADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    // TODO volver a colocar parametros en nullable = false
    @Enumerated(EnumType.STRING)
    @Column()
    private Status status;

    @Column()
    private int activatePassword;

    @Column(nullable = false)
    private float preset;

    @Column()
    private float initialWeighing;

    @Column()
    private float finalWeighing;

    // Ultimo estado de carga
    @Column()
    private Date lastTimeStamp;

    @Column()
    private float lastAccumulatedMass;

    @Column()
    private float lastDensity;

    @Column()
    private float lastTemperature;

    @Column()
    private float lastFlowRate;

    // relaciones con otras entidades
    @ManyToOne
    @JoinColumn(name = "id_truck", nullable = false)
    private Truck truck;

    @ManyToOne
    @JoinColumn(name = "id_driver", nullable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "id_customer", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;

    /*@OneToMany(mappedBy = "order")
    private Set<Detail> details;*/

    /*@OneToMany(mappedBy = "order")
    private Set<Alarm> alarms;*/

    // fechas y horas de los eventos durante el proceso
    @Column(nullable = false)
    private Date estimatedTime;

    @Column()
    private Date externalReceptionDate;

    @Column()
    private Date initialWeighingDate;

    @Column()
    private Date finalWeighingDate;

    @Column()
    private Date fuelingStartDate;

    @Column()
    private Date fuelingEndDate;
}
