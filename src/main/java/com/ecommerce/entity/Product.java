package com.ecommerce.entity; 
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private double price;
    private double weight;
    private String description;
    private String imageName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    public Product(long id, String name, double price, double weight, String description, String imageName, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.description = description;
        this.imageName = imageName;
        this.category = category;
    }
}