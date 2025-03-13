package com.online.checkoutbackend.controller;

import com.online.checkoutbackend.model.Product;
import com.online.checkoutbackend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;


import java.util.List;
import java.util.Optional;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Optional<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @PostMapping("/decrease-stock/{id}/{quantity}")
    public void decreaseStock(@PathVariable Long id, @PathVariable int quantity) {
        productService.decreaseStock(id, quantity);
    }
    // ✅ New API for increasing stock
    @PostMapping("/increase-stock/{id}/{quantity}")
    public void increaseStock(@PathVariable Long id, @PathVariable int quantity) {
        productService.increaseStock(id, quantity);
    }
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return productService.updateProduct(id, updatedProduct);
    }
    @DeleteMapping("/{id}")
    public void removeProduct(@PathVariable Long id) {
        productService.removeProduct(id);
    }
}
