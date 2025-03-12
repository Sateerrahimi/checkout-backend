package com.online.checkoutbackend;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.online.checkoutbackend.controller.CartController;
import com.online.checkoutbackend.model.CartItem;
import com.online.checkoutbackend.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc; // Allows us to send HTTP requests in tests

    @MockBean
    private CartService cartService; // Mock the CartService

    private ObjectMapper objectMapper = new ObjectMapper(); // For converting objects to JSON

    @Test
    void testGetCartItems() throws Exception {
        // Given: A cart with two items
        List<CartItem> cartItems = Arrays.asList(
                new CartItem("Shirt", 20.0, 2),
                new CartItem("Pants", 40.0, 1)
        );

        when(cartService.getAllCartItems()).thenReturn(cartItems);

        // When: Making a GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart")
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk()) // Then: Status 200
               .andExpect(jsonPath("$.size()").value(2)) // Should return 2 items
               .andExpect(jsonPath("$[0].name").value("Shirt")) // First item is "Shirt"
               .andExpect(jsonPath("$[1].name").value("Pants")); // Second item is "Pants"
    }

    @Test
    void testAddCartItem() throws Exception {
        // Given: A new cart item
        CartItem cartItem = new CartItem("Shoes", 50.0, 1);

        when(cartService.addCartItem(any(CartItem.class))).thenReturn(cartItem);

        // When: Making a POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(cartItem)))
               .andExpect(status().isOk()) // Then: Status 200
               .andExpect(jsonPath("$.name").value("Shoes")) // The item name should be "Shoes"
               .andExpect(jsonPath("$.price").value(50.0)) // The item price should be 50.0
               .andExpect(jsonPath("$.quantity").value(1)); // The quantity should be 1
    }

    @Test
    void testRemoveCartItem() throws Exception {
        // Given: A cart item with ID 1
        doNothing().when(cartService).removeCartItem(1L);

        // When: Making a DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/cart/1")
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk()); // Then: Status 200
    }
}
