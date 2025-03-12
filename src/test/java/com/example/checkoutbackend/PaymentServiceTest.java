package com.example.checkoutbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.checkoutbackend.model.CartItem;
import com.example.checkoutbackend.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        System.setProperty("stripe.secret.key", "sk_test_123456789"); // Mock Stripe API Key
    }

    @Test
    void testCreateCheckoutSession() throws StripeException {
        List<CartItem> cartItems = Arrays.asList(
                new CartItem("T-Shirt", 20.0, 2),
                new CartItem("Pants", 40.0, 1)
        );

        // Mock Stripe's Session.create() method
        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {
            Session mockSession = mock(Session.class);
            when(mockSession.getUrl()).thenReturn("https://checkout.stripe.com/test-session");
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(mockSession);

            // When: Creating a checkout session
            String sessionUrl = paymentService.createCheckoutSession(cartItems);

            // Then: It should return a valid URL
            assertNotNull(sessionUrl);
            assertTrue(sessionUrl.startsWith("https://checkout.stripe.com"));
        }
    }
}
