package com.aciworldwide.database_chat_service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A plain JUnit test that doesn't use Spring Boot at all
 */
public class PlainJUnitTest {

    /**
     * A simple test that verifies basic arithmetic
     */
    @Test
    public void testBasicArithmetic() {
        assertEquals(4, 2 + 2, "2 + 2 should equal 4");
    }
}
