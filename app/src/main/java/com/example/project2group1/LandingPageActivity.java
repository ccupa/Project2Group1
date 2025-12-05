package com.example.project2group1;

import java.util.concurrent.Executors;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LandingPageActivity extends AppCompatActivity {

    private static String un; //username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences(Session.PREFS, MODE_PRIVATE);
        if (!prefs.getBoolean(Session.KEY_LOGGED_IN, false)) {
            startActivity(new Intent(this, LoginScreen.class));
            finish();
            return;
        }

        String username = prefs.getString(Session.KEY_USERNAME, "");
        un = username;
        boolean isAdmin = prefs.getBoolean(Session.KEY_IS_ADMIN, false);

        TextView tvGeoHighScore = findViewById(R.id.tvGeoHighScore);

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            CategoryHighScoreDao dao = db.categoryHighScoreDao();
            CategoryHighScore hs = dao.getHighScore(username, "Geography");

            int bestScore = (hs != null) ? hs.score : 0;

            runOnUiThread(() ->
                    tvGeoHighScore.setText("High Score: " + bestScore)
            );
        });

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Welcome, " + username);

        Button btnAdmin = findViewById(R.id.btnAdmin);
        if (isAdmin) {
            btnAdmin.setVisibility(View.VISIBLE);
            btnAdmin.setOnClickListener(v ->
                    startActivity(new Intent(this, AdminActivity.class))
            );
        } else {
            // You can use INVISIBLE if you want the space to remain
            btnAdmin.setVisibility(View.GONE);
        }

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginScreen.class));
            finish();
        });

        //TODO
        //change to intent factory or else might get credit off
        Button geoBtn = findViewById(R.id.carlosTriviaButton);
        geoBtn.setOnClickListener(v ->
                startActivity(new Intent(this, GeographyQuizActivity.class)));
        //-------------//
        Button basketballBtn = findViewById(R.id.jackTriviaButton);
        basketballBtn.setOnClickListener(v ->
                startActivity(new Intent(this, JacksTriviaQuestions.class)));
        //-------------//
        Button computer_Science = findViewById(R.id.joshTriviaButton);
        computer_Science.setOnClickListener(v ->
                startActivity(new Intent(this, CSQuizActivity.class)));
        //-------------//
        Button josephButton = findViewById(R.id.josephTriviaButton);
        josephButton.setOnClickListener(v -> {
            Intent intent = new Intent(LandingPageActivity.this, PokemonQuizActivity.class);
            startActivity(intent);
        });


        Button leaderboardBtn = findViewById(R.id.leaderboardButton);
        leaderboardBtn.setOnClickListener(v ->
                startActivity(LeaderBoard.leaderboardIntentFactory(getApplicationContext())));

    }

    public static String getUsername() {
        return un;
    }

    static Intent landingPageIntentFactory(Context context) {
        return new Intent(context, LandingPageActivity.class);
    }

}
