package com.example.allgoods.UI.Main;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.allgoods.UI.Customer.Cart.CartFragment;
import com.example.allgoods.UI.Customer.Home.HomeFragment;
import com.example.allgoods.UI.Customer.Wishlist.WishlistFragment;
import com.example.allgoods.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupClicks();
        selectTab(0);
    }

    private void setupClicks() {
        binding.homeTab.setOnClickListener(v -> selectTab(0));
        binding.wishlistTab.setOnClickListener(v -> selectTab(1));
        binding.cartTab.setOnClickListener(v -> selectTab(2));
    }

    private void selectTab(int tab) {

        binding.homeText.setVisibility(View.GONE);
        binding.wishlistText.setVisibility(View.GONE);
        binding.cartText.setVisibility(View.GONE);

        binding.homeIcon.setVisibility(View.VISIBLE);
        binding.wishlistIcon.setVisibility(View.VISIBLE);
        binding.cartIcon.setVisibility(View.VISIBLE);

        if (tab == 0) {
            binding.homeIcon.setVisibility(View.GONE);
            binding.homeText.setVisibility(View.VISIBLE);
            replaceFragment(new HomeFragment());

        } else if (tab == 1) {
            binding.wishlistIcon.setVisibility(View.GONE);
            binding.wishlistText.setVisibility(View.VISIBLE);
            replaceFragment(new WishlistFragment());

        } else if (tab == 2) {
            binding.cartIcon.setVisibility(View.GONE);
            binding.cartText.setVisibility(View.VISIBLE);
            replaceFragment(new CartFragment());
        }
    }

    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.frameLayout.getId(), fragment)
                .commit();
    }
}