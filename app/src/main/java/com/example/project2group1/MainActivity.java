package com.example.project2group1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2group1.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(Session.PREFS, MODE_PRIVATE);
        if (prefs.getBoolean(Session.KEY_LOGGED_IN, false)) {
            startActivity(new Intent(this, com.example.project2group1.LandingPageActivity.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.jackTriviaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastMaker("Clicked");
                Intent intent = JacksActivity.jackIntentFactory(getApplicationContext());
                startActivity(intent);
            }
        });
    }

    static Intent mainActivityIntentFactory(Context context, String username) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }



    public void toastMaker(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}