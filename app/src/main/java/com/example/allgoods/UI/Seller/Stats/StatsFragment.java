package com.example.allgoods.UI.Seller.Stats;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allgoods.Data.repository.Auth.AuthRepositoryImpl;
import com.example.allgoods.Data.repository.Order.OrderRepository;
import com.example.allgoods.Data.repository.Order.OrderRepositoryImpl;
import com.example.allgoods.Data.repository.SellerProduct.ProductRepository;
import com.example.allgoods.Data.repository.SellerProduct.ProductRepositoryImpl;
import com.example.allgoods.R;
import com.example.allgoods.UI.Auth.login.LoginActivity;
import com.example.allgoods.databinding.FragmentStatsBinding;
import com.example.allgoods.databinding.ItemRatingProgressBinding;
import com.example.allgoods.model.OrderModel;
import com.example.allgoods.model.ProductModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatsFragment extends Fragment {
    private FragmentStatsBinding binding;
    private final ProductRepository productRepository = new ProductRepositoryImpl();
    private final OrderRepository orderRepository = new OrderRepositoryImpl();

    public StatsFragment() {}


    public static StatsFragment newInstance() {
        StatsFragment fragment = new StatsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.logoutLayout.setOnClickListener(v -> showLogoutDialog());
        loadStatistics();
    }

    private void loadStatistics() {
        String sellerId = FirebaseAuth.getInstance().getUid();
        if (sellerId == null) return;

        // Fetch Products for count and low stock
        productRepository.getProductsBySeller(sellerId, new ProductRepository.OnProductsFetchListener() {
            @Override
            public void onSuccess(List<ProductModel> products) {
                if (binding == null) return;
                binding.tvProductsCount.setText(String.valueOf(products.size()));
                
                int lowStockCount = 0;
                for (ProductModel p : products) {
                    int totalQty = 0;
                    if (p.getSizesQuantity() != null) {
                        for (int qty : p.getSizesQuantity().values()) {
                            totalQty += qty;
                        }
                    }
                    if (totalQty < 5) {
                        lowStockCount++;
                    }
                }
                binding.tvLowStockCount.setText(String.valueOf(lowStockCount));
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch Orders for revenue, sales and sizes
        orderRepository.getSellerOrders(sellerId, new OrderRepository.OnOrdersFetchListener() {
            @Override
            public void onSuccess(List<OrderModel> orders) {
                if (binding == null) return;
                
                double totalRevenue = 0;
                int totalSalesCount = 0;
                Map<String, ProductSalesInfo> productSales = new HashMap<>();
                Map<String, Integer> sizeSales = new HashMap<>();

                for (OrderModel order : orders) {
                    if ("delivered".equalsIgnoreCase(order.getStatus())) {
                        totalSalesCount++;
                        
                        if (order.getItems() != null) {
                            for (ProductModel item : order.getItems()) {
                                if (sellerId.equals(item.getSellerId())) {
                                    double itemTotal = item.getPrice() * item.getQuantity();
                                    totalRevenue += itemTotal;
                                    
                                    // Top Selling logic
                                    ProductSalesInfo info = productSales.getOrDefault(item.getId(), new ProductSalesInfo(item.getName(), item.getPrice()));
                                    info.soldCount += item.getQuantity();
                                    productSales.put(item.getId(), info);

                                    // Size Stats logic
                                    String size = item.getSelectedSize();
                                    if (size != null) {
                                        sizeSales.put(size, sizeSales.getOrDefault(size, 0) + item.getQuantity());
                                    }
                                }
                            }
                        }
                    }
                }

                binding.tvRevenueValue.setText("$" + String.format(Locale.US, "%.2f", totalRevenue));
                binding.tvSalesValue.setText(String.valueOf(totalSalesCount));
                
                displayTopSelling(productSales);
                displayMostSoldSizes(sizeSales);
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayTopSelling(Map<String, ProductSalesInfo> productSales) {
        List<ProductSalesInfo> sortedSales = new ArrayList<>(productSales.values());
        Collections.sort(sortedSales, (o1, o2) -> Integer.compare(o2.soldCount, o1.soldCount));

        binding.layoutRank1.setVisibility(View.GONE);
        binding.dividerRank1.setVisibility(View.GONE);
        binding.layoutRank2.setVisibility(View.GONE);
        binding.dividerRank2.setVisibility(View.GONE);
        binding.layoutRank3.setVisibility(View.GONE);

        if (sortedSales.size() >= 1) {
            ProductSalesInfo p = sortedSales.get(0);
            binding.layoutRank1.setVisibility(View.VISIBLE);
            binding.dividerRank1.setVisibility(View.VISIBLE);
            binding.tvNameRank1.setText(p.name);
            binding.tvSoldCountRank1.setText(p.soldCount + " sold");
            binding.tvPriceRank1.setText("$" + String.format(Locale.US, "%.2f", p.price));
        }
        if (sortedSales.size() >= 2) {
            ProductSalesInfo p = sortedSales.get(1);
            binding.layoutRank2.setVisibility(View.VISIBLE);
            binding.dividerRank2.setVisibility(View.VISIBLE);
            binding.tvNameRank2.setText(p.name);
            binding.tvSoldCountRank2.setText(p.soldCount + " sold");
            binding.tvPriceRank2.setText("$" + String.format(Locale.US, "%.2f", p.price));
        }
        if (sortedSales.size() >= 3) {
            ProductSalesInfo p = sortedSales.get(2);
            binding.layoutRank3.setVisibility(View.VISIBLE);
            binding.tvNameRank3.setText(p.name);
            binding.tvSoldCountRank3.setText(p.soldCount + " sold");
            binding.tvPriceRank3.setText("$" + String.format(Locale.US, "%.2f", p.price));
        }
    }

    private void displayMostSoldSizes(Map<String, Integer> sizeSales) {
        if (binding == null) return;
        binding.llMostSoldSizes.removeAllViews();

        if (sizeSales.isEmpty()) {
            TextView tv = new TextView(getContext());
            tv.setText("No data available");
            tv.setTextColor(Color.GRAY);
            binding.llMostSoldSizes.addView(tv);
            return;
        }

        // Find max for scale calculation
        int maxSales = 0;
        for (int sales : sizeSales.values()) {
            if (sales > maxSales) maxSales = sales;
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(sizeSales.entrySet());
        // Sort alphabetically or by size? Let's keep them sorted by sales for now or simple order.
        // Actually, sorting alphabetically is usually better for columns.
        Collections.sort(list, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (Map.Entry<String, Integer> entry : list) {
            View column = inflater.inflate(R.layout.item_column_size_stat, binding.llMostSoldSizes, false);
            
            TextView tvLabel = column.findViewById(R.id.tvSizeLabel);
            TextView tvCount = column.findViewById(R.id.tvSizeCount);
            View viewBar = column.findViewById(R.id.viewBar);
            
            tvLabel.setText(entry.getKey());
            tvCount.setText(String.valueOf(entry.getValue()));
            
            // Adjust bar height
            ViewGroup.LayoutParams params = viewBar.getLayoutParams();
            if (maxSales > 0) {
                // max height 140dp (approx from parent 180dp)
                params.height = (int) ((float) entry.getValue() / maxSales * 140 * getResources().getDisplayMetrics().density);
            } else {
                params.height = 0;
            }
            viewBar.setLayoutParams(params);
            
            binding.llMostSoldSizes.addView(column);
        }
    }

    private static class ProductSalesInfo {
        String name;
        double price;
        int soldCount = 0;

        ProductSalesInfo(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }

    private void showLogoutDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView title = dialog.findViewById(R.id.dialog_title);
        TextView subtitle = dialog.findViewById(R.id.dialog_content);

        MaterialButton btnDelete = dialog.findViewById(R.id.btnDelete);
        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);

        title.setText("Logout");
        subtitle.setText("Do you really want to exit your account?");
        btnDelete.setText("Logout");

        btnDelete.setOnClickListener(v -> {
            performLogout();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void performLogout() {
        AuthRepositoryImpl repo = new AuthRepositoryImpl(requireContext());
        repo.logout();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}