package com.example.project2group1;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class JacksTriviaTest {

    @Test
    public void clickingCorrectAnswerUpdatesScore() {

        ActivityScenario<JacksTriviaQuestions> scenario =
                ActivityScenario.launch(JacksTriviaQuestions.class);

        scenario.onActivity(activity -> {

            String correct = activity.answers[0][1];
            activity.binding.answerTopLeftButton.setText(correct);
            activity.setCorrectIndex(0);
        });

        onView(withId(R.id.answerTopLeftButton)).perform(click());
        onView(withId(R.id.actualScoreTextView))
                .check(matches(withText("1")));
    }
}
