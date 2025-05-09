package com.aciworldwide.database_chat_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {
    @Id
    private String id;
    
    // Standard Fields
    private String name;                    // Product Name
    private String productCode;             // Product Code
    private String description;             // Product Description
    private Boolean isActive;               // Active
    private String family;                  // Product Family
    private BigDecimal standardPrice;       // Standard Price (List Price)
    private String externalId;              // External ID
    private LocalDateTime createdDate;      // Created Date
    private LocalDateTime lastModifiedDate; // Last Modified Date
    private String stockKeepingUnit;        // Stock Keeping Unit (SKU)
    private String quantityUnitOfMeasure;   // Quantity Unit of Measure
    private String displayUrl;              // Display URL (changed from Boolean to String)
    private String type;                    // Type
    
    // Additional Standard Fields
    private String billingPolicyId;         // Billing Policy
    private String canUseQuantitySchedule;  // Can Use Quantity Schedule
    private String canUseRevenueSchedule;   // Can Use Revenue Schedule
    private String currencyIsoCode;         // Currency ISO Code
    private String customExternalId;        // Custom External Id
    private LocalDateTime lastReferencedDate; // Last Referenced Date
    private LocalDateTime lastViewedDate;   // Last Viewed Date
    private String numberOfQuantityInstallments; // Number of Quantity Installments
    private String numberOfRevenueInstallments; // Number of Revenue Installments
    private String productClass;            // Product Class
    private String provisioningType;        // Provisioning Type
    private String quantityInstallmentPeriod; // Quantity Installment Period
    private String quantityScheduleType;    // Quantity Schedule Type
    private String recordTypeId;            // Record Type ID
    private String revenueInstallmentPeriod; // Revenue Installment Period
    private String revenueScheduleType;     // Revenue Schedule Type
    private String scheduleTypeId;          // Schedule Type ID
    private String stockKeepingUnitCustom;  // Stock Keeping Unit Custom
    
    // Additional Product Information
    private BigDecimal defaultPrice;        // Default Price
    private String isArchived;              // Is Archived
    private Boolean isDeleted;              // Is Deleted
    private String productCategory;         // Product Category
    private String productLine;             // Product Line
    private String productSubcategory;      // Product Subcategory
    
    // System Fields
    private String createdById;             // Created By ID
    private String lastModifiedById;        // Last Modified By ID
    private String ownerId;                 // Owner ID
    private String systemModstamp;          // System Modstamp

    // Default constructor
    public Product() {}

    // Getters and Setters for all fields
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getFamily() { return family; }
    public void setFamily(String family) { this.family = family; }
    
    public BigDecimal getStandardPrice() { return standardPrice; }
    public void setStandardPrice(BigDecimal standardPrice) { this.standardPrice = standardPrice; }
    
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }
    
    public String getStockKeepingUnit() { return stockKeepingUnit; }
    public void setStockKeepingUnit(String stockKeepingUnit) { this.stockKeepingUnit = stockKeepingUnit; }
    
    public String getQuantityUnitOfMeasure() { return quantityUnitOfMeasure; }
    public void setQuantityUnitOfMeasure(String quantityUnitOfMeasure) { this.quantityUnitOfMeasure = quantityUnitOfMeasure; }
    
    public String getDisplayUrl() { return displayUrl; }
    public void setDisplayUrl(String displayUrl) { this.displayUrl = displayUrl; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBillingPolicyId() { return billingPolicyId; }
    public void setBillingPolicyId(String billingPolicyId) { this.billingPolicyId = billingPolicyId; }

    public String getCanUseQuantitySchedule() { return canUseQuantitySchedule; }
    public void setCanUseQuantitySchedule(String canUseQuantitySchedule) { this.canUseQuantitySchedule = canUseQuantitySchedule; }

    public String getCanUseRevenueSchedule() { return canUseRevenueSchedule; }
    public void setCanUseRevenueSchedule(String canUseRevenueSchedule) { this.canUseRevenueSchedule = canUseRevenueSchedule; }

    public String getCurrencyIsoCode() { return currencyIsoCode; }
    public void setCurrencyIsoCode(String currencyIsoCode) { this.currencyIsoCode = currencyIsoCode; }

    public String getCustomExternalId() { return customExternalId; }
    public void setCustomExternalId(String customExternalId) { this.customExternalId = customExternalId; }

    public LocalDateTime getLastReferencedDate() { return lastReferencedDate; }
    public void setLastReferencedDate(LocalDateTime lastReferencedDate) { this.lastReferencedDate = lastReferencedDate; }

    public LocalDateTime getLastViewedDate() { return lastViewedDate; }
    public void setLastViewedDate(LocalDateTime lastViewedDate) { this.lastViewedDate = lastViewedDate; }

    public String getNumberOfQuantityInstallments() { return numberOfQuantityInstallments; }
    public void setNumberOfQuantityInstallments(String numberOfQuantityInstallments) { this.numberOfQuantityInstallments = numberOfQuantityInstallments; }

    public String getNumberOfRevenueInstallments() { return numberOfRevenueInstallments; }
    public void setNumberOfRevenueInstallments(String numberOfRevenueInstallments) { this.numberOfRevenueInstallments = numberOfRevenueInstallments; }

    public String getProductClass() { return productClass; }
    public void setProductClass(String productClass) { this.productClass = productClass; }

    public String getProvisioningType() { return provisioningType; }
    public void setProvisioningType(String provisioningType) { this.provisioningType = provisioningType; }

    public String getQuantityInstallmentPeriod() { return quantityInstallmentPeriod; }
    public void setQuantityInstallmentPeriod(String quantityInstallmentPeriod) { this.quantityInstallmentPeriod = quantityInstallmentPeriod; }

    public String getQuantityScheduleType() { return quantityScheduleType; }
    public void setQuantityScheduleType(String quantityScheduleType) { this.quantityScheduleType = quantityScheduleType; }

    public String getRecordTypeId() { return recordTypeId; }
    public void setRecordTypeId(String recordTypeId) { this.recordTypeId = recordTypeId; }

    public String getRevenueInstallmentPeriod() { return revenueInstallmentPeriod; }
    public void setRevenueInstallmentPeriod(String revenueInstallmentPeriod) { this.revenueInstallmentPeriod = revenueInstallmentPeriod; }

    public String getRevenueScheduleType() { return revenueScheduleType; }
    public void setRevenueScheduleType(String revenueScheduleType) { this.revenueScheduleType = revenueScheduleType; }

    public String getScheduleTypeId() { return scheduleTypeId; }
    public void setScheduleTypeId(String scheduleTypeId) { this.scheduleTypeId = scheduleTypeId; }

    public String getStockKeepingUnitCustom() { return stockKeepingUnitCustom; }
    public void setStockKeepingUnitCustom(String stockKeepingUnitCustom) { this.stockKeepingUnitCustom = stockKeepingUnitCustom; }

    public BigDecimal getDefaultPrice() { return defaultPrice; }
    public void setDefaultPrice(BigDecimal defaultPrice) { this.defaultPrice = defaultPrice; }

    public String getIsArchived() { return isArchived; }
    public void setIsArchived(String isArchived) { this.isArchived = isArchived; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public String getProductCategory() { return productCategory; }
    public void setProductCategory(String productCategory) { this.productCategory = productCategory; }

    public String getProductLine() { return productLine; }
    public void setProductLine(String productLine) { this.productLine = productLine; }

    public String getProductSubcategory() { return productSubcategory; }
    public void setProductSubcategory(String productSubcategory) { this.productSubcategory = productSubcategory; }

    public String getCreatedById() { return createdById; }
    public void setCreatedById(String createdById) { this.createdById = createdById; }

    public String getLastModifiedById() { return lastModifiedById; }
    public void setLastModifiedById(String lastModifiedById) { this.lastModifiedById = lastModifiedById; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getSystemModstamp() { return systemModstamp; }
    public void setSystemModstamp(String systemModstamp) { this.systemModstamp = systemModstamp; }
} 