package com.example.project2group1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2group1.databinding.ActivityLoginScreenBinding;


public class LoginScreen extends AppCompatActivity {

    ActivityLoginScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUser();
            }
        });

        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastMaker("Working on it");
            }
        });

        binding.skipLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(), "Drew"));
            }
        });

    }

    public void verifyUser() {
        String testUsername = "Drew";
        String testPassword = "password";

        String username = binding.usernameEditText.getText().toString();
        String password = binding.passwordEditText.getText().toString();

        if (username.isEmpty()){
            toastMaker("Username may not be blank");
            return;
        }
        if (!(username.equals(testUsername))) {
            toastMaker("Incorrect Username");
            return;
        }
        if (password.isEmpty() || (!password.equals(testPassword))){
            toastMaker("Incorrect Password");
            return;
        }
        toastMaker("Welcome " + username);
        startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(), username));

    }

    static Intent loginIntentFactory(Context context) {
        return new Intent(context, LoginScreen.class);
    }

    public void toastMaker(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}