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

    private TextView tvGeoHighScore;
    private TextView tvBasketBallHighScore;
    private TextView tvPokemonHighScore;
    private TextView tvComputerScienceHighScore;

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

        tvGeoHighScore = findViewById(R.id.tvGeoHighScore);
        tvBasketBallHighScore = findViewById(R.id.tvBasketballHighScore);
        tvPokemonHighScore = findViewById(R.id.tvPokemonHighScore);
        tvComputerScienceHighScore = findViewById(R.id.tvComputerScienceHighScore);

        loadHighScores(username);

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

    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences(Session.PREFS, MODE_PRIVATE);
        String username = prefs.getString(Session.KEY_USERNAME, "");
        if (!username.isEmpty()) {
            loadHighScores(username);
        }
    }

    private void loadHighScores(String username) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            CategoryHighScoreDao dao = db.categoryHighScoreDao();

            CategoryHighScore basketBallScore = dao.getHighScore(username, "Basketball");
            int ballBest = (basketBallScore != null) ? basketBallScore.score : 0;

            CategoryHighScore geoScore = dao.getHighScore(username, "Geography");
            int geographyBest = (geoScore != null) ? geoScore.score : 0;

            CategoryHighScore pokemonScore = dao.getHighScore(username, "Pokemon");
            int pokeBest = (pokemonScore != null) ? pokemonScore.score : 0;

            CategoryHighScore computerScore = dao.getHighScore(username, "Computer Science");
            int computerBest = (computerScore != null) ? computerScore.score : 0;

            runOnUiThread(() -> {
                tvGeoHighScore.setText("High Score: " + geographyBest);
                tvBasketBallHighScore.setText("High Score: " + ballBest);
                tvPokemonHighScore.setText("High Score: " + pokeBest);
                tvComputerScienceHighScore.setText("High Score: " + computerBest);
            });
        });
    }

    public static String getUsername() {
        return un;
    }

    static Intent landingPageIntentFactory(Context context) {
        return new Intent(context, LandingPageActivity.class);
    }

}
