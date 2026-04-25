package com.example.allgoods.UI.Customer.Cart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allgoods.R;
import com.example.allgoods.UI.Customer.Cart.Adapter.CartAdapter;
import com.example.allgoods.UI.Customer.Cart.Address.AddressFragment;
import com.example.allgoods.UI.Customer.Home.Adapter.ProductAdapter;
import com.example.allgoods.UI.Customer.MyCards.MyCardsFragment;
import com.example.allgoods.UI.Customer.Reviews.View.ReviewsFragment;
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
        setupViewModel();

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    private void setupViewModel(){

        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        adapter = new CartAdapter(requireContext(), new ArrayList<>());
        binding.checkoutItemsRv.setAdapter(adapter);

        viewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {

            adapter.updateData(cartItems);

            updatePrices();
        });

        if (viewModel.getCartItems().getValue() == null) {
            viewModel.loadDummyCartProducts();
        }

        binding.OpenAddress.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new AddressFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.OpenPayment.setOnClickListener(v -> openPayment());
    }

    private void updatePrices() {
        binding.subTotalPrice.setText(String.valueOf(viewModel.calculateSubtotal()));
        binding.totalPrice.setText(String.valueOf(viewModel.calculateTotal()));
    }

    private void openPayment(){
        Bundle bundle = new Bundle();
        bundle.putString("source", "cart");

        MyCardsFragment fragment = new MyCardsFragment();
        fragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }
}