package com.example.project2group1;

import static org.junit.Assert.*;

import org.junit.Test;


public class CSQuizActivityTest {

    @Test
    public void showQuestion_doesNothing_whenIndexOutOfBounds() {
        CSQuizActivity activity = new CSQuizActivity();

        activity.questionList.clear();  // no questions
        activity.currentIndex = 5;      // invalid index

        // Try calling the private method through reflection
        try {
            var m = CSQuizActivity.class.getDeclaredMethod("showQuestion");
            m.setAccessible(true);
            m.invoke(activity);
        } catch (Exception e) {
            fail("showQuestion should not crash even with invalid index");
        }

        // If no crash, test passes
        assertTrue(true);
    }

}
