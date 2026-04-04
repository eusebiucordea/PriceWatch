package com.pricewatch.pricewatch.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_history")
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_link_id", nullable = false)
    private ProductLink productLink;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    // se apeleaza automat inainte de salvarea in baza de date
    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductLink getProductLink() {
        return productLink;
    }

    public void setProductLink(ProductLink productLink) {
        this.productLink = productLink;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
