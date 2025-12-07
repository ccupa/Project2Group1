package com.example.project2group1;

import android.content.Intent;
import android.content.SharedPreferences;
import java.util.concurrent.Executors;
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

/**
 * Activity that runs a 10-question Computer Science quiz using the OpenTDB API.
 *
 * <p>This activity loads questions asynchronously, displays one question at a time,
 * tracks the user's score, and updates both the category-specific high score and
 * the global leaderboard stored in Room.</p>
 *
 * <p>The quiz ends when all questions have been answered, at which point the UI
 * hides the answer buttons and displays a button to return to the main menu.</p>
 */
public class CSQuizActivity extends AppCompatActivity {

    TextView questionTextView;
    TextView scoreTextView;
    TextView counterTextView;

    Button answerButton1;
    Button answerButton2;
    Button answerButton3;
    Button answerButton4;
    Button nextButton;
    Button btnBackToMenu;

    ArrayList<Question> questionList = new ArrayList<>();
    int currentIndex = 0;
    int score = 0;
    String currentCorrectAnswer = "";

    /**
     * Initializes quiz UI components, sets button listeners,
     * and begins loading questions from the API.
     *
     * @param savedInstanceState previous activity state (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joshs);

        questionTextView = findViewById(R.id.tvQuestion);
        scoreTextView = findViewById(R.id.tvScore);
        counterTextView = findViewById(R.id.tvCounter);

        answerButton1 = findViewById(R.id.btnAnswer1);
        answerButton2 = findViewById(R.id.btnAnswer2);
        answerButton3 = findViewById(R.id.btnAnswer3);
        answerButton4 = findViewById(R.id.btnAnswer4);
        nextButton = findViewById(R.id.btnNextQuestion);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);

        btnBackToMenu.setVisibility(View.GONE);

        // Answer button listeners
        answerButton1.setOnClickListener(v -> checkAnswer(answerButton1.getText()));
        answerButton2.setOnClickListener(v -> checkAnswer(answerButton2.getText()));
        answerButton3.setOnClickListener(v -> checkAnswer(answerButton3.getText()));
        answerButton4.setOnClickListener(v -> checkAnswer(answerButton4.getText()));

        // Next button listener
        nextButton.setOnClickListener(v -> goToNextQuestion());

        // Return to main menu
        btnBackToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(CSQuizActivity.this, LandingPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        loadQuestionsApi();
    }

    /**
     * Loads 10 multiple-choice computer science questions from the OpenTDB API.
     *
     * <p>This runs on a background thread to avoid blocking the UI. Once fetched,
     * questions are decoded, parsed, shuffled, and stored in {@code questionList}.</p>
     *
     * <p>If API loading fails, a Toast is shown to the user.</p>
     */
    private void loadQuestionsApi() {
        new Thread(() -> {
            try {
                URL url = new URL("https://opentdb.com/api.php?amount=10&category=18&type=multiple");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();

                String jsonString = builder.toString();
                JSONObject root = new JSONObject(jsonString);

                JSONArray resultsArray = root.getJSONArray("results");
                ArrayList<Question> tempList = new ArrayList<>();

                // Parse and store all questions
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject obj = resultsArray.getJSONObject(i);
                    String question = htmlDecode(obj.getString("question"));
                    String correct = htmlDecode(obj.getString("correct_answer"));

                    JSONArray incorrectArray = obj.getJSONArray("incorrect_answers");
                    ArrayList<String> answers = new ArrayList<>();
                    answers.add(correct);
                    for (int j = 0; j < incorrectArray.length(); j++) {
                        answers.add(htmlDecode(incorrectArray.getString(j)));
                    }
                    Collections.shuffle(answers);

                    Question q = new Question();
                    q.questionText = question;
                    q.correctAnswer = correct;
                    q.answerList = answers;
                    tempList.add(q);
                }

                // Update UI on main thread
                runOnUiThread(() -> {
                    questionList.clear();
                    questionList.addAll(tempList);
                    currentIndex = 0;
                    score = 0;

                    if (!questionList.isEmpty()) {
                        showQuestion();
                    } else {
                        Toast.makeText(CSQuizActivity.this, "No questions found", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(CSQuizActivity.this, "Error loading questions", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    /**
     * Converts HTML-encoded strings from the API (e.g. &quot;, &amp;, &lt;)
     * into normal displayable characters.
     *
     * @param text the encoded text
     * @return decoded plain text
     */
    private String htmlDecode(String text) {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString();
    }

    /**
     * Displays the current question and updates the score counter and question counter.
     *
     * <p>If {@code currentIndex} is out of bounds, no action is taken.</p>
     */
    private void showQuestion() {
        if (currentIndex < 0 || currentIndex >= questionList.size()) return;

        Question q = questionList.get(currentIndex);
        currentCorrectAnswer = q.correctAnswer;

        questionTextView.setText(q.questionText);
        scoreTextView.setText("Score: " + score);
        counterTextView.setText("Question " + (currentIndex + 1) + " / " + questionList.size());

        // Ensure elements are visible
        answerButton1.setVisibility(View.VISIBLE);
        answerButton2.setVisibility(View.VISIBLE);
        answerButton3.setVisibility(View.VISIBLE);
        answerButton4.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        btnBackToMenu.setVisibility(View.GONE);

        // Populate button text
        answerButton1.setText(q.answerList.get(0));
        answerButton2.setText(q.answerList.get(1));
        answerButton3.setText(q.answerList.get(2));
        answerButton4.setText(q.answerList.get(3));
    }

    /**
     * Advances the quiz to the next question. If all questions are completed,
     * displays the final score, hides answer buttons, and shows the menu button.
     *
     * <p>Also updates Room database values for both category high score and main leaderboard.</p>
     */
    private void goToNextQuestion() {
        currentIndex++;

        if (currentIndex < questionList.size()) {
            showQuestion();
        } else {
            // Quiz complete
            questionTextView.setText("You finished!\nScore: " + score + " / " + questionList.size());
            counterTextView.setText("");

            // Hide quiz UI
            answerButton1.setVisibility(View.INVISIBLE);
            answerButton2.setVisibility(View.INVISIBLE);
            answerButton3.setVisibility(View.INVISIBLE);
            answerButton4.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.INVISIBLE);

            // Show navigation button
            btnBackToMenu.setVisibility(View.VISIBLE);

            // Update leaderboard scores
            updateLeaderboardScore(score);

            // Update category high score in Room
            SharedPreferences prefs = getSharedPreferences(Session.PREFS, MODE_PRIVATE);
            String username = prefs.getString(Session.KEY_USERNAME, "guest");
            int finalScore = score;

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                CategoryHighScoreDao dao = db.categoryHighScoreDao();

                String category = "Computer Science";
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
    }

    /**
     * Validates the selected answer, updates the user's score,
     * shows feedback via Toast, and then loads the next question.
     *
     * @param text the text of the selected answer button
     */
    private void checkAnswer(CharSequence text) {
        String chosen = text.toString();

        if (chosen.equals(currentCorrectAnswer)) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            score++;
            scoreTextView.setText("Score: " + score);
        } else {
            Toast.makeText(
                    this,
                    "Wrong! Correct Answer was: " + currentCorrectAnswer,
                    Toast.LENGTH_SHORT
            ).show();
        }

        goToNextQuestion();
    }

    /**
     * Updates the leaderboard entry for the currently logged-in user.
     *
     * <p>The update only occurs if the new score exceeds the user's previous
     * score for this category. After updating the category score, the user's
     * {@code totalScore} is recalculated as the sum of all quiz category scores.</p>
     *
     * <p>This operation runs asynchronously through Room's database executor.</p>
     *
     * @param score the final quiz score achieved by the player
     */
    private void updateLeaderboardScore(int score) {
        AppDatabase db = AppDatabase.getInstance(this);
        LeaderboardDao dao = db.leaderboardDao();
        String username = LandingPageActivity.getUsername();

        AppDatabase.dbExecutor.execute(() -> {
            LeaderboardEntity user = dao.getByUsername(username);
            if (user == null) return;

            if (score <= user.joshTriviaScore) return;

            user.joshTriviaScore = score;

            user.totalScore =
                    user.jackTriviaScore +
                            user.carlosTriviaScore +
                            user.joshTriviaScore +
                            user.joeTriviaScore;

            dao.update(user);
        });
    }
}
