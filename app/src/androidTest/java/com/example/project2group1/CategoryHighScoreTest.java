package com.example.project2group1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class CategoryHighScoreTest {
    @Test
    public void Test() {
        CategoryHighScore hs = new CategoryHighScore();

        hs.id = 1;
        hs.username = "testUser";
        hs.category = "Geography";
        hs.score = 7;

        assertEquals(1, hs.id);
        assertEquals("testUser", hs.username);
        assertEquals("Geography", hs.category);
        assertEquals(7, hs.score);
    }

    @Test
    public void score() {
        CategoryHighScore hs = new CategoryHighScore();
        hs.username = "testUser";
        hs.category = "Pokemon";
        hs.score = 3;

        hs.score = 10;

        assertEquals("testUser", hs.username);
        assertEquals("Pokemon", hs.category);
        assertEquals(10, hs.score);
    }

    @Test
    public void user() {
        CategoryHighScore hs = new CategoryHighScore();
        hs.username = "someone";
        hs.category = "Computer Science";

        assertNotNull(hs.username);
        assertNotNull(hs.category);
    }
}
