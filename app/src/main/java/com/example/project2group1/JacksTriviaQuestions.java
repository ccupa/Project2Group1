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
import java.util.List;
import java.util.Scanner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://triviaapp@ec2-13-52-247-185.us-west-1.compute.amazonaws.com:3000/random10")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TriviaApi api = retrofit.create(TriviaApi.class);

        api.getRandomTen().enqueue(new Callback<List<TriviaQuestion>>() {
            @Override
            public void onResponse(Call<List<TriviaQuestion>> call, Response<List<TriviaQuestion>> response) {

                if (!response.isSuccessful()) {
                    toastMaker("Bad response from api");
                    toastMaker(String.valueOf(response.code()));
                    loadQuestions_backup("basketball_trivia_2004_present.csv");
                    return;
                }

                List<TriviaQuestion> results = response.body();
                if (results == null) {
                    toastMaker("No questions received");
                    loadQuestions_backup("basketball_trivia_2004_present.csv");
                    return;
                }

                for (int i = 0; i < answers.length; i++) {

                    TriviaQuestion q = results.get(i);
                    String[] formated_question = new String[5];

                    formated_question[0] = q.question;
                    formated_question[1] = q.correct_answer;

                    List<String> wrong_answers = q.incorrect_answers;
                    Collections.shuffle(wrong_answers);

                    formated_question[2] = wrong_answers.get(0);
                    formated_question[3] = wrong_answers.get(1);
                    formated_question[4] = wrong_answers.get(2);

                    answers[i] = formated_question;
                }

            }

            @Override
            public void onFailure(Call<List<TriviaQuestion>> call, Throwable throwable) {
                loadQuestions_backup("basketball_trivia_2004_present.csv");
            }
        });



    }

    public void loadQuestions_backup(String filename) {

        ArrayList<String[]> allQuestions = new ArrayList<>();

        try{
            Scanner s = new Scanner(getAssets().open(filename));
            while (s.hasNext()) {
                // read the line
                String line = s.nextLine().trim();
                if (line.startsWith("#") || line.isEmpty()) continue;

                // parse through the parts of the line and store them
                String [] parts = line.split(",",-1);
                if (parts.length != 12) continue;
                for (int i = 0; i < 12; i++) parts[i] = parts[i].trim();

                // begin inputting the question
                // [question, correct, wrong, wrong, wrong]
                String[] question = new String[5];
                question[0] = parts[0]; //question
                question[1] = parts[1]; //correct answer

                // get three indexes so the wrong answers are always random
                int index1 = (int)(Math.random() * 10) + 2;
                int index2 = (int)(Math.random() * 10) + 2;
                int index3 = (int)(Math.random() * 10) + 2;

                while (index1 == index2 || index2 == index3 || index1 == index3) {
                    index1 = (int)(Math.random() * 9) + 2;
                    index2 = (int)(Math.random() * 9) + 2;
                    index3 = (int)(Math.random() * 9) + 2;
                }

                // add incorrect answers to question
                question[2] = parts[index1];
                question[3] = parts[index2];
                question[4] = parts[index3];

                // add question to array list
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

        // choose ten random questions and put them into answers
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

    class TriviaQuestion{
        public String question;
        public String correct_answer;
        public List<String> incorrect_answers;
    }

    interface TriviaApi {
        @GET("random10")
        Call<List<TriviaQuestion>> getRandomTen();
    }

    static Intent jackIntentFactory(Context context) {
        return new Intent(context, JacksTriviaQuestions.class);
    }

    public void toastMaker(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}

