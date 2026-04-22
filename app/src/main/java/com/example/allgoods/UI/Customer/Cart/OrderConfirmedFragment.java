package com.example.allgoods.UI.Customer.Cart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allgoods.R;
import com.example.allgoods.databinding.FragmentOrderConfirmedBinding;


public class OrderConfirmedFragment extends Fragment {

    private FragmentOrderConfirmedBinding binding;

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
}