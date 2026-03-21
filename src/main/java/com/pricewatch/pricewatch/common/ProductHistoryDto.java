package com.pricewatch.pricewatch.common;
import java.time.LocalDateTime;

public class ProductHistoryDto {
    private Long id;
    private Double price;
    private LocalDateTime recordedAt;

    public ProductHistoryDto(Long id, Double price, LocalDateTime recordedAt) {
        this.id = id;
        this.price = price;
        this.recordedAt = recordedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
