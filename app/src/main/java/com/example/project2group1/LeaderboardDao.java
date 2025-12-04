package com.example.project2group1;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LeaderboardDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(LeaderboardEntity entry); //returns ID

    @Update
    void update(LeaderboardEntity entry);

    // Get leaderboard sorted by totalScore DESC
    @Query("SELECT * FROM leaderboard ORDER BY totalScore DESC")
    List<LeaderboardEntity> getAllSorted();

    @Query("SELECT * FROM leaderboard ORDER BY totalScore DESC LIMIT :limit")
    List<LeaderboardEntity> getTop(int limit);

    // Get a single user's row
    @Query("SELECT * FROM leaderboard WHERE username = :username LIMIT 1")
    LeaderboardEntity getByUsername(String username);

    // Delete everything (optional)
    @Query("DELETE FROM leaderboard")
    void clearLeaderboard();





}
