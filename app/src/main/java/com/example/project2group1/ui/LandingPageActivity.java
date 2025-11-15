package com.example.project2group1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project2group1.R;
import com.example.project2group1.core.Prefs;

public class LandingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        Prefs prefs = new Prefs(this);
        String username = prefs.getUsername();
        boolean isAdmin = prefs.isAdmin();

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvRole = findViewById(R.id.tvRole);
        Button btnAdmin = findViewById(R.id.btnAdmin);
        Button btnLogout = findViewById(R.id.btnLogout);

        tvWelcome.setText("Welcome, " + username + "!");
        tvRole.setText(isAdmin ? "Role: Admin" : "Role: User");
        btnAdmin.setVisibility(isAdmin ? View.VISIBLE : View.INVISIBLE);

        btnLogout.setOnClickListener(v -> {
            prefs.logout();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
