package ar.edu.iw3.integration.cli1.model;

import ar.edu.iw3.model.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cli1_orders")
@PrimaryKeyJoinColumn(name = "id_order")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderCli1 extends Order {

    @Column(nullable = false, unique = true)
    private String orderNumberCli1;

    private boolean codCli1Temp=false;
}