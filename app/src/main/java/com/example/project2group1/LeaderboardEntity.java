package com.example.project2group1;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "leaderboard")
public class LeaderboardEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String username;
    public int jackTriviaScore;
    public int carlosTriviaScore;
    public int joshTriviaScore;
    public int joeTriviaScore;
    public int totalScore;

    @Ignore // admin use if necessary
    public LeaderboardEntity(String username, int jackTriviaScore, int carlosTriviaScore, int joshTriviaScore, int joeTriviaScore, int totalScore) {
        this.username = username;
        this.jackTriviaScore = jackTriviaScore;
        this.carlosTriviaScore = carlosTriviaScore;
        this.joshTriviaScore = joshTriviaScore;
        this.joeTriviaScore = joeTriviaScore;
        this.totalScore = totalScore;
    }

    public LeaderboardEntity(String username) {
        this.username = username;
        this.jackTriviaScore = 0;
        this.carlosTriviaScore = 0;
        this.joshTriviaScore = 0;
        this.joeTriviaScore = 0;
        this.totalScore = 0;
    }
}
