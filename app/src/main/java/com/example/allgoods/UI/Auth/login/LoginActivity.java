package com.example.allgoods.UI.Auth.login;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.allgoods.R;
import com.example.allgoods.UI.Auth.forgetpassword.ForgetPasswordActivity;
import com.example.allgoods.UI.Auth.signup.SignUpActivity;
import com.example.allgoods.UI.Main.MainActivity;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvForgotPassword, tvSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.forgetPassword);
        tvSignUp = findViewById(R.id.signUpText);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> validateInputs());
        tvForgotPassword.setOnClickListener(
                v -> startActivities(new android.content.Intent[]{new android.content.Intent(this, ForgetPasswordActivity.class)})
        );

        tvSignUp.setOnClickListener(
                v -> startActivities(new android.content.Intent[]{new android.content.Intent(this, SignUpActivity.class)})
        );


    }

    private void validateInputs() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        boolean isValid = true;

        // Email empty
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            isValid = false;
        }
        // Email format
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email");
            isValid = false;
        } else {
            etEmail.setError(null);
        }

        // Password empty
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            isValid = false;
        }
        // Password length
        else if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            etPassword.setError(null);
        }

        if (isValid) loginUser(email, password);

    }

    private void loginUser(String email, String password) {
        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
        android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
        if (email.equals("admin@gmail.com") && password.equals("Galal1234")) {
            intent.putExtra("USER_ROLE", "seller");
        } else {
            intent.putExtra("USER_ROLE", "customer");
        }
        startActivity(intent);
        finish();
    }
}