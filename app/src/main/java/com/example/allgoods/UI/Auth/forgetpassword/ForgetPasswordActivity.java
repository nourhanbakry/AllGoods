package com.example.allgoods.UI.Auth.forgetpassword;

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
import com.google.android.material.button.MaterialButton;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private MaterialButton btnConfirmPassword;
    private ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);
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
        etEmail = findViewById(R.id.etEmailForget);
        btnConfirmPassword = findViewById(R.id.btnConfirmEmail);
        ivBack = findViewById(R.id.backButton);
    }

    private void setupListeners() {
        btnConfirmPassword.setOnClickListener(v -> validateInputs());
        ivBack.setOnClickListener(v -> onBackPressed());
    }

    private void validateInputs() {
        String email = etEmail.getText().toString().trim();

        boolean isValid = true;
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email");
            isValid = false;
        } else {
            etEmail.setError(null);
        }


        if (isValid) {
            verifyEmail(email);
        }
    }

    private void verifyEmail(String email) {
        Toast.makeText(this, "Email Sent", Toast.LENGTH_SHORT).show();
        startActivities(new android.content.Intent[]{new android.content.Intent(this, VerificationActivity.class)});
    }


    private void setupTextWatchers() {

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etEmail.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        etEmail.addTextChangedListener(watcher);
    }


}

