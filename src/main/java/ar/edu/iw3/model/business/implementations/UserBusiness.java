package ar.edu.iw3.model.business.implementations;

import ar.edu.iw3.auth.model.User;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IUserBusiness;
import ar.edu.iw3.model.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserBusiness implements IUserBusiness {

    @Autowired
    private UserRepository userDAO;

    @Override
    public List<User> list() throws BusinessException {
        try {
            return userDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public User load(long id) throws NotFoundException, BusinessException {
        Optional<User> userFound;

        try {
            userFound = userDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (userFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Usuario id= " + id).build();
        return userFound.get();
    }

    @Override
    public User load(String user) throws NotFoundException, BusinessException {
        Optional<User> userFound;

        try {
            userFound = userDAO.findByUsername(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (userFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Usuario " + user).build();
        return userFound.get();
    }

    @Override
    public User add(User user) throws FoundException, BusinessException {

        try {
            load(user.getId());
            throw FoundException.builder().message("Se encontro el Usuario con id = " + user.getId()).build();
        } catch (Exception ignored) {
        }

        try {
            load(user.getUsername());
            throw FoundException.builder().message("Se encontro el Usuario " + user.getUsername()).build();
        } catch (NotFoundException ignored) {
        }

        try {
            return userDAO.save(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }


    @Override
    public User update(User user) throws NotFoundException, BusinessException, FoundException {
        load(user.getId() );
        Optional<User> userFound;

        try {
            userFound = userDAO.findByUsernameAndIdNot(user.getUsername(), user.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (userFound.isPresent()) {
            throw FoundException.builder().message("Se encontró el Usuario " + user.getUsername()).build();
        }

        try {
            return userDAO.save(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }


//    @Override
//    public User update(User user) throws NotFoundException, BusinessException, FoundException {
//        load(user.getId());
//        Optional<User> userFound;
//
//        try {
//            userFound = userDAO.findByUsernameAndIdNot(user.getUsername(), user.getId());
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            throw BusinessException.builder().ex(e).build();
//        }
//
//        if (userFound.isPresent()) {
//            throw FoundException.builder().message("Se encontro el Usuario " + user.getUsername()).build();
//        }
//
//        try {
//            return userDAO.save(user);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            throw BusinessException.builder().ex(e).build();
//        }
//    }

    @Override
    public void delete(User user) throws NotFoundException, BusinessException {
        delete(user.getId());
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);

        try {
            userDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

}