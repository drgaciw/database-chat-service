package com.aciworldwide.database_chat_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.aciworldwide.database_chat_service.model.Product;

@Service
public class ProductDataGenerator {
    private static final Random random = new Random();
    
    private static final String[] PRODUCT_CATEGORIES = {
        "Core Banking", "Payments", "Digital Banking", "Risk & Compliance", "Analytics",
        "Professional Services", "Educational Services", "Consulting", "Support", "Integration"
    };
    
    private static final String[] PRODUCT_FAMILIES = {
        "Banking Solutions", "Payment Solutions", "Digital Solutions", "Risk Solutions", "Analytics Solutions",
        "Implementation Services", "Training Services", "Consulting Services", "Support Services", "Integration Services"
    };
    
    private static final String[] PRODUCT_LINES = {
        "Enterprise", "Professional", "Standard", "Basic", "Premium",
        "Implementation", "Training", "Consulting", "Support", "Custom"
    };
    
    private static final String[] PRODUCT_TYPES = {
        "Software", "Service", "Subscription", "License", "Maintenance",
        "Implementation", "Training", "Consulting", "Support", "Custom"
    };
    
    private static final String[] QUANTITY_UNITS = {
        "License", "User", "Instance", "Core", "Transaction",
        "Day", "Hour", "Month", "Year", "Project"
    };
    
    private static final String[] CURRENCIES = {
        "USD", "EUR", "GBP", "JPY", "CAD"
    };
    
    private static final String[] PROVISIONING_TYPES = {
        "One-Time", "Recurring", "Usage-Based", "Hybrid", "Custom"
    };
    
    private static final String[] SCHEDULE_TYPES = {
        "Monthly", "Quarterly", "Annual", "Biennial", "Custom"
    };
    
    private static final String[] BANKING_FEATURES = {
        "Account Management", "Loan Processing", "Deposit Services", "Customer Onboarding", "KYC Compliance",
        "Transaction Processing", "Reporting", "Audit Trail", "Multi-Currency Support", "Regulatory Compliance"
    };
    
    private static final String[] PAYMENT_FEATURES = {
        "Real-time Payments", "ACH Processing", "Wire Transfers", "Card Processing", "Mobile Payments",
        "P2P Transfers", "B2B Payments", "Cross-border Payments", "Payment Analytics", "Fraud Detection"
    };
    
    private static final String[] SERVICE_TYPES = {
        "Implementation", "Training", "Consulting", "Support", "Custom Development",
        "System Integration", "Data Migration", "Process Optimization", "Compliance Review", "Security Assessment"
    };
    
