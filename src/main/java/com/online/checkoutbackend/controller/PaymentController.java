package com.online.checkoutbackend.controller;
import com.online.checkoutbackend.model.CartItem;
import com.online.checkoutbackend.service.PaymentService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-checkout-session")
    public Map<String, String> createCheckoutSession(@RequestBody List<CartItem> cartItems) {
        try {
            String sessionUrl = paymentService.createCheckoutSession(cartItems);
            return Map.of("url", sessionUrl);
        } catch (StripeException e) {
            return Map.of("error", e.getMessage());
        }
    }
}
