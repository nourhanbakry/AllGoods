package com.example.allgoods.Data.repository.SellerProduct;

import android.net.Uri;
import com.example.allgoods.model.ProductModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    public void uploadProduct(ProductModel product, List<Uri> imageUris, OnProductUploadListener listener) {
        List<String> uploadedUrls = new ArrayList<>();
        AtomicInteger uploadCounter = new AtomicInteger(0);

        for (Uri uri : imageUris) {
            StorageReference storageRef = storage.getReference()
                    .child("products/" + UUID.randomUUID().toString() + ".jpg");

            storageRef.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            uploadedUrls.add(downloadUrl.toString());

                            if (uploadCounter.incrementAndGet() == imageUris.size()) {
                                product.setImages(uploadedUrls);
                                saveToFirestore(product, listener);
                            }
                        });
                    })
                    .addOnFailureListener(e -> listener.onFailure("Storage: " + e.getMessage()));
        }
    }

    private void saveToFirestore(ProductModel product, OnProductUploadListener listener) {
        firestore.collection("Products")
                .add(product)
                .addOnSuccessListener(documentReference -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure("Firestore: " + e.getMessage()));
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