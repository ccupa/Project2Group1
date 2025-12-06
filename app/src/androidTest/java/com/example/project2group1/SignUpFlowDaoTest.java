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

public class SignUpFlowDaoTest {
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

}
