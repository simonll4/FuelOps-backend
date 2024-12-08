package ar.edu.iw3.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
        ORDER_CANCELLED,
        ORDER_RECEIVED,
        REGISTERED_INITIAL_WEIGHING,
        ORDER_CLOSED,
        REGISTERED_FINAL_WEIGHING
    }

    @Schema(hidden = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Schema(hidden = true)
    @Enumerated(EnumType.STRING)
    @Column()
    private Status status;

    @Column(unique = true)
    private Integer activatePassword;

    @Column(nullable = false)
    private int preset;

    private float initialWeighing;

    private float finalWeighing;

    // Ultimo estado de carga
    private Date lastTimeStamp;

    private float lastAccumulatedMass;

    private float lastDensity;

    private float lastTemperature;

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
    private Date estimatedDate;

    private Date externalReceptionDate;

    private Date initialWeighingDate;

    private Date finalWeighingDate;

    // fecha de primer detalle de carga
    private Date fuelingStartDate;

    // fecha de ultimo detalle de carga
    private Date fuelingEndDate;
}
