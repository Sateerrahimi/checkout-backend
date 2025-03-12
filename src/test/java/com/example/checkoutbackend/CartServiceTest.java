package com.example.checkoutbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.checkoutbackend.model.CartItem;
import com.example.checkoutbackend.repository.CartItemRepository;
import com.example.checkoutbackend.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCartItems() {
        CartItem item1 = new CartItem("Shirt", 20.0, 2);
        CartItem item2 = new CartItem("Pants", 40.0, 1);
        List<CartItem> mockCartItems = Arrays.asList(item1, item2);

        when(cartItemRepository.findAll()).thenReturn(mockCartItems);

        List<CartItem> cartItems = cartService.getAllCartItems();

        assertEquals(2, cartItems.size());
        assertEquals("Shirt", cartItems.get(0).getName());
        assertEquals("Pants", cartItems.get(1).getName());
    }

    @Test
    void testAddCartItem() {
        CartItem cartItem = new CartItem("Shoes", 50.0, 1);

        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);

        CartItem savedItem = cartService.addCartItem(cartItem);

        assertNotNull(savedItem);
        assertEquals("Shoes", savedItem.getName());
        assertEquals(50.0, savedItem.getPrice());
        assertEquals(1, savedItem.getQuantity());
    }

    @Test
    void testRemoveCartItem() {
        CartItem cartItem = new CartItem("Shirt", 20.0, 2);
        cartItem.setId(1L);

        doNothing().when(cartItemRepository).deleteById(1L);

        cartService.removeCartItem(1L);

        verify(cartItemRepository, times(1)).deleteById(1L);
    }
}
