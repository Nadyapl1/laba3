
package com.example.demo.service;

import com.example.demo.model.Supplier;
import com.example.demo.repository.SupplierRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository repository;

    public SupplierService(SupplierRepository repository) {
        this.repository = repository;
    }

    public List<Supplier> findAll() {
        return repository.findAll();
    }

    public Supplier findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Поставщик не найден"));
    }

    public Supplier create(Supplier supplier) {
        supplier.setId(null);
        return repository.save(supplier);
    }

    public Supplier update(Long id, Supplier updatedSupplier) {
        Supplier supplier = findById(id);

        if (updatedSupplier.getName() != null) {
            supplier.setName(updatedSupplier.getName());
        }

        if (updatedSupplier.getContactPerson() != null) {
            supplier.setContactPerson(updatedSupplier.getContactPerson());
        }

        if (updatedSupplier.getEmail() != null) {
            supplier.setEmail(updatedSupplier.getEmail());
        }

        if (updatedSupplier.getPhone() != null) {
            supplier.setPhone(updatedSupplier.getPhone());
        }

        if (updatedSupplier.getAddress() != null) {
            supplier.setAddress(updatedSupplier.getAddress());
        }

        if (updatedSupplier.getSupplyCategory() != null) {
            supplier.setSupplyCategory(updatedSupplier.getSupplyCategory());
        }

        supplier.setApproved(updatedSupplier.isApproved());

        return repository.save(supplier);
    }

    public void delete(Long id) {
        Supplier supplier = findById(id);
        repository.delete(supplier);
    }
}
