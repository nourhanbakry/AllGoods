package com.example.allgoods.UI.Auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.allgoods.UI.Auth.forgetpassword.ForgetPasswordActivity;
import com.example.allgoods.UI.Auth.signup.SignUpActivity;
import com.example.allgoods.UI.Main.MainActivity;
import com.example.allgoods.databinding.ActivityLoginBinding;
import com.example.allgoods.utils.Network.NetworkListener;
import com.example.allgoods.utils.Network.NetworkManager;
import com.example.allgoods.utils.SnackBarHelper;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    private NetworkManager networkManager;

    private boolean lastNetworkState = true;

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

        connection();
        setupListeners();
    }

    private void connection(){
        networkManager = new NetworkManager();

        if (!networkManager.isConnected(this)) {
            setLoginEnabled(false);
            SnackBarHelper.showError(binding.getRoot(), "No Internet Connection");
        } else {
            setLoginEnabled(true);
            SnackBarHelper.showSuccess(binding.getRoot(), "Internet Connection Available");

        }

        networkManager.register(this, new NetworkListener() {
            @Override
            public void onConnected() {
                runOnUiThread(() -> {
                    if (!lastNetworkState) SnackBarHelper.showSuccess(binding.getRoot(), "Internet Connection Available");

                    lastNetworkState = true;
                    setLoginEnabled(true);
                });
            }

            @Override
            public void onDisconnected() {
                runOnUiThread(() -> {
                    if (lastNetworkState) SnackBarHelper.showError(binding.getRoot(), "No Internet Connection");

                    lastNetworkState = false;
                    setLoginEnabled(false);
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLoginEnabled(networkManager.isConnected(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkManager.unregister(this);
    }

    private void setLoginEnabled(boolean enabled) {
        binding.btnLogin.setEnabled(enabled);
        binding.btnLogin.setAlpha(enabled ? 1f : 0.5f);
    }
    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> validateInputs());
        binding.forgetPassword.setOnClickListener(
                v -> startActivities(new android.content.Intent[]{new android.content.Intent(this, ForgetPasswordActivity.class)})
        );

        binding.signUpText.setOnClickListener(
                v -> startActivity(new android.content.Intent(this, SignUpActivity.class))
        );
    }

    private void validateInputs() {

        String email = String.valueOf(binding.etEmail.getText()).trim();
        String password = String.valueOf(binding.etPassword.getText()).trim();

        boolean isValid = isEmailValid(email) & isPasswordValid(password);


        if (isValid) {
            loginUser(email, password);
        }

    }

    private boolean isPasswordValid(String password) {
        if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            return false;
        }
        else if (password.length() < 6) {
            binding.etPassword.setError("Password Not Valid");
            return false;
        } else {
           binding.etPassword.setError(null);
           return true;
        }
    }

    private boolean isEmailValid(String email) {
        if (email.isEmpty()) {
            binding.etEmail.setError("Email is required");
            return false;
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Enter valid email");
            return false;
        }else {
            binding.etEmail.setError(null);
            return true;
        }
    }

    private void loginUser(String email, String password) {
        Toast.makeText(this, "Login Successful ", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}