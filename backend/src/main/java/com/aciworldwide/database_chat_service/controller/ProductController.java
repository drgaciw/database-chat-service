package com.aciworldwide.database_chat_service.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aciworldwide.database_chat_service.model.Product;
import com.aciworldwide.database_chat_service.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "Product ID") @PathVariable String id,
            @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "Product ID") @PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{productCode}")
    @Operation(summary = "Get a product by product code")
    public ResponseEntity<Product> getProductByCode(
            @Parameter(description = "Product Code") @PathVariable String productCode) {
        return productService.getProductByCode(productCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active products")
    public ResponseEntity<List<Product>> getAllActiveProducts() {
        return ResponseEntity.ok(productService.getAllActiveProducts());
    }

    @GetMapping
    @Operation(summary = "Get all products with pagination")
    public ResponseEntity<Page<Product>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/search/name")
    @Operation(summary = "Search products by name")
    public ResponseEntity<List<Product>> searchProductsByName(
            @Parameter(description = "Product name to search") @RequestParam String name) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    @GetMapping("/search/description")
    @Operation(summary = "Search products by description")
    public ResponseEntity<List<Product>> searchProductsByDescription(
            @Parameter(description = "Product description to search") @RequestParam String description) {
        return ResponseEntity.ok(productService.searchProductsByDescription(description));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @Parameter(description = "Product category") @PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @GetMapping("/family/{family}")
    @Operation(summary = "Get products by family")
    public ResponseEntity<List<Product>> getProductsByFamily(
            @Parameter(description = "Product family") @PathVariable String family) {
        return ResponseEntity.ok(productService.getProductsByFamily(family));
    }

    @GetMapping("/price-range")
    @Operation(summary = "Get products by price range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @Parameter(description = "Minimum price") @RequestParam BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(productService.getProductsByPriceRange(minPrice, maxPrice));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive a product")
    public ResponseEntity<Product> archiveProduct(
            @Parameter(description = "Product ID") @PathVariable String id) {
        return ResponseEntity.ok(productService.archiveProduct(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a product")
    public ResponseEntity<Product> activateProduct(
            @Parameter(description = "Product ID") @PathVariable String id) {
        return ResponseEntity.ok(productService.activateProduct(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a product")
    public ResponseEntity<Product> deactivateProduct(
            @Parameter(description = "Product ID") @PathVariable String id) {
        return ResponseEntity.ok(productService.deactivateProduct(id));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get products by multiple categories")
    public ResponseEntity<List<Product>> getProductsByCategories(
            @Parameter(description = "List of categories") @RequestParam List<String> categories) {
        return ResponseEntity.ok(productService.getProductsByCategories(categories));
    }

    @GetMapping("/product-lines")
    @Operation(summary = "Get products by multiple product lines")
    public ResponseEntity<List<Product>> getProductsByProductLines(
            @Parameter(description = "List of product lines") @RequestParam List<String> productLines) {
        return ResponseEntity.ok(productService.getProductsByProductLines(productLines));
    }

    @GetMapping("/families")
    @Operation(summary = "Get products by multiple families")
    public ResponseEntity<List<Product>> getProductsByFamilies(
            @Parameter(description = "List of families") @RequestParam List<String> families) {
        return ResponseEntity.ok(productService.getProductsByFamilies(families));
    }

    @GetMapping("/types")
    @Operation(summary = "Get products by multiple types")
    public ResponseEntity<List<Product>> getProductsByTypes(
            @Parameter(description = "List of types") @RequestParam List<String> types) {
        return ResponseEntity.ok(productService.getProductsByTypes(types));
    }

    @GetMapping("/price/greater-than")
    @Operation(summary = "Get products with price greater than")
    public ResponseEntity<List<Product>> getProductsWithPriceGreaterThan(
            @Parameter(description = "Price threshold") @RequestParam BigDecimal price) {
        return ResponseEntity.ok(productService.getProductsWithPriceGreaterThan(price));
    }

    @GetMapping("/price/less-than")
    @Operation(summary = "Get products with price less than")
    public ResponseEntity<List<Product>> getProductsWithPriceLessThan(
            @Parameter(description = "Price threshold") @RequestParam BigDecimal price) {
        return ResponseEntity.ok(productService.getProductsWithPriceLessThan(price));
    }

    @GetMapping("/search/criteria")
    @Operation(summary = "Get products by multiple criteria")
    public ResponseEntity<List<Product>> getProductsByMultipleCriteria(
            @Parameter(description = "Product family") @RequestParam String family,
            @Parameter(description = "Product type") @RequestParam String type,
            @Parameter(description = "Active status") @RequestParam boolean isActive) {
        return ResponseEntity.ok(productService.getProductsByMultipleCriteria(family, type, isActive));
    }
} 