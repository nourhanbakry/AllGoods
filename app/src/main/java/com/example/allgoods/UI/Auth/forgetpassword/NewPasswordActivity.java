package com.example.allgoods.UI.Auth.forgetpassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.allgoods.R;
import com.example.allgoods.UI.Auth.login.LoginActivity;
import com.example.allgoods.databinding.ActivityForgetPasswordBinding;
import com.example.allgoods.databinding.ActivityNewPasswordBinding;
import com.example.allgoods.utils.Network.NetworkListener;
import com.example.allgoods.utils.Network.NetworkManager;
import com.example.allgoods.utils.SnackBarHelper;
import com.example.allgoods.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;

public class NewPasswordActivity extends AppCompatActivity {

    ActivityNewPasswordBinding binding;

    private NetworkManager networkManager;

    private Boolean lastNetworkState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNewPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        connection();
        setupListeners();
    }

    private void connection() {

        networkManager = new NetworkManager();

        networkManager.register(this, new NetworkListener() {

            @Override
            public void onConnected() {
                runOnUiThread(() -> {

                    // show success ONLY if previously disconnected
                    if (lastNetworkState != null && !lastNetworkState) {
                        SnackBarHelper.showSuccess(binding.getRoot(),
                                "Internet Connection Available");
                    }

                    lastNetworkState = true;
                    setResetPassEnabled(true);
                });
            }

            @Override
            public void onDisconnected() {
                runOnUiThread(() -> {

                    // show error ONLY if previously connected
                    if (lastNetworkState == null || lastNetworkState) {
                        SnackBarHelper.showError(binding.getRoot(),
                                "No Internet Connection");
                    }

                    lastNetworkState = false;
                    setResetPassEnabled(false);
                });
            }
        });
    }

    private void setResetPassEnabled(boolean enabled) {
        binding.btnSResetPass.setEnabled(enabled);
        binding.btnSResetPass.setAlpha(enabled ? 1f : 0.5f);
    }
    private void setupListeners() {
        binding.backButton.setOnClickListener(v -> finish());

        binding.btnSResetPass.setOnClickListener(v -> validateAndReset());

        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.etPassword.setError(null);
                binding.etConfirmPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.etPassword.addTextChangedListener(clearErrorWatcher);
        binding.etConfirmPassword.addTextChangedListener(clearErrorWatcher);
    }

    private void validateAndReset() {
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        boolean isValid = true;

        isValid = isPasswordValid(password, isValid, confirmPassword);

        if (isValid) {
            // TODO: API call to reset password
            Toast.makeText(this, R.string.password_reset_successfully, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private boolean isPasswordValid(String password, boolean isValid, String confirmPassword) {
        if (!ValidationUtils.isValidPassword(password)) {
            binding.etPassword.setError(getString(R.string.password_must_be_at_least_6_characters));
            isValid = false;
        }

        if (!ValidationUtils.doPasswordsMatch(password, confirmPassword)) {
            binding.etConfirmPassword.setError(getString(R.string.passwords_do_not_match));
            isValid = false;
        }
        return isValid;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkManager.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setResetPassEnabled(networkManager.isConnected(this));
    }
}