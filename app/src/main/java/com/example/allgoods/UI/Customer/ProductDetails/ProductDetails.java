package com.example.allgoods.UI.Customer.ProductDetails;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.allgoods.R;
import com.example.allgoods.databinding.ActivityProductDetailsBinding;

public class ProductDetails extends AppCompatActivity {
    ActivityProductDetailsBinding binding;

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


        binding.backButton.setOnClickListener(v -> {
            finish();
        });

        binding.viewAllReviews.setOnClickListener(v->{
            //TODO: move to reviews screen
        });

        binding.btnAddToCart.setOnClickListener(v -> {
            //TODO: add to cart
        });
    }
}