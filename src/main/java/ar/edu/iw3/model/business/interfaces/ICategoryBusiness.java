//package ar.edu.iw3.model.business.interfaces;
//
//import ar.edu.iw3.model.Category;
//import ar.edu.iw3.model.business.exceptions.BusinessException;
//import ar.edu.iw3.model.business.exceptions.FoundException;
//import ar.edu.iw3.model.business.exceptions.NotFoundException;
//
//import java.util.List;
//
//public interface ICategoryBusiness {
//    public Category load(long id) throws NotFoundException, BusinessException;
//
//    public Category load(String category) throws NotFoundException,BusinessException;
//
//    public List<Category> list() throws BusinessException;
//
//    public Category add(Category category) throws FoundException,BusinessException;
//
//    public Category update(Category category) throws NotFoundException,BusinessException, FoundException;
//
//    public void delete(long id) throws NotFoundException,BusinessException;
//}
