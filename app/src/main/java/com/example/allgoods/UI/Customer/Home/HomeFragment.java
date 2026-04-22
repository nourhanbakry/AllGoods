package com.example.allgoods.UI.Customer.Home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.allgoods.R;
import com.example.allgoods.UI.Customer.AccountInfo.AccountInfoFragment;
import com.example.allgoods.UI.Customer.Categories.CategoriesFragment;
import com.example.allgoods.UI.Customer.Home.Adapter.ProductAdapter;
import com.example.allgoods.UI.Customer.MyCards.MyCardsFragment;
import com.example.allgoods.UI.Customer.Passwords.PassworsFragment;
import com.example.allgoods.UI.Customer.Wishlist.WishlistFragment;
import com.example.allgoods.UI.Main.MainActivity;
import com.example.allgoods.databinding.FragmentHomeBinding;
import com.example.allgoods.model.ProductModel;
import com.example.allgoods.utils.Category;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    HomeViewModel viewModel;


    public HomeFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.homeMenuIcon.setOnClickListener(v -> {
            ((MainActivity) requireActivity())
                    .getDrawerLayout()
                    .openDrawer(GravityCompat.START);
        });


        binding.pantsCategory.setOnClickListener(v -> openCategory(Category.PANTS));
        binding.tshirtCategory.setOnClickListener(v -> openCategory(Category.TSHIRT));
        binding.hoodieCategory.setOnClickListener(v -> openCategory(Category.HOODIE));


        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {

            ProductAdapter adapter = new ProductAdapter(requireContext(), products);
            binding.productsRv.setAdapter(adapter);
        });
        viewModel.loadProducts();
    }

    private void openCategory(Category category) {
        Bundle bundle = new Bundle();
        bundle.putString("category", category.name());

        CategoriesFragment fragment = new CategoriesFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

}