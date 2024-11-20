package ar.edu.iw3.model.persistence;

import ar.edu.iw3.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndIdNot(String username, long id);

}
