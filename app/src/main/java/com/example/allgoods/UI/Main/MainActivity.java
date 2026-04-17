package com.example.allgoods.UI.Main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.allgoods.R;
import com.example.allgoods.UI.Customer.AccountInfo.AccountInfoFragment;
import com.example.allgoods.UI.Customer.Cart.CartFragment;
import com.example.allgoods.UI.Customer.Home.HomeFragment;
import com.example.allgoods.UI.Customer.MyCards.MyCardsFragment;
import com.example.allgoods.UI.Customer.Passwords.PassworsFragment;
import com.example.allgoods.UI.Customer.Wishlist.WishlistFragment;
import com.example.allgoods.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ImageView drawerIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerMainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        setupDrawer();
        setupHeader();
        setupBottomNav();

        selectTab(0);
    }


    private void setupDrawer() {

        binding.homeMenuIcon.setOnClickListener(v ->
                binding.drawerMainLayout.openDrawer(GravityCompat.START)
        );

        binding.drawerMainLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        binding.drawerMainLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                binding.homeMenuIcon.setImageResource(R.drawable.menu_close);
                if (drawerIcon != null) {
                    drawerIcon.setImageResource(R.drawable.menu_close);
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                binding.homeMenuIcon.setImageResource(R.drawable.menu_open);
                if (drawerIcon != null) {
                    drawerIcon.setImageResource(R.drawable.menu_open);
                }
            }
        });
    }

    private void setupHeader() {
        View header = binding.navigationView.getHeaderView(0);
        drawerIcon = header.findViewById(R.id.drawerMenuIcon);

        drawerIcon.setOnClickListener(v ->
                binding.drawerMainLayout.closeDrawer(GravityCompat.START)
        );

        binding.navigationView.setNavigationItemSelectedListener(item -> {


            int id = item.getItemId();

            if (id == R.id.account_info) {
//                replaceFragment( new AccountInfoFragment());
            } else if (id == R.id.passwords) {
//                replaceFragment(new PassworsFragment());
            } else if (id == R.id.my_cards) {
//                replaceFragment(new MyCardsFragment());
            } else if (id == R.id.wishlist) {
                selectTab(1);
            }



            binding.drawerMainLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupBottomNav() {

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
            binding.homeMenuIcon.setVisibility(View.VISIBLE);
            replaceFragment(new HomeFragment());

        } else if (tab == 1) {
            binding.wishlistIcon.setVisibility(View.GONE);
            binding.wishlistText.setVisibility(View.VISIBLE);
            binding.homeMenuIcon.setVisibility(View.GONE);
            replaceFragment(new WishlistFragment());

        } else {
            binding.cartIcon.setVisibility(View.GONE);
            binding.cartText.setVisibility(View.VISIBLE);
            binding.homeMenuIcon.setVisibility(View.GONE);
            replaceFragment(new CartFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.frameLayout.getId(), fragment)
                .commit();
    }
}