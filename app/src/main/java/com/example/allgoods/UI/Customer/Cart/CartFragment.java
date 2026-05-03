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

        adapter = new CartAdapter(requireContext(), new ArrayList<>(), new CartAdapter.OnCartItemChangeListener() {
            @Override
            public void onDelete(String productId) {
                viewModel.removeFromCart(productId);
            }

            @Override
            public void onQuantityChange(String productId, int newQuantity) {
                viewModel.updateQuantity(productId, newQuantity);
            }
        });
        binding.checkoutItemsRv.setAdapter(adapter);

        viewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            if (cartItems.isEmpty()) {
                binding.emptyCartLayout.setVisibility(View.VISIBLE);
                binding.mainScrollView.setVisibility(View.GONE);
            } else {
                binding.emptyCartLayout.setVisibility(View.GONE);
                binding.mainScrollView.setVisibility(View.VISIBLE);
                adapter.updateData(cartItems);
                updatePrices();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.emptyCartLayout.setVisibility(View.GONE);
                binding.mainScrollView.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.loadCartProducts();

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