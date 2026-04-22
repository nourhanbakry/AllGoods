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
import com.example.allgoods.databinding.ActivityForgetPasswordBinding;
import com.example.allgoods.utils.Network.NetworkListener;
import com.example.allgoods.utils.Network.NetworkManager;
import com.example.allgoods.utils.SnackBarHelper;
import com.google.android.material.button.MaterialButton;

public class ForgetPasswordActivity extends AppCompatActivity {
    ActivityForgetPasswordBinding binding;

    private NetworkManager networkManager;

    private Boolean lastNetworkState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
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

    @Override
    protected void onResume() {

        super.onResume();
        setConfirmEmailEnabled(networkManager.isConnected(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkManager.unregister(this);
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
                    setConfirmEmailEnabled(true);
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
                    setConfirmEmailEnabled(false);
                });
            }
        });
    }

    private void setConfirmEmailEnabled(boolean enabled) {
        binding.btnConfirmEmail.setEnabled(enabled);
        binding.btnConfirmEmail.setAlpha(enabled ? 1f : 0.5f);
    }
    private void setupListeners() {
        binding.btnConfirmEmail.setOnClickListener(v -> validateInputs());
        binding.backButton.setOnClickListener(v -> onBackPressed());
    }

    private void validateInputs() {
        String email = String.valueOf(binding.etEmailForget.getText()).trim();

        boolean isValid = isEmailValid(email);


        if (isValid) {
            verifyEmail(email);
        }
    }

    private boolean isEmailValid(String email) {
        if (email.isEmpty()) {
            binding.etEmailForget.setError(getString(R.string.email_is_required));
            return false;
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailForget.setError(getString(R.string.enter_valid_email));
            return false;
        }else {
            binding.etEmailForget.setError(null);
            return true;
        }
    }

    private void verifyEmail(String email) {
        SnackBarHelper.showSuccess(binding.getRoot(), getString(R.string.email_sent) + email);
        binding.getRoot().postDelayed(() -> startActivity(new Intent(this, VerificationActivity.class)), 1500);
    }


    private void setupTextWatchers() {

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.etEmailForget.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        binding.etEmailForget.addTextChangedListener(watcher);
    }


}

