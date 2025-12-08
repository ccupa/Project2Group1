package com.example.project2group1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * AdminActivity serves as the central hub for administrative controls within the application.
 *
 * <p>This screen is accessible only to administrator-level users and provides:
 * <ul>
 *     <li>Navigation to the Add/Delete User management screen</li>
 *     <li>Navigation to the User History viewer for administrators</li>
 *     <li>A return option to the main landing menu</li>
 * </ul>
 *
 * <p>The activity itself performs no database operations but acts solely as a menu
 * for accessing administrative tools.</p>
 */
public class AdminActivity extends AppCompatActivity {

    Button btnBackToMenu;
    Button btnAddDeleteUser;
    Button btnUserHistory;

    /**
     * Initializes the admin menu interface, binds UI components,
     * and assigns click listeners for admin navigation actions.
     *
     * @param savedInstanceState previously saved state (unused)
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
         * Navigates to the Add/Delete User management activity.
         * This allows administrators to create or remove user accounts.
         */
        btnAddDeleteUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AddDeleteUserActivity.class);
            startActivity(intent);
        });

        /**
         * Navigates to the User History activity.
         * Administrators can view account and quiz history for any user.
         */
        btnUserHistory.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, UserHistoryforAdmin.class);
            startActivity(intent);
        });

        /**
         * Returns the administrator to the main landing page.
         * Using CLEAR_TOP and SINGLE_TOP ensures the navigation stack
         * is cleaned to prevent back-navigation into admin-only screens.
         */
        btnBackToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, LandingPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
