package ar.edu.iw3.auth.model.persistence;

import java.util.Optional;

import ar.edu.iw3.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthRepository extends JpaRepository<User, Long>{

	// metodos usados en la autenticacion
	public Optional<User> findOneByUsernameOrEmail(String username, String email);

}
