package com.online.checkoutbackend.service;

import com.online.checkoutbackend.model.Product;
import com.online.checkoutbackend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return Optional.ofNullable(productRepository.findById(id)
                                                    .orElseThrow(() -> new RuntimeException("Product not found")));
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }


    public void decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (product.getStock() >= quantity) {
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock available");
        }
    }

    public void increaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = productRepository.findById(id)
                                                   .orElseThrow(() -> new RuntimeException("Product not found"));

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStock(updatedProduct.getStock());

        return productRepository.save(existingProduct);
    }
    public void removeProduct(Long id) {
        productRepository.deleteById(id);
    }

}
