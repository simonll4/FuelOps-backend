package ar.edu.iw3.model;

import ar.edu.iw3.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "alarms")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Alarm {

    public enum Status {
        PENDING_REVIEW,  // La alarma ha sido generada pero aún no ha sido revisada por ningún operador.
        ACKNOWLEDGED,    // La alarma ha sido revisada y aceptada, indicando que está bajo control.
        CONFIRMED_ISSUE, // La alarma ha sido revisada y se ha confirmado que hay un problema.
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "id_order", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column()
    private Alarm.Status status;

    @Column(nullable = false)
    private Date timeStamp;

    @Column(nullable = false)
    private float temperature;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    private String observation;
}
