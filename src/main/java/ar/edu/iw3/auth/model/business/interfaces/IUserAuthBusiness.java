package ar.edu.iw3.auth.model.business.interfaces;

import ar.edu.iw3.auth.model.User;
import ar.edu.iw3.auth.model.business.exceptions.BadPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface IUserAuthBusiness {

    public User load(String usernameOrEmail) throws NotFoundException, BusinessException;

    public void changePassword(String usernameOrEmail, String oldPassword, String newPassword, PasswordEncoder pEncoder)
            throws BadPasswordException, NotFoundException, BusinessException;

    public void disable(String usernameOrEmail) throws NotFoundException, BusinessException;

    public void enable(String usernameOrEmail) throws NotFoundException, BusinessException;

    public List<User> list() throws BusinessException;

}
