package com.example.project2group1;

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

    TextView questionTextView;
    TextView scoreTextView;
    TextView counterTextVIew;

    Button answerButton1;
    Button answerButton2;
    Button answerButton3;
    Button answerButton4;
    Button nextButton;

    ArrayList<Question> questionList = new ArrayList<>();
    int currentIndex = 0;
    int score = 0;
    String currentCorrectAnswer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geography_quiz);

        questionTextView = findViewById(R.id.tvQuestion);
        scoreTextView = findViewById(R.id.tvScore);
        counterTextVIew = findViewById(R.id.tvCounter);

        answerButton1 = findViewById(R.id.btnAnswer1);
        answerButton2 = findViewById(R.id.btnAnswer2);
        answerButton3 = findViewById(R.id.btnAnswer3);
        answerButton4 = findViewById(R.id.btnAnswer4);
        nextButton = findViewById(R.id.btnNextQuestion);

        answerButton1.setOnClickListener(v -> checkAnswer(answerButton1.getText()));
        answerButton2.setOnClickListener(v -> checkAnswer(answerButton2.getText()));
        answerButton3.setOnClickListener(v -> checkAnswer(answerButton3.getText()));
        answerButton4.setOnClickListener(v -> checkAnswer(answerButton4.getText()));

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
                }
        });

        loadQuestionsApi();
    }

    private void loadQuestionsApi() {
        new Thread(() -> {
            try {
                URL url = new URL("https://opentdb.com/api.php?amount=5&category=22&type=multiple");
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
                        Toast.makeText(GeographyQuizActivity.this, "No questions found", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GeographyQuizActivity.this, "Error loading questions", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private String htmlDecode(String text) {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString();
    }

    private void showQuestion() {
    }

    private void checkAnswer(CharSequence text) {
    }

}
