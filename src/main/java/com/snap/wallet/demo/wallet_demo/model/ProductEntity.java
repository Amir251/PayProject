package com.snap.wallet.demo.wallet_demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "products")
@NoArgsConstructor
public class ProductEntity extends Auditable {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    @Version
    private Integer version;
}
