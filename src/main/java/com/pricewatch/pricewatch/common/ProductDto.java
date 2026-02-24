package com.pricewatch.pricewatch.common;

public class ProductDto {
    private Long id;
    private String name;
    private String image_url;
    private Float all_time_low;

    public ProductDto(Long id, String name, String image_url, Float all_time_low) {
        this.id = id;
        this.name = name;
        this.image_url = image_url;
        this.all_time_low = all_time_low;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Float getAll_time_low() {
        return all_time_low;
    }

    public void setAll_time_low(Float all_time_low) {
        this.all_time_low = all_time_low;
    }
}
