package com.example.allgoods.Data.repository.SellerProduct;

import android.net.Uri;
import com.example.allgoods.model.ProductModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProductRepositoryImpl implements ProductRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    public void uploadProduct(ProductModel product, Uri imageUri, OnProductUploadListener listener) {
        // select place to upload in
        StorageReference storageRef = storage.getReference().child("products/" + System.currentTimeMillis() + ".jpg");

        // start upload
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        product.setImage(uri.toString());

                        // store in firestore
                        firestore.collection("Products")
                                .add(product)
                                .addOnSuccessListener(documentReference -> listener.onSuccess())
                                .addOnFailureListener(e -> listener.onFailure("Firestore: " + e.getMessage()));
                    });
                })
                .addOnFailureListener(e -> {
                    listener.onFailure("Storage: " + e.getMessage());
                });
    }
}