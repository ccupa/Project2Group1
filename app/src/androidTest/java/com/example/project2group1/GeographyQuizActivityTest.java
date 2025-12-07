package com.example.project2group1;

import static org.junit.Assert.assertEquals;
import android.widget.Button;
import android.widget.TextView;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class GeographyQuizActivityTest {

    @Rule
    public ActivityScenarioRule<GeographyQuizActivity> rule =
            new ActivityScenarioRule<>(GeographyQuizActivity.class);

    @Test
    public void clickingCorrectAnswer() {
        rule.getScenario().onActivity(activity -> {
            Question q = new Question();
            q.questionText = "What is capital of France?";
            q.correctAnswer = "Paris";
            q.answerList = new ArrayList<>(Arrays.asList("Paris", "London", "Berlin", "Rome"));

            activity.questionList.clear();
            activity.questionList.add(q);
            activity.currentIndex = 0;
            activity.score = 0;

            activity.showQuestion();

            Button btnAnswer1 = activity.findViewById(R.id.btnAnswer1);
            TextView tvScore = activity.findViewById(R.id.tvScore);

            assertEquals("Paris", btnAnswer1.getText().toString());

            btnAnswer1.performClick();

            assertEquals(1, activity.score);
            assertEquals("Score: 1", tvScore.getText().toString());
        });
    }
}
