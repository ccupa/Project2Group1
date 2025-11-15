package com.example.project2group1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project2group1.R;
import com.example.project2group1.core.Prefs;
import com.example.project2group1.data.AppDatabase;
import com.example.project2group1.data.User;
import com.example.project2group1.data.UserRepository;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "mode";
    public static final String MODE_LOGIN = "login";
    public static final String MODE_CREATE = "create";

    private UserRepository repo;
    private Prefs prefs;

    private View groupLogin, groupCreate;
    private EditText etUsernameLogin, etPasswordLogin, etUsernameCreate, etPasswordCreate;
    private Button btnLogin, btnCreate, toggleMode;
    private String mode = MODE_LOGIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        repo = new UserRepository(AppDatabase.get(this).userDao());
        prefs = new Prefs(this);

        groupLogin = findViewById(R.id.groupLogin);
        groupCreate = findViewById(R.id.groupCreate);
        etUsernameLogin = findViewById(R.id.etUsernameLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        etUsernameCreate = findViewById(R.id.etUsernameCreate);
        etPasswordCreate = findViewById(R.id.etPasswordCreate);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreate = findViewById(R.id.btnCreate);
        toggleMode = findViewById(R.id.toggleMode);

        String startMode = getIntent().getStringExtra(EXTRA_MODE);
        if (startMode != null) mode = startMode;
        setMode(mode);

        toggleMode.setOnClickListener(v ->
                setMode(mode.equals(MODE_LOGIN) ? MODE_CREATE : MODE_LOGIN));

        btnLogin.setOnClickListener(v -> doLogin());
        btnCreate.setOnClickListener(v -> doCreate());
    }

    private void setMode(String newMode) {
        mode = newMode;
        boolean isLogin = mode.equals(MODE_LOGIN);
        groupLogin.setVisibility(isLogin ? View.VISIBLE : View.GONE);
        groupCreate.setVisibility(isLogin ? View.GONE : View.VISIBLE);
        toggleMode.setText(isLogin ? "Create an account instead" : "I already have an account");
    }

    private void doLogin() {
        new Thread(() -> {
            try {
                User user = repo.validateLogin(
                        etUsernameLogin.getText().toString().trim(),
                        etPasswordLogin.getText().toString().trim());
                runOnUiThread(() -> {
                    prefs.setLoggedIn(user.username, user.isAdmin);
                    startActivity(new Intent(this, LandingPageActivity.class));
                    finish();
                });
            } catch (Exception e) {
                showToast(e.getMessage());
            }
        }).start();
    }

    private void doCreate() {
        new Thread(() -> {
            try {
                repo.createUser(
                        etUsernameCreate.getText().toString().trim(),
                        etPasswordCreate.getText().toString().trim(),
                        false);
                runOnUiThread(() -> {
                    prefs.setLoggedIn(etUsernameCreate.getText().toString().trim(), false);
                    startActivity(new Intent(this, LandingPageActivity.class));
                    finish();
                });
            } catch (Exception e) {
                showToast(e.getMessage());
            }
        }).start();
    }

    private void showToast(String msg) {
        runOnUiThread(() ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }
}
