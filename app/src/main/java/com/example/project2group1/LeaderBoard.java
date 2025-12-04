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

        displayTable(dao);




    }

    public void displayTable(LeaderboardDao dao) {

        dao.getTop(5).observe(this, leaders -> {

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

            if (leaders.size() < 3) return;

            LeaderboardEntity third = leaders.get(2);
            totalScore = 0;
            totalScore += third.jackTriviaScore;
            totalScore += third.carlosTriviaScore;
            totalScore += third.joshTriviaScore;
            totalScore += third.joeTriviaScore;

            binding.p2NameTextView.setText(String.valueOf(third.username));
            binding.p2JackTextView.setText(String.valueOf(third.jackTriviaScore));
            binding.p2CarlosTextView.setText(String.valueOf(third.carlosTriviaScore));
            binding.p2joshTextView.setText(String.valueOf(third.joshTriviaScore));
            binding.p2joeTextView.setText(String.valueOf(third.joeTriviaScore));
            binding.p2totalTextView.setText(String.valueOf(totalScore));

            if (leaders.size() < 4) return;

            LeaderboardEntity fourth = leaders.get(3);
            totalScore = 0;
            totalScore += fourth.jackTriviaScore;
            totalScore += fourth.carlosTriviaScore;
            totalScore += fourth.joshTriviaScore;
            totalScore += fourth.joeTriviaScore;

            binding.p2NameTextView.setText(String.valueOf(fourth.username));
            binding.p2JackTextView.setText(String.valueOf(fourth.jackTriviaScore));
            binding.p2CarlosTextView.setText(String.valueOf(fourth.carlosTriviaScore));
            binding.p2joshTextView.setText(String.valueOf(fourth.joshTriviaScore));
            binding.p2joeTextView.setText(String.valueOf(fourth.joeTriviaScore));
            binding.p2totalTextView.setText(String.valueOf(totalScore));

            if (leaders.size() < 4) return;

            LeaderboardEntity fifth = leaders.get(4);
            totalScore = 0;
            totalScore += fifth.jackTriviaScore;
            totalScore += fifth.carlosTriviaScore;
            totalScore += fifth.joshTriviaScore;
            totalScore += fifth.joeTriviaScore;

            binding.p2NameTextView.setText(String.valueOf(fifth.username));
            binding.p2JackTextView.setText(String.valueOf(fifth.jackTriviaScore));
            binding.p2CarlosTextView.setText(String.valueOf(fifth.carlosTriviaScore));
            binding.p2joshTextView.setText(String.valueOf(fifth.joshTriviaScore));
            binding.p2joeTextView.setText(String.valueOf(fifth.joeTriviaScore));
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