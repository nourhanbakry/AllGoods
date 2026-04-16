package com.example.allgoods.UI.Auth.signup;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.allgoods.R;
import com.google.android.material.button.MaterialButton;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnSignup;
    private ImageView ivBack;
    private TextView signInText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
        setupTextWatchers();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignup = findViewById(R.id.btnSignUp);
        ivBack = findViewById(R.id.backButton);
        signInText = findViewById(R.id.signUpText);
    }

    private void setupListeners() {
        btnSignup.setOnClickListener(v -> validateInputs());
        ivBack.setOnClickListener(v -> onBackPressed());
        signInText.setOnClickListener(v -> onBackPressed());
    }

    private void validateInputs() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        boolean isValid = true;

        // Name
        if (name.isEmpty()) {
            etName.setError("Name is required");
            isValid = false;
        } else if (name.length() < 3) {
            etName.setError("Name must be at least 3 characters");
            isValid = false;
        } else {
            etName.setError(null);
        }

        // Email
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email");
            isValid = false;
        } else {
            etEmail.setError(null);
        }

        // Password
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            isValid = false;
        }
        else if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }
        else if (!isStrongPassword(password)) {
            etPassword.setError("Must contain upper, lower, number & symbol");
            isValid = false;
        }
        else {
            etPassword.setError(null);
        }

        // Confirm Password
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Confirm your password");
            isValid = false;
        }
        else if (!confirmPassword.equals(password)) {
            etConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }
        else {
            etConfirmPassword.setError(null);
        }

        if (isValid) {
            registerUser(name, email, password);
        }
    }

    private boolean isStrongPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$");
    }

    private void registerUser(String name, String email, String password) {
        Toast.makeText(this, "Account Created ", Toast.LENGTH_SHORT).show();
    }

    private void setupTextWatchers() {

        TextWatcher watcher = new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                etName.setError(null);
                etEmail.setError(null);
                etPassword.setError(null);
                etConfirmPassword.setError(null);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        };

        etName.addTextChangedListener(watcher);
        etEmail.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
        etConfirmPassword.addTextChangedListener(watcher);
    }
}