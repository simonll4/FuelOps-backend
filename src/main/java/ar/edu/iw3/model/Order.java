package ar.edu.iw3.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
        ORDER_RECEIVED,
        REGISTERED_INITIAL_WEIGHING,
        ORDER_CLOSED,
        REGISTERED_FINAL_WEIGHING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // TODO volver a colocar parametros en nullable = false

    @Enumerated(EnumType.STRING)
    @Column()
    private Status status;

    @Column(unique = true)
    private Integer activatePassword;

    @Min(value = 1, message = "El atributo 'preset' tiene que ser mayor a cero")
    @Column(nullable = false)
    private float preset;

    @Column(columnDefinition = "tinyint default 1")
    private boolean alarmAccepted = true;

    @Column()
    private float initialWeighing;

    @Column()
    private float finalWeighing;

    // todo ver esto si usamos wrappers para poner null
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

    @OneToMany(mappedBy = "order")
    private Set<Detail> details;

    @OneToMany(mappedBy = "order")
    private Set<Alarm> alarms;

    // fechas y horas de los eventos durante el proceso
    @Column()
    private Date estimatedTime;

    @Column()
    private Date externalReceptionDate;

    @Column()
    private Date initialWeighingDate;

    @Column()
    private Date finalWeighingDate;

    // fecha de primer detalle de carga
    @Column()
    private Date fuelingStartDate;

    // fecha de ultimo detalle de carga
    @Column()
    private Date fuelingEndDate;
}
