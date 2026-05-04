package com.example.allgoods.Data.repository.Order;

import com.example.allgoods.model.OrderModel;
import java.util.List;

public interface OrderRepository {
    void placeOrder(OrderModel order, OnOrderChangeListener listener);
    void getOrders(OnOrdersFetchListener listener);
    void getSellerOrders(String sellerId, OnOrdersFetchListener listener);
    void updateOrderStatus(String orderId, String status, long deliveredTimestamp, OnOrderChangeListener listener);

    interface OnOrderChangeListener {
        void onSuccess();
        void onFailure(String error);
    }

    interface OnOrdersFetchListener {
        void onSuccess(List<OrderModel> orders);
        void onFailure(String error);
    }
}
