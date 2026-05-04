package com.example.allgoods.Data.repository.SellerProduct;

import android.net.Uri;

import com.example.allgoods.model.ProductModel;

import java.util.List;
import java.util.Map;

public interface ProductRepository {
    void uploadProduct(ProductModel product, List<Uri> imageUris, OnProductUploadListener listener);
    void getAllProducts(OnProductsFetchListener listener);
    void getProductsByCategory(String category, OnProductsFetchListener listener);
    void getProductsBySeller(String sellerId, OnProductsFetchListener listener);
    void searchProducts(String query, OnProductsFetchListener listener);
    void updateProductQuantity(String productId, Map<String, Integer> newSizesQuantity, OnProductUploadListener listener);
    void deleteProduct(String productId, OnProductUploadListener listener);

    interface OnProductUploadListener {
        void onSuccess();
        void onFailure(String error);
    }

    interface OnProductsFetchListener {
        void onSuccess(List<ProductModel> products);
        void onFailure(String error);
    }
}