package com.example.project2group1;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Simple sanity tests for UserHistoryforAdmin.
 */
public class UserHistoryforAdminTest {

    @Test
    public void activity_instantiatesSuccessfully() {
        UserHistoryforAdmin activity = new UserHistoryforAdmin();
        assertNotNull(activity);
    }

    @Test
    public void userHistory_classExists() {
        assertEquals("com.example.project2group1.UserHistoryforAdmin",
                UserHistoryforAdmin.class.getName());
    }
}
