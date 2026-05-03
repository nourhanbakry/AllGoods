package com.example.allgoods.UI.Customer.Wishlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.allgoods.model.ProductModel;
import com.example.allgoods.utils.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.allgoods.Data.repository.Wishlist.WishlistRepository;
import com.example.allgoods.Data.repository.Wishlist.WishlistRepositoryImpl;

public class WishlistViewModel extends ViewModel {
    private final MutableLiveData<List<ProductModel>> products = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final WishlistRepository wishlistRepository = new WishlistRepositoryImpl();

    public LiveData<List<ProductModel>> getProducts() {
        return products;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadProducts() {
        isLoading.setValue(true);
        wishlistRepository.getWishlist(new WishlistRepository.OnWishlistFetchListener() {
            @Override
            public void onSuccess(List<ProductModel> productList) {
                products.setValue(productList);
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                // Handle error
            }
        });
    }
}
