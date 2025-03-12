package com.example.checkoutbackend.service;
import com.example.checkoutbackend.model.CartItem;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public String createCheckoutSession(List<CartItem> cartItems) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                                                                       .setMode(SessionCreateParams.Mode.PAYMENT)
                                                                       .setSuccessUrl("http://localhost:3000/success")
                                                                       .setCancelUrl("http://localhost:3000/cancel");

        for (CartItem item : cartItems) {
            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                                                .setPriceData(
                                                        SessionCreateParams.LineItem.PriceData.builder()
                                                                                              .setCurrency("usd")
                                                                                              .setUnitAmount((long) (item.getPrice() * 100))
                                                                                              .setProductData(
                                                                                                      SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                                                                                                        .setName(item.getName())
                                                                                                                                                        .build()
                                                                                              )
                                                                                              .build()
                                                )
                                                .setQuantity((long) item.getQuantity())
                                                .build()
            );
        }

        Session session = Session.create(paramsBuilder.build());
        return session.getUrl();
    }
}
