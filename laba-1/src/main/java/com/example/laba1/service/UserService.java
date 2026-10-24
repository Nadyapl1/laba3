
package com.example.laba1.service;

import com.example.laba1.model.User;
import com.example.laba1.model.PurchaseRequest;
import com.example.laba1.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository repository;
    private final PurchaseRequestService requestService;

    public UserService(
            UserRepository repository,
            PurchaseRequestService requestService) {
        this.repository = repository;
        this.requestService = requestService;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Пользователь не найден: " + id));
    }

    public User create(User user) {
        user.setId(null);
        return repository.save(user);
    }

    public User update(Long id, User changes) {
        User user = findById(id);

        if (changes.getName() != null) {
            user.setName(changes.getName());
        }

        if (changes.getEmail() != null) {
            user.setEmail(changes.getEmail());
        }

        return repository.save(user);
    }

    public void delete(Long id) {
        findById(id);

        List<Long> requestIds = requestService.findAll()
                .stream()
                .filter(request -> id.equals(request.getAuthorId()))
                .map(PurchaseRequest::getId)
                .toList();

        requestIds.forEach(requestService::delete);
        repository.deleteById(id);
    }
}
