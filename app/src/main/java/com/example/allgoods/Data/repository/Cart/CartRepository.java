package com.example.allgoods.Data.repository.Cart;

import com.example.allgoods.model.ProductModel;
import java.util.List;

public interface CartRepository {
    void addToCart(ProductModel product, OnCartChangeListener listener);
    void removeFromCart(String cartItemId, OnCartChangeListener listener);
    void updateQuantity(String cartItemId, int quantity, OnCartChangeListener listener);
    void getCart(OnCartFetchListener listener);
    void clearCart(OnCartChangeListener listener);

    interface OnCartChangeListener {
        void onSuccess();
        void onFailure(String error);
    }

    interface OnCartFetchListener {
        void onSuccess(List<ProductModel> products);
        void onFailure(String error);
    }
}
