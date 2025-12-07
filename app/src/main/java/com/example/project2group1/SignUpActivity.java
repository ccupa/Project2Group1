package com.example.project2group1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2group1.databinding.ActivitySignUpBinding;

import java.util.concurrent.Executors;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private UserDao userDao;
    private LeaderboardDao lbDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Use same DB pattern as LoginScreen: AppDatabase.getInstance(...)
        userDao = AppDatabase.getInstance(getApplicationContext()).userDao();
        lbDao = AppDatabase.getInstance(getApplicationContext()).leaderboardDao();

        binding.buttonCreateAccount.setOnClickListener(v -> attemptSignUp());
        binding.buttonCancel.setOnClickListener(v -> finish());
    }

    private void attemptSignUp() {
        String username = binding.etNewUsername.getText().toString().trim();
        String password = binding.etNewPassword.getText().toString();
        String confirmPassword = binding.etConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            toastMaker("Username may not be blank");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            toastMaker("Password may not be blank");
            return;
        }

        if (!password.equals(confirmPassword)) {
            toastMaker("Passwords do not match");
            return;
        }

        // DB work off main thread (same style as LoginScreen)
        Executors.newSingleThreadExecutor().execute(() -> {
            User existing = userDao.findByUsername(username);
            if (existing != null) {
                runOnUiThread(() -> toastMaker("Username already taken"));
                return;
            }

            User newUser = new User(username, password, false); // regular user, not admin
            long id = userDao.insert(newUser);

            LeaderboardEntity newUser2 = new LeaderboardEntity(username);
            long lb_id = lbDao.insert(newUser2);
            Log.d("SIGNUP", "Leaderboard inserted rowId=" + lb_id);


            runOnUiThread(() -> {
                if (id > 0) {
                    toastMaker("Account created! Please log in.");
                    finish();  // go back to LoginScreen
                } else {
                    toastMaker("Error creating account, please try again.");
                }
            });
        });
    }

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Helper factory, like in LoginScreen/MainActivity
    static Intent signUpIntentFactory(Context context) {
        return new Intent(context, SignUpActivity.class);
    }
}
