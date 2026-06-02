package com.codewithmosh.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
public class CartItemDto {
    private CartProductDto product;
    private int quantity;
    private BigDecimal totalPrice;

}
