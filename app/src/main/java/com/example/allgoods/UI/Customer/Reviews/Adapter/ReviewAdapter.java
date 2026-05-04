package com.example.allgoods.UI.Customer.Reviews.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allgoods.databinding.ReviewsItemBinding;
import com.example.allgoods.model.ReviewModel;
import com.example.allgoods.utils.RateFormate;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewModel> reviewList;

    public ReviewAdapter(List<ReviewModel> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReviewsItemBinding binding = ReviewsItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewModel review = reviewList.get(position);
        holder.binding.tvReviewerName.setText(review.getReviewerName());
        holder.binding.tvReviewTime.setText(review.getReviewDate());
        holder.binding.ratingBar.setRating(review.getRating());
        holder.binding.tvRateNumber.setText(RateFormate.formateRating(review.getRating()));
        holder.binding.tvReviewerDiscription.setText(review.getDescription());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ReviewsItemBinding binding;

        public ReviewViewHolder(ReviewsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
