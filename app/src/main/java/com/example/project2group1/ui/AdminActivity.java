package com.example.project2group1.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2group1.R;
import com.example.project2group1.data.AppDatabase;
import com.example.project2group1.data.UserDao;
//import com.example.project2group1.database.AppDatabase;
//import com.example.project2group1.database.UserDao;
//import com.example.project2group1.model.User;


public class AdminActivity extends AppCompatActivity {

    private UserDao userDao;
    private Button btnEditQuestions;
    private Button btnViwHistoryofUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        userDao = AppDatabase.getInstance(this).userDao();
        btnEditQuestions = findViewById(R.id.btnEditQuestions);
        btnViwHistoryofUser= findViewById(R.id.btnViwHistoryofUser);


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
