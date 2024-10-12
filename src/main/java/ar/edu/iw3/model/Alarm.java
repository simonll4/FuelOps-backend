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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "id_order", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Date timeStamp;

    private String observation;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

}
