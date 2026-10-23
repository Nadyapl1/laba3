
package com.example.demo.controller;

import com.example.demo.model.PurchaseRequest;
import com.example.demo.service.PurchaseRequestService;
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
    public PurchaseRequest create(@RequestBody PurchaseRequest request) {
        return service.create(request);
    }

    @PostMapping("/{id}/publish")
    public PurchaseRequest publish(@PathVariable Long id) {
        return service.publish(id);
    }

    @PatchMapping("/{id}")
    public PurchaseRequest update(
            @PathVariable Long id,
            @RequestBody PurchaseRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
