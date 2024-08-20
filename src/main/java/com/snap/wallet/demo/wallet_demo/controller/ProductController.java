package com.snap.wallet.demo.wallet_demo.controller;

import com.snap.wallet.demo.wallet_demo.domain.Response;
import com.snap.wallet.demo.wallet_demo.dto.ProductDto;
import com.snap.wallet.demo.wallet_demo.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
@Tag(name = "Product Management", description = "Endpoints for managing products and product-related operations.")
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Save a new product (Only ADMIN)", description = "This endpoint allows an admin to save a new product to the catalog.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product saved successfully."),
            @ApiResponse(responseCode = "403", description = "Access denied. The user does not have the required role."),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping("/saveProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> saveProduct(@Parameter(description = "The product details to be saved.")
                                                @RequestBody ProductDto productDto,
                                                HttpServletRequest request) {
        productService.saveProduct(productDto);
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Product Saved!", HttpStatus.OK));
    }

    @Operation(summary = "Update an existing product (Only ADMIN)", description = "This endpoint allows an admin to update the details of an existing product. You must first load product and copy JSON and after yoy can update it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully."),
            @ApiResponse(responseCode = "403", description = "Access denied. The user does not have the required role."),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PatchMapping("/updateProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> updateProduct(@RequestBody @Valid ProductDto productDto, HttpServletRequest request) {
        productService.updateProduct(productDto);
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Product Updated!", HttpStatus.OK));
    }

    @Operation(summary = "Retrieve all products", description = "This endpoint allows anyone to retrieve all products from the catalog.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products loaded successfully."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping("/findAllProducts")
    public ResponseEntity<Response> findAllProducts(HttpServletRequest request) {
        return ResponseEntity.ok(getResponse(request, Map.of("products", productService.findAllProducts()), "Product Loaded!", HttpStatus.OK));
    }

    @Operation(summary = "Buy a product", description = "This endpoint allows a user to purchase a product by specifying the product ID and quantity.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product purchased successfully."),
            @ApiResponse(responseCode = "403", description = "Access denied. The user does not have the required role."),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "404", description = "Product not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping("/buyProduct")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Response> buyProduct(@Parameter(description = "The ID of the product to be purchased.")
                                               @RequestParam Long productId,
                                               @Parameter(description = "The quantity of the product to be purchased.")
                                               @RequestParam Integer quantity,
                                               HttpServletRequest request) {
        productService.buyProduct(productId, quantity);
        return ResponseEntity.ok(getResponse(request, emptyMap(), "Buy Product Successful!", HttpStatus.OK));
    }

    private URI getUri() {
        return URI.create("");
    }
}
