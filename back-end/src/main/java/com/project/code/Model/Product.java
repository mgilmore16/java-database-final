package com.project.code.Model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name="product",uniqueConstraints = @UniqueConstraint(columnNames = "sku"))
public class Product {

// 1. Add 'id' field:
//    - Type: private long 
//    - This field will be auto-incremented.
//    - Use @Id to mark it as the primary key.
//    - Use @GeneratedValue(strategy = GenerationType.IDENTITY) to auto-increment it.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


// 2. Add 'name' field:
//    - Type: private String
//    - This field cannot be empty, use the @NotNull annotation to enforce this rule.
    @NotNull(message = "Name cannot be null")
    private String name;

// 3. Add 'category' field:
//    - Type: private String
//    - This field cannot be empty, use the @NotNull annotation to enforce this rule.
    @NotNull(message = "Category cannot be null")
    private String category;

// 4. Add 'price' field:
//    - Type: private Double
//    - This field cannot be empty, use the @NotNull annotation to enforce this rule.
    @NotNull(message= "Price cannot be null")
    private Double price;

// 5. Add 'sku' field:
//    - Type: private String
//    - This field cannot be empty, must be unique, use the @NotNull annotation to enforce this rule.
//    - Use the @Table annotation with uniqueConstraints to ensure the 'sku' column is unique.
    @NotNull(message="SKU cannot be null")
    private String sku;
//    Example: @Table(name = "product", uniqueConstraints = @UniqueConstraint(columnNames = "sku"))

// 6. Add relationships:
//    - **Inventory**: A product can have multiple inventory entries.
//    - Use @OneToMany(mappedBy = "product") to reflect the one-to-many relationship with Inventory.
//    - Use @JsonManagedReference("inventory-product") to manage bidirectional relationships and avoid circular references.
    @OneToMany(mappedBy = "product")
    @JsonManagedReference("inventory-product")
    private List<Inventory> inventory;

    public List<Inventory> getInventory() {
        return inventory;
    }

    public void setInventories(List<Inventory> inventory) {
        this.inventory = inventory;
    }
// 7. Add @Entity annotation:
//    - Use @Entity above the class name to mark it as a JPA entity.

// 8. Add Getters and Setters:
//    - Add getter and setter methods for all fields (id, name, category, price, sku).

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}


