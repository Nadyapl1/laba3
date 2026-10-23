
package com.example.demo.service;

import com.example.demo.model.CommercialOffer;
import com.example.demo.model.PurchaseOrder;
import com.example.demo.repository.CommercialOfferRepository;
import com.example.demo.repository.PurchaseOrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository repository;
    private final CommercialOfferRepository offerRepository;

    public PurchaseOrderService(
            PurchaseOrderRepository repository,
            CommercialOfferRepository offerRepository) {
        this.repository = repository;
        this.offerRepository = offerRepository;
    }

    public List<PurchaseOrder> findAll() {
        return repository.findAll();
    }

    public Optional<PurchaseOrder> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public PurchaseOrder save(PurchaseOrder order) {
        if (order.getSelectedOffer() == null
                || order.getSelectedOffer().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Укажите selectedOffer.id");
        }

        CommercialOffer offer = offerRepository
                .findById(order.getSelectedOffer().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Коммерческое предложение не найдено"));

        if ("REJECTED".equals(offer.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Нельзя создать заказ по отклонённому предложению");
        }

        if ("ACCEPTED".equals(offer.getStatus())
                && repository.findAll().stream().anyMatch(existing ->
                    existing.getSelectedOffer().getId().equals(offer.getId())
                    && !existing.getId().equals(order.getId()))) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "По этому предложению уже создан заказ");
        }

        order.setSelectedOffer(offer);

        if (order.getDescription() == null
                || order.getDescription().isBlank()) {
            order.setDescription(offer.getDescription());
        }

        if (order.getTotalAmount() == null) {
            order.setTotalAmount(offer.getPrice());
        }

        if (order.getTotalAmount().compareTo(offer.getPrice()) != 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Сумма заказа должна совпадать с ценой предложения");
        }

        if (order.getStatus() == null || order.getStatus().isBlank()) {
            order.setStatus("CREATED");
        }

        offer.setStatus("ACCEPTED");
        offerRepository.save(offer);

        return repository.save(order);
    }

    @Transactional
    public void deleteById(Long id) {
        PurchaseOrder order = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Заказ не найден"));

        if ("COMPLETED".equals(order.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Нельзя удалить завершённый заказ");
        }

        repository.delete(order);
    }
}
