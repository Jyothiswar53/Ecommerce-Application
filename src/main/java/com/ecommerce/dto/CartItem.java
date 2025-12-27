package com.ecommerce.dto;

import lombok.Data;

@Data
public class CartItem {

    private Long productId;
    private String productName;
    private double price;
    private String imageName;
    private int quantity = 1;

    public CartItem() {
    }

    public CartItem(Long productId, String productName, double price, String imageName) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.imageName = imageName;
    }

    public double getTotal() {
        return price * quantity;
    }
}