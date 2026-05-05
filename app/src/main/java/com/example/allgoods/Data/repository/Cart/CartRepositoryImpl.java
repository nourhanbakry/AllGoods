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

        String productId = product.getId();
        String selectedSize = product.getSelectedSize();
        String cartItemId = productId + "_" + selectedSize;

        // 1. Fetch the latest product data to check stock
        firestore.collection("Products").document(productId).get()
                .addOnSuccessListener(productDoc -> {
                    if (!productDoc.exists()) {
                        listener.onFailure("Product no longer exists");
                        return;
                    }

                    ProductModel latestProduct = productDoc.toObject(ProductModel.class);
                    if (latestProduct == null || latestProduct.getSizesQuantity() == null) {
                        listener.onFailure("Stock information unavailable");
                        return;
                    }

                    Integer availableStock = latestProduct.getSizesQuantity().get(selectedSize);
                    if (availableStock == null || availableStock <= 0) {
                        listener.onFailure("Sorry, this size is out of stock");
                        return;
                    }

                    // 2. Check how many are already in the cart
                    firestore.collection("Cart")
                            .document(userId)
                            .collection("Products")
                            .document(cartItemId)
                            .get()
                            .addOnSuccessListener(cartDoc -> {
                                long currentCartQty = 0;
                                if (cartDoc.exists()) {
                                    currentCartQty = cartDoc.getLong("quantity") != null ? cartDoc.getLong("quantity") : 0;
                                }

                                long totalRequestedQty = currentCartQty + product.getQuantity();

                                if (totalRequestedQty > availableStock) {
                                    listener.onFailure("Cannot add more. Only " + availableStock + " items available in stock for size " + selectedSize);
                                    return;
                                }

                                // 3. Proceed to add or update cart
                                if (cartDoc.exists()) {
                                    cartDoc.getReference().update("quantity", totalRequestedQty)
                                            .addOnSuccessListener(aVoid -> listener.onSuccess())
                                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                                } else {
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
                .get()
                .addOnSuccessListener(cartDoc -> {
                    if (!cartDoc.exists()) {
                        listener.onFailure("Item not found in cart");
                        return;
                    }

                    ProductModel itemInCart = cartDoc.toObject(ProductModel.class);
                    // Try to get original productId from field 'id', otherwise extract from cartItemId (format: productId_size)
                    String productId = cartDoc.getString("id"); 
                    if (productId == null || productId.isEmpty()) {
                        if (cartItemId.contains("_")) {
                            productId = cartItemId.substring(0, cartItemId.lastIndexOf("_"));
                        }
                    }
                    
                    String selectedSize = itemInCart != null ? itemInCart.getSelectedSize() : null;

                    if (productId == null || selectedSize == null || selectedSize.isEmpty()) {
                        android.util.Log.e("CartRepo", "Missing info: productId=" + productId + ", size=" + selectedSize);
                        listener.onFailure("Product information missing");
                        return;
                    }

                    // Fetch latest stock
                    firestore.collection("Products").document(productId).get()
                            .addOnSuccessListener(productDoc -> {
                                if (!productDoc.exists()) {
                                    listener.onFailure("Product no longer exists");
                                    return;
                                }

                                ProductModel latestProduct = productDoc.toObject(ProductModel.class);
                                if (latestProduct == null || latestProduct.getSizesQuantity() == null) {
                                    listener.onFailure("Stock information unavailable");
                                    return;
                                }

                                Integer availableStock = latestProduct.getSizesQuantity().get(selectedSize);
                                if (availableStock != null && quantity > availableStock) {
                                    listener.onFailure("Cannot add more. Only " + availableStock + " items available in stock");
                                } else {
                                    cartDoc.getReference().update("quantity", quantity)
                                            .addOnSuccessListener(aVoid -> listener.onSuccess())
                                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                                }
                            })
                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                })
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
