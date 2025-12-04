package com.example.project2group1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project2group1.databinding.ActivityLeaderBoardBinding;

public class LeaderBoard extends AppCompatActivity {

    ActivityLeaderBoardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLeaderBoardBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        //get instance of leaderboard then create a dao
        AppDatabase db = AppDatabase.getInstance(this);
        LeaderboardDao dao = db.leaderboardDao();

        dao.getTop(2).observe(this, leaders -> {

            if (leaders == null || leaders.isEmpty()) {
                toastMaker("No data in leaderboard table :(");
                return;
            }

            LeaderboardEntity first = leaders.get(0);
            int totalScore = 0;
            totalScore += first.jackTriviaScore;
            totalScore += first.carlosTriviaScore;
            totalScore += first.joshTriviaScore;
            totalScore += first.joeTriviaScore;

            binding.p1NameTextView.setText(String.valueOf(first.username));
            binding.p1JackTextView.setText(String.valueOf(first.jackTriviaScore));
            binding.p1CarlosTextView.setText(String.valueOf(first.carlosTriviaScore));
            binding.p1joshTextView.setText(String.valueOf(first.joshTriviaScore));
            binding.p1joeTextView.setText(String.valueOf(first.joeTriviaScore));
            binding.p1totalTextView.setText(String.valueOf(totalScore));



            if (leaders.size() < 2) return;

            LeaderboardEntity second = leaders.get(1);
            totalScore = 0;
            totalScore += second.jackTriviaScore;
            totalScore += second.carlosTriviaScore;
            totalScore += second.joshTriviaScore;
            totalScore += second.joeTriviaScore;

            binding.p2NameTextView.setText(String.valueOf(second.username));
            binding.p2JackTextView.setText(String.valueOf(second.jackTriviaScore));
            binding.p2CarlosTextView.setText(String.valueOf(second.carlosTriviaScore));
            binding.p2joshTextView.setText(String.valueOf(second.joshTriviaScore));
            binding.p2joeTextView.setText(String.valueOf(second.joeTriviaScore));
            binding.p2totalTextView.setText(String.valueOf(totalScore));


        });


    }

    static Intent leaderboardIntentFactory(Context context) {
        return new Intent(context, LeaderBoard.class);
    }

    public void toastMaker(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}