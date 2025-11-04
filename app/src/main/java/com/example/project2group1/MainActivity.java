package com.example.project2group1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project2group1.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    static Intent mainActivityIntentFactory(Context context, String username) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    public void toastMaker(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}