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
 * Activity that runs a Computer Science multiple-choice quiz using
 * questions loaded from the OpenTDB API.
 *
 * <p>This activity:
 * <ul>
 *     <li>Fetches 10 CS questions from a remote API</li>
 *     <li>Displays one question at a time with four possible answers</li>
 *     <li>Keeps track of the user's score</li>
 *     <li>Updates both category high scores and the global leaderboard
 *         when the quiz is finished</li>
 * </ul>
 * </p>
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
     * Initializes the quiz UI, sets up button listeners,
     * and starts loading questions from the API.
     *
     * @param savedInstanceState previously saved state (unused)
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

        // Hide back button during the quiz; will show it only at the end
        btnBackToMenu.setVisibility(View.GONE);

        // Answer button listeners
        answerButton1.setOnClickListener(v -> checkAnswer(answerButton1.getText()));
        answerButton2.setOnClickListener(v -> checkAnswer(answerButton2.getText()));
        answerButton3.setOnClickListener(v -> checkAnswer(answerButton3.getText()));
        answerButton4.setOnClickListener(v -> checkAnswer(answerButton4.getText()));

        // Next button moves to the next question (or ends quiz)
        nextButton.setOnClickListener(v -> goToNextQuestion());

        // Back to main menu (LandingPageActivity) after quiz completion
        btnBackToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(CSQuizActivity.this, LandingPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Load questions from API
        loadQuestionsApi();
    }

    /**
     * Loads 10 computer science trivia questions from the OpenTDB API.
     *
     * <p>This method runs on a background thread, parses the JSON response,
     * constructs {@code Question} objects, and populates {@link #questionList}.
     * Once loaded, it resets the score and shows the first question on the UI thread.</p>
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

                runOnUiThread(() -> {
                    questionList.clear();
                    questionList.addAll(tempList);
                    currentIndex = 0;
                    score = 0;

                    if (questionList.size() > 0) {
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
     * Decodes HTML-encoded text from the API into plain text.
     * For example, converts entities like {@code &quot;} back to quotes.
     *
     * @param text the HTML-encoded string
     * @return a decoded string safe for display in a TextView
     */
    private String htmlDecode(String text) {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString();
    }

    /**
     * Displays the current question and possible answers on screen.
     *
     * <p>If {@link #currentIndex} is out of bounds, this method safely returns
     * without updating the UI.</p>
     */
    private void showQuestion() {
        if (currentIndex < 0 || currentIndex >= questionList.size()) {
            return;
        }

        Question q = questionList.get(currentIndex);
        currentCorrectAnswer = q.correctAnswer;

        questionTextView.setText(q.questionText);

        scoreTextView.setText("Score: " + score);
        counterTextView.setText("Question " + (currentIndex + 1) + " / " + questionList.size());

        // Make sure quiz UI is visible
        answerButton1.setVisibility(View.VISIBLE);
        answerButton2.setVisibility(View.VISIBLE);
        answerButton3.setVisibility(View.VISIBLE);
        answerButton4.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);

        // Back button stays hidden until quiz is over
        btnBackToMenu.setVisibility(View.GONE);

        answerButton1.setText(q.answerList.get(0));
        answerButton2.setText(q.answerList.get(1));
        answerButton3.setText(q.answerList.get(2));
        answerButton4.setText(q.answerList.get(3));
    }

    /**
     * Advances to the next question in the quiz, or finishes the quiz
     * if there are no more questions left.
     *
     * <p>When the quiz is finished, this method:
     * <ul>
     *     <li>Shows the final score</li>
     *     <li>Updates the leaderboard and category high score</li>
     *     <li>Hides quiz controls</li>
     *     <li>Shows the "Back to Menu" button</li>
     * </ul>
     * </p>
     */
    private void goToNextQuestion() {
        currentIndex++;
        if (currentIndex < questionList.size()) {
            showQuestion();
        } else {
            // Quiz finished
            questionTextView.setText("You finished!\nScore: " + score + " / " + questionList.size());
            updateLeaderboardScore();
            counterTextView.setText(""); // optional: clear counter

            // Hide quiz buttons
            answerButton1.setVisibility(View.INVISIBLE);
            answerButton2.setVisibility(View.INVISIBLE);
            answerButton3.setVisibility(View.INVISIBLE);
            answerButton4.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.INVISIBLE);

            // Show back-to-menu button now
            btnBackToMenu.setVisibility(View.VISIBLE);

            // Save high score in Room (background thread)
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
     * Checks whether the selected answer is correct and updates the score.
     *
     * <p>If the answer is correct, the score is incremented and shown on screen.
     * Regardless of correctness, the quiz then moves on to the next question.</p>
     *
     * @param text the text of the button the user tapped
     */
    private void checkAnswer(CharSequence text) {
        String chosen = text.toString();
        if (chosen.equals(currentCorrectAnswer)) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            score++;
            scoreTextView.setText("Score: " + score);
        } else {
            Toast.makeText(this,
                    "Wrong! Correct Answer was: " + currentCorrectAnswer,
                    Toast.LENGTH_SHORT).show();
        }

        goToNextQuestion();
    }

    /**
     * Updates the global leaderboard for the current user after the quiz ends.
     *
     * <p>This method:
     * <ul>
     *     <li>Retrieves the logged-in user's leaderboard record</li>
     *     <li>Only updates {@code joshTriviaScore} if the new score is higher</li>
     *     <li>Recalculates {@code totalScore} from all category scores</li>
     *     <li>Runs entirely on a background thread using {@link AppDatabase#dbExecutor}</li>
     * </ul>
     * </p>
     */
    private void updateLeaderboardScore() {

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
