package com.example.project2group1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project2group1.R;
import com.example.project2group1.core.Prefs;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Prefs prefs = new Prefs(this);

        if (prefs.isLoggedIn()) {
            startActivity(new Intent(this, LandingPageActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnCreate = findViewById(R.id.btnCreateAccount);

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)
                        .putExtra("mode", "login")));

        btnCreate.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)
                        .putExtra("mode", "create")));
    }
}
}