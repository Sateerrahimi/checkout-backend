package com.example.checkoutbackend.integration;

import com.example.checkoutbackend.model.CartItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Ensures DB reset after each test
public class CartIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<CartItem> cartItems;

    @BeforeEach
    void setup() {
        cartItems = Arrays.asList(
                new CartItem("Shirt", 20.0, 2),
                new CartItem("Pants", 40.0, 1)
        );
    }

    @Test
    void testAddItemsToCart() throws Exception {
        for (CartItem item : cartItems) {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/cart")
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(objectMapper.writeValueAsString(item)))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.name").value(item.getName()))
                   .andExpect(jsonPath("$.price").value(item.getPrice()))
                   .andExpect(jsonPath("$.quantity").value(item.getQuantity()));
        }
    }

    @Test
    void testGetCartItems() throws Exception {
        // Given: Add items first
        for (CartItem item : cartItems) {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/cart")
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(objectMapper.writeValueAsString(item)))
                   .andExpect(status().isOk());
        }

        // When: Fetching cart items
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart")
                                              .contentType(MediaType.APPLICATION_JSON))
               // Then: Expect success
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()").value(cartItems.size())) // Should return 2 items
               .andExpect(jsonPath("$[0].name").value("Shirt"))
               .andExpect(jsonPath("$[1].name").value("Pants"));
    }

    @Test
    void testRemoveCartItem() throws Exception {
        // Given: Add an item first
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/cart")
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .content(objectMapper.writeValueAsString(cartItems.get(0))))
                                 .andExpect(status().isOk())
                                 .andReturn().getResponse().getContentAsString();

        // Extract the generated ID from response
        CartItem addedItem = objectMapper.readValue(response, CartItem.class);
        Long itemId = addedItem.getId();

        // When: Sending DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/cart/" + itemId))
               .andExpect(status().isOk());

        // Then: Verify item is removed by checking GET
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart")
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()").value(0)); // Should be empty
    }
}
