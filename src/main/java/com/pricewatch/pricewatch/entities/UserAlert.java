package com.pricewatch.pricewatch.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "user_alerts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
public class UserAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;

    @Column(name = "target_price")
    private Double targetPrice;

    @Column(name = "alert_on_all_time_low")
    private Boolean alertOnAllTimeLow = false;

    // --- Getteri și Setteri ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public Double getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(Double targetPrice) {
        this.targetPrice = targetPrice;
    }

    public Boolean getAlertOnAllTimeLow() {
        return alertOnAllTimeLow;
    }

    public void setAlertOnAllTimeLow(Boolean alertOnAllTimeLow) {
        this.alertOnAllTimeLow = alertOnAllTimeLow;
    }
}
