package com.example.project2group1;

import android.content.SharedPreferences;
import java.util.concurrent.Executors;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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
import java.util.Collections;

public class GeographyQuizActivity extends AppCompatActivity{
    // UI elements
    TextView questionTextView;
    TextView scoreTextView;
    TextView counterTextView;

    //Buttons
    Button answerButton1;
    Button answerButton2;
    Button answerButton3;
    Button answerButton4;
    Button nextButton;
    Button backToMenuButton;

    //List of questions that are pull from the api
    ArrayList<Question> questionList = new ArrayList<>();
    int currentIndex = 0;
    int score = 0;
    String currentCorrectAnswer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geography_quiz);

        //UI elements connection to layout
        questionTextView = findViewById(R.id.tvQuestion);
        scoreTextView = findViewById(R.id.tvScore);
        counterTextView = findViewById(R.id.tvCounter);

        answerButton1 = findViewById(R.id.btnAnswer1);
        answerButton2 = findViewById(R.id.btnAnswer2);
        answerButton3 = findViewById(R.id.btnAnswer3);
        answerButton4 = findViewById(R.id.btnAnswer4);
        nextButton = findViewById(R.id.btnNextQuestion);
        backToMenuButton = findViewById(R.id.btnBackToMenu);
        backToMenuButton.setVisibility(View.INVISIBLE);

        //Buttons clicked they check for answer using checkAnswer method
        answerButton1.setOnClickListener(v -> checkAnswer(answerButton1.getText()));
        answerButton2.setOnClickListener(v -> checkAnswer(answerButton2.getText()));
        answerButton3.setOnClickListener(v -> checkAnswer(answerButton3.getText()));
        answerButton4.setOnClickListener(v -> checkAnswer(answerButton4.getText()));

        //Logic for next question button
        nextButton.setOnClickListener(v -> {
                currentIndex++;
                if (currentIndex < questionList.size()) {
                    showQuestion();
                } else {
                    questionTextView.setText("You finished!\nScore: " + score + " / " + questionList.size());
                    answerButton1.setVisibility(View.INVISIBLE);
                    answerButton2.setVisibility(View.INVISIBLE);
                    answerButton3.setVisibility(View.INVISIBLE);
                    answerButton4.setVisibility(View.INVISIBLE);
                    nextButton.setVisibility(View.INVISIBLE);

                    updateLeaderboardScore();

                    backToMenuButton.setVisibility(View.VISIBLE);

                    SharedPreferences prefs = getSharedPreferences(Session.PREFS, MODE_PRIVATE);
                    String username = prefs.getString(Session.KEY_USERNAME, "guest");
                    int finalScore = score;

                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                        CategoryHighScoreDao dao = db.categoryHighScoreDao();

                        String category = "Geography";
                        CategoryHighScore existing = dao.getHighScore(username, category);

                        if (existing == null) {
                            CategoryHighScore hs = new CategoryHighScore();
                            hs.username = username;
                            hs.category = category;
                            hs.score = finalScore;
                            dao.insert(hs);
                        } else if (finalScore > existing.score) {
                            dao.updateScore(existing.id, finalScore);
                        }
                    });

                }
        });

        //backToMenuButton goes back to landing page and finishes activity
        backToMenuButton.setOnClickListener(v -> {
            Intent i = new Intent(GeographyQuizActivity.this, LandingPageActivity.class);
            startActivity(i);
            finish();
        });

        loadQuestionsApi();
    }

    /**
     * Loads questions from the Open Trivia DB API in a background thread,
     * parses them into Question objects, and then updates the UI on the main thread.
     */
    private void loadQuestionsApi() {
        new Thread(() -> {
            try {
                //connects to API and grabs 5 Geography questions
                URL url = new URL("https://opentdb.com/api.php?amount=5&category=22&type=multiple");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);

                //reads the response of API into StringBuilder
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();

                String jsonString = builder.toString();

                //Parse response into JSON
                JSONObject root = new JSONObject(jsonString);
                JSONArray resultsArray = root.getJSONArray("results");

                ArrayList<Question> tempList = new ArrayList<>();

                // Go through each question in results
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject obj = resultsArray.getJSONObject(i);
                    String question = htmlDecode(obj.getString("question"));
                    String correct = htmlDecode(obj.getString("correct_answer"));

                    // Decode HTML entities from the AP
                    JSONArray incorrectArray = obj.getJSONArray("incorrect_answers");
                    ArrayList<String> answers = new ArrayList<>();
                    answers.add(correct);
                    for (int j = 0; j < incorrectArray.length(); j++) {
                        answers.add(htmlDecode(incorrectArray.getString(j)));
                    }

                    //shuffles answer among the buttons
                    Collections.shuffle(answers);

                    // Create a Question object and add it to a temporary list
                    Question q = new Question();
                    q.questionText = question;
                    q.correctAnswer = correct;
                    q.answerList = answers;
                    tempList.add(q);
                }
                // Switch back to the main (UI) thread to update the UI
                runOnUiThread(() -> {
                    // Replace old questions with newly loaded ones
                    questionList.clear();
                    questionList.addAll(tempList);
                    currentIndex = 0;
                    score = 0;

                    // Show the first question or an error message if none were found
                    if (questionList.size() > 0) {
                        showQuestion();
                    } else {
                        Toast.makeText(GeographyQuizActivity.this, "No questions found", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                // Log error and show a toast if something goes wrong
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GeographyQuizActivity.this, "Error loading questions", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
    //Helper method to decode HTML coded strings from api
    private String htmlDecode(String text) {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString();
    }

    //Displays current question and updates UI elements
    void showQuestion() {
        //currentIndex valid check
        if (currentIndex < 0 || currentIndex >= questionList.size()) {
            return;
        }

        //Grabs current questions
        Question q = questionList.get(currentIndex);
        currentCorrectAnswer = q.correctAnswer;

        //Sets question text
        questionTextView.setText(q.questionText);

        //Score update and question counter update
        scoreTextView.setText("Score: " + score);
        counterTextView.setText("Question " + (currentIndex + 1) + " / " + questionList.size());

        answerButton1.setVisibility(View.VISIBLE);
        answerButton2.setVisibility(View.VISIBLE);
        answerButton3.setVisibility(View.VISIBLE);
        answerButton4.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);

        answerButton1.setText(q.answerList.get(0));
        answerButton2.setText(q.answerList.get(1));
        answerButton3.setText(q.answerList.get(2));
        answerButton4.setText(q.answerList.get(3));
    }

    //Checks if answer that was chosen is correct and updates score with a toast message
    private void checkAnswer(CharSequence text) {
        String chosen = text.toString();
        if(chosen.equals(currentCorrectAnswer)) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            score++;
            scoreTextView.setText("Score: " + score);
        } else {
            Toast.makeText(this, "Wrong! Correct Answer was: " + currentCorrectAnswer, Toast.LENGTH_SHORT).show();

        }
    }

    //Update app leaderboard for the user
    private void updateLeaderboardScore() {

        AppDatabase db = AppDatabase.getInstance(this);
        LeaderboardDao dao = db.leaderboardDao();
        String username = LandingPageActivity.getUsername();

        AppDatabase.dbExecutor.execute(() -> {

            LeaderboardEntity user = dao.getByUsername(username);
            if (user == null) return;

            if (score <= user.carlosTriviaScore) return;

            user.carlosTriviaScore = score;

            user.totalScore =
                    user.jackTriviaScore +
                            user.carlosTriviaScore +
                            user.joshTriviaScore +
                            user.joeTriviaScore;

            dao.update(user);

        });

    }

}
