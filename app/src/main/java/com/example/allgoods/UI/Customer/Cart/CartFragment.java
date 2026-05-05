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
import com.example.allgoods.model.AddressModel;
import com.example.allgoods.utils.PriceUtils;

import android.widget.Toast;
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

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        adapter = new CartAdapter(requireContext(), new ArrayList<>(), new CartAdapter.OnCartItemChangeListener() {
            @Override
            public void onDelete(String cartItemId) {
                viewModel.removeFromCart(cartItemId);
            }

            @Override
            public void onQuantityChange(String cartItemId, int newQuantity) {
                viewModel.updateQuantity(cartItemId, newQuantity);
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

        viewModel.getSelectedAddress().observe(getViewLifecycleOwner(), address -> {
            if (address != null) {
                binding.addressInfo.setVisibility(View.VISIBLE);
                binding.noAddressText.setVisibility(View.GONE);
                binding.txtDetaildAddress.setText(address.getAddress());
                binding.cityName.setText(address.getCity());
            } else {
                // If no selected address, try loading the primary one
                viewModel.loadPrimaryAddress();
            }
        });

        viewModel.getPrimaryAddress().observe(getViewLifecycleOwner(), address -> {
            // Only use primary address if no address is currently selected
            if (viewModel.getSelectedAddress().getValue() == null && address != null) {
                viewModel.setSelectedAddress(address);
            } else if (viewModel.getSelectedAddress().getValue() == null) {
                binding.addressInfo.setVisibility(View.GONE);
                binding.noAddressText.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getSelectedCard().observe(getViewLifecycleOwner(), card -> {
            if (card != null) {
                binding.txtVisa.setText(card.name);
                String last4 = card.number.length() > 4
                        ? card.number.substring(card.number.length() - 4)
                        : card.number;
                binding.visaCardNumber.setText("**** " + last4);
            } else {
                viewModel.loadPrimaryCard();
            }
        });

        viewModel.getPrimaryCard().observe(getViewLifecycleOwner(), card -> {
            if (viewModel.getSelectedCard().getValue() == null && card != null) {
                viewModel.setSelectedCard(card);
            }
        });

        viewModel.getOrderStatus().observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                if (status.equals("Success")) {
                    viewModel.resetOrderStatus();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, new OrderConfirmedFragment())
                            .commit();
                } else {
                    Toast.makeText(getContext(), status, Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                viewModel.resetErrorMessage();
            }
        });

        viewModel.loadCartProducts();

        if (viewModel.getSelectedAddress().getValue() == null) {
            viewModel.loadPrimaryAddress();
        }

        if (viewModel.getSelectedCard().getValue() == null) {
            viewModel.loadPrimaryCard();
        }

        binding.OpenAddress.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new AddressFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.OpenPayment.setOnClickListener(v -> openPayment());

        binding.btnCheckout.setOnClickListener(v -> viewModel.checkout());
    }

    private void updatePrices() {
        binding.subTotalPrice.setText("$" + PriceUtils.formatPrice(viewModel.calculateSubtotal()));
        binding.shippingCostPrice.setText("$" + PriceUtils.formatPrice(20.0)); // Shipping is flat 20
        binding.totalPrice.setText("$" + PriceUtils.formatPrice(viewModel.calculateTotal()));
    }

    private void openPayment() {
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
