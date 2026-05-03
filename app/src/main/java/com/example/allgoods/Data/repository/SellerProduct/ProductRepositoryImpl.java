package com.example.allgoods.Data.repository.SellerProduct;

import android.net.Uri;
import com.example.allgoods.model.ProductModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void getAllProducts(OnProductsFetchListener listener) {
        firestore.collection("Products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ProductModel> products = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ProductModel product = document.toObject(ProductModel.class);
                        product.setId(document.getId()); // Use Firestore Document ID
                        products.add(product);
                    }
                    listener.onSuccess(products);
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e.getMessage());
                });
    }

    @Override
    public void getProductsByCategory(String category, OnProductsFetchListener listener) {
        firestore.collection("Products")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ProductModel> products = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ProductModel product = document.toObject(ProductModel.class);
                        product.setId(document.getId());
                        products.add(product);
                    }
                    listener.onSuccess(products);
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e.getMessage());
                });
    }

    @Override
    public void searchProducts(String query, OnProductsFetchListener listener) {
        firestore.collection("Products")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ProductModel> products = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ProductModel product = document.toObject(ProductModel.class);
                        product.setId(document.getId());
                        products.add(product);
                    }
                    listener.onSuccess(products);
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e.getMessage());
                });
    }
}