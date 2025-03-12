package com.example.checkoutbackend;

import com.example.checkoutbackend.controller.PaymentController;
import com.example.checkoutbackend.model.CartItem;
import com.example.checkoutbackend.service.PaymentService;
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

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PaymentController.class) // Load only PaymentController
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc; // Auto-injected by Spring Boot

    @MockBean
    private PaymentService paymentService; // Mocking PaymentService

    private final ObjectMapper objectMapper = new ObjectMapper(); // For JSON conversion

    @Test
    void testCreateCheckoutSession_Success() throws Exception {
        // Given: Mock Cart Items
        List<CartItem> cartItems = Arrays.asList(
                new CartItem("Shirt", 20.0, 2),
                new CartItem("Pants", 40.0, 1)
        );

        // Given: Mock Stripe Session URL
        String mockSessionUrl = "https://checkout.stripe.com/test_session";

        // Ensure that paymentService returns a valid response
        when(paymentService.createCheckoutSession(anyList())).thenReturn(mockSessionUrl);

        // When: Sending a POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/payment/create-checkout-session")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(cartItems)))
               // Then: Expect a 200 OK status
               .andExpect(status().isOk())
               // Then: Expect a JSON response containing the session URL
               .andExpect(jsonPath("$.url").value(mockSessionUrl));
    }
/*
    @Test
    void testCreateCheckoutSession_Failure() throws Exception {
        // Given: Mock Cart Items
        List<CartItem> cartItems = Arrays.asList(
                new CartItem("Shirt", 20.0, 2),
                new CartItem("Pants", 40.0, 1)
        );

        // Given: Mock Exception from PaymentService
        doThrow(new RuntimeException("Stripe API error"))
                .when(paymentService)
                .createCheckoutSession(anyList()); // Ensure any List is matched

        // When: Sending a POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/payment/create-checkout-session")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(cartItems)))
               // Then: Expect 500 Internal Server Error
               .andExpect(status().isInternalServerError());
    }

 */
}
