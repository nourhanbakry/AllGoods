package com.example.allgoods.UI.Seller.Orders;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allgoods.R;
import com.example.allgoods.model.OrderModel;
import com.example.allgoods.model.ProductModel;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SellerOrdersAdapter extends RecyclerView.Adapter<SellerOrdersAdapter.OrderViewHolder> {

    private List<OrderModel> orders;
    private OnOrderActionListener listener;

    public SellerOrdersAdapter(List<OrderModel> orders, OnOrderActionListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView userName, orderId, address, phone, paymentMethod, totalValue, deliveredTime;
        MaterialButton btnPending, btnDelivered, btnMarkDelivered;
        LinearLayout itemsContainer;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.UserNameOrder);
            orderId = itemView.findViewById(R.id.OrderId);
            address = itemView.findViewById(R.id.txtAddressValue);
            phone = itemView.findViewById(R.id.txtPhoneValue);
            paymentMethod = itemView.findViewById(R.id.txtCredit);
            totalValue = itemView.findViewById(R.id.total_price_value);
            deliveredTime = itemView.findViewById(R.id.DeliveredTime);
            btnPending = itemView.findViewById(R.id.btnPending);
            btnDelivered = itemView.findViewById(R.id.btnDelivered);
            btnMarkDelivered = itemView.findViewById(R.id.btnMarkDelivered);
            itemsContainer = itemView.findViewById(R.id.llItemsContainer);
        }

        @SuppressLint("SetTextI18n")
        void bind(OrderModel order, OnOrderActionListener listener) {
            userName.setText(order.getCustomerName());
            orderId.setText("Order ID: #" + order.getOrderId());
            address.setText(order.getAddress().getAddress() + ", " + order.getAddress().getCity());
            phone.setText(order.getPhoneNumber());
            
            if (order.getPaymentMethod() != null && order.getPaymentMethod().number != null) {
                String cardNumber = order.getPaymentMethod().number;
                paymentMethod.setText("Credit Card (**** " + cardNumber.substring(Math.max(0, cardNumber.length() - 4)) + ")");
            } else {
                paymentMethod.setText("Payment method not available");
            }
            
            totalValue.setText("$ " + String.format(Locale.US, "%.2f", order.getTotal()));

            // Dynamic items inflation
            itemsContainer.removeAllViews();
            List<ProductModel> items = order.getItems();
            if (items != null) {
                LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
                for (ProductModel item : items) {
                    View row = inflater.inflate(R.layout.item_order_product_row, itemsContainer, false);
                    TextView tvName = row.findViewById(R.id.tvItemName);
                    TextView tvPrice = row.findViewById(R.id.tvItemPrice);
                    
                    tvName.setText(item.getName() + " (" + item.getSelectedSize() + ") x " + item.getQuantity());
                    tvPrice.setText("$ " + String.format(Locale.US, "%.2f", item.getPrice() * item.getQuantity()));
                    
                    itemsContainer.addView(row);
                }
            }

            // Handle Status
            if ("delivered".equalsIgnoreCase(order.getStatus())) {
                btnPending.setVisibility(View.GONE);
                btnMarkDelivered.setVisibility(View.GONE);
                btnDelivered.setVisibility(View.VISIBLE);
                deliveredTime.setVisibility(View.VISIBLE);
                
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(order.getDeliveredTimestamp()));
                deliveredTime.setText("Delivered on " + date);
            } else {
                btnPending.setVisibility(View.VISIBLE);
                btnMarkDelivered.setVisibility(View.VISIBLE);
                btnDelivered.setVisibility(View.GONE);
                deliveredTime.setVisibility(View.GONE);

                btnMarkDelivered.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onMarkDelivered(order, getAdapterPosition());
                    }
                });
            }
        }
    }

    public interface OnOrderActionListener {
        void onMarkDelivered(OrderModel order, int position);
    }
}