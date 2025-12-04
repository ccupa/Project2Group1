package com.example.project2group1;

import android.content.Context;


import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, LeaderboardEntity.class}, version = 1, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract LeaderboardDao leaderboardDao();

    private static volatile AppDatabase INSTANCE;
    static final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "project02.db"
                    ).fallbackToDestructiveMigration() // deletes database when version number is changed
                            .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            dbExecutor.execute(() -> {

                                AppDatabase database = INSTANCE;

                                UserDao u_dao = database.userDao();
                                LeaderboardDao lb_dao = database.leaderboardDao();

                                if (u_dao.count() == 0) {
                                    u_dao.insert(new User("testuser1", "testuser1", false));
                                    u_dao.insert(new User ("admin2", "admin2", true));
                                }

                                if (lb_dao.getCount() == 0) {
                                    lb_dao.insert(new LeaderboardEntity("testuser1"));
                                    lb_dao.insert(new LeaderboardEntity("admin2"));
                                }
                            });
                        }
                    })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
