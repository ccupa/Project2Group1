package com.example.project2group1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LandingPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences(Session.PREFS, MODE_PRIVATE);
        if (!prefs.getBoolean(Session.KEY_LOGGED_IN, false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        String username = prefs.getString(Session.KEY_USERNAME, "");
        boolean isAdmin = prefs.getBoolean(Session.KEY_IS_ADMIN, false);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
    }
}
