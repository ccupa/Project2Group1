package com.example.project2group1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2group1.data.UserDao;


import androidx.appcompat.app.AppCompatActivity;

import com.example.project2group1.R;
import com.example.project2group1.core.Prefs;
import com.example.project2group1.data.AppDatabase;
import com.example.project2group1.data.User;
import com.example.project2group1.data.UserRepository;

public class LoginActivity extends AppCompatActivity {

    private View groupLogin, groupCreate;
    private TextView title;
    private Button toggleMode, btnLogin, btnCreate;
    private EditText etUsernameLogin, etPasswordLogin;
    private EditText etUsernameCreate, etPasswordCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        title = findViewById(R.id.title);
        groupLogin = findViewById(R.id.groupLogin);
        groupCreate = findViewById(R.id.groupCreate);
        toggleMode = findViewById(R.id.toggleMode);

        etUsernameLogin = findViewById(R.id.etUsernameLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);

        etUsernameCreate = findViewById(R.id.etUsernameCreate);
        etPasswordCreate = findViewById(R.id.etPasswordCreate);
        btnCreate = findViewById(R.id.btnCreate);

        applyMode(false);

        toggleMode.setOnClickListener(v -> applyMode(groupCreate.getVisibility() != View.VISIBLE));

        btnLogin.setOnClickListener(v -> doLogin());
        btnCreate.setOnClickListener(v -> doCreate());
    }

    private void applyMode(boolean create) {
        groupCreate.setVisibility(create ? View.VISIBLE : View.GONE);
        groupLogin.setVisibility(create ? View.GONE : View.VISIBLE);
        title.setText(create ? R.string.create_account : R.string.login);
        toggleMode.setText(create ? R.string.switch_to_login : R.string.switch_to_create);
    }

    private void doLogin() {
        String username = etUsernameLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString();


        if (username.isEmpty() || password.isEmpty()) {
            toast("Please fill in all fields");
            return;
        }
        new Thread(() -> {
            try {
                UserDao dao = AppDatabase.getInstance(getApplicationContext()).userDao();
                UserRepository repo = new UserRepository(dao);

                // validate credentials
                User u = repo.validateLogin(username, password);

                // if admin2 exists but isn't marked admin, promote & re-fetch
                if ("admin2".equalsIgnoreCase(u.username) && !u.isAdmin) {
                    dao.makeAdmin(u.username);
                    u = dao.getUser(u.username);  // re-fetch updated record with isAdmin = true
                }

                // Save updated user (now with correct isAdmin) to prefs
                new Prefs(getApplicationContext()).setLoggedIn(u);

                runOnUiThread(() -> {
                    toast("Welcome back!");
                    startActivity(new Intent(this, LandingPageActivity.class));
                    finish();
                });
            } catch (Exception e) {
                toast(e.getMessage() != null ? e.getMessage() : "Login failed");
            }
        }).start();
    }


    private void doCreate() {
        String username = etUsernameCreate.getText().toString().trim();
        String password = etPasswordCreate.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            toast("Please fill in all fields");
            return;
        }
        new Thread(() -> {
            try {
                UserDao dao = AppDatabase.getInstance(getApplicationContext()).userDao();
                UserRepository repo = new UserRepository(dao);

                repo.createUser(username, password, false);
                User u = repo.validateLogin(username, password);

                if ("admin2".equalsIgnoreCase(u.username) && !u.isAdmin) {
                    dao.makeAdmin(u.username);
                    u = dao.getUser(u.username);
                }

                new Prefs(getApplicationContext()).setLoggedIn(u);

                runOnUiThread(() -> {
                    toast("Account created!");
                    startActivity(new Intent(this, LandingPageActivity.class));
                    finish();
                });
            } catch (Exception e) {
                toast(e.getMessage() != null ? e.getMessage() : "Sign up failed");
            }
        }).start();
    }


    private void toast(String msg) {
        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }
}