    public List<Product> generateProducts(int count) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            products.add(generateProduct());
        }
        return products;
    }
    
    private Product generateProduct() {
        Product product = new Product();
        
        // Generate a unique product code
        String productCode = generateProductCode();
        
        // Set basic fields
        product.setName(generateProductName(productCode));
        product.setProductCode(productCode);
        product.setDescription(generateDescription(product.getName()));
        product.setIsActive(random.nextBoolean());
        product.setFamily(PRODUCT_FAMILIES[random.nextInt(PRODUCT_FAMILIES.length)]);
        product.setStandardPrice(generatePrice());
        product.setProductCategory(PRODUCT_CATEGORIES[random.nextInt(PRODUCT_CATEGORIES.length)]);
        product.setProductLine(PRODUCT_LINES[random.nextInt(PRODUCT_LINES.length)]);
        product.setType(PRODUCT_TYPES[random.nextInt(PRODUCT_TYPES.length)]);
        product.setStockKeepingUnit(generateSKU(productCode));
        product.setQuantityUnitOfMeasure(QUANTITY_UNITS[random.nextInt(QUANTITY_UNITS.length)]);
        
        // Set additional fields
        product.setExternalId(UUID.randomUUID().toString());
        product.setCreatedDate(LocalDateTime.now());
        product.setLastModifiedDate(LocalDateTime.now());
        product.setIsDeleted(false);
        product.setIsArchived(random.nextBoolean() ? "true" : "false");
        product.setDefaultPrice(product.getStandardPrice().multiply(new BigDecimal("0.9")));
        
        // Set business-specific fields
        product.setCurrencyIsoCode(CURRENCIES[random.nextInt(CURRENCIES.length)]);
        product.setProvisioningType(PROVISIONING_TYPES[random.nextInt(PROVISIONING_TYPES.length)]);
        product.setQuantityScheduleType(SCHEDULE_TYPES[random.nextInt(SCHEDULE_TYPES.length)]);
        product.setRevenueScheduleType(SCHEDULE_TYPES[random.nextInt(SCHEDULE_TYPES.length)]);
        
        // Set random boolean fields
        product.setCanUseQuantitySchedule(String.valueOf(random.nextBoolean()));
        product.setCanUseRevenueSchedule(String.valueOf(random.nextBoolean()));
        
        // Set random numeric fields
        product.setNumberOfQuantityInstallments(String.valueOf(random.nextInt(12) + 1));
        product.setNumberOfRevenueInstallments(String.valueOf(random.nextInt(12) + 1));
        
        return product;
    }
    
    private String generateProductCode() {
        String prefix = random.nextBoolean() ? "BANK" : "PAY";
        return String.format("%s-%03d-%s", 
            prefix,
            random.nextInt(1000),
            UUID.randomUUID().toString().substring(0, 4).toUpperCase());
    }
    
    private String generateProductName(String productCode) {
        String[] prefixes = {"Enterprise", "Professional", "Advanced", "Standard", "Basic",
                           "Implementation", "Training", "Consulting", "Support", "Custom"};
        String[] suffixes = {"Solution", "Platform", "Service", "System", "Suite",
                           "Implementation", "Training", "Consulting", "Support", "Package"};
        
        return String.format("%s %s %s",
            prefixes[random.nextInt(prefixes.length)],
            productCode,
            suffixes[random.nextInt(suffixes.length)]);
    }
    
    private String generateDescription(String productName) {
        String[] adjectives = {"enterprise-grade", "secure", "scalable", "compliant", "innovative",
                             "comprehensive", "integrated", "cloud-native", "AI-powered", "cutting-edge"};
        String[] purposes = {"banking", "payments", "financial services", "compliance", "risk management",
                           "implementation", "training", "consulting", "support", "integration"};
        
        String feature1, feature2;
        if (productName.contains("BANK")) {
            feature1 = BANKING_FEATURES[random.nextInt(BANKING_FEATURES.length)];
            feature2 = BANKING_FEATURES[random.nextInt(BANKING_FEATURES.length)];
        } else if (productName.contains("PAY")) {
            feature1 = PAYMENT_FEATURES[random.nextInt(PAYMENT_FEATURES.length)];
            feature2 = PAYMENT_FEATURES[random.nextInt(PAYMENT_FEATURES.length)];
        } else {
            feature1 = SERVICE_TYPES[random.nextInt(SERVICE_TYPES.length)];
            feature2 = SERVICE_TYPES[random.nextInt(SERVICE_TYPES.length)];
        }
        
        return String.format("A %s solution for %s, %s provides %s and %s capabilities.",
            adjectives[random.nextInt(adjectives.length)],
            purposes[random.nextInt(purposes.length)],
            productName,
            feature1,
            feature2);
    }
    
    private BigDecimal generatePrice() {
        // Generate prices with different ranges based on product type
        double basePrice;
        if (random.nextDouble() < 0.2) { // 20% chance of being an enterprise product
            basePrice = 50000.0 + (random.nextDouble() * 450000.0); // $50,000-$500,000
        } else if (random.nextDouble() < 0.4) { // 32% chance of being a professional product
            basePrice = 5000.0 + (random.nextDouble() * 45000.0); // $5,000-$50,000
        } else { // 48% chance of being a standard product
            basePrice = 500.0 + (random.nextDouble() * 4500.0); // $500-$5,000
        }
        return new BigDecimal(basePrice).setScale(2, RoundingMode.HALF_UP);
    }
    
    private String generateSKU(String productCode) {
        String[] versions = {"ENT", "PRO", "STD", "BAS", "IMP", "TRN", "CON", "SUP", "CUS", "DEV"};
        return String.format("%s-%s", productCode, versions[random.nextInt(versions.length)]);
    }
} 