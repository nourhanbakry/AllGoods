package com.example.allgoods.UI.Seller.Reviews;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allgoods.Data.repository.Review.ReviewRepository;
import com.example.allgoods.Data.repository.Review.ReviewRepositoryImpl;
import com.example.allgoods.R;
import com.example.allgoods.databinding.FragmentReviewsBinding;
import com.example.allgoods.model.ProductModel;
import com.example.allgoods.model.ReviewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Map;

public class ReviewsFragment extends Fragment {


    private FragmentReviewsBinding reviewsBinding;
    private final ReviewRepository reviewRepository = new ReviewRepositoryImpl();
    private SellerReviewAdapter adapter;


    public static ReviewsFragment newInstance(String param1, String param2) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reviewsBinding = FragmentReviewsBinding.inflate(inflater, container, false);
        return  reviewsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        fetchSellerReviews();
    }

    private void setupRecyclerView() {
        adapter = new SellerReviewAdapter();
        reviewsBinding.rvReviews.setLayoutManager(new LinearLayoutManager(requireContext()));
        reviewsBinding.rvReviews.setAdapter(adapter);
    }

    private void fetchSellerReviews() {
        String sellerId = FirebaseAuth.getInstance().getUid();
        if (sellerId == null) return;

        reviewsBinding.progressBarLoading.setVisibility(VISIBLE);
        reviewsBinding.tvCustomerReviews.setVisibility(GONE);
        reviewsBinding.tvProductsCount.setVisibility(GONE);
        reviewsBinding.cvRatingSummary.setVisibility(GONE);
        reviewsBinding.rvReviews.setVisibility(GONE);
        reviewsBinding.tvNoReviews.setVisibility(GONE);

        reviewRepository.getReviewsForSeller(sellerId, new ReviewRepository.OnSellerReviewsFetchListener() {
            @Override
            public void onSuccess(Map<ProductModel, List<ReviewModel>> productReviews) {
                if (reviewsBinding == null) return;

                reviewsBinding.progressBarLoading.setVisibility(GONE);

                if (productReviews.isEmpty()) {
                    reviewsBinding.rvReviews.setVisibility(GONE);
                    reviewsBinding.cvRatingSummary.setVisibility(VISIBLE);
                    reviewsBinding.tvNoReviews.setVisibility(VISIBLE);
                    updateSummary(0, 0, new int[5]);
                } else {
                    reviewsBinding.tvCustomerReviews.setVisibility(VISIBLE);
                    reviewsBinding.tvProductsCount.setVisibility(VISIBLE);
                    reviewsBinding.rvReviews.setVisibility(VISIBLE);
                    reviewsBinding.cvRatingSummary.setVisibility(VISIBLE);
                    reviewsBinding.tvNoReviews.setVisibility(GONE);
                    adapter.setData(productReviews);

                    calculateAndDisplaySummary(productReviews);
                }
            }

            @Override
            public void onFailure(String error) {
                if (reviewsBinding != null) {
                    reviewsBinding.progressBarLoading.setVisibility(GONE);
                    reviewsBinding.tvNoReviews.setText("Error: " + error);
                    reviewsBinding.tvNoReviews.setVisibility(VISIBLE);
                }
            }
        });
    }

    private void calculateAndDisplaySummary(Map<ProductModel, List<ReviewModel>> productReviews) {
        int totalReviews = 0;
        float totalRatingSum = 0;
        int[] ratingCounts = new int[5];

        for (List<ReviewModel> reviews : productReviews.values()) {
            for (ReviewModel review : reviews) {
                totalReviews++;
                totalRatingSum += review.getRating();
                int rating = Math.round(review.getRating());
                if (rating >= 1 && rating <= 5) {
                    ratingCounts[rating - 1]++;
                }
            }
        }

        float averageRating = totalReviews > 0 ? totalRatingSum / totalReviews : 0;
        updateSummary(totalReviews, averageRating, ratingCounts);
    }

    private void updateSummary(int totalReviews, float averageRating, int[] ratingCounts) {
        reviewsBinding.tvProductsCount.setText(totalReviews + " reviews");
        reviewsBinding.tvTotalReviewsDetail.setText(totalReviews + " reviews");
        reviewsBinding.tvRatingValue.setText(String.format("%.1f", averageRating));
        reviewsBinding.rbAverage.setRating(averageRating);

        // Update progress bars
        updateRatingRow(reviewsBinding.rating5, 5, ratingCounts[4], totalReviews);
        updateRatingRow(reviewsBinding.rating4, 4, ratingCounts[3], totalReviews);
        updateRatingRow(reviewsBinding.rating3, 3, ratingCounts[2], totalReviews);
        updateRatingRow(reviewsBinding.rating2, 2, ratingCounts[1], totalReviews);
        updateRatingRow(reviewsBinding.rating1, 1, ratingCounts[0], totalReviews);
    }

    private void updateRatingRow(com.example.allgoods.databinding.ItemRatingProgressBinding rowBinding, int label, int count, int total) {
        rowBinding.tvRatingLabel.setText(String.valueOf(label));
        rowBinding.tvRatingCount.setText(String.valueOf(count));
        if (total > 0) {
            int progress = (count * 100) / total;
            rowBinding.progressBar.setProgress(progress);
        } else {
            rowBinding.progressBar.setProgress(0);
        }
    }
}