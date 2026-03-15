package com.project.code.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
public class OrderItem {

// 1. Add 'id' field:
//    - Type: private Long
//    - This field will be auto-incremented.
//    - Use @Id to mark it as the primary key.
//    - Use @GeneratedValue(strategy = GenerationType.IDENTITY) to auto-increment it.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


// 2. Add 'order' field:
//    - Type: private OrderDetails
//    - This field refers to the order this item belongs to.
//    - Use @ManyToOne with @JoinColumn(name = "order_id") to define the foreign key relationship.
//    - Apply @JsonManagedReference to manage bidirectional relationships and JSON serialization.
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonManagedReference
    private OrderDetails order;

// 3. Add 'product' field:
//    - Type: private Product
//    - This field refers to the product in the order.
//    - Use @ManyToOne with @JoinColumn(name = "product_id") to define the foreign key relationship.
//    - Apply @JsonManagedReference to prevent circular references during JSON serialization.
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonManagedReference
    private Product product;

// 4. Add 'quantity' field:
//    - Type: private Integer
//    - This field represents the quantity of the product in the order.
    private Integer quantity;

// 5. Add 'price' field:
//    - Type: private Double
//    - This field represents the price of the product at the time of the order.
    private Double price;

// 6. Add constructors:
//    - A no-argument constructor.
//    - A parameterized constructor that accepts OrderDetails, Product, quantity, and price as parameters.
    public OrderItem() {}
    public OrderItem(OrderDetails order, Product product, Integer quantity, Double price) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
     }
// 7. Add @Entity annotation:
//    - Use @Entity above the class name to mark it as a JPA entity.

// 8. Add Getters and Setters:
//    - Add getter and setter methods for all fields (id, order, product, quantity, price).

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderDetails getOrder() {
        return order;
    }

    public void setOrder(OrderDetails order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

