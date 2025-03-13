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
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    private ObjectMapper objectMapper = new ObjectMapper();

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
               .andExpect(jsonPath("$.size()").value(2))
               .andExpect(jsonPath("$[0].name").value("Shirt"))
               .andExpect(jsonPath("$[1].name").value("Pants"));
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
               .andExpect(jsonPath("$.name").value("Shoes"))
               .andExpect(jsonPath("$.price").value(50.0))
               .andExpect(jsonPath("$.quantity").value(1));
    }

    @Test
    void testRemoveCartItem() throws Exception {
        doNothing().when(cartService).removeCartItem(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/cart/1")
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }
}
