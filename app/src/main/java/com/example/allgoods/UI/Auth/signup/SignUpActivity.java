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
import com.example.allgoods.utils.Network.NetworkListener;
import com.example.allgoods.utils.Network.NetworkManager;
import com.example.allgoods.utils.SnackBarHelper;
import com.google.android.material.button.MaterialButton;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;

    private NetworkManager networkManager;

    private boolean lastNetworkState = true;

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

        connection();
        setupListeners();
        setupTextWatchers();
    }

    private void connection(){
        networkManager = new NetworkManager();

        if (!networkManager.isConnected(this)) {
            setSignUpEnabled(false);
            SnackBarHelper.showError(binding.getRoot(), "No Internet Connection");
        } else {
            setSignUpEnabled(true);
            SnackBarHelper.showSuccess(binding.getRoot(), "Internet Connection Available");

        }

        networkManager.register(this, new NetworkListener() {
            @Override
            public void onConnected() {
                runOnUiThread(() -> {
                    if (!lastNetworkState) SnackBarHelper.showSuccess(binding.getRoot(), "Internet Connection Available");

                    lastNetworkState = true;
                    setSignUpEnabled(true);
                });
            }

            @Override
            public void onDisconnected() {
                runOnUiThread(() -> {
                    if (lastNetworkState) SnackBarHelper.showError(binding.getRoot(), "No Internet Connection");

                    lastNetworkState = false;
                    setSignUpEnabled(false);
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkManager.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSignUpEnabled(networkManager.isConnected(this));
    }

    private void setSignUpEnabled(boolean enabled) {
        binding.btnSignUp.setEnabled(enabled);
        binding.btnSignUp.setAlpha(enabled ? 1f : 0.5f);
    }
    private void setupListeners() {
        binding.btnSignUp.setOnClickListener(v -> validateInputs());
        binding.backButton.setOnClickListener(v -> onBackPressed());
        binding.signUpText.setOnClickListener(v -> onBackPressed());
    }

    private void validateInputs() {

        String name = String.valueOf(binding.etName.getText()).trim();
        String email = String.valueOf(binding.etEmail.getText()).trim();
        String password = String.valueOf(binding.etPassword.getText()).trim();
        String confirmPassword = String.valueOf(binding.etConfirmPassword.getText()).trim();

        boolean isValid = true;

        // Name
        isValid = isNameValid(name, isValid);

        // Email
        isValid = isEmailValid(email, isValid);

        // Password
        isValid = isPasswordAdndConfirmPasswordValid(password, isValid, confirmPassword);

        if (isValid) {
            registerUser(name, email, password);
        }
    }

    private boolean isEmailValid(String email, boolean isValid) {
        if (email.isEmpty()) {
            binding.etEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Enter valid email");
            isValid = false;
        } else {
            binding.etEmail.setError(null);
        }
        return isValid;
    }

    private boolean isNameValid(String name, boolean isValid) {
        if (name.isEmpty()) {
            binding.etName.setError("Name is required");
            isValid = false;
        } else if (name.length() < 3) {
            binding.etName.setError("Name must be at least 3 characters");
            isValid = false;
        } else {
            binding.etName.setError(null);
        }
        return isValid;
    }

    private boolean isPasswordAdndConfirmPasswordValid(String password, boolean isValid, String confirmPassword) {
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
        return isValid;
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