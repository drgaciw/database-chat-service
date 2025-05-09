package com.aciworldwide.database_chat_service.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.aciworldwide.database_chat_service.model.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    // Find by product code
    Optional<Product> findByProductCode(String productCode);
    
    // Find by external ID
    Optional<Product> findByExternalId(String externalId);
    
    // Find by custom external ID
    Optional<Product> findByCustomExternalId(String customExternalId);
    
    // Find active products
    List<Product> findByIsActiveTrue();
    
    // Find products by family
    List<Product> findByFamily(String family);
    
    // Find products by type
    List<Product> findByType(String type);
    
    // Find products by product class
    List<Product> findByProductClass(String productClass);
    
    // Find products by category
    List<Product> findByProductCategory(String productCategory);
    
    // Find products by subcategory
    List<Product> findByProductSubcategory(String productSubcategory);
    
    // Find products by product line
    List<Product> findByProductLine(String productLine);
    
    // Find products with price range
    List<Product> findByStandardPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Find products by SKU
    Optional<Product> findByStockKeepingUnit(String sku);
    
    // Find products by custom SKU
    Optional<Product> findByStockKeepingUnitCustom(String customSku);
    
    // Find products by provisioning type
    List<Product> findByProvisioningType(String provisioningType);
    
    // Find products by record type
    List<Product> findByRecordTypeId(String recordTypeId);
    
    // Find products by billing policy
    List<Product> findByBillingPolicyId(String billingPolicyId);
    
    // Find products by currency
    List<Product> findByCurrencyIsoCode(String currencyIsoCode);
    
    // Find products by schedule type
    List<Product> findByQuantityScheduleType(String scheduleType);
    
    // Find products by revenue schedule type
    List<Product> findByRevenueScheduleType(String revenueScheduleType);
    
    // Find products by archived status
    List<Product> findByIsArchived(String isArchived);
    
    // Find products by deleted status
    List<Product> findByIsDeleted(boolean isDeleted);
    
    // Custom query to find products with partial name match
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Product> findByNameLike(String name);
    
    // Custom query to find products with partial description match
    @Query("{ 'description': { $regex: ?0, $options: 'i' } }")
    List<Product> findByDescriptionLike(String description);
    
    // Custom query to find products with multiple criteria
    @Query("{ 'family': ?0, 'type': ?1, 'isActive': ?2 }")
    List<Product> findByFamilyAndTypeAndActive(String family, String type, boolean isActive);
    
    // Custom query to find products with price greater than
    @Query("{ 'standardPrice': { $gt: ?0 } }")
    List<Product> findByStandardPriceGreaterThan(BigDecimal price);
    
    // Custom query to find products with price less than
    @Query("{ 'standardPrice': { $lt: ?0 } }")
    List<Product> findByStandardPriceLessThan(BigDecimal price);
    
    // Custom query to find products by multiple categories
    @Query("{ 'productCategory': { $in: ?0 } }")
    List<Product> findByProductCategories(List<String> categories);
    
    // Custom query to find products by multiple product lines
    @Query("{ 'productLine': { $in: ?0 } }")
    List<Product> findByProductLines(List<String> productLines);
    
    // Custom query to find products by multiple families
    @Query("{ 'family': { $in: ?0 } }")
    List<Product> findByFamilies(List<String> families);
    
    // Custom query to find products by multiple types
    @Query("{ 'type': { $in: ?0 } }")
    List<Product> findByTypes(List<String> types);
    
    // Custom query to find products by multiple record types
    @Query("{ 'recordTypeId': { $in: ?0 } }")
    List<Product> findByRecordTypeIds(List<String> recordTypeIds);
    
    // Custom query to find products by multiple billing policies
    @Query("{ 'billingPolicyId': { $in: ?0 } }")
    List<Product> findByBillingPolicyIds(List<String> billingPolicyIds);
    
    // Custom query to find products by multiple currencies
    @Query("{ 'currencyIsoCode': { $in: ?0 } }")
    List<Product> findByCurrencyIsoCodes(List<String> currencyIsoCodes);
    
    // Custom query to find products by multiple provisioning types
    @Query("{ 'provisioningType': { $in: ?0 } }")
    List<Product> findByProvisioningTypes(List<String> provisioningTypes);
    
    // Custom query to find products by multiple schedule types
    @Query("{ 'quantityScheduleType': { $in: ?0 } }")
    List<Product> findByQuantityScheduleTypes(List<String> scheduleTypes);
    
    // Custom query to find products by multiple revenue schedule types
    @Query("{ 'revenueScheduleType': { $in: ?0 } }")
    List<Product> findByRevenueScheduleTypes(List<String> revenueScheduleTypes);

    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByDescriptionContainingIgnoreCase(String description);
    List<Product> findByProductCategoryIn(List<String> categories);
    List<Product> findByProductLineIn(List<String> productLines);
    List<Product> findByFamilyIn(List<String> families);
    List<Product> findByTypeIn(List<String> types);
    List<Product> findByFamilyAndTypeAndIsActive(String family, String type, Boolean isActive);

    @Query("{ 'name': { $regex: ?0, $options: 'i' }, " +
           "'description': { $regex: ?1, $options: 'i' }, " +
           "'productCategory': ?2, " +
           "'productLine': ?3, " +
           "'family': ?4, " +
           "'type': ?5 }")
    List<Product> findByMultipleCriteria(String name, String description, String category,
                                       String productLine, String family, String type);
} 