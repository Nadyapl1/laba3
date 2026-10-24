
package com.example.laba1.controller;

import com.example.laba1.model.PurchaseRequest;
import com.example.laba1.service.PurchaseRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-requests")
public class PurchaseRequestController {
    private final PurchaseRequestService service;

    public PurchaseRequestController(PurchaseRequestService service) {
        this.service = service;
    }

    @GetMapping
    public List<PurchaseRequest> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PurchaseRequest findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<PurchaseRequest> create(
            @RequestBody PurchaseRequest request) {
        PurchaseRequest created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public PurchaseRequest update(
            @PathVariable Long id,
            @RequestBody PurchaseRequest changes) {
        return service.update(id, changes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/submit-for-approval")
public PurchaseRequest submitForApproval(@PathVariable Long id) {
    return service.submitForApproval(id);
    }
}
