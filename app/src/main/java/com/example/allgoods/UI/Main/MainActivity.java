package com.example.allgoods.UI.Main;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.allgoods.Data.repository.Auth.AuthRepositoryImpl;
import com.example.allgoods.Data.repository.Order.OrderRepository;
import com.example.allgoods.Data.repository.Order.OrderRepositoryImpl;
import com.example.allgoods.R;
import com.example.allgoods.UI.Auth.forgetpassword.ForgetPasswordActivity;
import com.example.allgoods.UI.Auth.login.LoginActivity;
import com.example.allgoods.UI.Customer.AccountInfo.AccountInfoFragment;
import com.example.allgoods.UI.Customer.Cart.CartFragment;
import com.example.allgoods.UI.Customer.Home.HomeFragment;
import com.example.allgoods.UI.Customer.MyCards.MyCardsFragment;
import com.example.allgoods.UI.Customer.Wishlist.WishlistFragment;
import com.example.allgoods.UI.Seller.AddProduct.AddProductFragment;
import com.example.allgoods.UI.Seller.Inventory.InventoryFragment;
import com.example.allgoods.UI.Seller.Orders.OrdersFragment;
import com.example.allgoods.UI.Seller.Reviews.ReviewsFragment;
import com.example.allgoods.UI.Seller.Stats.StatsFragment;
import com.example.allgoods.databinding.ActivityMainBinding;
import com.example.allgoods.model.OrderModel;
import com.example.allgoods.utils.Network.NetworkListener;
import com.example.allgoods.utils.Network.NetworkManager;
import com.example.allgoods.utils.Network.NetworkOverlayController;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ImageView drawerIcon;
    private String userRole = "customer";
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent() != null && getIntent().hasExtra("USER_ROLE")) {
            userRole = getIntent().getStringExtra("USER_ROLE");
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerMainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupDrawer();
        setupHeader();
        setupBottomNav();
        selectCustomerOrSellerBottomNavBar();
        connection();
    }

    private void  selectCustomerOrSellerBottomNavBar(){
        if ("seller".equals(userRole)) {
            binding.bottomBar.setVisibility(View.GONE);
            binding.bottomBarSeller.setVisibility(View.VISIBLE);
            selectTabSeller(0);
        } else {
            binding.bottomBar.setVisibility(View.VISIBLE);
            binding.bottomBarSeller.setVisibility(View.GONE);
            selectTab(0);
        }
    }

    private void connection(){
        networkManager = new NetworkManager();

        networkManager.register(this, new NetworkListener() {
            @Override
            public void onConnected() {
                runOnUiThread(() -> hideOverlay());
            }

            @Override
            public void onDisconnected() {
                runOnUiThread(() -> {
                    if(shouldShowOverlay()) showOverlay();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkManager.unregister(this);
    }

    public DrawerLayout getDrawerLayout() {
        return binding.drawerMainLayout;
    }

    private void setupDrawer() {

        binding.drawerMainLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        binding.drawerMainLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (drawerIcon != null) {
                    drawerIcon.setImageResource(R.drawable.menu_close);
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (drawerIcon != null) {
                    drawerIcon.setImageResource(R.drawable.menu_open);
                }
            }
        });
    }

    private void setupHeader() {
        View header = binding.navigationView.getHeaderView(0);
        drawerIcon = header.findViewById(R.id.drawerMenuIcon);
        TextView ordersCountText = header.findViewById(R.id.orders);
        TextView userNameText = header.findViewById(R.id.userNameNavigation);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener(task -> {
                String name = currentUser.getDisplayName();
                if (name != null && !name.isEmpty()) {
                    userNameText.setText(name);
                } else {
                    userNameText.setText(currentUser.getEmail());
                }
            });
        } else {
            userNameText.setText("Guest User");
        }


        // Fetch and update orders count
        new OrderRepositoryImpl().getOrders(new OrderRepository.OnOrdersFetchListener() {
            @Override
            public void onSuccess(List<OrderModel> orders) {
                if (orders != null) {
                    String ordersText = orders.size() + (orders.size() == 1 ? " Order" : " Orders");
                    ordersCountText.setText(ordersText);
                }
            }

            @Override
            public void onFailure(String error) {
                // If failed, we keep the default "3 Orders" or could set to "0 Orders"
                ordersCountText.setText(R.string._0_orders);
            }
        });

        drawerIcon.setOnClickListener(v ->
                binding.drawerMainLayout.closeDrawer(GravityCompat.START)
        );

        View logoutLayout = binding.navigationView.findViewById(R.id.logout_layout);
        logoutLayout.setOnClickListener(v -> showLogoutDialog());

        binding.navigationView.setNavigationItemSelectedListener(item -> {


            int id = item.getItemId();

            if (id == R.id.account_info) {
                replaceFragment( new AccountInfoFragment());
            } else if (id == R.id.passwords) {
                openForgetPassword();
            } else if (id == R.id.my_cards) {
                navigateToMyCards();
            } else if (id == R.id.wishlist) {
                selectTab(1);
            }
            binding.drawerMainLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
    private void navigateToMyCards(){
        MyCardsFragment fragment = new MyCardsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("source", "main");
        fragment.setArguments(bundle);

        replaceFragment(fragment);
    }
    private void setupBottomNav() {
        if ("seller".equals(userRole)) {
            binding.statsTab.setOnClickListener(v -> selectTabSeller(0));
            binding.inventoryTab.setOnClickListener(v -> selectTabSeller(1));
            binding.addProductTab.setOnClickListener(v -> selectTabSeller(2));
            binding.ordersTab.setOnClickListener(v -> selectTabSeller(3));
            binding.reviewsTab.setOnClickListener(v -> selectTabSeller(4));
        } else {
            binding.homeTab.setOnClickListener(v -> selectTab(0));
            binding.wishlistTab.setOnClickListener(v -> selectTab(1));
            binding.cartTab.setOnClickListener(v -> selectTab(2));
        }
    }

    private void selectTabSeller(int tab) {
        binding.statsText.setVisibility(View.GONE);
        binding.inventoryText.setVisibility(View.GONE);
        binding.ordersText.setVisibility(View.GONE);
        binding.reviewsText.setVisibility(View.GONE);

        binding.statsIcon.setVisibility(View.VISIBLE);
        binding.inventoryIcon.setVisibility(View.VISIBLE);
        binding.addProductIcon.setVisibility(View.VISIBLE);
        binding.ordersIcon.setVisibility(View.VISIBLE);
        binding.reviewsIcon.setVisibility(View.VISIBLE);

        if (tab == 0) {
            binding.statsIcon.setVisibility(View.GONE);
            binding.statsText.setVisibility(View.VISIBLE);
            replaceFragment(new StatsFragment());
        } else if (tab == 1) {
            binding.inventoryIcon.setVisibility(View.GONE);
            binding.inventoryText.setVisibility(View.VISIBLE);
            replaceFragment(new InventoryFragment());
        } else if (tab == 2) {
            //binding.addProductIcon.setVisibility(View.GONE);
            replaceFragment(new AddProductFragment());
        } else if (tab == 3) {
            binding.ordersIcon.setVisibility(View.GONE);
            binding.ordersText.setVisibility(View.VISIBLE);
            replaceFragment(new OrdersFragment());
        } else if (tab == 4) {
            binding.reviewsIcon.setVisibility(View.GONE);
            binding.reviewsText.setVisibility(View.VISIBLE);
            replaceFragment(new ReviewsFragment());
        }
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

        } else {
            binding.cartIcon.setVisibility(View.GONE);
            binding.cartText.setVisibility(View.VISIBLE);
            replaceFragment(new CartFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.frameLayout.getId(), fragment)
                .addToBackStack(null)
                .commit();
    }
    private void openForgetPassword() {
        Intent intent = new Intent(MainActivity.this, ForgetPasswordActivity.class);
        intent.putExtra("SOURCE", "IN_APP");
        startActivity(intent);
    }

    public void hideBottomBar() {
        findViewById(R.id.bottomBar).setVisibility(View.GONE);
    }

    public void showBottomBar() {
        findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
    }

    private void showOverlay() {
        binding.noInternetAnimation.setVisibility(View.VISIBLE);
        binding.noInternetAnimation.playAnimation();
        binding.internetText.setVisibility(View.VISIBLE);
    }

    private void hideOverlay() {
        binding.noInternetAnimation.setVisibility(View.GONE);
        binding.internetText.setVisibility(View.GONE);
    }

    private boolean shouldShowOverlay() {
        Fragment current = getSupportFragmentManager()
                .findFragmentById(binding.frameLayout.getId());

        if (current instanceof NetworkOverlayController) {
            return ((NetworkOverlayController) current).showNetworkOverlay();
        }

        return true; // default
    }

    private void showLogoutDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView title = dialog.findViewById(R.id.dialog_title);
        TextView subtitle = dialog.findViewById(R.id.dialog_content);

        MaterialButton btnDelete = dialog.findViewById(R.id.btnDelete);
        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);

        title.setText(getString(R.string.logout));
        subtitle.setText(R.string.do_you_really_want_to_exit_your_account);
        btnDelete.setText(getString(R.string.logout));

        btnDelete.setOnClickListener(v -> {
            performLogout();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void performLogout() {

       AuthRepositoryImpl repo =
                new AuthRepositoryImpl(this);

        repo.logout();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}