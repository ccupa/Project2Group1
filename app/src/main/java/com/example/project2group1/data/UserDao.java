package com.example.project2group1.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(User user);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUser(String username);

    @Query("SELECT COUNT(*) FROM users")
    int countUsers();

    @Query("UPDATE users SET isAdmin = 1 WHERE username = :username")
    void makeAdmin(String username);
}
