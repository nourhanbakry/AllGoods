package com.example.allgoods.Data.repository.Review;

import com.example.allgoods.model.ReviewModel;
import com.example.allgoods.model.ProductModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Transaction;
import java.util.ArrayList;
import java.util.List;

public class ReviewRepositoryImpl implements ReviewRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public void addReview(String productId, ReviewModel review, OnReviewAddedListener listener) {
        // Add review to sub-collection
        firestore.collection("Products").document(productId)
                .collection("Reviews")
                .add(review)
                .addOnSuccessListener(documentReference -> {
                    // Update product rating and review count
                    updateProductRating(productId, review.getRating(), listener);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    private void updateProductRating(String productId, float newRating, OnReviewAddedListener listener) {
        DocumentReference productRef = firestore.collection("Products").document(productId);

        firestore.runTransaction((Transaction.Function<Void>) transaction -> {
            ProductModel product = transaction.get(productRef).toObject(ProductModel.class);
            if (product != null) {
                int oldReviewCount = product.getReviewCount();
                float oldRating = product.getRating();
                
                int newReviewCount = oldReviewCount + 1;
                float updatedRating = ((oldRating * oldReviewCount) + newRating) / newReviewCount;
                
                transaction.update(productRef, "reviewCount", newReviewCount);
                transaction.update(productRef, "rating", updatedRating);
            }
            return null;
        }).addOnSuccessListener(aVoid -> listener.onSuccess())
          .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void getReviewsForProduct(String productId, OnReviewsFetchListener listener) {
        firestore.collection("Products").document(productId)
                .collection("Reviews")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ReviewModel> reviews = queryDocumentSnapshots.toObjects(ReviewModel.class);
                    listener.onSuccess(reviews);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void getReviewsForSeller(String sellerId, OnSellerReviewsFetchListener listener) {
        firestore.collection("Products")
                .whereEqualTo("sellerId", sellerId)
                .get()
                .addOnSuccessListener(productSnapshots -> {
                    List<ProductModel> products = new ArrayList<>();
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : productSnapshots) {
                        ProductModel product = doc.toObject(ProductModel.class);
                        product.setId(doc.getId());
                        products.add(product);
                    }

                    if (products.isEmpty()) {
                        listener.onSuccess(new java.util.HashMap<>());
                        return;
                    }

                    java.util.Map<ProductModel, List<ReviewModel>> productReviewsMap = new java.util.HashMap<>();
                    java.util.concurrent.atomic.AtomicInteger counter = new java.util.concurrent.atomic.AtomicInteger(0);
                    int totalProducts = products.size();

                    for (ProductModel product : products) {
                        firestore.collection("Products").document(product.getId())
                                .collection("Reviews")
                                .get()
                                .addOnSuccessListener(reviewSnapshots -> {
                                    List<ReviewModel> reviews = reviewSnapshots.toObjects(ReviewModel.class);
                                    productReviewsMap.put(product, reviews);
                                    if (counter.incrementAndGet() == totalProducts) {
                                        listener.onSuccess(productReviewsMap);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Even if one fails, we continue? Or fail all?
                                    // For now, let's just count it as finished but maybe with empty list
                                    productReviewsMap.put(product, new ArrayList<>());
                                    if (counter.incrementAndGet() == totalProducts) {
                                        listener.onSuccess(productReviewsMap);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
}
