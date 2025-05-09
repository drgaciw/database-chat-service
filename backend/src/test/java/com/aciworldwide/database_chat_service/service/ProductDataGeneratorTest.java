package com.aciworldwide.database_chat_service.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.aciworldwide.database_chat_service.model.Product;

@SpringBootTest(
    classes = ProductDataGeneratorTest.TestConfig.class,
    properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.gateway.enabled=false",
        "spring.cloud.config.discovery.enabled=false",
        "spring.cloud.compatibility-verifier.enabled=false"
    }
)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.main.web-application-type=none"
})
public class ProductDataGeneratorTest {

    @Autowired
    private ProductDataGenerator productDataGenerator;

    @Test
    public void testGenerateProducts() {
        // Generate 10 products
        List<Product> products = productDataGenerator.generateProducts(10);

        // Verify the number of products
        assertEquals(10, products.size());

        // Verify each product
        for (Product product : products) {
            assertNotNull(product.getName());
            assertNotNull(product.getProductCode());
            assertNotNull(product.getDescription());
            assertNotNull(product.getFamily());
            assertNotNull(product.getStandardPrice());
            assertNotNull(product.getProductCategory());
            assertNotNull(product.getProductLine());
            assertNotNull(product.getType());
            assertNotNull(product.getStockKeepingUnit());
            assertNotNull(product.getQuantityUnitOfMeasure());

            // Verify price range
            assertTrue(product.getStandardPrice().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(product.getStandardPrice().compareTo(new BigDecimal("500000")) <= 0);

            // Verify product code format
            assertTrue(product.getProductCode().matches("(BANK|PAY)-\\d{3}-[A-Z0-9]{4}"));

            // Verify SKU format
            assertTrue(product.getStockKeepingUnit().matches("(BANK|PAY)-\\d{3}-[A-Z0-9]{4}-(ENT|PRO|STD|BAS|IMP|TRN|CON|SUP|CUS|DEV)"));
        }
    }

    @Test
    public void testProductUniqueness() {
        // Generate 100 products
        List<Product> products = productDataGenerator.generateProducts(100);

        // Verify unique product codes
        long uniqueProductCodes = products.stream()
            .map(Product::getProductCode)
            .distinct()
            .count();

        assertEquals(100, uniqueProductCodes);

        // Verify unique SKUs
        long uniqueSKUs = products.stream()
            .map(Product::getStockKeepingUnit)
            .distinct()
            .count();

        assertEquals(100, uniqueSKUs);
    }

    @Test
    public void testProductRelationships() {
        // Generate a single product
        Product product = productDataGenerator.generateProducts(1).get(0);

        // Verify default price is 90% of standard price
        BigDecimal expectedDefaultPrice = product.getStandardPrice().multiply(new BigDecimal("0.9"));
        assertEquals(0, expectedDefaultPrice.compareTo(product.getDefaultPrice()));

        // Verify description contains product name
        assertTrue(product.getDescription().contains(product.getName()));

        // Verify SKU contains product code
        assertTrue(product.getStockKeepingUnit().contains(product.getProductCode()));
    }

    /**
     * Test configuration class
     */
    @Configuration
    @EnableAutoConfiguration(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class,
        RedisAutoConfiguration.class,
        DataSourceAutoConfiguration.class
    })
    static class TestConfig {

        @Bean
        public static ProductDataGenerator productDataGenerator() {
            return new ProductDataGenerator();
        }
    }
}