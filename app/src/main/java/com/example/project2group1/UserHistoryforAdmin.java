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

/**
 * Activity that allows administrators to view a list of all registered users.
 *
 * <p>This screen retrieves user accounts from the Room database and displays them
 * in a scrollable ListView. Admin accounts are marked visually in the list.</p>
 *
 * <p>The activity supports:</p>
 * <ul>
 *     <li>Navigating back to the admin dashboard</li>
 *     <li>Displaying all users asynchronously to avoid blocking the UI</li>
 *     <li>Simple toast feedback when no users exist in the database</li>
 * </ul>
 */
public class UserHistoryforAdmin extends AppCompatActivity {

    private ActivityUserHistoryBinding binding;
    private UserDao userDao;

    private Button btnBackToAdmin;
    private ArrayAdapter<String> adapter;

    /** List of formatted usernames displayed in the ListView. */
    private final List<String> displayList = new ArrayList<>();

    /**
     * Initializes the User History UI, loads database references, and configures
     * the ListView and navigation controls.
     *
     * @param savedInstanceState previously saved state (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding for layout connection
        binding = ActivityUserHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize DAO for user queries
        userDao = AppDatabase.getInstance(getApplicationContext()).userDao();

        // Back button → returns admin to the main admin dashboard
        btnBackToAdmin = binding.btnBackToMenu;
        btnBackToAdmin.setVisibility(Button.VISIBLE);
        btnBackToAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(UserHistoryforAdmin.this, AdminActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Adapter used to populate the ListView with user information
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayList
        );
        binding.listUsers.setAdapter(adapter);

        // Load user list in the background
        loadUsers();
    }

    /**
     * Loads all users from the Room database asynchronously.
     *
     * <p>Once loaded, the method formats each entry (including marking admin accounts)
     * and updates the ListView on the UI thread.</p>
     *
     * <p>If no users exist, a toast message is displayed.</p>
     */
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

    /**
     * Displays a short toast message to the user.
     *
     * @param message the message to display
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Factory method for creating an intent to launch this activity.
     *
     * @param context the context requesting navigation
     * @return an Intent configured for {@link UserHistoryforAdmin}
     */
    static Intent userHistoryIntentFactory(Context context) {
        return new Intent(context, UserHistoryforAdmin.class);
    }
}
