package com.example.allgoods.UI.Seller.Orders;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.allgoods.Data.repository.Order.OrderRepository;
import com.example.allgoods.Data.repository.Order.OrderRepositoryImpl;
import com.example.allgoods.databinding.FragmentOrdersBinding;
import com.example.allgoods.model.OrderModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private final OrderRepository orderRepository = new OrderRepositoryImpl();
    private SellerOrdersAdapter adapter;
    private List<OrderModel> orderList = new ArrayList<>();

    public static OrdersFragment newInstance(String param1, String param2) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        fetchOrders();
    }

    private void setupRecyclerView() {
        adapter = new SellerOrdersAdapter(orderList, (order, position) -> {
            markOrderAsDelivered(order, position);
        });
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvReviews.setAdapter(adapter);
    }

    private void fetchOrders() {
        String sellerId = FirebaseAuth.getInstance().getUid();
        if (sellerId == null) return;

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.emptyOrdersLayout.setVisibility(View.GONE);

        orderRepository.getSellerOrders(sellerId, new OrderRepository.OnOrdersFetchListener() {
            @Override
            public void onSuccess(List<OrderModel> orders) {
                if (binding == null) return;
                binding.progressBar.setVisibility(View.GONE);
                orderList.clear();
                orderList.addAll(orders);
                adapter.notifyDataSetChanged();
                updateOrderSummary();
            }

            @Override
            public void onFailure(String error) {
                if (binding == null) return;
                binding.progressBar.setVisibility(View.GONE);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                }
                updateOrderSummary();
            }
        });
    }

    private void markOrderAsDelivered(OrderModel order, int position) {
        long timestamp = System.currentTimeMillis();
        orderRepository.updateOrderStatus(order.getOrderId(), "delivered", timestamp, new OrderRepository.OnOrderChangeListener() {
            @Override
            public void onSuccess() {
                if (binding == null) return;
                order.setStatus("delivered");
                order.setDeliveredTimestamp(timestamp);
                adapter.notifyItemChanged(position);
                updateOrderSummary();
                Toast.makeText(requireContext(), "Order marked as delivered", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateOrderSummary() {
        int totalOrders = orderList.size();
        int deliveredCount = 0;
        for (OrderModel o : orderList) {
            if ("delivered".equalsIgnoreCase(o.getStatus())) {
                deliveredCount++;
            }
        }

        binding.tvOrderTotalCount.setText(totalOrders + " Total");
        binding.DeliveredOrders.setText("Delivered Orders: " + deliveredCount);

        if (totalOrders == 0) {
            binding.emptyOrdersLayout.setVisibility(View.VISIBLE);
            binding.rvReviews.setVisibility(View.GONE);
        } else {
            binding.emptyOrdersLayout.setVisibility(View.GONE);
            binding.rvReviews.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}