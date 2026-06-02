package com.codewithmosh.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class CartProductDto {
    private Long id;
    private String name ;
    private BigDecimal price;
}
