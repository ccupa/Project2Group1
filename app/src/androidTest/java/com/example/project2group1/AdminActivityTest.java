package com.example.project2group1;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Simple sanity tests for AdminActivity.
 * Ensures activity can be instantiated without issues.
 */
public class AdminActivityTest {

    @Test
    public void activity_instantiatesSuccessfully() {
        AdminActivity activity = new AdminActivity();
        assertNotNull(activity);
    }

    @Test
    public void adminActivity_classExists() {
        // Basic test to ensure class loads correctly
        assertEquals("com.example.project2group1.AdminActivity",
                AdminActivity.class.getName());
    }
}
