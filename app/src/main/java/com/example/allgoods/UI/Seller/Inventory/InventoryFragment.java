package com.example.allgoods.UI.Seller.Inventory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allgoods.R;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {

    public static InventoryFragment newInstance(String param1, String param2) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        RecyclerView rv = view.findViewById(R.id.rvInventory);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> dummyList = new ArrayList<>();
        dummyList.add("1");
        dummyList.add("2");
        dummyList.add("3");

        rv.setAdapter(new InventoryAdapter(dummyList));

        return view;
    }
}