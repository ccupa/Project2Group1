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
}
