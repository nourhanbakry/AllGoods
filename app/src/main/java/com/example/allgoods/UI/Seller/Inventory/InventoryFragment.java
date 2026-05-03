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
import android.app.Dialog;

import android.graphics.Color;

import android.graphics.drawable.ColorDrawable;

import android.view.Window;

import android.widget.TextView;
import com.example.allgoods.R;
import com.google.android.material.button.MaterialButton;

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

        rv.setAdapter(new InventoryAdapter(dummyList, position -> {

            showDeleteDialog(position, dummyList, rv);

        }));

        return view;
    }

    private void showDeleteDialog(int position, List<String> list, RecyclerView rv) {

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        TextView title = dialog.findViewById(R.id.dialog_title);
        TextView content = dialog.findViewById(R.id.dialog_content);
        MaterialButton btnDelete = dialog.findViewById(R.id.btnDelete);
        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);

        title.setText("Delete Product?");
        content.setText("This action cannot be undone. This will permanently delete the product from your inventory .");

        btnDelete.setOnClickListener(v -> {

            if (position >= 0 && position < list.size()) {

                list.remove(position);

                rv.getAdapter().notifyItemRemoved(position);

            }

            dialog.dismiss();

        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}