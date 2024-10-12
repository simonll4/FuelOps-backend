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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private int activatePassword;

    @Column(nullable = false)
    private float preset;

    @Column(nullable = false)
    private float initialWeighing;

    @Column(nullable = false)
    private float finalWeighing;

    // Ultimo estado de carga
    @Column(nullable = false)
    private Date lastTimeStamp;

    @Column(nullable = false)
    private float lastAccumulatedMass;

    @Column(nullable = false)
    private float lastDensity;

    @Column(nullable = false)
    private float lastTemperature;

    @Column(nullable = false)
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
    @Column(nullable = false)
    private Date estimatedTime;

    @Column(nullable = false)
    private Date externalReceptionDate;

    @Column(nullable = false)
    private Date initialWeighingDate;

    @Column(nullable = false)
    private Date finalWeighingDate;

    @Column(nullable = false)
    private Date fuelingStartDate;

    @Column(nullable = false)
    private Date fuelingEndDate;
}
