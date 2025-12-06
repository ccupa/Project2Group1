package com.example.project2group1;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
public class PokemonHighScoreDaoTest {

    private AppDatabase db;
    private CategoryHighScoreDao hsDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        // In-memory DB so tests don’t touch your real data
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        hsDao = db.categoryHighScoreDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void pokemonHighScore_updatesWhenHigherScore() {
        String username = "ash";
        String category = "Pokemon";

        // Insert initial score 3
        CategoryHighScore hs = new CategoryHighScore();
        hs.username = username;
        hs.category = category;
        hs.score = 3;
        long id = hsDao.insert(hs);
        assertTrue(id > 0);

        CategoryHighScore initial = hsDao.getHighScore(username, category);
        assertNotNull(initial);
        assertEquals(3, initial.score);

        // Update with higher score 9
        hsDao.updateScore(initial.id, 9);

        CategoryHighScore updated = hsDao.getHighScore(username, category);
        assertNotNull(updated);
        assertEquals(9, updated.score);
    }
}
