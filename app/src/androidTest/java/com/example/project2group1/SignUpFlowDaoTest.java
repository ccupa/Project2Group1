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

@RunWith(AndroidJUnit4.class)
public class SignUpFlowDaoTest {

    private AppDatabase db;
    private UserDao userDao;
    private LeaderboardDao lbDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        userDao = db.userDao();
        lbDao = db.leaderboardDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void signUp_createsUserAndLeaderboardRow() {
        String username = "newUser";
        String password = "secret";

        // Make sure user/leaderboard rows don't exist yet
        assertNull(userDao.findByUsername(username));
        assertNull(lbDao.getByUsername(username));

        // Simulate SignUpActivity's DB work
        User newUser = new User(username, password, false);  // not admin
        long userId = userDao.insert(newUser);
        assertTrue(userId > 0);

        LeaderboardEntity entry = new LeaderboardEntity(username);
        long lbId = lbDao.insert(entry);
        assertTrue(lbId > 0);

        // Verify user exists
        User fromDb = userDao.findByUsername(username);
        assertNotNull(fromDb);
        assertEquals(username, fromDb.username);
        assertEquals(password, fromDb.password);
        assertFalse(fromDb.isAdmin);

        // Verify leaderboard row exists and is initialized to zero scores
        LeaderboardEntity lbFromDb = lbDao.getByUsername(username);
        assertNotNull(lbFromDb);
        assertEquals(username, lbFromDb.username);
        assertEquals(0, lbFromDb.jackTriviaScore);
        assertEquals(0, lbFromDb.carlosTriviaScore);
        assertEquals(0, lbFromDb.joshTriviaScore);
        assertEquals(0, lbFromDb.joeTriviaScore);
        assertEquals(0, lbFromDb.totalScore);
    }
}
