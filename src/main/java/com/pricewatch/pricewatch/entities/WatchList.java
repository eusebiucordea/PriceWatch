package com.pricewatch.pricewatch.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
// adaugam unique pentru a preveni duplicatele
@Table(name = "watchlist", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
public class WatchList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "product_id", nullable = false)
    private int productId; // camelCase

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(name = "target_discount")
    private Integer targetDiscount;

    public Integer getTargetDiscount() {
        return targetDiscount;
    }

    public void setTargetDiscount(Integer targetDiscount) {
        this.targetDiscount = targetDiscount;
    }

    public WatchList() {
    }

    // constructor pentru adaugarea obiectelor
    public WatchList(int userId, int productId) {
        this.userId = userId;
        this.productId = productId;
    }

    // seteaza data si ora exacta
    @PrePersist
    protected void onCreate() {
        this.addedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}