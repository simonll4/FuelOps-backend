package ar.edu.iw3.model.persistence;

import ar.edu.iw3.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    public Optional<Customer> findByBusinessName(String businessName);

    public Optional<Customer> findByBusinessNameAndIdNot(String businessName, long id);
}
