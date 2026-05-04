package com.example.allgoods.UI.Seller.Reviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allgoods.R;
import com.example.allgoods.model.ProductModel;
import com.example.allgoods.model.ReviewModel;
import com.example.allgoods.utils.RateFormate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SellerReviewAdapter extends RecyclerView.Adapter<SellerReviewAdapter.ReviewViewHolder> {

    private List<CombinedReview> items = new ArrayList<>();

    public void setData(Map<ProductModel, List<ReviewModel>> productReviewsMap) {
        items.clear();
        for (Map.Entry<ProductModel, List<ReviewModel>> entry : productReviewsMap.entrySet()) {
            ProductModel product = entry.getKey();
            for (ReviewModel review : entry.getValue()) {
                items.add(new CombinedReview(product, review));
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seller_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatarText, tvReviewerName, tvReviewDate, tvRateNumber, tvProductName, tvReviewDescription;
        RatingBar ratingBar;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatarText = itemView.findViewById(R.id.tvAvatarText);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            tvRateNumber = itemView.findViewById(R.id.tvRateNumber);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvReviewDescription = itemView.findViewById(R.id.tvReviewDescription);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        void bind(CombinedReview combined) {
            ReviewModel review = combined.review;
            ProductModel product = combined.product;

            tvReviewerName.setText(review.getReviewerName());
            tvReviewDate.setText(review.getReviewDate());
            ratingBar.setRating(review.getRating());
            tvProductName.setText(product.getName());
            tvReviewDescription.setText(review.getDescription());

            // Set Avatar Text
            tvAvatarText.setText(getAvatarText(review.getReviewerName()));
        }

        private String getAvatarText(String name) {
            if (name == null || name.isEmpty()) return "?";
            String[] parts = name.trim().split("\\s+");
            if (parts.length >= 2) {
                return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
            } else if (parts[0].length() >= 2) {
                return parts[0].substring(0, 2).toUpperCase();
            } else {
                return parts[0].substring(0, 1).toUpperCase();
            }
        }
    }

    private static class CombinedReview {
        ProductModel product;
        ReviewModel review;

        CombinedReview(ProductModel product, ReviewModel review) {
            this.product = product;
            this.review = review;
        }
    }
}