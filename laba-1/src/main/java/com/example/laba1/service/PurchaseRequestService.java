package com.example.laba1.service;

import com.example.laba1.model.PurchaseRequest;
import com.example.laba1.model.CommercialOffer;
import com.example.laba1.model.PurchaseOrder;
import com.example.laba1.repository.PurchaseRequestRepository;
import com.example.laba1.repository.UserRepository;
import com.example.laba1.repository.CommercialOfferRepository;
import com.example.laba1.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PurchaseRequestService {

    private final PurchaseRequestRepository repository;
    private final UserRepository userRepository;
    private final CommercialOfferRepository offerRepository;
    private final PurchaseOrderRepository orderRepository;

    public PurchaseRequestService(
            PurchaseRequestRepository repository,
            UserRepository userRepository,
            CommercialOfferRepository offerRepository,
            PurchaseOrderRepository orderRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.offerRepository = offerRepository;
        this.orderRepository = orderRepository;
    }

    public List<PurchaseRequest> findAll() {
        return repository.findAll();
    }

    public PurchaseRequest findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Заявка не найдена: " + id));
    }

    public PurchaseRequest create(PurchaseRequest request) {
        if (request.getDescription() == null
                || request.getDescription().isBlank()) {
            throw new IllegalArgumentException(
                    "Описание заявки обязательно");
        }

        if (request.getMaxAmount() == null
                || request.getMaxAmount().signum() <= 0) {
            throw new IllegalArgumentException(
                    "Бюджет заявки должен быть больше нуля");
        }

        if (request.getAuthorId() == null
                || !userRepository.existsById(request.getAuthorId())) {
            throw new NoSuchElementException("Автор не найден");
        }

        request.setId(null);

        if (request.getStatus() == null
                || request.getStatus().isBlank()) {
            request.setStatus("OPEN");
        }

        return repository.save(request);
    }

    public PurchaseRequest update(Long id, PurchaseRequest changes) {
        PurchaseRequest request = findById(id);

        if (changes.getAuthorId() != null) {
            if (!userRepository.existsById(changes.getAuthorId())) {
                throw new NoSuchElementException("Автор не найден");
            }
            request.setAuthorId(changes.getAuthorId());
        }

        if (changes.getDescription() != null) {
            request.setDescription(changes.getDescription());
        }

        if (changes.getMaxAmount() != null) {
            request.setMaxAmount(changes.getMaxAmount());
        }

        if (changes.getStatus() != null) {
            request.setStatus(changes.getStatus());
        }

        return repository.save(request);
    }

    public void delete(Long id) {
        findById(id);

        List<CommercialOffer> offers = offerRepository.findAll()
                .stream()
                .filter(o -> o.getPurchaseRequestId() != null
                        && o.getPurchaseRequestId().equals(id))
                .toList();

        for (CommercialOffer offer : offers) {
            orderRepository.findAll().stream()
                    .filter(order -> order.getCommercialOfferId() != null
                            && order.getCommercialOfferId()
                                    .equals(offer.getId()))
                    .map(PurchaseOrder::getId)
                    .toList()
                    .forEach(orderRepository::deleteById);

            offerRepository.deleteById(offer.getId());
        }

        orderRepository.findAll().stream()
                .filter(order -> order.getPurchaseRequestId() != null
                        && order.getPurchaseRequestId().equals(id))
                .map(PurchaseOrder::getId)
                .toList()
                .forEach(orderRepository::deleteById);

        repository.deleteById(id);
    }

    public PurchaseRequest submitForApproval(Long id) {
        PurchaseRequest request = findById(id);

        if (!"OPEN".equalsIgnoreCase(request.getStatus())) {
            throw new IllegalStateException(
                    "На согласование можно отправить только открытую заявку");
        }

        if (request.getDescription() == null
                || request.getDescription().isBlank()) {
            throw new IllegalStateException(
                    "У заявки должно быть описание");
        }

        if (request.getMaxAmount() == null
                || request.getMaxAmount().signum() <= 0) {
            throw new IllegalStateException(
                    "Бюджет заявки должен быть больше нуля");
        }

        request.setStatus("PENDING_APPROVAL");
        return repository.save(request);
    }
}