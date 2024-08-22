package com.snap.wallet.demo.wallet_demo.repository;

import com.snap.wallet.demo.wallet_demo.model.ProductEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindByIdWithLock() {
        // Given
        ProductEntity product = ProductEntity.builder()
                .name("Test Product")
                .description("This is a test product")
                .price(new BigDecimal("10000"))
                .stock(10)
                .build();
        product = productRepository.save(product);

        // When
        Optional<ProductEntity> foundProduct = productRepository.findById(product.getId());

        // Then
        assertTrue(foundProduct.isPresent(), "Product should be found");
        assertEquals(product.getId(), foundProduct.get().getId(), "Product ID should match");
    }
}