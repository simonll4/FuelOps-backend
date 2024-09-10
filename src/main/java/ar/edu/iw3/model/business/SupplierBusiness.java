package ar.edu.iw3.model.business;

import ar.edu.iw3.model.Supplier;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.persistence.SupplierRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SupplierBusiness implements ISupplierBusiness {
    @Autowired
    private SupplierRepository supplierDAO;

    private Supplier supplier;

    @Override
    public List<Supplier> list() throws BusinessException {
        try {
            return supplierDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Supplier load(long id) throws NotFoundException, BusinessException {
        Optional<Supplier> supplierFound;
        try {
            supplierFound = supplierDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (supplierFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Provedor id = " + id).build();
        return supplierFound.get();
    }

    @Override
    public Supplier load(String supplier) throws NotFoundException, BusinessException {
        Optional<Supplier> supplierFound;
        try {
            supplierFound = supplierDAO.findOneBySupplier(supplier);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (supplierFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Provedor id = " + supplier).build();
        return supplierFound.get();
    }

    @Override
    public Supplier add(Supplier supplier) throws FoundException, BusinessException {
        try {
            load(supplier.getId());
            throw FoundException.builder().message("Ya existe el Proveedor id=" + supplier.getId()).build();
        } catch (NotFoundException ignored) {

        }

        try {
            load(supplier.getSupplier());
            throw FoundException.builder().message("Ya existe el Proveedor=" + supplier.getSupplier()).build();
        } catch (NotFoundException ignored) {

        }

        try {
            return supplierDAO.save(supplier);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw BusinessException.builder().ex(ex).build();
        }
    }

    @Override
    public Supplier update(Supplier supplier) throws NotFoundException, FoundException, BusinessException {
        load(supplier.getId());

        Optional<Supplier> supplierFound;
        try {
            supplierFound = supplierDAO.findOneBySupplierAndIdNot(supplier.getSupplier(), supplier.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (supplierFound.isPresent()) {
            throw FoundException.builder().message("Ya existe el Provedor =" + supplier.getSupplier()).build();
        }

        try {
            return supplierDAO.save(supplier);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public void delete(Supplier supplier) throws NotFoundException, BusinessException {
        delete(supplier.getId());
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);
        try {
            supplierDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }
}