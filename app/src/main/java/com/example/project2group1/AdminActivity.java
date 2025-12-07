package com.example.project2group1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for administrative controls available only to admin users.
 *
 * <p>This screen provides navigation options to:</p>
 * <ul>
 *     <li>Add or delete user accounts</li>
 *     <li>View the quiz and login history of any user</li>
 *     <li>Return to the main landing menu</li>
 * </ul>
 *
 * <p>AdminActivity serves as the central hub for all administrative operations
 * within the application.</p>
 */
public class AdminActivity extends AppCompatActivity {

    Button btnBackToMenu;
    Button btnAddDeleteUser;
    Button btnUserHistory;

    /**
     * Initializes the admin menu, sets up navigation buttons,
     * and binds UI components to their corresponding actions.
     *
     * @param savedInstanceState previously saved instance state (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnAddDeleteUser = findViewById(R.id.btnAddDelUser);
        btnUserHistory = findViewById(R.id.btnViwHistoryofUser);
        btnBackToMenu.setVisibility(Button.VISIBLE);

        /**
         * Navigates to the Add/Delete User management screen.
         * Allows administrators to create new users or remove existing accounts.
         */
        btnAddDeleteUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AddDeleteUserActivity.class);
            startActivity(intent);
        });

        /**
         * Navigates to the User History viewer screen.
         * Administrators can search for users and view their quiz results or login activity.
         */
        btnUserHistory.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, UserHistoryforAdmin.class);
            startActivity(intent);
        });

        /**
         * Returns the admin user back to the main menu.
         * The intent flags ensure the back stack is cleared properly.
         */
        btnBackToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, LandingPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
