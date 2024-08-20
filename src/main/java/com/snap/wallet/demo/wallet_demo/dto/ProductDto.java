package com.snap.wallet.demo.wallet_demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Product Data Transfer Object")
public record ProductDto(Long id,String name, String description, BigDecimal price,Integer stock) {
}
