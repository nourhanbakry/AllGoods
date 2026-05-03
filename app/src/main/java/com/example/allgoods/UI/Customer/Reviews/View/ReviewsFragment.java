package com.example.allgoods.UI.Customer.Reviews.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allgoods.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
import com.example.allgoods.Data.repository.Review.ReviewRepository;
import com.example.allgoods.Data.repository.Review.ReviewRepositoryImpl;
import com.example.allgoods.UI.Customer.Reviews.Adapter.ReviewAdapter;
import com.example.allgoods.databinding.FragmentReviews2Binding;
import com.example.allgoods.model.ReviewModel;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ReviewsFragment extends Fragment {

    private static final String ARG_PRODUCT_ID = "product_id";
    private String productId;
    private com.example.allgoods.databinding.FragmentReviews2Binding binding;
    private final ReviewRepository reviewRepository = new ReviewRepositoryImpl();

    public static ReviewsFragment newInstance(String productId, String param2) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString(ARG_PRODUCT_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = com.example.allgoods.databinding.FragmentReviews2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.backButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        binding.btnAddReview.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(android.R.id.content, AddReviewsFragment.newInstance(productId, ""))
                    .addToBackStack(null)
                    .commit();
        });

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        if (productId == null || productId.isEmpty()) return;

        reviewRepository.getReviewsForProduct(productId, new ReviewRepository.OnReviewsFetchListener() {
            @Override
            public void onSuccess(List<ReviewModel> reviews) {
                if (binding == null) return;

                if (reviews.isEmpty()) {
                    binding.reviewsRecyclerView.setVisibility(View.GONE);
                    binding.tvNoReviews.setVisibility(View.VISIBLE);
                    binding.tvNumberOfReviews.setText("0");
                    binding.ratingBar.setRating(0);
                } else {
                    binding.reviewsRecyclerView.setVisibility(View.VISIBLE);
                    binding.tvNoReviews.setVisibility(View.GONE);

                    binding.tvNumberOfReviews.setText(String.valueOf(reviews.size()));

                    // Calculate average rating
                    float totalRating = 0;
                    for (ReviewModel r : reviews) {
                        totalRating += r.getRating();
                    }
                    binding.ratingBar.setRating(totalRating / reviews.size());

                    ReviewAdapter adapter = new ReviewAdapter(reviews);
                    binding.reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    binding.reviewsRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(String error) {
                if (binding != null) {
                    binding.tvNoReviews.setText("Error loading reviews: " + error);
                    binding.tvNoReviews.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}