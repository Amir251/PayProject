package com.snap.wallet.demo.wallet_demo.controller;

import com.snap.wallet.demo.wallet_demo.domain.Response;
import com.snap.wallet.demo.wallet_demo.dto.ProductDto;
import com.snap.wallet.demo.wallet_demo.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.snap.wallet.demo.wallet_demo.util.RequestUtil.getResponse;
import static java.util.Collections.emptyMap;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/saveProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> saveProduct(@RequestBody ProductDto productDto, HttpServletRequest request) {
        productService.saveProduct(productDto);
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Product Saved!", HttpStatus.OK));
    }

    @PatchMapping("/updateProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> updateProduct(@RequestBody ProductDto productDto, HttpServletRequest request) {
        productService.updateProduct(productDto);
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Product Updated!", HttpStatus.OK));
    }

    @PostMapping("/findAllProducts")
    public ResponseEntity<Response> findAllProducts(HttpServletRequest request) {
        return ResponseEntity.ok(getResponse(request, Map.of("products", productService.findAllProducts()), "Product Loaded!", HttpStatus.OK));
    }

    @PostMapping("/buyProduct")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Response> buyProduct(@RequestParam Long productId, @RequestParam Integer quantity, HttpServletRequest request){
        productService.buyProduct(productId, quantity);
        return ResponseEntity.ok(getResponse(request, emptyMap(), "Buy Product Successful!", HttpStatus.OK));
    }

    private URI getUri() {
        return URI.create("");
    }
}
