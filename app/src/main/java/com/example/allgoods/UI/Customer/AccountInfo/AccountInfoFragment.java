package com.example.allgoods.UI.Customer.AccountInfo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allgoods.Data.repository.User.UserRepository;
import com.example.allgoods.Data.repository.User.UserRepositoryImpl;
import com.example.allgoods.R;
import com.example.allgoods.UI.Main.MainActivity;
import com.example.allgoods.databinding.FragmentAccountInfoBinding;
import com.example.allgoods.model.AddressModel;

import android.widget.Toast;


public class AccountInfoFragment extends Fragment {
    FragmentAccountInfoBinding binding;
    private UserRepository userRepository;

    public AccountInfoFragment() {}

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomBar();
        }
    }

    public static AccountInfoFragment newInstance(String param1, String param2) {
        AccountInfoFragment fragment = new AccountInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = new UserRepositoryImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountInfoBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.backButtonAccount.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        loadAccountInfo();
    }

    private void loadAccountInfo() {
        userRepository.getPrimaryAddress(new UserRepository.OnAddressFetchListener() {
            @Override
            public void onSuccess(AddressModel address) {
                if (address != null) {
                    binding.txtAddressValue.setText(address.getAddress());
                    binding.txtPhoneValue.setText(address.getPhoneNumber());
                } else {
                    binding.txtAddressValue.setText("No primary address set");
                    binding.txtPhoneValue.setText("N/A");
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBottomBar();
        }
    }
}