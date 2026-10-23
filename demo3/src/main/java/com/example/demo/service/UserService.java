
package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Пользователь не найден"));
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
        if (changes.getRole() != null) {
            user.setRole(changes.getRole());
        }

        return repository.save(user);
    }

    public void delete(Long id) {
        User user = findById(id);
        repository.delete(user);
    }
}
