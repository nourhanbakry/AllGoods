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
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.example.allgoods.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import com.example.allgoods.Data.repository.SellerProduct.ProductRepository;
import com.example.allgoods.Data.repository.SellerProduct.ProductRepositoryImpl;
import com.example.allgoods.model.ProductModel;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

public class InventoryFragment extends Fragment {

    private final ProductRepository productRepository = new ProductRepositoryImpl();
    private InventoryAdapter adapter;
    private List<ProductModel> productList = new ArrayList<>();
    private TextView tvProductsCount;
    private View emptyInventoryLayout;
    private ProgressBar progressBar;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        RecyclerView rv = view.findViewById(R.id.rvInventory);
        tvProductsCount = view.findViewById(R.id.tvProductsCount);
        emptyInventoryLayout = view.findViewById(R.id.emptyInventoryLayout);
        progressBar = view.findViewById(R.id.progressBar);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new InventoryAdapter(productList, new InventoryAdapter.OnInventoryActionListener() {
            @Override
            public void onDeleteClick(ProductModel product, int position) {
                showDeleteDialog(product, position);
            }

            @Override
            public void onRestockClick(ProductModel product, int position) {
                showRestockDialog(product, position);
            }
        });

        rv.setAdapter(adapter);

        fetchProducts();

        return view;
    }

    private void fetchProducts() {
        String sellerId = FirebaseAuth.getInstance().getUid();
        if (sellerId == null) return;

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        productRepository.getProductsBySeller(sellerId, new ProductRepository.OnProductsFetchListener() {
            @Override
            public void onSuccess(List<ProductModel> products) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                productList.clear();
                productList.addAll(products);
                if (adapter != null) adapter.notifyDataSetChanged();
                updateProductsCount();
            }

            @Override
            public void onFailure(String error) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (isAdded()) Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                updateProductsCount();
            }
        });
    }

    private void updateProductsCount() {
        if (tvProductsCount != null) {
            tvProductsCount.setText(productList.size() + " Products");
        }
        if (emptyInventoryLayout != null) {
            emptyInventoryLayout.setVisibility(productList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void showDeleteDialog(ProductModel product, int position) {

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
            productRepository.deleteProduct(product.getId(), new ProductRepository.OnProductUploadListener() {
                @Override
                public void onSuccess() {
                    if (isAdded()) {
                        productList.remove(position);
                        adapter.notifyItemRemoved(position);
                        updateProductsCount();
                        dialog.dismiss();
                        Toast.makeText(requireContext(), "Product deleted", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(String error) {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Error deleting: " + error, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showRestockDialog(ProductModel product, int position) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_restock_product);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        TextView tvProductName = dialog.findViewById(R.id.tvProductName);
        tvProductName.setText(product.getName());

        Map<String, Integer> currentStock = product.getSizesQuantity();
        Map<String, Integer> addedStock = new HashMap<>();

        // Helper to setup picker
        setupPicker(dialog, R.id.restockPickerXS, "XS", currentStock.getOrDefault("XS", 0), addedStock);
        setupPicker(dialog, R.id.restockPickerS, "S", currentStock.getOrDefault("S", 0), addedStock);
        setupPicker(dialog, R.id.restockPickerM, "M", currentStock.getOrDefault("M", 0), addedStock);
        setupPicker(dialog, R.id.restockPickerL, "L", currentStock.getOrDefault("L", 0), addedStock);
        setupPicker(dialog, R.id.restockPickerXL, "XL", currentStock.getOrDefault("XL", 0), addedStock);
        setupPicker(dialog, R.id.restockPickerXXL, "XXL", currentStock.getOrDefault("XXL", 0), addedStock);

        // Update current stock text views
        ((TextView)dialog.findViewById(R.id.tvStockXS)).setText(String.valueOf(currentStock.getOrDefault("XS", 0)));
        ((TextView)dialog.findViewById(R.id.tvStockS)).setText(String.valueOf(currentStock.getOrDefault("S", 0)));
        ((TextView)dialog.findViewById(R.id.tvStockM)).setText(String.valueOf(currentStock.getOrDefault("M", 0)));
        ((TextView)dialog.findViewById(R.id.tvStockL)).setText(String.valueOf(currentStock.getOrDefault("L", 0)));
        ((TextView)dialog.findViewById(R.id.tvStockXL)).setText(String.valueOf(currentStock.getOrDefault("XL", 0)));
        ((TextView)dialog.findViewById(R.id.tvStockXXL)).setText(String.valueOf(currentStock.getOrDefault("XXL", 0)));

        MaterialButton btnRestock = dialog.findViewById(R.id.btnRestock);
        btnRestock.setOnClickListener(v -> {
            Map<String, Integer> newSizesQuantity = new HashMap<>(currentStock);
            for (Map.Entry<String, Integer> entry : addedStock.entrySet()) {
                newSizesQuantity.put(entry.getKey(), newSizesQuantity.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }

            productRepository.updateProductQuantity(product.getId(), newSizesQuantity, new ProductRepository.OnProductUploadListener() {
                @Override
                public void onSuccess() {
                    product.setSizesQuantity(newSizesQuantity);
                    adapter.notifyItemChanged(position);
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Restock successful", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void setupPicker(Dialog dialog, int pickerId, String size, int current, Map<String, Integer> addedStock) {
        View picker = dialog.findViewById(pickerId);
        TextView tvLabel = picker.findViewById(R.id.tvSizeLabel);
        TextView tvQty = picker.findViewById(R.id.tvQuantity);
        ImageView btnPlus = picker.findViewById(R.id.btnPlus);
        ImageView btnMinus = picker.findViewById(R.id.btnMinus);

        tvLabel.setText(size);
        addedStock.put(size, 0);

        btnPlus.setOnClickListener(v -> {
            int currentAdd = addedStock.get(size);
            addedStock.put(size, currentAdd + 1);
            tvQty.setText(String.valueOf(addedStock.get(size)));
        });

        btnMinus.setOnClickListener(v -> {
            int currentAdd = addedStock.get(size);
            if (currentAdd > 0) {
                addedStock.put(size, currentAdd - 1);
                tvQty.setText(String.valueOf(addedStock.get(size)));
            }
        });
    }
}