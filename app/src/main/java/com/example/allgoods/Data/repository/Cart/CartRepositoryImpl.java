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

        String cartItemId = product.getId() + "_" + product.getSelectedSize();

        firestore.collection("Cart")
                .document(userId)
                .collection("Products")
                .document(cartItemId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Product with same size already in cart, increment quantity
                        long currentQty = documentSnapshot.getLong("quantity") != null ? documentSnapshot.getLong("quantity") : 1;
                        documentSnapshot.getReference().update("quantity", currentQty + product.getQuantity())
                                .addOnSuccessListener(aVoid -> listener.onSuccess())
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                    } else {
                        // New item or new size, add to cart
                        firestore.collection("Cart")
                                .document(userId)
                                .collection("Products")
                                .document(cartItemId)
                                .set(product)
                                .addOnSuccessListener(aVoid -> listener.onSuccess())
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void removeFromCart(String cartItemId, OnCartChangeListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        firestore.collection("Cart")
                .document(userId)
                .collection("Products")
                .document(cartItemId)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void updateQuantity(String cartItemId, int quantity, OnCartChangeListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        firestore.collection("Cart")
                .document(userId)
                .collection("Products")
                .document(cartItemId)
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
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ProductModel product = doc.toObject(ProductModel.class);
                        product.setId(doc.getId());
                        products.add(product);
                    }
                    listener.onSuccess(products);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void clearCart(OnCartChangeListener listener) {
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
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().delete();
                    }
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
}
