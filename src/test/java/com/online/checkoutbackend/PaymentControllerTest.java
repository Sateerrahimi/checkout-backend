package com.online.checkoutbackend;

import com.online.checkoutbackend.controller.PaymentController;
import com.online.checkoutbackend.model.CartItem;
import com.online.checkoutbackend.service.PaymentService;
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
@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCreateCheckoutSession_Success() throws Exception {
        List<CartItem> cartItems = Arrays.asList(
                new CartItem("Shirt", 20.0, 2),
                new CartItem("Pants", 40.0, 1)
        );

        String mockSessionUrl = "https://checkout.stripe.com/test_session";

        when(paymentService.createCheckoutSession(anyList())).thenReturn(mockSessionUrl);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/payment/create-checkout-session")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(cartItems)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.url").value(mockSessionUrl));
    }

}
