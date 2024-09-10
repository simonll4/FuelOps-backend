package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Category;

import java.util.List;

public interface ICategoryBussiness {
    public Category load(long id) throws NotFoundException,BusinessException;

    public Category load(String category) throws NotFoundException,BusinessException;

    public List<Category> list() throws BusinessException;

    public Category add(Category category) throws FoundException,BusinessException;

    public Category update(Category category) throws NotFoundException,BusinessException;

    public void delete(long id) throws NotFoundException,BusinessException;
}
