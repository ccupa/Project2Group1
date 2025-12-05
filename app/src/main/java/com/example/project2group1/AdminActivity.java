package com.example.project2group1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    Button btnBackToMenu;
    Button btnAddDeleteUser;
    Button btnUserHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnAddDeleteUser = findViewById(R.id.btnAddDelUser);
        btnUserHistory = findViewById(R.id.btnViwHistoryofUser);

        // Go to Add/Delete users page
        btnAddDeleteUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AddDeleteUserActivity.class);
            startActivity(intent);
        });

        // Go to User history page
        btnUserHistory.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, UserHistoryforAdmin.class);
            startActivity(intent);
        });

        // Existing back-to-menu logic
        btnBackToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, LandingPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
