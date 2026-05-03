package com.example.allgoods.UI.Customer.Categories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.allgoods.model.ProductModel;
import com.example.allgoods.utils.Category;

import java.util.ArrayList;
import java.util.List;

import com.example.allgoods.Data.repository.SellerProduct.ProductRepository;
import com.example.allgoods.Data.repository.SellerProduct.ProductRepositoryImpl;

public class CategoriesViewModel extends ViewModel {

    private final MutableLiveData<List<ProductModel>> products = new MutableLiveData<>();
    private final ProductRepository productRepository = new ProductRepositoryImpl();

    public LiveData<List<ProductModel>> getProducts() {
        return products;
    }

    public void loadProductsByCategory(String category) {
        productRepository.getProductsByCategory(category, new ProductRepository.OnProductsFetchListener() {
            @Override
            public void onSuccess(List<ProductModel> productList) {
                products.setValue(productList);
            }

            @Override
            public void onFailure(String error) {
                // Handle error
            }
        });
    }
}
