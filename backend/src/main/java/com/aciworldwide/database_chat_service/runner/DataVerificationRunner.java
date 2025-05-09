package com.aciworldwide.database_chat_service.runner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.aciworldwide.database_chat_service.model.Product;
import com.aciworldwide.database_chat_service.service.ProductService;

@Component
public class DataVerificationRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataVerificationRunner.class);
    
    private final ProductService productService;
    
    public DataVerificationRunner(ProductService productService) {
        this.productService = productService;
    }
    
    @Override
    public void run(String... args) {
        if (args.length > 0 && args[0].equals("--verify-data")) {
            logger.info("Starting data verification...");
            
            // Get all products
            List<Product> products = productService.findAll();
            
            // Basic statistics
            logger.info("\n=== Basic Statistics ===");
            logger.info("Total Products: {}", products.size());
            
            // Active vs Archived
            long activeCount = products.stream()
                .filter(Product::getIsActive)
                .count();
            long archivedCount = products.stream()
                .filter(p -> "true".equals(p.getIsArchived()))
                .count();
            logger.info("Active Products: {} ({}%)", activeCount, (activeCount * 100) / products.size());
            logger.info("Archived Products: {} ({}%)", archivedCount, (archivedCount * 100) / products.size());
            
            // Category distribution
            logger.info("\n=== Category Distribution ===");
            Map<String, Long> categoryCounts = products.stream()
                .collect(Collectors.groupingBy(Product::getProductCategory, Collectors.counting()));
            categoryCounts.forEach((category, count) -> 
                logger.info("{}: {} products ({}%)", 
                    category, count, (count * 100) / products.size()));
            
            // Product type distribution
            logger.info("\n=== Product Type Distribution ===");
            Map<String, Long> typeCounts = products.stream()
                .collect(Collectors.groupingBy(Product::getType, Collectors.counting()));
            typeCounts.forEach((type, count) -> 
                logger.info("{}: {} products ({}%)", 
                    type, count, (count * 100) / products.size()));
            
            // Price statistics
            logger.info("\n=== Price Statistics ===");
            double avgPrice = products.stream()
                .mapToDouble(p -> p.getStandardPrice().doubleValue())
                .average()
                .orElse(0.0);
            double maxPrice = products.stream()
                .mapToDouble(p -> p.getStandardPrice().doubleValue())
                .max()
                .orElse(0.0);
            double minPrice = products.stream()
                .mapToDouble(p -> p.getStandardPrice().doubleValue())
                .min()
                .orElse(0.0);
            logger.info("Average Price: ${:,.2f}", avgPrice);
            logger.info("Maximum Price: ${:,.2f}", maxPrice);
            logger.info("Minimum Price: ${:,.2f}", minPrice);
            
            // Sample products from each category
            logger.info("\n=== Sample Products ===");
            categoryCounts.keySet().forEach(category -> {
                List<Product> categoryProducts = products.stream()
                    .filter(p -> category.equals(p.getProductCategory()))
                    .limit(2)
                    .collect(Collectors.toList());
                
                logger.info("\nCategory: {}", category);
                categoryProducts.forEach(p -> 
                    logger.info("- {} (${:,.2f}): {}", 
                        p.getName(), 
                        p.getStandardPrice().doubleValue(),
                        p.getDescription()));
            });
            
            logger.info("\nData verification completed.");
        }
    }
} 