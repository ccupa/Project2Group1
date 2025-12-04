package com.example.project2group1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class PokemonQuizActivity extends AppCompatActivity {
    private TextView tvCounter, tvScore, tvQuestion;
    private Button btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4, btnNextQuestion;

    private int questionNumber = 1;
    private int score = 0;
    private String currentCorrectAnswer;

    private Random random = new Random();

    // Basic list of Pokémon types for fake answers
    private static final ArrayList<String> ALL_TYPES = new ArrayList<>(
            Arrays.asList(
                    "normal", "fire", "water", "grass", "electric",
                    "ice", "fighting", "poison", "ground", "flying",
                    "psychic", "bug", "rock", "ghost", "dark",
                    "dragon", "steel", "fairy"
            )
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geography_quiz); // reuse same layout

        tvCounter = findViewById(R.id.tvCounter);
        tvScore = findViewById(R.id.tvScore);
        tvQuestion = findViewById(R.id.tvQuestion);

        btnAnswer1 = findViewById(R.id.btnAnswer1);
        btnAnswer2 = findViewById(R.id.btnAnswer2);
        btnAnswer3 = findViewById(R.id.btnAnswer3);
        btnAnswer4 = findViewById(R.id.btnAnswer4);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);

        tvScore.setText("Score: 0");
        tvCounter.setText("Question: 1/5");

        View.OnClickListener answerClickListener = v -> {
            Button b = (Button) v;
            checkAnswer(b.getText());
        };

        btnAnswer1.setOnClickListener(answerClickListener);
        btnAnswer2.setOnClickListener(answerClickListener);
        btnAnswer3.setOnClickListener(answerClickListener);
        btnAnswer4.setOnClickListener(answerClickListener);

        btnNextQuestion.setOnClickListener(v -> loadNewPokemonQuestion());

        loadNewPokemonQuestion();
    }
}