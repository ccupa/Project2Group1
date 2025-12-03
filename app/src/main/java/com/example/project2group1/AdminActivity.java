package com.example.project2group1;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;




public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        UserDao userDao = AppDatabase.getInstance(this).userDao();
        Button btnEditQuestions = findViewById(R.id.btnEditQuestions);
        Button btnViwHistoryofUser = findViewById(R.id.btnViwHistoryofUser);


        //TODO: ALSO add in a way to see all the current users and login history

        btnEditQuestions.setOnClickListener(v -> {
            // TODO: Implement the edit questions activity
            // This method allows the admin to change the questions and their answers. Allows them to view and try to quiz too
        });

        btnViwHistoryofUser.setOnClickListener(v -> {
            // TODO: Implement a way to view all the people that logged in the past
            // This is allowing the user to get a list of all the previously logged in people.
        });
    }

}
