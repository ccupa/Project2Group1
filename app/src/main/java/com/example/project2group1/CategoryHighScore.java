package com.example.project2group1;

import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_high_scores")
public class CategoryHighScore {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;
    public String category;
    public int score;
}
