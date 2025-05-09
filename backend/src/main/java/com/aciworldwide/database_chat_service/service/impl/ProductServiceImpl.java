package com.aciworldwide.database_chat_service.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aciworldwide.database_chat_service.model.Product;
import com.aciworldwide.database_chat_service.repository.ProductRepository;
import com.aciworldwide.database_chat_service.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(String id, Product product) {
        product.setId(id);
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public Optional<Product> getProductByCode(String productCode) {
        return productRepository.findByProductCode(productCode);
    }

    @Override
    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameLike(name);
    }

    @Override
    public List<Product> searchProductsByDescription(String description) {
        return productRepository.findByDescriptionLike(description);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByProductCategory(category);
    }

    @Override
    public List<Product> getProductsByFamily(String family) {
        return productRepository.findByFamily(family);
    }

    @Override
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByStandardPriceBetween(minPrice, maxPrice);
    }

    @Override
    @Transactional
    public Product archiveProduct(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setIsArchived("true");
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Product activateProduct(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setIsActive(true);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product deactivateProduct(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setIsActive(false);
        return productRepository.save(product);
    }

    @Override
    public List<Product> getProductsByCategories(List<String> categories) {
        return productRepository.findByProductCategoryIn(categories);
    }

    @Override
    public List<Product> getProductsByProductLines(List<String> productLines) {
        return productRepository.findByProductLineIn(productLines);
    }

    @Override
    public List<Product> getProductsByFamilies(List<String> families) {
        return productRepository.findByFamilyIn(families);
    }

    @Override
    public List<Product> getProductsByTypes(List<String> types) {
        return productRepository.findByTypeIn(types);
    }

    @Override
    public List<Product> getProductsWithPriceGreaterThan(BigDecimal price) {
        return productRepository.findByStandardPriceGreaterThan(price);
    }

    @Override
    public List<Product> getProductsWithPriceLessThan(BigDecimal price) {
        return productRepository.findByStandardPriceLessThan(price);
    }

    @Override
    public List<Product> getProductsByMultipleCriteria(String family, String type, boolean isActive) {
        return productRepository.findByFamilyAndTypeAndIsActive(family, type, isActive);
    }

    @Override
    @Transactional
    public List<Product> saveAll(List<Product> products) {
        return productRepository.saveAll(products);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        productRepository.deleteById(id);
    }

    @Override
    public Product update(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> findByProductLine(String productLine) {
        return productRepository.findByProductLine(productLine);
    }

    @Override
    public List<Product> findByProductFamily(String family) {
        return productRepository.findByFamily(family);
    }

    @Override
    public List<Product> findByProductType(String type) {
        return productRepository.findByType(type);
    }

    @Override
    public List<Product> findActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    @Override
    public List<Product> findArchivedProducts() {
        return productRepository.findByIsArchived("true");
    }

    @Override
    public List<Product> searchByName(String name) {
        return productRepository.findByNameLike(name);
    }

    @Override
    public List<Product> searchByDescription(String description) {
        return productRepository.findByDescriptionLike(description);
    }

    @Override
    public List<Product> findByPriceGreaterThan(BigDecimal price) {
        return productRepository.findByStandardPriceGreaterThan(price);
    }

    @Override
    public List<Product> findByPriceLessThan(BigDecimal price) {
        return productRepository.findByStandardPriceLessThan(price);
    }

    @Override
    public List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByStandardPriceBetween(minPrice, maxPrice);
    }

    @Override
    public List<Product> findByMultipleCategories(List<String> categories) {
        return productRepository.findByProductCategories(categories);
    }

    @Override
    public List<Product> findByMultipleProductLines(List<String> productLines) {
        return productRepository.findByProductLines(productLines);
    }

    @Override
    public List<Product> findByMultipleFamilies(List<String> families) {
        return productRepository.findByFamilies(families);
    }

    @Override
    public List<Product> findByMultipleTypes(List<String> types) {
        return productRepository.findByTypes(types);
    }

    @Override
    public List<Product> searchByMultipleCriteria(String name, String description, String category,
                                                 String productLine, String family, String type) {
        return productRepository.findByMultipleCriteria(name, description, category, productLine, family, type);
    }
}