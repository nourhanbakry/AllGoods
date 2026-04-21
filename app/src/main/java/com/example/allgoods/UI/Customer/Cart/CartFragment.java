package com.example.allgoods.UI.Customer.Cart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allgoods.UI.Customer.Cart.Adapter.CartAdapter;
import com.example.allgoods.UI.Customer.Home.Adapter.ProductAdapter;
import com.example.allgoods.databinding.FragmentCartBinding;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    FragmentCartBinding binding;
    CartViewModel viewModel;
    CartAdapter adapter;

    public CartFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       setupViewModel();
    }
    private void setupViewModel(){
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        viewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {

            if (adapter == null) {
                adapter = new CartAdapter(requireContext(), cartItems);
                binding.checkoutItemsRv.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            updatePrices();
        });

        viewModel.loadDummyCartProducts();

    }

    private void updatePrices() {
        binding.subTotalPrice.setText(String.valueOf(viewModel.calculateSubtotal()));
        binding.totalPrice.setText(String.valueOf(viewModel.calculateTotal()));
    }
}