package com.example.allgoods.Data.repository.Order;

import com.example.allgoods.model.OrderModel;
import com.example.allgoods.model.ProductModel;
import com.example.allgoods.utils.PriceUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepositoryImpl implements OrderRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public void placeOrder(OrderModel order, OnOrderChangeListener listener) {
        String orderId = firestore.collection("Orders").document().getId();
        order.setOrderId(orderId);
        order.setTimestamp(System.currentTimeMillis());

        firestore.collection("Orders")
                .document(orderId)
                .set(order)
                .addOnSuccessListener(aVoid -> {
                    // Also save to user's orders subcollection
                    if (order.getCustomerId() != null) {
                        firestore.collection("Users")
                                .document(order.getCustomerId())
                                .collection("Orders")
                                .document(orderId)
                                .set(order);
                        
                        // Trigger Automatic Email via Firebase "Trigger Email" Extension
                        sendAutomaticEmail(order);
                    }
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    private void sendAutomaticEmail(OrderModel order) {
        String userEmail = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;
        if (userEmail == null) return;

        Map<String, Object> mail = new HashMap<>();
        mail.put("to", userEmail);
        
        Map<String, Object> message = new HashMap<>();
        message.put("subject", "Order Confirmed! - #" + order.getOrderId());
        
        StringBuilder htmlBody = new StringBuilder();
        htmlBody.append("<h1>Thank you for your order!</h1>");
        htmlBody.append("<p>Hello ").append(order.getCustomerName()).append(", your order has been successfully placed.</p>");
        htmlBody.append("<h3>Order Summary:</h3>");
        htmlBody.append("<ul>");
        for (ProductModel item : order.getItems()) {
            htmlBody.append("<li>")
                    .append(item.getName())
                    .append(" (Size: ").append(item.getSelectedSize()).append(") ")
                    .append("x").append(item.getQuantity())
                    .append(" - $").append(PriceUtils.formatPrice(item.getPrice() * item.getQuantity()))
                    .append("</li>");
        }
        htmlBody.append("</ul>");
        htmlBody.append("<p><b>Total Amount: $").append(PriceUtils.formatPrice(order.getTotal())).append("</b></p>");
        htmlBody.append("<hr>");
        htmlBody.append("<p>Shipping to: ").append(order.getAddress().getAddress()).append(", ").append(order.getAddress().getCity()).append("</p>");
        
        message.put("html", htmlBody.toString());
        mail.put("message", message);

        // This document creation triggers the Firebase Extension to send the email
        firestore.collection("mail").add(mail);
    }

    @Override
    public void getOrders(OnOrdersFetchListener listener) {
        String userId = auth.getUid();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        firestore.collection("Users")
                .document(userId)
                .collection("Orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<OrderModel> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        orders.add(doc.toObject(OrderModel.class));
                    }
                    listener.onSuccess(orders);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
}
