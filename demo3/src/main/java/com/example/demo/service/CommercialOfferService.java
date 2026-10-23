
package com.example.demo.service;

import com.example.demo.model.CommercialOffer;
import com.example.demo.model.PurchaseRequest;
import com.example.demo.model.Supplier;
import com.example.demo.repository.CommercialOfferRepository;
import com.example.demo.repository.PurchaseRequestRepository;
import com.example.demo.repository.SupplierRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CommercialOfferService {

    private final CommercialOfferRepository repository;
    private final SupplierRepository supplierRepository;
    private final PurchaseRequestRepository requestRepository;

    public CommercialOfferService(
            CommercialOfferRepository repository,
            SupplierRepository supplierRepository,
            PurchaseRequestRepository requestRepository) {
        this.repository = repository;
        this.supplierRepository = supplierRepository;
        this.requestRepository = requestRepository;
    }

    public List<CommercialOffer> findAll() {
        return repository.findAll();
    }

    public Optional<CommercialOffer> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public CommercialOffer save(CommercialOffer offer) {
        if (offer.getSupplier() == null
                || offer.getSupplier().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Укажите supplier.id");
        }

        if (offer.getPurchaseRequest() == null
                || offer.getPurchaseRequest().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Укажите purchaseRequest.id");
        }

        if (offer.getDescription() == null
                || offer.getDescription().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Описание предложения обязательно");
        }

        if (offer.getPrice() == null
                || offer.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Цена должна быть больше нуля");
        }

        Supplier supplier = supplierRepository
                .findById(offer.getSupplier().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Поставщик не найден"));

        if (!supplier.isApproved()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Поставщик не прошёл согласование");
        }

        PurchaseRequest request = requestRepository
                .findById(offer.getPurchaseRequest().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Заявка не найдена"));

        BigDecimal limit = request.getCeilingAmount();

        if (limit == null
                || limit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "У заявки не задан корректный предельный бюджет");
        }

        if (offer.getPrice().compareTo(limit) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Цена предложения " + offer.getPrice()
                            + " превышает предельную сумму заявки "
                            + limit);
        }

        if (!"SUBMITTED".equals(request.getStatus())
                && !"OPEN".equals(request.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Предложение можно добавить только к опубликованной заявке");
        }

        if (offer.getId() != null) {
            CommercialOffer existing = repository
                    .findById(offer.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Коммерческое предложение не найдено"));

            if ("ACCEPTED".equals(existing.getStatus())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Нельзя изменять уже принятое предложение");
            }
        }

        offer.setSupplier(supplier);
        offer.setPurchaseRequest(request);

        if (offer.getStatus() == null
                || offer.getStatus().isBlank()) {
            offer.setStatus("SUBMITTED");
        }

        return repository.save(offer);
    }

    public void deleteById(Long id) {
        CommercialOffer offer = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Предложение не найдено"));

        if ("ACCEPTED".equals(offer.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Нельзя удалить принятое предложение");
        }

        repository.delete(offer);
    }
}
