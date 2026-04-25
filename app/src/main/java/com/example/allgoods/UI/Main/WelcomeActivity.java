package com.example.allgoods.UI.Main;

import static com.example.allgoods.utils.Constants.SPLASH_DELAY;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.allgoods.Data.local.SharedPrefManager;
import com.example.allgoods.R;
import com.example.allgoods.UI.Auth.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        navigateNext();
    }

    private void navigateNext() {

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            SharedPrefManager prefManager = new SharedPrefManager(this);

            boolean isLogged = prefManager.isLoggedIn();
            String role = prefManager.getRole();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (isLogged && user != null) {

                user.reload().addOnCompleteListener(task -> {

                    if (task.isSuccessful() && FirebaseAuth.getInstance().getCurrentUser() != null) {

                        if ("customer".equals(role)) {
                            goToMain();
                        } else {
                            goToLogin();
                        }

                    } else {
                        prefManager.logout();
                        goToLogin();
                    }
                });

            } else {
                prefManager.logout();
                goToLogin();
            }

        }, SPLASH_DELAY);
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}

