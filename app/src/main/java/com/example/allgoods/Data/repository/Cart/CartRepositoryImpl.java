package com.example.allgoods.Data.repository.Cart;

import com.example.allgoods.model.ProductModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartRepositoryImpl implements CartRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private String getUserId() {
        return auth.getUid();
    }

    @Override
    public void addToCart(ProductModel product, OnCartChangeListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        firestore.collection("Cart")
                .document(userId)
                .collection("Products")
                .document(product.getId())
                .set(product)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void removeFromCart(String productId, OnCartChangeListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        firestore.collection("Cart")
                .document(userId)
                .collection("Products")
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void updateQuantity(String productId, int quantity, OnCartChangeListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        firestore.collection("Cart")
                .document(userId)
                .collection("Products")
                .document(productId)
                .update("quantity", quantity)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void getCart(OnCartFetchListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        firestore.collection("Cart")
                .document(userId)
                .collection("Products")
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
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
}
