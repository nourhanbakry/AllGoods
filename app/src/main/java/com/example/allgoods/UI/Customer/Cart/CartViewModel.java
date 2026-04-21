package com.example.allgoods.UI.Customer.Cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.allgoods.model.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {

    private final MutableLiveData<List<ProductModel>> cartItems = new MutableLiveData<>();
    public LiveData<List<ProductModel>> getCartItems() {
        return cartItems;
    }

    public void loadDummyCartProducts() {
        List<ProductModel> list = new ArrayList<>();

        list.add(new ProductModel(
                1,
                "Nike Sportswear",
                "https://png.pngtree.com/png-vector/20210602/ourmid/pngtree-3d-beauty-cosmetics-product-design-png-image_3350323.jpg",
                45
        ));

        list.add(new ProductModel(
                2,
                "Nike T-shirt",
                "https://png.pngtree.com/png-vector/20210602/ourmid/pngtree-3d-beauty-cosmetics-product-design-png-image_3350323.jpg",
                45
        ));

        list.add(new ProductModel(
                3,
                "Nike T-shirt",
                "https://png.pngtree.com/png-vector/20210602/ourmid/pngtree-3d-beauty-cosmetics-product-design-png-image_3350323.jpg",
                45
        ));

        cartItems.setValue(list);
    }

    public double calculateSubtotal() {
        double subtotal = 0;

        if (cartItems.getValue() == null) return 0;

        for (ProductModel p : cartItems.getValue()) {
            subtotal += p.getPrice() * p.getQuantity();
        }

        return subtotal;
    }

    public double calculateTotal() {
        double subtotal = calculateSubtotal();
        double shipping = 20;
        return subtotal + shipping;
    }
}