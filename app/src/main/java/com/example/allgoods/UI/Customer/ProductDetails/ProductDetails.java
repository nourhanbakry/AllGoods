package com.example.allgoods.UI.Customer.ProductDetails;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.allgoods.R;
import com.example.allgoods.UI.Customer.Reviews.View.ReviewsFragment;
import com.example.allgoods.databinding.ActivityProductDetailsBinding;
import com.example.allgoods.model.ProductModel;
import com.example.allgoods.model.ReviewModel;
import com.example.allgoods.utils.PriceUtils;
import com.example.allgoods.Data.repository.Review.ReviewRepository;
import com.example.allgoods.Data.repository.Review.ReviewRepositoryImpl;
import com.example.allgoods.UI.Customer.Reviews.Adapter.ReviewAdapter;
import com.example.allgoods.UI.Customer.ProductDetails.Adapter.SizeAdapter;
import com.example.allgoods.Data.repository.Cart.CartRepository;
import com.example.allgoods.Data.repository.Cart.CartRepositoryImpl;
import com.bumptech.glide.Glide;
import com.example.allgoods.utils.RateFormate;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ProductDetails extends AppCompatActivity {
    ActivityProductDetailsBinding binding;
    ProductModel product;
    private final ReviewRepository reviewRepository = new ReviewRepositoryImpl();
    private final CartRepository cartRepository = new CartRepositoryImpl();
    private String selectedSize = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.productDetails, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        product = (ProductModel) getIntent().getSerializableExtra("product");
        if (product != null) {
            displayProductDetails();
            loadReviews();
        }


        binding.backButton.setOnClickListener(v -> {
            finish();
        });

        binding.viewAllReviews.setOnClickListener(v->{
            ReviewsFragment fragment = ReviewsFragment.newInstance(product != null ? product.getId() : "", "");
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnAddToCart.setOnClickListener(v -> {
            if (selectedSize.isEmpty()) {
                Toast.makeText(this, "Please select a size", Toast.LENGTH_SHORT).show();
                return;
            }

            product.setSelectedSize(selectedSize);
            cartRepository.addToCart(product, new CartRepository.OnCartChangeListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ProductDetails.this, "Added to cart", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(ProductDetails.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void displayProductDetails() {
        binding.productTitle.setText(product.getName());
        binding.productPrice.setText("$" + PriceUtils.formatPrice(product.getPrice()));
        binding.descText.setText(product.getDescription());
        binding.ratingValue.setText(RateFormate.formateRating(product.getRating()));
        //binding.ratingValue.setText(String.valueOf(product.getRating()));
        binding.ratingBar.setRating(product.getRating());
        binding.totalPriceValue.setText("$" + PriceUtils.formatPrice(product.getPrice()));

        Glide.with(this)
                .load(product.getImage())
                .into(binding.productImage);

        displaySmallImages();
        setupSizes();
    }

    private void displaySmallImages() {
        List<String> images = new ArrayList<>();
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            images.add(product.getImage());
        }
        if (product.getImages() != null) {
            for (String img : product.getImages()) {
                if (!images.contains(img)) {
                    images.add(img);
                }
            }
        }

        ImageView[] imageViews = {binding.image1, binding.image2, binding.image3, binding.image4};

        if (images.isEmpty()) {
            binding.smallImagesLayout.setVisibility(View.GONE);
            return;
        }

        binding.smallImagesLayout.setVisibility(View.VISIBLE);
        for (int i = 0; i < imageViews.length; i++) {
            if (i < images.size()) {
                imageViews[i].setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(images.get(i))
                        .into(imageViews[i]);
                
                int finalI = i;
                imageViews[i].setOnClickListener(v -> {
                    Glide.with(this)
                            .load(images.get(finalI))
                            .into(binding.productImage);
                });
            } else {
                imageViews[i].setVisibility(View.GONE);
            }
        }
    }

    private void setupSizes() {
        List<String> availableSizes = new ArrayList<>();
        Map<String, Integer> sizesMap = product.getSizesQuantity();

        if (sizesMap != null) {
            for (Map.Entry<String, Integer> entry : sizesMap.entrySet()) {
                if (entry.getValue() > 0) {
                    availableSizes.add(entry.getKey());
                }
            }
        }

        if (availableSizes.isEmpty()) {
            binding.sizeHeader.setVisibility(View.GONE);
            binding.sizesRecyclerView.setVisibility(View.GONE);
        } else {
            SizeAdapter sizeAdapter = new SizeAdapter(availableSizes, size -> {
                selectedSize = size;
            });
            binding.sizesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.sizesRecyclerView.setAdapter(sizeAdapter);
        }
    }

    private void loadReviews() {
        if (product.getId() == null) return;

        reviewRepository.getReviewsForProduct(product.getId(), new ReviewRepository.OnReviewsFetchListener() {
            @Override
            public void onSuccess(List<ReviewModel> reviews) {
                if (reviews.isEmpty()) {
                    binding.userImg.setVisibility(View.GONE);
                    binding.userName.setVisibility(View.GONE);
                    binding.reviewDate.setVisibility(View.GONE);
                    binding.ratingBar.setVisibility(View.GONE);
                    binding.ratingValue.setVisibility(View.GONE);
                    binding.reviewComment.setText(R.string.no_reviews_for_this_product_yet);
                } else {
                    // Show the first review as a preview
                    ReviewModel firstReview = reviews.get(0);
                    binding.userName.setText(firstReview.getReviewerName());
                    binding.reviewDate.setText(firstReview.getReviewDate());
                    binding.reviewComment.setText(firstReview.getDescription());
                    binding.ratingBar.setRating(firstReview.getRating());
                    binding.ratingValue.setText(RateFormate.formateRating(firstReview.getRating()));
                    //binding.ratingValue.setText(String.valueOf(firstReview.getRating()));
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(String error) {
                binding.reviewComment.setText(R.string.error_loading_reviews);
            }
        });
    }
}