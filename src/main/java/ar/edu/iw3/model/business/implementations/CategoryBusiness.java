package ar.edu.iw3.model.business.implementations;

import ar.edu.iw3.model.Category;
import ar.edu.iw3.model.business.interfaces.ICategoryBusiness;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.persistence.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CategoryBusiness implements ICategoryBusiness {

    @Autowired
    private CategoryRepository categoryDAO;

    @Override
    public Category load(long id) throws NotFoundException, BusinessException {
        Optional<Category> CategoryFound;
        try {
            CategoryFound = categoryDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (CategoryFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra la Category id= " + id).build();
        return CategoryFound.get();
    }

    @Override
    public Category load(String category) throws NotFoundException, BusinessException {
        Optional<Category> CategoryFound;
        try {
            CategoryFound = categoryDAO.findOneByCategory(category);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (CategoryFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra la Category nombre= " + category).build();
        return CategoryFound.get();
    }

    @Override
    public List<Category> list() throws BusinessException {
        try {
            return categoryDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Category add(Category category) throws FoundException, BusinessException {
        try {
            load(category.getId());
            throw FoundException.builder().message("Ya existe la Categoria id = " + category.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            load(category.getCategory());
            throw FoundException.builder().message("Ya existe la Cateogoria = " + category.getCategory()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            return categoryDAO.save(category);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Category update(Category category) throws NotFoundException, FoundException, BusinessException {
        load(category.getId());
        Optional<Category> categoryFound;
        try {
            categoryFound = categoryDAO.findByCategoryAndIdNot(category.getCategory(), category.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (categoryFound.isPresent()) {
            throw FoundException.builder().message("Se encontró la Categoria nombre =" + category.getCategory()).build();
        }

        try {
            return categoryDAO.save(category);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);
        try {
            categoryDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }
}
