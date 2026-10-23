
package com.example.demo.controller;

import com.example.demo.model.CommercialOffer;
import com.example.demo.service.CommercialOfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commercial-offers")
public class CommercialOfferController {

    private final CommercialOfferService service;

    public CommercialOfferController(CommercialOfferService service) {
        this.service = service;
    }

    @GetMapping
    public List<CommercialOffer> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommercialOffer> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CommercialOffer> create(
            @RequestBody CommercialOffer offer) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.save(offer));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommercialOffer> update(
            @PathVariable Long id,
            @RequestBody CommercialOffer updatedOffer) {

        return service.findById(id).map(offer -> {
            if (updatedOffer.getSupplier() != null) {
                offer.setSupplier(updatedOffer.getSupplier());
            }

            if (updatedOffer.getPurchaseRequest() != null) {
                offer.setPurchaseRequest(updatedOffer.getPurchaseRequest());
            }

            if (updatedOffer.getDescription() != null) {
                offer.setDescription(updatedOffer.getDescription());
            }

            if (updatedOffer.getPrice() != null) {
                offer.setPrice(updatedOffer.getPrice());
            }

            if (updatedOffer.getStatus() != null) {
                offer.setStatus(updatedOffer.getStatus());
            }

            return ResponseEntity.ok(service.save(offer));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
