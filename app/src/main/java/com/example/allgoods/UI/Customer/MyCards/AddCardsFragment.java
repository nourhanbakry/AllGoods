package com.example.allgoods.UI.Customer.MyCards;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.allgoods.R;
import com.example.allgoods.databinding.FragmentAddCardsBinding;
import com.example.allgoods.utils.ValidationUtils;


public class AddCardsFragment extends Fragment {

    private FragmentAddCardsBinding binding;

    public static AddCardsFragment newInstance(String param1, String param2) {
        AddCardsFragment fragment = new AddCardsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddCardsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListeners();
    }

    private void setupListeners() {
        binding.backButtonAddCard.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Card Number Formatter (XXXX XXXX XXXX XXXX)
        binding.etCardNumber.addTextChangedListener(new TextWatcher() {
            private boolean isDeleting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isDeleting = after < count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String original = s.toString();
                String clean = original.replace(" ", "");
                
                if (clean.length() > 16) {
                    clean = clean.substring(0, 16);
                }

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < clean.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        sb.append(" ");
                    }
                    sb.append(clean.charAt(i));
                }

                String formatted = sb.toString();
                if (!formatted.equals(original)) {
                    binding.etCardNumber.removeTextChangedListener(this);
                    binding.etCardNumber.setText(formatted);
                    binding.etCardNumber.setSelection(formatted.length());
                    binding.etCardNumber.addTextChangedListener(this);
                }
            }
        });

        // Expiry Date Formatter (MM/YY)
        binding.etEXP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                
                // Remove existing slashes to re-format
                String clean = input.replace("/", "");
                
                if (clean.length() > 4) {
                    clean = clean.substring(0, 4);
                }

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < clean.length(); i++) {
                    if (i == 2) {
                        sb.append("/");
                    }
                    sb.append(clean.charAt(i));
                }

                String formatted = sb.toString();
                if (!formatted.equals(input)) {
                    binding.etEXP.removeTextChangedListener(this);
                    binding.etEXP.setText(formatted);
                    binding.etEXP.setSelection(formatted.length());
                    binding.etEXP.addTextChangedListener(this);
                }
            }
        });

        binding.btnAddCard.setOnClickListener(v -> validateAndAddCard());
    }

    private void validateAndAddCard() {
        String owner = binding.etCardOwner.getText().toString().trim();
        String number = binding.etCardNumber.getText().toString().trim();
        String exp = binding.etEXP.getText().toString().trim();
        String cvv = binding.etCVV.getText().toString().trim();

        if (!ValidationUtils.isValidName(owner)) {
            showError("Please enter a valid card owner name");
            return;
        }

        if (!ValidationUtils.isValidCardNumber(number)) {
            showError("Please enter a valid card number");
            return;
        }

        if (!ValidationUtils.isValidExpiryDate(exp)) {
            showError("Please enter a valid expiry date (MM/YY)");
            return;
        }

        if (!ValidationUtils.isValidCVV(cvv)) {
            showError("Please enter a valid CVV");
            return;
        }

        // If all valid
        Toast.makeText(requireContext(), "Card added successfully!", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack();
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}