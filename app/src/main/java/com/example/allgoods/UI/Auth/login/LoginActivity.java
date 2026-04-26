package com.example.allgoods.UI.Auth.login;

import static com.example.allgoods.utils.Result.Status.LOADING;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.allgoods.Data.local.SharedPrefManager;
import com.example.allgoods.Data.repository.Auth.AuthRepositoryImpl;
import com.example.allgoods.UI.Auth.forgetpassword.ForgetPasswordActivity;
import com.example.allgoods.UI.Auth.signup.SignUpActivity;
import com.example.allgoods.UI.Auth.signup.SignUpViewModel;
import com.example.allgoods.UI.Main.MainActivity;
import com.example.allgoods.databinding.ActivityLoginBinding;
import com.example.allgoods.utils.Network.NetworkListener;
import com.example.allgoods.utils.Network.NetworkManager;
import com.example.allgoods.utils.SnackBarHelper;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    private NetworkManager networkManager;

    private boolean lastNetworkState = true;

    private LoginViewModel viewModel;

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

        setupViewModel();
        connection();
        setupListeners();
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

    private void setupViewModel(){
        viewModel = new LoginViewModel(new AuthRepositoryImpl(this));
        viewModel.getLoginState().observe(this, result -> {

            switch (result.status) {

                case LOADING:
                    setLoginEnabled(false);
                    break;

                case SUCCESS:

                    setLoginEnabled(true);

                    String role = result.data.role;
                    String email = result.data.email;
                    String name = result.data.name;
                    boolean remember = binding.loginRememberMeSwitch.isChecked();

                    navigateToHome(role, remember, email, name);

                    break;

                case ERROR:
                    setLoginEnabled(true);
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

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

    private void setLoginEnabled(boolean enabled) {
        binding.btnLogin.setEnabled(enabled);
        binding.btnLogin.setAlpha(enabled ? 1f : 0.5f);
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> validateInputs());
        binding.forgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgetPasswordActivity.class);
            intent.putExtra("SOURCE", "AUTH");
            startActivity(intent);
        });

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
       viewModel.login(email,password);
    }

    private void navigateToHome(String role, boolean rememberMe, String email, String name) {

        SharedPrefManager pref = new SharedPrefManager(this);

        if (role.equals("seller")) {
            pref.logout();
        } else if (rememberMe) {
            pref.saveUser(name, email, role);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER_ROLE", role);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }
}