package com.project.code.Controller;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to indicate that this is a REST controller, which handles HTTP requests and responses.
//    - Use `@RequestMapping("/inventory")` to set the base URL path for all methods in this controller. All endpoints related to inventory will be prefixed with `/inventory`.


// 2. Autowired Dependencies:
//    - Autowire necessary repositories and services:
//      - `ProductRepository` will be used to interact with product data (i.e., finding, updating products).
//      - `InventoryRepository` will handle CRUD operations related to the inventory.
//      - `ServiceClass` will help with the validation logic (e.g., validating product IDs and inventory data).

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ServiceClass serviceClass;

// 3. Define the `updateInventory` Method:
//    - This method handles HTTP PUT requests to update inventory for a product.
//    - It takes a `CombinedRequest` (containing `Product` and `Inventory`) in the request body.
//    - The product ID is validated, and if valid, the inventory is updated in the database.
//    - If the inventory exists, update it and return a success message. If not, return a message indicating no data available.
    @PutMapping
    public Map<String, String> updateInventory(@RequestBody CombinedRequest combinedRequest) {
        if (!serviceClass.validateProductId(combinedRequest.getProduct().getId())) {
            return Map.of("message", "ProductID does not exist");
        }
        else if (serviceClass.validateInventory(combinedRequest.getInventory())) {
            return Map.of("message", "No data available");
        }
        else {
            try {
                Inventory existingRecord = serviceClass.getInventoryId(combinedRequest.getInventory());
                existingRecord.setStockLevel(combinedRequest.getInventory().getStockLevel());
                inventoryRepository.save(existingRecord);
            }
            catch (Exception e) {
                return  Map.of("message", "Exception occurred trying to save inventory: " + e.getMessage());
            }
        }
        return Map.of("message", "Inventory saved successfully");
    }

// 4. Define the `saveInventory` Method:
//    - This method handles HTTP POST requests to save a new inventory entry.
//    - It accepts an `Inventory` object in the request body.
//    - It first validates whether the inventory already exists. If it exists, it returns a message stating so. If it doesn’t exist, it saves the inventory and returns a success message.
    @PostMapping
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {
        if (serviceClass.validateInventory(inventory)) {
            try {
                inventoryRepository.save(inventory);
            }
            catch (Exception e) {
                return  Map.of("message", "Exception occurred trying to save inventory: " + e.getMessage());
            }
            return  Map.of("message", "Data saved successfully");
        }
        else  {
            return Map.of("message", "Data already exists");
        }
    }

// 5. Define the `getAllProducts` Method:
//    - This method handles HTTP GET requests to retrieve products for a specific store.
//    - It uses the `storeId` as a path variable and fetches the list of products from the database for the given store.
//    - The products are returned in a `Map` with the key `"products"`.
    @GetMapping("/{storeId}")
    public Map<String, Object> getAllProducts(@PathVariable Long storeId) {
        List<Product> products = productRepository.findProductsByStoreId(storeId);
        return  Map.of("products", products);
    }

// 6. Define the `getProductName` Method:
//    - This method handles HTTP GET requests to filter products by category and name.
//    - If either the category or name is `"null"`, adjust the filtering logic accordingly.
//    - Return the filtered products in the response with the key `"product"`.
    @GetMapping("filter/{category}/{name}/{storeid}")
    public Map<String, Object> getProductName(@PathVariable String category, @PathVariable String name, @PathVariable Long storeId) {

        List<Product> products = null;
        if (category == null) {
            products = productRepository.findByNameLike(storeId, name);
        }
        else if (name == null) {
            products = productRepository.findByCategoryAndStoreId(storeId, category);
        }
        else {
            products = productRepository.findByNameAndCategory(storeId, category, name);
        }
        return  Map.of("product", products);
    }

// 7. Define the `searchProduct` Method:
//    - This method handles HTTP GET requests to search for products by name within a specific store.
//    - It uses `name` and `storeId` as parameters and searches for products that match the `name` in the specified store.
//    - The search results are returned in the response with the key `"product"`.
    @GetMapping("search/{name}/{storeId}")
    public Map<String,Object> searchProduct(@PathVariable String name, @PathVariable Long storeId) {
        return Map.of("product", productRepository.findByNameLike(storeId, name));
    }

// 8. Define the `removeProduct` Method:
//    - This method handles HTTP DELETE requests to delete a product by its ID.
//    - It first validates if the product exists. If it does, it deletes the product from the `ProductRepository` and also removes the related inventory entry from the `InventoryRepository`.
//    - Returns a success message with the key `"message"` indicating successful deletion.
    @DeleteMapping("/{id}")
    public Map<String,Object> removeProduct(@PathVariable Long id) {
        if (serviceClass.validateProductId(id)) {
            inventoryRepository.deleteByProductId(id);
            //productRepository.deleteById(id);
            return  Map.of("message", "Product removed successfully");
        }
        else  {
            return  Map.of("message", "Product not found");
        }
    }

// 9. Define the `validateQuantity` Method:
//    - This method handles HTTP GET requests to validate if a specified quantity of a product is available in stock for a given store.
//    - It checks the inventory for the product in the specified store and compares it to the requested quantity.
//    - If sufficient stock is available, return `true`; otherwise, return `false`.
    @GetMapping("validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(@PathVariable Long quantity, @PathVariable Long storeId, @PathVariable Long productId) {
        Inventory inventory = inventoryRepository.findByProductIdAndStoreId(productId, storeId);
        return (inventory != null && inventory.getStockLevel() >= quantity);
    }
}
