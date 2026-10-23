
package com.example.demo.repository;

import com.example.demo.model.PurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRequestRepository
        extends JpaRepository<PurchaseRequest, Long> {
}
