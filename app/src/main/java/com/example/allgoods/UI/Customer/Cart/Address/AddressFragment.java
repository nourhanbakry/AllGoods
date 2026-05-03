package com.example.allgoods.UI.Customer.Cart.Address;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.allgoods.R;
import com.example.allgoods.UI.Main.MainActivity;
import com.example.allgoods.databinding.FragmentAddressBinding;


import com.example.allgoods.Data.repository.User.UserRepository;
import com.example.allgoods.Data.repository.User.UserRepositoryImpl;
import com.example.allgoods.model.AddressModel;
import android.widget.Toast;


import com.example.allgoods.UI.Customer.Cart.CartViewModel;
import androidx.lifecycle.ViewModelProvider;


public class AddressFragment extends Fragment {

    private FragmentAddressBinding binding;
    private final UserRepository userRepository = new UserRepositoryImpl();
    private CartViewModel cartViewModel;

    public static AddressFragment newInstance(String param1, String param2) {
        AddressFragment fragment = new AddressFragment();
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
        binding = FragmentAddressBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        binding.backButtonAddress.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        binding.btnSave.setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        String name = binding.etName.getText().toString().trim();
        String country = binding.etCountry.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();
        String phone = binding.etNumberPhone.getText().toString().trim();
        String addressLine = binding.etAddress.getText().toString().trim();
        boolean isPrimary = binding.switchPrimary.isChecked();

        if (name.isEmpty() || country.isEmpty() || city.isEmpty() || phone.isEmpty() || addressLine.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        AddressModel address = new AddressModel(name, country, city, phone, addressLine, isPrimary);

        userRepository.saveAddress(address, new UserRepository.OnAddressChangeListener() {
            @Override
            public void onSuccess() {
                cartViewModel.setSelectedAddress(address);
                Toast.makeText(requireContext(), "Address saved successfully", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).hideBottomBar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) requireActivity()).showBottomBar();
    }
}