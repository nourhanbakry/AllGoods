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
import com.example.allgoods.databinding.ActivitySignUpBinding;
import com.google.android.material.button.MaterialButton;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupListeners();
        setupTextWatchers();
    }

    private void setupListeners() {
        binding.btnSignUp.setOnClickListener(v -> validateInputs());
        binding.backButton.setOnClickListener(v -> onBackPressed());
        binding.signUpText.setOnClickListener(v -> onBackPressed());
    }

    private void validateInputs() {

        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        boolean isValid = true;

        // Name
        if (name.isEmpty()) {
            binding.etName.setError("Name is required");
            isValid = false;
        } else if (name.length() < 3) {
            binding.etName.setError("Name must be at least 3 characters");
            isValid = false;
        } else {
            binding.etName.setError(null);
        }

        // Email
        if (email.isEmpty()) {
            binding.etEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Enter valid email");
            isValid = false;
        } else {
            binding.etEmail.setError(null);
        }

        // Password
        if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            isValid = false;
        }
        else if (password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }
        else if (!isStrongPassword(password)) {
            binding.etPassword.setError("Must contain upper, lower, number & symbol");
            isValid = false;
        }
        else {
            binding.etPassword.setError(null);
        }

        // Confirm Password
        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.setError("Confirm your password");
            isValid = false;
        }
        else if (!confirmPassword.equals(password)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }
        else {
            binding.etConfirmPassword.setError(null);
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
                binding.etName.setError(null);
                binding.etEmail.setError(null);
                binding.etPassword.setError(null);
                binding.etConfirmPassword.setError(null);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        };

        binding.etName.addTextChangedListener(watcher);
        binding.etEmail.addTextChangedListener(watcher);
        binding.etPassword.addTextChangedListener(watcher);
        binding.etConfirmPassword.addTextChangedListener(watcher);
    }
}