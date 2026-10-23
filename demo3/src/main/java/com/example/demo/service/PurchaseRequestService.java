
package com.example.demo.service;

import com.example.demo.model.PurchaseRequest;
import com.example.demo.repository.PurchaseRequestRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PurchaseRequestService {

    private final PurchaseRequestRepository repository;
    private final UserRepository userRepository;

    public PurchaseRequestService(
            PurchaseRequestRepository repository,
            UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<PurchaseRequest> findAll() {
        return repository.findAll();
    }

    public PurchaseRequest findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Заявка не найдена"));
    }

    @Transactional
    public PurchaseRequest create(PurchaseRequest request) {
        request.setId(null);
        validate(request);

        if (request.getAuthor() == null || request.getAuthor().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Укажите author.id существующего пользователя");
        }

        Long userId = request.getAuthor().getId();
        request.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Пользователь не найден")));

        request.setStatus("DRAFT");
        return repository.save(request);
    }

    @Transactional
    public PurchaseRequest publish(Long id) {
        PurchaseRequest request = findById(id);

        if (!"DRAFT".equals(request.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Опубликовать можно только черновик заявки");
        }

        validate(request);
        request.setStatus("SUBMITTED");
        return repository.save(request);
    }

    @Transactional
    public PurchaseRequest update(Long id, PurchaseRequest changes) {
        PurchaseRequest request = findById(id);

        if (!"DRAFT".equals(request.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Изменять можно только черновик заявки");
        }

        if (changes.getDescription() != null) {
            request.setDescription(changes.getDescription());
        }

        if (changes.getCeilingAmount() != null) {
            request.setCeilingAmount(changes.getCeilingAmount());
        }

        if (changes.getAuthor() != null && changes.getAuthor().getId() != null) {
            request.setAuthor(userRepository.findById(changes.getAuthor().getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Пользователь не найден")));
        }

        validate(request);
        return repository.save(request);
    }

    @Transactional
    public void delete(Long id) {
        PurchaseRequest request = findById(id);

        if (!"DRAFT".equals(request.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Удалять можно только черновик заявки");
        }

        repository.delete(request);
    }

    private void validate(PurchaseRequest request) {
        if (request.getDescription() == null
                || request.getDescription().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Описание заявки обязательно");
        }

        BigDecimal amount = request.getCeilingAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Предельная сумма должна быть больше нуля");
        }
    }
}
