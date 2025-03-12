package com.online.checkoutbackend.integration;

import com.online.checkoutbackend.model.CartItem;
import com.online.checkoutbackend.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;


import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentService paymentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static WireMockServer wireMockServer;

    private List<CartItem> cartItems;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8081));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8081);
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setup() {
        cartItems = Arrays.asList(
                new CartItem("Shirt", 20.0, 2),
                new CartItem("Pants", 40.0, 1)
        );
    }

    @Test
    void testCreateCheckoutSession_Success() throws Exception {
        // When: Sending a POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/payment/create-checkout-session")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(cartItems)))
               // Then: Expect a 200 OK status
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.url").isNotEmpty());
    }
}
