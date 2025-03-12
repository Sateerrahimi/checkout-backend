package com.example.checkoutbackend.service;

import com.example.checkoutbackend.model.CartItem;
import com.example.checkoutbackend.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

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
        cartItemRepository.deleteById(id);
    }
}
