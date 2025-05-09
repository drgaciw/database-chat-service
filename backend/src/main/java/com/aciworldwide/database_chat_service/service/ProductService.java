package com.aciworldwide.database_chat_service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aciworldwide.database_chat_service.model.Product;

public interface ProductService {

    /**
     * Create a new product
     */
    Product createProduct(Product product);

    /**
     * Update an existing product
     */
    Product updateProduct(String id, Product product);

    /**
     * Get a product by ID
     */
    Optional<Product> getProductById(String id);

    /**
     * Get a product by product code
     */
    Optional<Product> getProductByCode(String productCode);

    /**
     * Get all active products
     */
    List<Product> getAllActiveProducts();

    /**
     * Get all products with pagination
     */
    Page<Product> getAllProducts(Pageable pageable);

    /**
     * Search products by name
     */
    List<Product> searchProductsByName(String name);

    /**
     * Search products by description
     */
    List<Product> searchProductsByDescription(String description);

    /**
     * Get products by category
     */
    List<Product> getProductsByCategory(String category);

    /**
     * Get products by family
     */
    List<Product> getProductsByFamily(String family);

    /**
     * Get products by price range
     */
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Archive a product
     */
    Product archiveProduct(String id);

    /**
     * Delete a product (soft delete)
     */
    void deleteProduct(String id);

    /**
     * Activate a product
     */
    Product activateProduct(String id);

    /**
     * Deactivate a product
     */
    Product deactivateProduct(String id);

    /**
     * Get products by multiple categories
     */
    List<Product> getProductsByCategories(List<String> categories);

    /**
     * Get products by multiple product lines
     */
    List<Product> getProductsByProductLines(List<String> productLines);

    /**
     * Get products by multiple families
     */
    List<Product> getProductsByFamilies(List<String> families);

    /**
     * Get products by multiple types
     */
    List<Product> getProductsByTypes(List<String> types);

    /**
     * Get products with price greater than
     */
    List<Product> getProductsWithPriceGreaterThan(BigDecimal price);

    /**
     * Get products with price less than
     */
    List<Product> getProductsWithPriceLessThan(BigDecimal price);

    /**
     * Get products by multiple criteria
     */
    List<Product> getProductsByMultipleCriteria(String family, String type, boolean isActive);

    /**
     * Save all products
     */
    List<Product> saveAll(List<Product> products);

    /**
     * Find all products
     */
    List<Product> findAll();

    /**
     * Delete a product by ID
     */
    void deleteById(String id);

    /**
     * Update a product
     */
    Product update(Product product);

    /**
     * Find products by product line
     */
    List<Product> findByProductLine(String productLine);

    /**
     * Find products by product family
     */
    List<Product> findByProductFamily(String family);

    /**
     * Find products by product type
     */
    List<Product> findByProductType(String type);

    /**
     * Find active products
     */
    List<Product> findActiveProducts();

    /**
     * Find archived products
     */
    List<Product> findArchivedProducts();

    /**
     * Search products by name
     */
    List<Product> searchByName(String name);

    /**
     * Search products by description
     */
    List<Product> searchByDescription(String description);

    /**
     * Find products by price greater than
     */
    List<Product> findByPriceGreaterThan(BigDecimal price);

    /**
     * Find products by price less than
     */
    List<Product> findByPriceLessThan(BigDecimal price);

    /**
     * Find products by price between
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Find products by multiple categories
     */
    List<Product> findByMultipleCategories(List<String> categories);

    /**
     * Find products by multiple product lines
     */
    List<Product> findByMultipleProductLines(List<String> productLines);

    /**
     * Find products by multiple families
     */
    List<Product> findByMultipleFamilies(List<String> families);

    /**
     * Find products by multiple types
     */
    List<Product> findByMultipleTypes(List<String> types);

    /**
     * Search products by multiple criteria
     */
    List<Product> searchByMultipleCriteria(String name, String description, String category,
                                          String productLine, String family, String type);
}