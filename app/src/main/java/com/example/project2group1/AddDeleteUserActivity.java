package com.example.project2group1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2group1.databinding.ActivityAddRemoveUserBinding;

import java.util.concurrent.Executors;

public class AddDeleteUserActivity extends AppCompatActivity {

    private ActivityAddRemoveUserBinding binding;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddRemoveUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDao = AppDatabase.getInstance(getApplicationContext()).userDao();

        binding.btnAddUser.setOnClickListener(v -> attemptAddUser());
        binding.btnDeleteUser.setOnClickListener(v -> attemptDeleteUser());
    }

    private void attemptAddUser() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            toastMaker("Username may not be blank");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            toastMaker("Password may not be blank");
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            User existing = userDao.findByUsername(username);
            if (existing != null) {
                runOnUiThread(() -> toastMaker("User already exists"));
                return;
            }

            User newUser = new User(username, password, false);
            long id = userDao.insert(newUser);

            runOnUiThread(() -> {
                if (id > 0) {
                    toastMaker("User added successfully");
                    binding.etUsername.setText("");
                    binding.etPassword.setText("");
                } else {
                    toastMaker("Error adding user, please try again");
                }
            });
        });
    }

    private void attemptDeleteUser() {
        String username = binding.etUsername.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            toastMaker("Enter a username to delete");
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            User existing = userDao.findByUsername(username);
            if (existing == null) {
                runOnUiThread(() -> toastMaker("User not found"));
                return;
            }

            userDao.deleteUser(username);

            runOnUiThread(() -> {
                toastMaker("User deleted successfully");
                binding.etUsername.setText("");
                binding.etPassword.setText("");
            });
        });
    }

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    static Intent addDeleteUserIntentFactory(Context context) {
        return new Intent(context, AddDeleteUserActivity.class);
    }
}
