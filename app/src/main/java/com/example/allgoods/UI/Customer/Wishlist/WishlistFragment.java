package com.example.allgoods.UI.Customer.Wishlist;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allgoods.R;
import com.example.allgoods.UI.Customer.Home.Adapter.ProductAdapter;
import com.example.allgoods.UI.Customer.Home.HomeViewModel;
import com.example.allgoods.UI.Customer.Wishlist.Adapter.WishlistProductAdapter;
import com.example.allgoods.databinding.FragmentWishlistBinding;


public class WishlistFragment extends Fragment {
    FragmentWishlistBinding binding;
    WishlistViewModel viewModel;

    public WishlistFragment() {}


    public static WishlistFragment newInstance(String param1, String param2) {
        WishlistFragment fragment = new WishlistFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWishlistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(WishlistViewModel.class);
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {

            if (products == null || products.isEmpty()){
                showEmptyState();
            } else {
                binding.emptyWishlistLayout.setVisibility(View.GONE);
                binding.wishlistS.setVisibility(View.VISIBLE);
                binding.favItemsNum.setText(String.valueOf(products.size()));

                WishlistProductAdapter adapter = new WishlistProductAdapter(requireContext(), products, remainingCount -> {
                    binding.favItemsNum.setText(String.valueOf(remainingCount));

                    if (remainingCount == 0) {
                        showEmptyState();
                    }
                });
                binding.wishlistProductsRv.setAdapter(adapter);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.wishlistS.setVisibility(GONE);
                binding.progressBar.setVisibility(VISIBLE);
                binding.emptyWishlistLayout.setVisibility(GONE);
            } else {
                binding.progressBar.setVisibility(GONE);
            }
        });

        viewModel.loadProducts();

    }

    private void showEmptyState() {
        binding.wishlistS.setVisibility(View.GONE);
        binding.emptyWishlistLayout.setVisibility(View.VISIBLE);
        binding.favItemsNum.setText("0");
        binding.emptyCartAnimation.playAnimation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding= null;
    }
}