package com.online.checkoutbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.online.checkoutbackend.model.Product;
import com.online.checkoutbackend.repository.ProductRepository;
import com.online.checkoutbackend.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() {
        Product product1 = new Product("T-Shirt", 20.0, 10);
        Product product2 = new Product("Pants", 40.0, 5);
        List<Product> mockProducts = Arrays.asList(product1, product2);

        when(productRepository.findAll()).thenReturn(mockProducts);

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        assertEquals("T-Shirt", products.get(0).getName());
        assertEquals(20.0, products.get(0).getPrice());
        assertEquals(10, products.get(0).getStock());
        assertEquals("Pants", products.get(1).getName());
        assertEquals(40.0, products.get(1).getPrice());
        assertEquals(5, products.get(1).getStock());

    }

    @Test
    void testSaveProduct() {
        Product product = new Product("Shoes", 50.0, 8);

        when(productRepository.save(product)).thenReturn(product);

        Product savedProduct = productService.saveProduct(product);

        assertNotNull(savedProduct);
        assertEquals("Shoes", savedProduct.getName());
        assertEquals(50.0, savedProduct.getPrice());
        assertEquals(8, savedProduct.getStock());
    }

    @Test
    void testUpdateProduct() {
        Product existingProduct = new Product("Jacket", 100.0, 3);
        existingProduct.setId(1L);

        Product updatedProduct = new Product("Jacket", 120.0, 2);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(1L, updatedProduct);

        assertEquals("Jacket", result.getName());
        assertEquals(120.0, result.getPrice());
        assertEquals(2, result.getStock());
    }
    @Test
    void testIncreaseStock_Success() {
        // Given: A product with stock of 5
        Product product = new Product("Laptop", 1500.0, 5);
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When: We increase the stock by 3
        productService.increaseStock(1L, 3);

        // Then: The stock should be 8
        assertEquals(8, product.getStock());
        verify(productRepository, times(1)).save(product);
    }
    @Test
    void testDecreaseStock_Success() {
        // Given: A product with stock of 10
        Product product = new Product("T-Shirt", 20.0, 10);
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When: We decrease the stock by 3
        productService.decreaseStock(1L, 3);

        // Then: The stock should be 7
        assertEquals(7, product.getStock());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testDecreaseStock_NotEnoughStock() {
        // Given: A product with stock of 2
        Product product = new Product("T-Shirt", 20.0, 2);
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When: We try to decrease stock by 5 (not enough stock)
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.decreaseStock(1L, 5);
        });

        // Then: An error should be thrown with BAD_REQUEST status
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Not enough stock available"));
    }

}
