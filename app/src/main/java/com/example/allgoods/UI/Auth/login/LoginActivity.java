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
import com.example.allgoods.databinding.ActivityLoginBinding;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    private EditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvForgotPassword, tvSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupListeners();
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> validateInputs());
        binding.forgetPassword.setOnClickListener(
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
            binding.etEmail.setError("Email is required");
            isValid = false;
        }
        // Email format
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Enter valid email");
            isValid = false;
        } else {
            etEmail.setError(null);
        }

        // Password empty
        if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            isValid = false;
        }
        // Password length
        else if (password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
           binding.etPassword.setError(null);
        }

        if (isValid) loginUser(email, password);

    }

    private void loginUser(String email, String password) {
        Toast.makeText(this, "Login Successful ", Toast.LENGTH_SHORT).show();
        startActivities(new android.content.Intent[]{new android.content.Intent(this, MainActivity.class)});
    }
}