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

    private void loadNewPokemonQuestion() {
        tvQuestion.setText("Loading Pokémon...");

        // Run network code on a background thread
        new Thread(() -> {
            try {
                // Pick a random Pokémon ID (1–151 for Gen 1, change range if you want)
                int id = random.nextInt(151) + 1;
                URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + id);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();

                String json = builder.toString();
                JSONObject root = new JSONObject(json);

                String name = root.getString("name"); // e.g. "pikachu"
                JSONArray typesArray = root.getJSONArray("types");
                // Get the first type as the "main" type
                JSONObject firstTypeObj = typesArray.getJSONObject(0)
                        .getJSONObject("type");
                String typeName = firstTypeObj.getString("name"); // e.g. "electric"

                currentCorrectAnswer = capitalize(typeName);

                // Build answer choices
                ArrayList<String> options = new ArrayList<>();
                options.add(currentCorrectAnswer);

                // Add 3 random wrong types
                ArrayList<String> shuffledTypes = new ArrayList<>(ALL_TYPES);
                Collections.shuffle(shuffledTypes);
                for (String t : shuffledTypes) {
                    String cap = capitalize(t);
                    if (!cap.equals(currentCorrectAnswer)) {
                        options.add(cap);
                    }
                    if (options.size() == 4) break;
                }

                Collections.shuffle(options);

                runOnUiThread(() -> {
                    tvQuestion.setText("What is the main type of " + capitalize(name) + "?");
                    tvCounter.setText("Question: " + questionNumber + "/5");

                    btnAnswer1.setText(options.get(0));
                    btnAnswer2.setText(options.get(1));
                    btnAnswer3.setText(options.get(2));
                    btnAnswer4.setText(options.get(3));
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        tvQuestion.setText("Error loading Pokémon. Check your internet.")
                );
            }
        }).start();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }


    private void checkAnswer(CharSequence text) {
        String chosen = text.toString();
        if (chosen.equals(currentCorrectAnswer)) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            score++;
            tvScore.setText("Score: " + score);
        } else {
            Toast.makeText(this,
                    "Wrong! Correct answer was: " + currentCorrectAnswer,
                    Toast.LENGTH_SHORT).show();
        }

        questionNumber++;
        if (questionNumber <= 5) {
            loadNewPokemonQuestion();
        } else {
            tvQuestion.setText("Quiz finished! Final score: " + score + "/5");
        }
    }
}
}