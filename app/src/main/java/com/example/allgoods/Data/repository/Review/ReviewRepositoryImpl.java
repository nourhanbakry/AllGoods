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
}
