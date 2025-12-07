package com.example.project2group1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.SharedPreferences;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2group1.databinding.ActivityLoginScreenBinding;


public class LoginScreen extends AppCompatActivity {

    ActivityLoginScreenBinding binding;
    private static String username;
    private String password;

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
                startActivity(SignUpActivity.signUpIntentFactory(LoginScreen.this));
            }
        });


        //binding.skipLoginButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
            //    startActivity(new Intent(getApplicationContext(), LandingPageActivity.class));
            //}
       // });

    }

    public void verifyUser() {
        username = binding.usernameEditText.getText().toString().trim();
        password = binding.passwordEditText.getText().toString().trim();

        if (username.isEmpty()){
            toastMaker("Username may not be blank");
            return;
        }
        if (password.isEmpty()) {
            toastMaker("Password may not be blank");
            return;

        }
        Executors.newSingleThreadExecutor().execute(() -> {
            UserDao dao = AppDatabase.getInstance(getApplicationContext()).userDao();
            User user = dao.findByUsername(username);

            runOnUiThread(() -> {
                if (user != null && password.equals(user.password)) {
                    SharedPreferences prefs = getSharedPreferences(Session.PREFS, MODE_PRIVATE);
                    prefs.edit()
                            .putBoolean(Session.KEY_LOGGED_IN, true)
                            .putString(Session.KEY_USERNAME, user.username)
                            .putBoolean(Session.KEY_IS_ADMIN, user.isAdmin)
                            .apply();
                    toastMaker("Welcome " + username);
                    startActivity(new Intent(getApplicationContext(), LandingPageActivity.class));
                    finish();
                } else {
                    toastMaker("Incorrect username or password");
                }
            });
        });
    }

    public static String getUserName() {
        return username;
    }

    static Intent loginIntentFactory(Context context) {
        return new Intent(context, LoginScreen.class);
    }

    public void toastMaker(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}