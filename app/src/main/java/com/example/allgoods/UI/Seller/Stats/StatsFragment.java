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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
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
                                android.util.Log.d("StatsDebug", "Processing Item: " + item.getName() + " | ID: " + item.getId() + " | Qty: " + item.getQuantity());
                                if (sellerId.equals(item.getSellerId())) {
                                    double itemTotal = item.getPrice() * item.getQuantity();
                                    totalRevenue += itemTotal;
                                    
                                    // Top Selling logic
                                    String productId = (item.getId() != null && !item.getId().isEmpty()) ? item.getId() : item.getName();
                                    ProductSalesInfo info = productSales.getOrDefault(productId, new ProductSalesInfo(item.getName(), item.getPrice()));
                                    info.soldCount += item.getQuantity();
                                    productSales.put(productId, info);

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
        if (binding == null) return;
        android.util.Log.d("StatsDebug", "ProductSales map size: " + productSales.size());
        
        List<ProductSalesInfo> sortedSales = new ArrayList<>(productSales.values());
        Collections.sort(sortedSales, (o1, o2) -> Integer.compare(o2.soldCount, o1.soldCount));
        
        android.util.Log.d("StatsDebug", "SortedSales list size: " + sortedSales.size());
        for (ProductSalesInfo info : sortedSales) {
            android.util.Log.d("StatsDebug", "Product: " + info.name + ", Sold: " + info.soldCount);
        }

        binding.rvTopSelling.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        binding.rvTopSelling.setAdapter(new TopSellingAdapter(sortedSales));
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

        int maxSales = Collections.max(sizeSales.values());

// Set Y Axis values

       /* int maxRounded = ((maxSales + 9) / 10) * 10;

        binding.yMax.setText(String.valueOf(maxRounded));

        binding.yMid.setText(String.valueOf(maxRounded / 2));

        binding.yMin.setText("0");*/
        int maxValue = 60;

        binding.yMax.setText("60");
        binding.yMidTop.setText("45");
        binding.yMid.setText("30");
        binding.yMidBottom.setText("15");
        binding.yMin.setText("0");


        List<String> sizesOrder = Arrays.asList("XS", "S", "M", "L", "XL", "XXL");

        List<Map.Entry<String, Integer>> list = new ArrayList<>();

        for (String size : sizesOrder) {
            int value = sizeSales.getOrDefault(size, 0);
            list.add(new AbstractMap.SimpleEntry<>(size, value));
        }

        float maxHeightDp = 120f;
        float density = getResources().getDisplayMetrics().density;

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (Map.Entry<String, Integer> entry : list) {

            View column = inflater.inflate(R.layout.item_column_size_stat, binding.llMostSoldSizes, false);

            TextView tvLabel = column.findViewById(R.id.tvSizeLabel);
            TextView tvCount = column.findViewById(R.id.tvSizeCount);
            View viewBar = column.findViewById(R.id.viewBar);

            tvLabel.setText(entry.getKey());
            if (tvCount != null) {

                tvCount.setVisibility(View.GONE);

            }
            //int height = (int) ((entry.getValue() * maxHeightDp / maxSales) * density);
            /*float maxHeightPx = 140 * getResources().getDisplayMetrics().density;

            int height = (int) ((entry.getValue() * maxHeightPx) / maxSales);*/
            float maxHeightPx = 140 * getResources().getDisplayMetrics().density;

            int height = (int) ((entry.getValue() * maxHeightPx) / maxValue);

            ViewGroup.LayoutParams params = viewBar.getLayoutParams();
            params.height = height;
            viewBar.setLayoutParams(params);

            binding.llMostSoldSizes.addView(column);
        }
    }

    public static class ProductSalesInfo {
        public String name;
        public double price;
        public int soldCount = 0;

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