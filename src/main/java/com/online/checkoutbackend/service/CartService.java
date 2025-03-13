package com.online.checkoutbackend.service;

import com.online.checkoutbackend.model.CartItem;
import com.online.checkoutbackend.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAll();
    }

    public CartItem addCartItem(CartItem item) {
        return cartItemRepository.save(item);
    }

    public void removeCartItem(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cart item cannot be null.");
        }
        Optional<CartItem> cartItem = cartItemRepository.findById(id);
        if (cartItem.isEmpty()) {
            throw new IllegalArgumentException("Cart item with ID " + id + " does not exist.");
        }
        cartItemRepository.deleteById(id);
    }
}
