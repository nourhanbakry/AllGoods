package com.example.allgoods.UI.Auth.forgetpassword;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
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
import com.example.allgoods.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class VerificationActivity extends AppCompatActivity {

    private EditText etDigit1, etDigit2, etDigit3, etDigit4;
    private TextView tvTimer, tvResend;
    private MaterialButton btnConfirmEmail;
    private ImageView backButton;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupOtpInputs();
        setupClickListeners();
        startResendTimer();
    }

    private void initViews() {
        etDigit1 = findViewById(R.id.etDigit1);
        etDigit2 = findViewById(R.id.etDigit2);
        etDigit3 = findViewById(R.id.etDigit3);
        etDigit4 = findViewById(R.id.etDigit4);
        tvTimer = findViewById(R.id.tvTimer);
        tvResend = findViewById(R.id.tvResend);
        btnConfirmEmail = findViewById(R.id.btnConfirmEmail);
        backButton = findViewById(R.id.backButton);
    }

    private void setupOtpInputs() {
        etDigit1.addTextChangedListener(new OtpTextWatcher(etDigit1, etDigit2));
        etDigit2.addTextChangedListener(new OtpTextWatcher(etDigit2, etDigit3));
        etDigit3.addTextChangedListener(new OtpTextWatcher(etDigit3, etDigit4));
        etDigit4.addTextChangedListener(new OtpTextWatcher(etDigit4, null));

        etDigit1.setOnKeyListener(new OtpKeyListener(etDigit1, null));
        etDigit2.setOnKeyListener(new OtpKeyListener(etDigit2, etDigit1));
        etDigit3.setOnKeyListener(new OtpKeyListener(etDigit3, etDigit2));
        etDigit4.setOnKeyListener(new OtpKeyListener(etDigit4, etDigit3));
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        btnConfirmEmail.setOnClickListener(v -> {
            String otp = getOtpCode();
            if (ValidationUtils.isValidOtp(otp)) {
                // TODO: Handle OTP verification logic
                Toast.makeText(this, getString(R.string.otp_confirmed, otp), Toast.LENGTH_SHORT).show();
                startActivities(new Intent[]{new Intent(this, NewPasswordActivity.class)});
            } else {
                Toast.makeText(this, R.string.please_enter_all_digits, Toast.LENGTH_SHORT).show();
            }
        });

        tvResend.setOnClickListener(v -> {
            if (!isTimerRunning) {
                // TODO: Resend OTP logic
                Toast.makeText(this, R.string.otp_resent, Toast.LENGTH_SHORT).show();
                startResendTimer();
            }
        });
    }

    private String getOtpCode() {
        return etDigit1.getText().toString() +
                etDigit2.getText().toString() +
                etDigit3.getText().toString() +
                etDigit4.getText().toString();
    }

    private void startResendTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        isTimerRunning = true;
        tvResend.setEnabled(false);
        tvResend.setTextColor(getResources().getColor(android.R.color.darker_gray, getTheme()));

        countDownTimer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                tvTimer.setText(String.format(Locale.getDefault(), "00:%02d", seconds));
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                tvResend.setEnabled(true);
                tvResend.setTextColor(getResources().getColor(android.R.color.black, getTheme()));
                tvTimer.setText("00:00");
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private class OtpTextWatcher implements TextWatcher {
        private final View currentView;
        private final View nextView;

        public OtpTextWatcher(View currentView, View nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }
    }

    private class OtpKeyListener implements View.OnKeyListener {
        private final EditText currentView;
        private final EditText previousView;

        public OtpKeyListener(EditText currentView, EditText previousView) {
            this.currentView = currentView;
            this.previousView = previousView;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (currentView.getText().length() == 0 && previousView != null) {
                    previousView.requestFocus();
                    previousView.setText("");
                    return true;
                }
            }
            return false;
        }
    }
}