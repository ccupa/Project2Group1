package com.example.project2group1;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LeaderBoardInstrumentedTest {

    // Simple Test #1: Activity launches without crashing
    @Test
    public void testActivityLaunches() {
        try (ActivityScenario<LeaderBoard> scenario =
                     ActivityScenario.launch(LeaderBoard.class)) {
            // If it launches successfully, the test passes.
        }
    }

    // Simple Test #2: Header text is visible on screen
    @Test
    public void testLeaderboardHeaderIsDisplayed() {
        try (ActivityScenario<LeaderBoard> scenario =
                     ActivityScenario.launch(LeaderBoard.class)) {

            onView(withId(R.id.leaderboardTextView))
                    .check(matches(isDisplayed()));
        }
    }
}
