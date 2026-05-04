package com.example.allgoods.model;

import java.io.Serializable;
import java.util.List;

public class OrderModel implements Serializable {
    private String orderId;
    private String customerId;
    private String customerName;
    private String phoneNumber;
    private AddressModel address;
    private CardModel paymentMethod;
    private List<ProductModel> items;
    private double subtotal;
    private double shippingCost;
    private double total;
    private String status; // e.g., "pending", "confirmed", "shipped", "delivered"
    private long timestamp;
    private long deliveredTimestamp;

    public OrderModel() {}

    public OrderModel(String orderId, String customerId, String customerName, String phoneNumber, 
                      AddressModel address, CardModel paymentMethod, List<ProductModel> items, 
                      double subtotal, double shippingCost, double total, String status, long timestamp) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.items = items;
        this.subtotal = subtotal;
        this.shippingCost = shippingCost;
        this.total = total;
        this.status = status;
        this.timestamp = timestamp;
    }

    public OrderModel(String orderId, String customerId, String customerName, String phoneNumber, 
                      AddressModel address, CardModel paymentMethod, List<ProductModel> items, 
                      double subtotal, double shippingCost, double total, String status, long timestamp, long deliveredTimestamp) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.items = items;
        this.subtotal = subtotal;
        this.shippingCost = shippingCost;
        this.total = total;
        this.status = status;
        this.timestamp = timestamp;
        this.deliveredTimestamp = deliveredTimestamp;
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public AddressModel getAddress() { return address; }
    public void setAddress(AddressModel address) { this.address = address; }

    public CardModel getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(CardModel paymentMethod) { this.paymentMethod = paymentMethod; }

    public List<ProductModel> getItems() { return items; }
    public void setItems(List<ProductModel> items) { this.items = items; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getShippingCost() { return shippingCost; }
    public void setShippingCost(double shippingCost) { this.shippingCost = shippingCost; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public long getDeliveredTimestamp() { return deliveredTimestamp; }
    public void setDeliveredTimestamp(long deliveredTimestamp) { this.deliveredTimestamp = deliveredTimestamp; }
}
