package com.example.allgoods.Data.repository.Wishlist;

import com.example.allgoods.model.ProductModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WishlistRepositoryImpl implements WishlistRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private String getUserId() {
        return auth.getUid();
    }

    @Override
    public void addToWishlist(ProductModel product, OnWishlistChangeListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        product.setFav(true);
        firestore.collection("Wishlist")
                .document(userId)
                .collection("Products")
                .document(product.getId())
                .set(product)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void removeFromWishlist(String productId, OnWishlistChangeListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        firestore.collection("Wishlist")
                .document(userId)
                .collection("Products")
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void getWishlist(OnWishlistFetchListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        // 1. Get all product IDs from the user's Wishlist collection
        firestore.collection("Wishlist")
                .document(userId)
                .collection("Products")
                .get()
                .addOnSuccessListener(wishlistSnapshot -> {
                    List<String> productIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : wishlistSnapshot) {
                        productIds.add(doc.getId());
                    }

                    if (productIds.isEmpty()) {
                        listener.onSuccess(new ArrayList<>());
                        return;
                    }

                    // 2. Fetch the actual product details from the main 'Products' collection
                    // Note: Firestore 'whereIn' supports up to 30 IDs per query. 
                    // For a real app with large wishlists, you might need to chunk this or fetch individually.
                    firestore.collection("Products")
                            .whereIn(com.google.firebase.firestore.FieldPath.documentId(), productIds)
                            .get()
                            .addOnSuccessListener(productsSnapshot -> {
                                List<ProductModel> products = new ArrayList<>();
                                for (QueryDocumentSnapshot document : productsSnapshot) {
                                    ProductModel product = document.toObject(ProductModel.class);
                                    product.setId(document.getId());
                                    product.setFav(true); // Since it came from wishlist
                                    products.add(product);
                                }
                                listener.onSuccess(products);
                            })
                            .addOnFailureListener(e -> listener.onFailure("Error fetching latest products: " + e.getMessage()));
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void isFavorite(String productId, OnFavoriteCheckListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onResult(false);
            return;
        }

        firestore.collection("Wishlist")
                .document(userId)
                .collection("Products")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    listener.onResult(documentSnapshot.exists());
                })
                .addOnFailureListener(e -> listener.onResult(false));
    }
}
