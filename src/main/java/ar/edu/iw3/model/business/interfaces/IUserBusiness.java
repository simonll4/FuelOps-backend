package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.auth.model.User;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface IUserBusiness {

    public List<User> list() throws BusinessException;

    public User load(long id) throws NotFoundException, BusinessException;

    public User load(String user) throws NotFoundException, BusinessException;

    public User add(User user) throws FoundException, BusinessException;

    public User update(User user) throws NotFoundException, BusinessException, FoundException;

    public void delete(User user) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;

}
