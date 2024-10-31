package ar.edu.iw3.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Detail {

    @Schema(hidden = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Schema(hidden = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order", nullable = false)
    @JsonIgnoreProperties("details")
    private Order order;

    @Column(nullable = false)
    private Date timeStamp;

    @Column(nullable = false)
    private float accumulatedMass;

    @Column(nullable = false)
    private float density;

    @Column(nullable = false)
    private float temperature;

    @Column(nullable = false)
    private float flowRate;

}
