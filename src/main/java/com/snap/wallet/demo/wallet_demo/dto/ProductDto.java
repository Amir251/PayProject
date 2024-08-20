package com.snap.wallet.demo.wallet_demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Product Data Transfer Object")
public record ProductDto(Long id, @NotNull(message = "Name cannot be null") String name,
                         @NotNull(message = "description cannot be null") String description,
                         @NotNull(message = "price cannot be null") BigDecimal price,
                         @NotNull(message = "stock cannot be null") Integer stock) {
}
