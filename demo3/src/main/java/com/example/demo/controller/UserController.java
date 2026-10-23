
package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // Получить список всех пользователей
    @GetMapping
    public List<User> findAll() {
        return service.findAll();
    }

    // Получить пользователя по ID
    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return service.findById(id);
    }

    // Создать пользователя
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        User created = service.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Частично обновить пользователя
    @PatchMapping("/{id}")
    public User update(
            @PathVariable Long id,
            @RequestBody User changes) {
        return service.update(id, changes);
    }

    // Удалить пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
