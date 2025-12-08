package com.example.project2group1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2group1.databinding.ActivityAddRemoveUserBinding;

import java.util.concurrent.Executors;

/**
 * Activity that provides administrative tools for managing application users.
 *
 * <p>This screen allows an admin to:</p>
 * <ul>
 *     <li>Create new user accounts</li>
 *     <li>Delete existing user accounts</li>
 *     <li>Automatically create or remove corresponding leaderboard entries</li>
 *     <li>Return to the admin dashboard</li>
 * </ul>
 *
 * <p>All database operations are performed asynchronously using
 * {@link Executors#newSingleThreadExecutor()} to keep the UI responsive.</p>
 */
public class AddDeleteUserActivity extends AppCompatActivity {

    private ActivityAddRemoveUserBinding binding;
    private UserDao userDao;
    private LeaderboardDao lbDao;

    Button btnBackToAdmin;

    /**
     * Initializes UI components, retrieves Room DAO objects,
     * and sets click listeners for user management actions.
     *
     * @param savedInstanceState previously saved state (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddRemoveUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDao = AppDatabase.getInstance(getApplicationContext()).userDao();
        lbDao = AppDatabase.getInstance(getApplicationContext()).leaderboardDao();

        btnBackToAdmin = findViewById(R.id.btnBackToMenu);
        btnBackToAdmin.setVisibility(Button.VISIBLE);

        // Add/Delete user buttons
        binding.btnAddUser.setOnClickListener(v -> attemptAddUser());
        binding.btnDeleteUser.setOnClickListener(v -> attemptDeleteUser());

        // Return to admin home page
        btnBackToAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(AddDeleteUserActivity.this, AdminActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Attempts to create a new user in the system.
     *
     * <p>Steps performed:</p>
     * <ol>
     *     <li>Validate that both username and password fields are filled</li>
     *     <li>Check whether a user with the same username already exists</li>
     *     <li>Insert a new {@link User} record into the database</li>
     *     <li>Create a corresponding {@link LeaderboardEntity} entry for score tracking</li>
     *     <li>Clear the input fields and display a success message</li>
     * </ol>
     *
     * <p>All database work is executed on a background thread to avoid blocking the UI.</p>
     */
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

            // Create corresponding leaderboard entry for new user
            LeaderboardEntity leaderboardEntry = new LeaderboardEntity(username);
            lbDao.insert(leaderboardEntry);

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

    /**
     * Attempts to delete an existing user from both the User table
     * and the Leaderboard table in the Room database.
     *
     * <p>Steps performed:</p>
     * <ol>
     *     <li>Ensure a username was provided</li>
     *     <li>Verify that the user exists</li>
     *     <li>Delete the user record from {@link UserDao}</li>
     *     <li>Delete the associated leaderboard record</li>
     *     <li>Clear input fields and display confirmation message</li>
     * </ol>
     *
     * <p>All database operations run on a background executor thread.</p>
     */
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
            lbDao.deleteUser(username);

            runOnUiThread(() -> {
                toastMaker("User deleted successfully");
                binding.etUsername.setText("");
                binding.etPassword.setText("");
            });
        });
    }

    /**
     * Displays a short toast message.
     *
     * @param message the text to display in a Toast notification
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Factory method for generating an intent targeting this activity.
     *
     * @param context the context requesting navigation
     * @return a configured Intent for launching AddDeleteUserActivity
     */
    static Intent addDeleteUserIntentFactory(android.content.Context context) {
        return new Intent(context, AddDeleteUserActivity.class);
    }
}
