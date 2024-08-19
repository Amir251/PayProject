package com.snap.wallet.demo.wallet_demo.dto;

import java.math.BigDecimal;

public record ProductDto(Long id,String name, String description, BigDecimal price,Integer stock) {
}
