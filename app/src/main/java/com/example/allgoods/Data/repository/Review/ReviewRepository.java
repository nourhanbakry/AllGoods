package com.example.allgoods.Data.repository.Review;

import com.example.allgoods.model.ReviewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public interface ReviewRepository {
    void addReview(String productId, ReviewModel review, OnReviewAddedListener listener);
    void getReviewsForProduct(String productId, OnReviewsFetchListener listener);
    void getReviewsForSeller(String sellerId, OnSellerReviewsFetchListener listener);

    interface OnReviewAddedListener {
        void onSuccess();
        void onFailure(String error);
    }

    interface OnReviewsFetchListener {
        void onSuccess(List<ReviewModel> reviews);
        void onFailure(String error);
    }

    interface OnSellerReviewsFetchListener {
        void onSuccess(java.util.Map<com.example.allgoods.model.ProductModel, List<ReviewModel>> productReviews);
        void onFailure(String error);
    }
}
