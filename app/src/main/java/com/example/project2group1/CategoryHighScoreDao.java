package com.example.project2group1;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CategoryHighScoreDao {

    @Query("SELECT * FROM category_high_scores WHERE username = :username AND category = :category LIMIT 1")
    CategoryHighScore getHighScore(String username, String category);

    @Insert
    long insert(CategoryHighScore score);

    @Query("Update category_high_scores SET score = :newScore WHERE id = :id")
    void updateScore(int id, int newScore);
}
