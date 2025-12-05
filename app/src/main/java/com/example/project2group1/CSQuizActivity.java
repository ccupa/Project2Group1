package com.example.project2group1;

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


        answerButton1.setOnClickListener(v -> checkAnswer(answerButton1.getText()));
        answerButton2.setOnClickListener(v -> checkAnswer(answerButton2.getText()));
        answerButton3.setOnClickListener(v -> checkAnswer(answerButton3.getText()));
        answerButton4.setOnClickListener(v -> checkAnswer(answerButton4.getText()));

        nextButton.setOnClickListener(v -> {
            goToNextQuestion();
        });

        loadQuestionsApi();
        // Back button only visible at the very end (also set to GONE in XML)
        btnBackToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(CSQuizActivity.this, LandingPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

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
                runOnUiThread(() -> Toast.makeText(CSQuizActivity.this, "Error loading questions", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private String htmlDecode(String text) {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString();
    }

    private void showQuestion() {
        if (currentIndex < 0 || currentIndex >= questionList.size()) {
            return;
        }

        Question q = questionList.get(currentIndex);
        currentCorrectAnswer = q.correctAnswer;

        questionTextView.setText(q.questionText);

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

    private void goToNextQuestion() {
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
            btnBackToMenu.setVisibility(Button.VISIBLE);

        }
    }

    private void checkAnswer(CharSequence text) {
        String chosen = text.toString();
        if (chosen.equals(currentCorrectAnswer)) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            score++;
            scoreTextView.setText("Score: " + score);

        } else {
            Toast.makeText(this, "Wrong! Correct Answer was: " + currentCorrectAnswer, Toast.LENGTH_SHORT).show();
        }

        goToNextQuestion();
    }

}
