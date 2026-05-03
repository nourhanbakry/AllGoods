package com.example.allgoods.UI.Customer.Home;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.allgoods.Data.repository.SellerProduct.ProductRepository;
import com.example.allgoods.Data.repository.SellerProduct.ProductRepositoryImpl;
import com.example.allgoods.model.ProductModel;
import com.example.allgoods.utils.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<ProductModel>> products = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final List<ProductModel> allProductsList = new ArrayList<>(); // Store original list
    private final ProductRepository productRepository = new ProductRepositoryImpl();

    public LiveData<List<ProductModel>> getProducts() {
        return products;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadProducts() {
        isLoading.setValue(true);
        productRepository.getAllProducts(new ProductRepository.OnProductsFetchListener() {
            @Override
            public void onSuccess(List<ProductModel> productList) {
                allProductsList.clear();
                allProductsList.addAll(productList);
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

    public void searchProducts(String query) {
        if (query.isEmpty()) {
            products.setValue(new ArrayList<>(allProductsList));
            return;
        }
        
        String lowerCaseQuery = query.toLowerCase().trim();
        List<ProductModel> filteredList = new ArrayList<>();
        
        for (ProductModel product : allProductsList) {
            if (product.getName().toLowerCase().contains(lowerCaseQuery)) {
                filteredList.add(product);
            }
        }
        products.setValue(filteredList);
    }
}