package com.example.allgoods.UI.Customer.Cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.allgoods.model.ProductModel;
import com.example.allgoods.utils.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.allgoods.Data.repository.Cart.CartRepository;
import com.example.allgoods.Data.repository.Cart.CartRepositoryImpl;
import com.example.allgoods.Data.repository.User.UserRepository;
import com.example.allgoods.Data.repository.User.UserRepositoryImpl;
import com.example.allgoods.model.AddressModel;

public class CartViewModel extends ViewModel {

    private final MutableLiveData<List<ProductModel>> cartItems = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<AddressModel> primaryAddress = new MutableLiveData<>();
    private final MutableLiveData<AddressModel> selectedAddress = new MutableLiveData<>();
    private final CartRepository cartRepository = new CartRepositoryImpl();
    private final UserRepository userRepository = new UserRepositoryImpl();

    public LiveData<List<ProductModel>> getCartItems() {
        return cartItems;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<AddressModel> getPrimaryAddress() {
        return primaryAddress;
    }

    public LiveData<AddressModel> getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(AddressModel address) {
        selectedAddress.setValue(address);
    }

    public void loadCartProducts() {
        isLoading.setValue(true);
        cartRepository.getCart(new CartRepository.OnCartFetchListener() {
            @Override
            public void onSuccess(List<ProductModel> productList) {
                cartItems.setValue(productList);
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                // Handle error
            }
        });
    }

    public void loadPrimaryAddress() {
        userRepository.getPrimaryAddress(new UserRepository.OnAddressFetchListener() {
            @Override
            public void onSuccess(AddressModel address) {
                primaryAddress.setValue(address);
            }

            @Override
            public void onFailure(String error) {
                // Handle error
            }
        });
    }

    public void removeFromCart(String productId) {
        cartRepository.removeFromCart(productId, new CartRepository.OnCartChangeListener() {
            @Override
            public void onSuccess() {
                loadCartProducts();
            }

            @Override
            public void onFailure(String error) {
                // Handle error
            }
        });
    }

    public void updateQuantity(String productId, int quantity) {
        cartRepository.updateQuantity(productId, quantity, new CartRepository.OnCartChangeListener() {
            @Override
            public void onSuccess() {
                loadCartProducts();
            }

            @Override
            public void onFailure(String error) {
                // Handle error
            }
        });
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