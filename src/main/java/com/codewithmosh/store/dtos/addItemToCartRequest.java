package com.codewithmosh.store.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class addItemToCartRequest {
    @NotBlank
    private Long productId;
}
