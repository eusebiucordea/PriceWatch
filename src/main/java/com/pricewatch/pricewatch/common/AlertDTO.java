package com.pricewatch.pricewatch.common;

public class AlertDTO {

    private Long productId;
    private String productName;
    private Double currentPrice;
    private Integer targetDiscount;


    public AlertDTO(Long productId, String productName, Double currentPrice, Integer targetDiscount) {
        this.productId = productId;
        this.productName = productName;
        this.currentPrice = currentPrice;
        this.targetDiscount = targetDiscount;
    }

    // Getters
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Double getCurrentPrice() { return currentPrice; }
    public Integer getTargetDiscount() { return targetDiscount; }
}