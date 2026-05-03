package com.example.allgoods.Data.repository.SellerProduct;

import android.net.Uri;

import com.example.allgoods.model.ProductModel;

import java.util.List;

public interface ProductRepository {
    void uploadProduct(ProductModel product, List<Uri> imageUris, OnProductUploadListener listener);

    interface OnProductUploadListener {
        void onSuccess();
        void onFailure(String error);
    }
}