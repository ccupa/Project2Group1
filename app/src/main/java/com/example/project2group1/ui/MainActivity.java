package com.example.project2group1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2group1.R;
import com.example.project2group1.core.Prefs;
import com.example.project2group1.data.AppDatabase;
import com.example.project2group1.data.UserDao;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If already logged in, jump to landing page
        Prefs prefs = new Prefs(this);
        if (prefs.isLoggedIn()) {
            startActivity(new Intent(this, LandingPageActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        /* ------------------------------
           TEMP: Promote admin2 to admin
           ------------------------------ */
        new Thread(() -> {
            UserDao dao = AppDatabase.getInstance(getApplicationContext()).userDao();
            // Make sure you have this query in UserDao:
            // @Query("UPDATE users SET isAdmin = 1 WHERE username = :username")
            // void makeAdmin(String username);
            try {
                dao.makeAdmin("admin2");
            } catch (Exception ignored) {}
        }).start();
        /* --- remove this block once admin2 is updated --- */

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnCreate = findViewById(R.id.btnCreateAccount);

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        btnCreate.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }
}
