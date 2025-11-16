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

        // If not logged in, go back to main
        if (!prefs.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        String username = prefs.getUsername();
        boolean isAdmin = prefs.isAdmin();

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvRole = findViewById(R.id.tvRole);
        Button btnAdmin = findViewById(R.id.btnAdmin);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Show username and role
        tvWelcome.setText("Welcome, " + username + "!");
        tvRole.setText(isAdmin ? "Role: Admin" : "Role: User");

        // Show admin button only for admins
        btnAdmin.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        btnAdmin.setOnClickListener(v -> {
            // TODO: start your real AdminActivity when you add it
            tvRole.setText("Role: Admin • Admin area tapped");
        });

        // Logout: clear prefs and return to MainActivity
        btnLogout.setOnClickListener(v -> {
            prefs.logout();
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
    }
}
