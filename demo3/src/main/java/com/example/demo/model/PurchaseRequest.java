
package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "purchase_requests")
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal ceilingAmount;

    @Column(nullable = false, length = 30)
    private String status = "DRAFT";

    public PurchaseRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCeilingAmount() { return ceilingAmount; }
    public void setCeilingAmount(BigDecimal ceilingAmount) {
        this.ceilingAmount = ceilingAmount;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
