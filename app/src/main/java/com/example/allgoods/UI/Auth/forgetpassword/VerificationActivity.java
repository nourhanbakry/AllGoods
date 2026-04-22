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
import com.example.allgoods.databinding.ActivityVerificationBinding;
import com.example.allgoods.utils.Network.NetworkListener;
import com.example.allgoods.utils.Network.NetworkManager;
import com.example.allgoods.utils.SnackBarHelper;
import com.example.allgoods.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class VerificationActivity extends AppCompatActivity {
    ActivityVerificationBinding binding;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;

    private NetworkManager networkManager;

    private Boolean lastNetworkState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        connection();
        setupOtpInputs();
        setupClickListeners();
        startResendTimer();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setConfirmEmailEnabled(networkManager.isConnected(this));
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
    private void setupOtpInputs() {
        binding.etDigit1.addTextChangedListener(new OtpTextWatcher(binding.etDigit1, binding.etDigit2));
        binding.etDigit2.addTextChangedListener(new OtpTextWatcher(binding.etDigit2, binding.etDigit3));
        binding.etDigit3.addTextChangedListener(new OtpTextWatcher(binding.etDigit3, binding.etDigit4));
        binding.etDigit4.addTextChangedListener(new OtpTextWatcher(binding.etDigit4, null));

        binding.etDigit1.setOnKeyListener(new OtpKeyListener(binding.etDigit1, null));
        binding.etDigit2.setOnKeyListener(new OtpKeyListener(binding.etDigit2, binding.etDigit1));
        binding.etDigit3.setOnKeyListener(new OtpKeyListener(binding.etDigit3, binding.etDigit2));
        binding.etDigit4.setOnKeyListener(new OtpKeyListener(binding.etDigit4, binding.etDigit3));
    }

    private void setupClickListeners() {
        binding.backButton.setOnClickListener(v -> finish());

        binding.btnConfirmEmail.setOnClickListener(v -> {
            String otp = getOtpCode();
            if (ValidationUtils.isValidOtp(otp)) {
                // TODO: Handle OTP verification logic
                SnackBarHelper.showSuccess(binding.getRoot(), getString(R.string.otp_confirmed, otp));
                startActivities(new Intent[]{new Intent(this, NewPasswordActivity.class)});
                finish();
            } else {
                SnackBarHelper.showError(binding.getRoot(), getString(R.string.please_enter_all_digits));
            }
        });

        binding.tvResend.setOnClickListener(v -> {
            if (!isTimerRunning) {
                // TODO: Resend OTP logic
                SnackBarHelper.showSuccess(binding.getRoot(), getString(R.string.otp_resent));
                startResendTimer();
            }
        });
    }

    private String getOtpCode() {
        return binding.etDigit1.getText().toString() +
                binding.etDigit2.getText().toString() +
                binding.etDigit3.getText().toString() +
                binding.etDigit4.getText().toString();
    }

    private void startResendTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        isTimerRunning = true;
        binding.tvResend.setEnabled(false);
        binding.tvResend.setTextColor(getResources().getColor(android.R.color.darker_gray, getTheme()));

        countDownTimer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                binding.tvTimer.setText(String.format(Locale.getDefault(), "00:%02d", seconds));
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                binding.tvResend.setEnabled(true);
                binding.tvResend.setTextColor(getResources().getColor(android.R.color.black, getTheme()));
                binding.tvTimer.setText("00:00");
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        networkManager.unregister(this);
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