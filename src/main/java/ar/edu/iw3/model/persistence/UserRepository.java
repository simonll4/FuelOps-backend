package ar.edu.iw3.model.persistence;

import ar.edu.iw3.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
