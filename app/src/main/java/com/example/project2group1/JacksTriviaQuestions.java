package com.example.project2group1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2group1.databinding.ActivityJacksBinding;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class JacksTriviaQuestions extends AppCompatActivity {

    ActivityJacksBinding binding;
    String [][] answers = new String[10][5]; // ten questions, 4 possible answers + the question
    private int highScore = 0;
    private int score = 0;
    private int currentIndex = 0;
    private int correctIndex;
    private boolean roundOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityJacksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        loadQuestions("basketball_trivia_2004_present.csv");

        // tried to code each button set up in this order to keep consistency
        binding.answerTopLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roundOver) endGameView(0);
                else checkAnswer(0);
            }
        });

        binding.answerTopRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roundOver) endGameView(1);
                else checkAnswer(1);

            }
        });

        binding.answerBottomLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roundOver) endGameView(2);
                else checkAnswer(2);
            }
        });

        binding.answerBottomRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roundOver) endGameView(3);
                else checkAnswer(3);
            }
        });

        showQuestion(currentIndex);

    }

    public void checkAnswer(int buttonClicked) {

        if (buttonClicked == correctIndex){
            toastMaker("Correct!!");
            score += 1;
        }
        else {
            toastMaker("Incorrect :(");
        }

        String score_as_String;

        score_as_String = String.valueOf(score);

        binding.actualScoreTextView.setText(score_as_String);
        currentIndex++;
        showQuestion(currentIndex);

    }

    private void updateLeaderboardScore(int score) {

        AppDatabase db = AppDatabase.getInstance(this);
        LeaderboardDao dao = db.leaderboardDao();
        String username = LandingPageActivity.getUsername();

        AppDatabase.dbExecutor.execute(() -> {

            LeaderboardEntity user = dao.getByUsername(username);
            if (user == null) return;

            if (score <= user.jackTriviaScore) return;

            user.jackTriviaScore = score;

            user.totalScore =
                            user.jackTriviaScore +
                            user.carlosTriviaScore +
                            user.joshTriviaScore +
                            user.joeTriviaScore;

            dao.update(user);

        });

    }



    @SuppressLint("SetTextI18n")
    private void endGameView() {
        //probably a better way to hide the textViews but this works
        binding.scoreTextView.setText("");
        binding.actualScoreTextView.setText("");

        String scorePlaceHolder = String.valueOf(score);
        binding.questionTextView.setText("Good Job!!\nYour Score: " + scorePlaceHolder);
        updateLeaderboardScore(score);

        binding.answerTopLeftButton.setText("Logout"); // userExitClick = 0
        binding.answerTopRightButton.setText("LeaderBoard"); // userExitClick = 1
        binding.answerBottomLeftButton.setText("Back"); // userExitClick = 2
        binding.answerBottomRightButton.setText("Play Again"); // userExitClick = 3

       roundOver = true;
    }

    @SuppressLint("SetTextI18n")
    private void endGameView(int userExitClicked) {

        if (userExitClicked == 0) {
            startActivity(LoginScreen.loginIntentFactory(getApplicationContext()));
        }
        else if (userExitClicked == 1) {
            startActivity(LeaderBoard.leaderboardIntentFactory(getApplicationContext()));
        }
        else if (userExitClicked == 2) {
            startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(), LoginScreen.getUserName()));
        }
        else if (userExitClicked == 3) {

            binding.questionHeaderTextView.setText("Question: ");
            binding.scoreTextView.setText("Score: ");
            binding.actualScoreTextView.setText("0");

            currentIndex = 0;
            score = 0;
            roundOver = false;

            showQuestion(currentIndex);
        }
        else{
            toastMaker("Not accepted exit: " + userExitClicked);
        }

    }

    public void showQuestion(int index) {

        if (index >= answers.length) {

            binding.displayQuestionCountTextView.setText("");
            binding.questionHeaderTextView.setText("");

            toastMaker("All questions done");
            currentIndex = 0;
            if (score > highScore) {
                toastMaker("New High Score!!!");
                highScore = score;
            }
           // binding.questionTextView.setText("High Score: " + highScore);

            endGameView();
        }

        else {

            String questionCount = (index + 1) + "/10";
            binding.displayQuestionCountTextView.setText(questionCount);

            String[] question = answers[index];
            binding.questionTextView.setText(question[0]);

            ArrayList<String> choices = new ArrayList<>();
            for (int i = 1; i < question.length; i++) choices.add(question[i]);
            Collections.shuffle(choices);
            correctIndex = choices.indexOf(question[1]);

            binding.answerTopLeftButton.setText(choices.get(0));
            binding.answerTopRightButton.setText(choices.get(1));
            binding.answerBottomLeftButton.setText(choices.get(2));
            binding.answerBottomRightButton.setText(choices.get(3));
        }
    }

    // plan to modify this later to get the questions from an API, but for now this will have to do
    private void loadQuestions(String filename) {

        ArrayList<String[]> allQuestions = new ArrayList<>();

        try{
            Scanner s = new Scanner(getAssets().open(filename));
            while (s.hasNext()) {
                String line = s.nextLine().trim();
                if (line.startsWith("#") || line.isEmpty()) continue;

                String [] parts = line.split(",",-1);
                if (parts.length != 12) continue;
                for (int i = 0; i < 12; i++) parts[i] = parts[i].trim();


                String[] question = new String[5];
                question[0] = parts[0];
                question[1] = parts[1];

                int index1 = (int)(Math.random() * 10) + 2;
                int index2 = (int)(Math.random() * 10) + 2;
                int index3 = (int)(Math.random() * 10) + 2;

                while (index1 == index2 || index2 == index3 || index1 == index3) {
                    index1 = (int)(Math.random() * 9) + 2;
                    index2 = (int)(Math.random() * 9) + 2;
                    index3 = (int)(Math.random() * 9) + 2;
                }

                question[2] = parts[index1];
                question[3] = parts[index2];
                question[4] = parts[index3];

                allQuestions.add(question);
            }
            s.close();
        }
        catch (FileNotFoundException e) {
            toastMaker("Couldn't find the file");
            return;
        } catch (IOException e) {
            toastMaker("Couldn't open the file");
            return;
        }

        for (int i = 0; i < answers.length; i++) {

            int randomIndex = (int)(Math.random() * allQuestions.size());
            if (answersContainsQuestion(allQuestions.get(randomIndex))) {
                i--;
                continue;
            }
            answers[i] = allQuestions.get(randomIndex);

        }
    }

    private boolean answersContainsQuestion(String[] list) {

        if (list == null || list.length == 0) return false;

        for (String[] answer : answers) {
            if (answer == null || answer.length == 0 || answer[0] == null) continue;
            if (answer[0].equals(list[0])) return true;
        }

        return false;

    }

    static Intent jackIntentFactory(Context context) {
        return new Intent(context, JacksTriviaQuestions.class);
    }

    public void toastMaker(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}