package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CartItemDto;
import com.codewithmosh.store.dtos.CartProductDto;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.entities.CartItem;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.ProductNotFoundException;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {

    private CartRepository cartRepository;
    private ProductRepository productRepository;

    public CartDto createCart(){
        Cart cart = new Cart();
        cart = cartRepository.save(cart);

        CartDto cartDto = new CartDto(
                cart.getId(),
                new ArrayList<>(),
                BigDecimal.ZERO
        );
        return cartDto ;
    }
    
    public CartItemDto addToCart(UUID cartId , Long productId){
        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new ProductNotFoundException();
        }
        var cartItem = cart.getItem(product.getId());

        cart.addItem(product) ;
        cartRepository.save(cart);
        var cartItemDto = new CartItemDto(
                new CartProductDto(
                        cartItem.getProduct().getId(),
                        cartItem.getProduct().getName(),
                        cartItem.getProduct().getPrice()
                ),
                cartItem.getQuantity(),
                cartItem.getTotalPrice()
        );
        return  cartItemDto;
    }

    public CartDto getCart(UUID cartId){


        var cart =  cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        List<CartItemDto> items = cart.getItems()
                .stream()
                .map(item -> new CartItemDto(
                        new CartProductDto(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getProduct().getPrice()
                        ),
                        item.getQuantity(),
                        item.getTotalPrice()
                ))
                .toList();


        BigDecimal totalPrice = cart.getItems()
                .stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        CartDto cartDto = new CartDto(
                cart.getId(),
                items,
                totalPrice
        );
        return cartDto;
    }

    public CartItemDto updateCart(UUID cartId , Long productId , Integer quantity ){
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
           throw new ProductNotFoundException();
        }

        var cartItem = cart.getItem(product.getId());
        if (cartItem == null) {
            throw new ProductNotFoundException();

        }
        cartItem.setQuantity(quantity);
        cartRepository.save(cart);
        var cartItemDto = new CartItemDto(
                new CartProductDto(
                        cartItem.getProduct().getId(),
                        cartItem.getProduct().getName(),
                        cartItem.getProduct().getPrice()
                ),
                cartItem.getQuantity(),
                cartItem.getTotalPrice()
        );
        return  cartItemDto;
    }

    public void removeCart(UUID cartId , Long productId ){
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
         throw new CartNotFoundException();
        }
        cart.removeItem(productId);
        cartRepository.save(cart);
    }

    public void  clearCart(UUID cartId ){
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {

            throw new CartNotFoundException();
        }
        cart.clear();
        cartRepository.save(cart);
    }
}
