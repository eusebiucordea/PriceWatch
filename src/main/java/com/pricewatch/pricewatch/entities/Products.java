package com.pricewatch.pricewatch.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "all_time_low", nullable = false)
    private Double all_time_low;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String image_url;

    @Column(name = "name", nullable = false)
    private String name;

    public Double getAll_time_low() {
        return all_time_low;
    }

    public void setAll_time_low(Double all_time_low) {
        this.all_time_low = all_time_low;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}