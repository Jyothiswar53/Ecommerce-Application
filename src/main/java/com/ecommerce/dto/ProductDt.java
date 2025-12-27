package com.ecommerce.dto;

import lombok.Data;

@Data
public class ProductDt {

    private long id;
    private String name;
    private double price;
    private double weight;
    private String description;
    private String imageName;
    private int categoryId;

    public ProductDt() {
        super();
    }

    public ProductDt(long id, String name, double price, double weight, String description, String imageName,
                     int categoryId) {
        super();
        this.id = id;
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.description = description;
        this.imageName = imageName;
        this.categoryId = categoryId;
    }
}