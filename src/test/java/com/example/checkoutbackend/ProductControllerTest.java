package com.example.checkoutbackend;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.checkoutbackend.controller.ProductController;
import com.example.checkoutbackend.model.Product;
import com.example.checkoutbackend.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(ProductController.class) //  Load ONLY ProductController
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Fake HTTP client for testing API

    @MockBean // Replace the actual ProductService with a mock version
    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetAllProducts() throws Exception {
        List<Product> products = Arrays.asList(
                new Product("Shirt", 20.0, 10),
                new Product("Pants", 40.0, 5)
        );

        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()").value(2))
               .andExpect(jsonPath("$[0].name").value("Shirt"))
               .andExpect(jsonPath("$[1].name").value("Pants"));
    }

    @Test
    void testAddProduct() throws Exception {
        Product product = new Product("Shoes", 50.0, 3);

        when(productService.saveProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(product)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Shoes"))
               .andExpect(jsonPath("$.price").value(50.0))
               .andExpect(jsonPath("$.stock").value(3));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product updatedProduct = new Product("Shirt", 25.0, 8);

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/1")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(updatedProduct)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Shirt"))
               .andExpect(jsonPath("$.price").value(25.0))
               .andExpect(jsonPath("$.stock").value(8));
    }

    @Test
    void testDecreaseStock() throws Exception {
        doNothing().when(productService).decreaseStock(1L, 2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products/decrease-stock/1/2")
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }
}
