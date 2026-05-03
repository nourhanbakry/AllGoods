package com.example.allgoods.UI.Customer.Cart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allgoods.R;
import com.example.allgoods.UI.Customer.Home.HomeFragment;
import com.example.allgoods.databinding.FragmentOrderConfirmedBinding;
import com.example.allgoods.model.OrderModel;
import com.example.allgoods.utils.EmailUtils;

import androidx.lifecycle.ViewModelProvider;


public class OrderConfirmedFragment extends Fragment {

    private FragmentOrderConfirmedBinding binding;
    private CartViewModel viewModel;

    public static OrderConfirmedFragment newInstance(String param1, String param2) {
        OrderConfirmedFragment fragment = new OrderConfirmedFragment();
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
        binding = FragmentOrderConfirmedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        binding.btnSave.setOnClickListener(v -> {
            viewModel.resetOrderStatus();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new HomeFragment())
                    .commit();
        });
    }
}