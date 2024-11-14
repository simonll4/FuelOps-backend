package ar.edu.iw3.auth.model.business.implementations;

import java.util.List;
import java.util.Optional;

import ar.edu.iw3.auth.model.User;
import ar.edu.iw3.auth.model.persistence.UserAuthRepository;
import ar.edu.iw3.auth.model.business.exceptions.BadPasswordException;
import ar.edu.iw3.auth.model.business.interfaces.IUserAuthBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserAuthBusiness implements IUserAuthBusiness {

	@Autowired
	private UserAuthRepository userDAO;

	@Override
	public User load(String usernameOrEmail) throws NotFoundException, BusinessException {
		Optional<User> ou;
		try {
			ou = userDAO.findOneByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (ou.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra el usuari@ email o nombre =" + usernameOrEmail)
					.build();
		}
		return ou.get();
	}

	@Override
	public void changePassword(String usernameOrEmail, String oldPassword, String newPassword, PasswordEncoder pEncoder)
			throws BadPasswordException, NotFoundException, BusinessException {
		User user = load(usernameOrEmail);
		if (!pEncoder.matches(oldPassword, user.getPassword())) {
			throw BadPasswordException.builder().build();
		}
		user.setPassword(pEncoder.encode(newPassword));
		try {
			userDAO.save(user);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public void disable(String usernameOrEmail) throws NotFoundException, BusinessException {
		setDisable(usernameOrEmail, false);
	}

	@Override
	public void enable(String usernameOrEmail) throws NotFoundException, BusinessException {
		setDisable(usernameOrEmail, true);
	}

	@Override
	public List<User> list() throws BusinessException {
		try {
			return userDAO.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	private void setDisable(String usernameOrEmail, boolean enable) throws NotFoundException, BusinessException {
		User user = load(usernameOrEmail);
		user.setEnabled(enable);
		try {
			userDAO.save(user);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

}

