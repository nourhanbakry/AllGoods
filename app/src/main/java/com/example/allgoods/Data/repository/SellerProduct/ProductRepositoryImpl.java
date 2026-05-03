package com.example.allgoods.Data.repository.SellerProduct;

import android.net.Uri;
import com.example.allgoods.model.ProductModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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
}