package com.example.laba1.controller;

import com.example.laba1.model.PurchaseOrder;
import com.example.laba1.service.PurchaseOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    public PurchaseOrderController(PurchaseOrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<PurchaseOrder> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PurchaseOrder findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> create(
            @RequestBody PurchaseOrder order) {
        PurchaseOrder created = service.create(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public PurchaseOrder update(
            @PathVariable Long id,
            @RequestBody PurchaseOrder changes) {
        return service.update(id, changes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accept-offer/{offerId}")
    public ResponseEntity<PurchaseOrder> acceptOffer(
            @PathVariable Long offerId) {
        PurchaseOrder order = service.acceptOffer(offerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}