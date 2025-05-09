package com.aciworldwide.database_chat_service.runner;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.aciworldwide.database_chat_service.model.Product;
import com.aciworldwide.database_chat_service.service.ProductDataGenerator;
import com.aciworldwide.database_chat_service.service.ProductService;

@Component
public class DataLoaderRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataLoaderRunner.class);
    
    private final ProductDataGenerator productDataGenerator;
    private final ProductService productService;
    
    public DataLoaderRunner(ProductDataGenerator productDataGenerator, ProductService productService) {
        this.productDataGenerator = productDataGenerator;
        this.productService = productService;
    }
    
    @Override
    public void run(String... args) {
        if (args.length > 0 && args[0].equals("--load-mock-data")) {
            logger.info("Starting mock data generation and loading...");
            
            // Generate 5000 products for a more comprehensive dataset
            List<Product> products = productDataGenerator.generateProducts(5000);
            
            // Save products in batches of 100
            int batchSize = 100;
            for (int i = 0; i < products.size(); i += batchSize) {
                int end = Math.min(i + batchSize, products.size());
                List<Product> batch = products.subList(i, end);
                productService.saveAll(batch);
                logger.info("Loaded batch {}-{} of {} products", i + 1, end, products.size());
            }
            
            // Log some statistics about the loaded data
            logDataStatistics(products);
            
            logger.info("Mock data loading completed successfully");
        }
    }
    
    private void logDataStatistics(List<Product> products) {
        long activeProducts = products.stream()
            .filter(Product::getIsActive)
            .count();
        
        long archivedProducts = products.stream()
            .filter(p -> "true".equals(p.getIsArchived()))
            .count();
        
        logger.info("Data Statistics:");
        logger.info("- Total Products: {}", products.size());
        logger.info("- Active Products: {}", activeProducts);
        logger.info("- Archived Products: {}", archivedProducts);
        logger.info("- Active/Total Ratio: {}%", (activeProducts * 100) / products.size());
        
        // Log distribution by category
        products.stream()
            .collect(java.util.stream.Collectors.groupingBy(Product::getProductCategory, java.util.stream.Collectors.counting()))
            .forEach((category, count) -> 
                logger.info("- Category '{}': {} products ({}%)", 
                    category, count, (count * 100) / products.size()));
        
        // Log price statistics
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
        
        logger.info("Price Statistics:");
        logger.info("- Average Price: ${:.2f}", avgPrice);
        logger.info("- Maximum Price: ${:.2f}", maxPrice);
        logger.info("- Minimum Price: ${:.2f}", minPrice);
    }
} 