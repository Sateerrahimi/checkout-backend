package com.example.checkoutbackend.controller;

import com.example.checkoutbackend.model.CartItem;
import com.example.checkoutbackend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public List<CartItem> getCartItems() {
        return cartService.getAllCartItems();
    }

    @PostMapping
    public CartItem addCartItem(@RequestBody CartItem item) {
        return cartService.addCartItem(item);
    }

    @DeleteMapping("/{id}")
    public void removeCartItem(@PathVariable Long id) {
        cartService.removeCartItem(id);
    }
}


