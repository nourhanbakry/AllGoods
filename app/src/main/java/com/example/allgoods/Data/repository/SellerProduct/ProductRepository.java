package com.example.allgoods.Data.repository.SellerProduct;

import android.net.Uri;

import com.example.allgoods.model.ProductModel;

public interface ProductRepository {
    void uploadProduct(ProductModel product, Uri imageUri, OnProductUploadListener listener);

    interface OnProductUploadListener {
        void onSuccess();
        void onFailure(String error);
    }
}