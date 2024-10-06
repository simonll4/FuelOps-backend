package ar.edu.iw3.integration.cli2.model;

import java.util.Date;
import java.util.Set;

import ar.edu.iw3.model.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cli2_products")
@PrimaryKeyJoinColumn(name = "id_product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductCli2 extends Product {

	@Column(nullable = false)
	private Date expirationDate;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "cli2_product_component", joinColumns = {
			@JoinColumn(name = "id_product", referencedColumnName = "id_product") }, inverseJoinColumns = {
					@JoinColumn(name = "id_component", referencedColumnName = "id") })

	private Set<ComponentCli2> components;
}
