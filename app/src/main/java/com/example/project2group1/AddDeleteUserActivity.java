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
 * Activity that allows an administrator to add or delete users from the system.
 *
 * <p>This screen provides two text fields—username and password—and two actions:
 * adding a new user or deleting an existing one. All database operations are handled
 * on a background thread using {@link Executors} to prevent UI blocking.</p>
 *
 * <p>The activity interacts with {@link UserDao} through the Room database and
 * performs validation on input fields before attempting user operations.</p>
 */
public class AddDeleteUserActivity extends AppCompatActivity {

    private ActivityAddRemoveUserBinding binding;
    private UserDao userDao;

    Button btnBackToAdmin;

    /**
     * Initializes UI bindings, retrieves database access objects,
     * and configures button listeners for adding, deleting,
     * and navigating back to the admin menu.
     *
     * @param savedInstanceState previously saved instance state (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddRemoveUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDao = AppDatabase.getInstance(getApplicationContext()).userDao();

        btnBackToAdmin = findViewById(R.id.btnBackToMenu);
        btnBackToAdmin.setVisibility(Button.VISIBLE);

        // Configure action buttons
        binding.btnAddUser.setOnClickListener(v -> attemptAddUser());
        binding.btnDeleteUser.setOnClickListener(v -> attemptDeleteUser());

        // Return to admin dashboard
        btnBackToAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(AddDeleteUserActivity.this, AdminActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Attempts to add a new user to the database.
     *
     * <p>This method validates the username and password fields, ensuring neither is blank.
     * If validation passes, it checks whether the username already exists in the database.
     * If not, a new {@link User} object is created and inserted using the DAO.</p>
     *
     * <p>All database access is run on a background thread, with UI feedback posted
     * back through {@code runOnUiThread()}.</p>
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
     * Attempts to delete an existing user from the database.
     *
     * <p>This method first validates that a username has been entered.
     * If the user exists, it is removed from the database via {@link UserDao#deleteUser(String)}.</p>
     *
     * <p>Database operations run off the UI thread using an executor, and
     * UI updates are posted back safely using {@code runOnUiThread()}.</p>
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

            runOnUiThread(() -> {
                toastMaker("User deleted successfully");
                binding.etUsername.setText("");
                binding.etPassword.setText("");
            });
        });
    }

    /**
     * Displays a short toast message to the user.
     *
     * @param message the text to display in the toast
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Factory method used to create an intent for launching
     * {@link AddDeleteUserActivity} from other activities.
     *
     * @param context the context creating the intent
     * @return a configured Intent targeting AddDeleteUserActivity
     */
    static Intent addDeleteUserIntentFactory(android.content.Context context) {
        return new Intent(context, AddDeleteUserActivity.class);
    }
}
