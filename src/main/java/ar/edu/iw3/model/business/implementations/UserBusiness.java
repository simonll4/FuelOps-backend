package ar.edu.iw3.model.business.implementations;

import ar.edu.iw3.auth.model.User;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IUserBusiness;
import ar.edu.iw3.model.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserBusiness implements IUserBusiness {

    @Autowired
    private UserRepository userDAO;

    // todo implement methods

    @Override
    public List<User> list() throws BusinessException {
        return List.of();
    }

    @Override
    public User load(long id) throws NotFoundException, BusinessException {
        return null;
    }

    @Override
    public User load(String user) throws NotFoundException, BusinessException {
        return null;
    }

    @Override
    public User add(User user) throws FoundException, BusinessException {
        return null;
    }

    @Override
    public User update(User user) throws NotFoundException, BusinessException, FoundException {
        return null;
    }

    @Override
    public void delete(User user) throws NotFoundException, BusinessException {

    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {

    }


}
