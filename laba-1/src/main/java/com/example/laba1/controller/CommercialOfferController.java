
package com.example.laba1.controller;

import com.example.laba1.model.CommercialOffer;
import com.example.laba1.service.CommercialOfferService;
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
    public CommercialOffer findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<CommercialOffer> create(
            @RequestBody CommercialOffer offer) {
        CommercialOffer created = service.create(offer);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public CommercialOffer update(
            @PathVariable Long id,
            @RequestBody CommercialOffer changes) {
        return service.update(id, changes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
