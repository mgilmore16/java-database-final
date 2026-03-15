package com.project.code.Service;

import com.project.code.Model.*;
import com.project.code.Repo.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private CustomerRepository customerRepository ;
    @Autowired
    private StoreRepository storeRepository ;
    @Autowired
    private OrderDetailsRepository orderDetailsRepository ;
    @Autowired
    private OrderItemRepository  orderItemRepository ;

// 1. **saveOrder Method**:
//    - Processes a customer's order, including saving the order details and associated items.
//    - Parameters: `PlaceOrderRequestDTO placeOrderRequest` (Request data for placing an order)
//    - Return Type: `void` (This method doesn't return anything, it just processes the order
    @Transactional
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) throws Exception {

// 2. **Retrieve or Create the Customer**:
//    - Check if the customer exists by their email using `findByEmail`.
//    - If the customer exists, use the existing customer; otherwise, create and save a new customer using `customerRepository.save()`.
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());

        // If null, create and save the customer
        if (customer == null) {
            customer = new Customer();
            customer.setEmail(placeOrderRequest.getCustomerEmail());
            customer.setName(placeOrderRequest.getCustomerName());
            customer.setPhone(placeOrderRequest.getCustomerPhone());
            customerRepository.save(customer);
        }

// 3. **Retrieve the Store**:
//    - Fetch the store by ID from `storeRepository`.
//    - If the store doesn't exist, throw an exception. Use `storeRepository.findById()`.
        Store store = storeRepository.findById(placeOrderRequest.getStoreId()).orElseThrow(() -> new EntityNotFoundException("Store not found"));

// 4. **Create OrderDetails**:
//    - Create a new `OrderDetails` object and set customer, store, total price, and the current timestamp.
//    - Set the order date using `java.time.LocalDateTime.now()` and save the order with `orderDetailsRepository.save()`.
        OrderDetails orderDetails = new OrderDetails(customer, store, placeOrderRequest.getTotalPrice(), LocalDateTime.now());
        orderDetailsRepository.save(orderDetails);

// 5. **Create and Save OrderItems**:
//    - For each product purchased, find the corresponding inventory, update stock levels, and save the changes using `inventoryRepository.save()`.
//    - Create and save `OrderItem` for each product and associate it with the `OrderDetails` using `orderItemRepository.save()`.
        for (PurchaseProductDTO productDto : placeOrderRequest.getPurchaseProduct()) {
            // Decrement stock level
            Inventory inventory = inventoryRepository.findByProductIdandStoreId(placeOrderRequest.getStoreId(), productDto.getId());
            if (inventory == null) { throw new EntityNotFoundException("Inventory not found"); }
            inventory.setStockLevel(inventory.getStockLevel()-productDto.getQuantity());
            inventoryRepository.save(inventory);
            // Create and save OrderItem for each product and attach to OrderDetails
            // Make sure the product exists and the total on the line item is correct
            Product product = productRepository.findById(productDto.getId()).orElseThrow(() -> new EntityNotFoundException("Product not found"));
            if (productDto.getTotal() != product.getPrice() *  productDto.getQuantity()) { throw new Exception("Total is Incorrect on line item.");}
            OrderItem orderItem = new OrderItem(orderDetails, product, productDto.getQuantity(), productDto.getTotal());
            orderItemRepository.save(orderItem);
        }
    }

   
}
