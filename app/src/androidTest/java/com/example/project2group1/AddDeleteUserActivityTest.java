package com.example.project2group1;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Basic instantiation and class check tests.
 */
public class AddDeleteUserActivityTest {

    @Test
    public void activity_instantiatesSuccessfully() {
        AddDeleteUserActivity activity = new AddDeleteUserActivity();
        assertNotNull(activity);
    }

    @Test
    public void addDeleteUserActivity_classExists() {
        assertEquals("com.example.project2group1.AddDeleteUserActivity",
                AddDeleteUserActivity.class.getName());
    }
}
