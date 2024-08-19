package com.snap.wallet.demo.wallet_demo.service;

import com.snap.wallet.demo.wallet_demo.dto.ProductDto;

import java.util.List;

public interface ProductService {
    void saveProduct(ProductDto dto);

    void updateProduct(ProductDto dto);

    List<ProductDto> findAllProducts();

    void buyProduct(Long productId,int quantity);
}
