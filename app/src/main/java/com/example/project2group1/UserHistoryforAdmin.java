package com.example.project2group1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2group1.databinding.ActivityUserHistoryBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class UserHistoryforAdmin extends AppCompatActivity {

    private ActivityUserHistoryBinding binding;
    private UserDao userDao;

    private Button btnBackToAdmin;
    private ArrayAdapter<String> adapter;
    private final List<String> displayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding
        binding = ActivityUserHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // DB
        userDao = AppDatabase.getInstance(getApplicationContext()).userDao();

        // Back button (using ViewBinding; make sure your XML has btnBackToMenu)
        btnBackToAdmin = binding.btnBackToMenu;
        btnBackToAdmin.setVisibility(Button.VISIBLE);
        btnBackToAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(UserHistoryforAdmin.this, AdminActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Adapter for the ListView
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayList
        );
        binding.listUsers.setAdapter(adapter);

        // Load all users from DB (off main thread)
        loadUsers();
    }

    private void loadUsers() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<User> users = userDao.getAllUsers();

            List<String> tempList = new ArrayList<>();
            for (User u : users) {
                tempList.add(u.username + (u.isAdmin ? " (Admin)" : ""));
            }

            runOnUiThread(() -> {
                displayList.clear();
                displayList.addAll(tempList);
                adapter.notifyDataSetChanged();

                if (displayList.isEmpty()) {
                    toastMaker("No users found in the database");
                }
            });
        });
    }

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    static Intent userHistoryIntentFactory(Context context) {
        return new Intent(context, UserHistoryforAdmin.class);
    }
}
