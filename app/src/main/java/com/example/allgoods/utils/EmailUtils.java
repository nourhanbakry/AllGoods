package com.example.allgoods.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.example.allgoods.model.OrderModel;
import com.example.allgoods.model.ProductModel;
import com.google.firebase.auth.FirebaseAuth;

public class EmailUtils {

    public static void sendOrderConfirmationEmail(Context context, OrderModel order) {
        String recipient = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            recipient = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        String subject = "Order Confirmation - #" + order.getOrderId();
        
        StringBuilder body = new StringBuilder();
        body.append("Hello ").append(order.getCustomerName()).append(",\n\n");
        body.append("Your order has been confirmed!\n\n");
        body.append("Order ID: ").append(order.getOrderId()).append("\n");
        body.append("Total Amount: $").append(PriceUtils.formatPrice(order.getTotal())).append("\n\n");
        
        body.append("Items:\n");
        for (ProductModel item : order.getItems()) {
            body.append("- ").append(item.getName())
                .append(" (Size: ").append(item.getSelectedSize()).append(") ")
                .append("x").append(item.getQuantity())
                .append(" : $").append(PriceUtils.formatPrice(item.getPrice() * item.getQuantity()))
                .append("\n");
        }
        
        body.append("\nShipping Address:\n");
        body.append(order.getAddress().getAddress()).append(", ")
            .append(order.getAddress().getCity()).append("\n");
        body.append("Phone: ").append(order.getPhoneNumber()).append("\n\n");
        
        body.append("Thank you for shopping with AllGoods!");

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body.toString());

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
