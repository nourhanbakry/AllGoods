package com.example.allgoods.UI.Customer.Categories;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allgoods.R;
import com.example.allgoods.UI.Customer.Home.Adapter.ProductAdapter;
import com.example.allgoods.UI.Customer.Home.HomeViewModel;
import com.example.allgoods.UI.Main.MainActivity;
import com.example.allgoods.databinding.FragmentCategoriesBinding;
import com.example.allgoods.utils.Category;


public class CategoriesFragment extends Fragment {
    FragmentCategoriesBinding binding;

    CategoriesViewModel viewModel;

    String category;



    public CategoriesFragment() {}


    public static CategoriesFragment newInstance() {
        CategoriesFragment fragment = new CategoriesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString("category");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.backToHomeButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        setupUI();
        loadData();




    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).hideBottomBar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) requireActivity()).showBottomBar();
    }

    private void setupUI() {
        if (category.equals(Category.PANTS.name())) {
            binding.categoryIcon.setImageResource(R.drawable.pants);
        } else if (category.equals(Category.TSHIRT.name())) {
            binding.categoryIcon.setImageResource(R.drawable.tshirt);
        } else if (category.equals(Category.HOODIE.name())) {
            binding.categoryIcon.setImageResource(R.drawable.hoodie);
        }
    }

    private void loadData() {
        viewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {

            ProductAdapter adapter = new ProductAdapter(requireContext(), products);
            binding.categoryProductsRv.setAdapter(adapter);
            binding.categoryItemsNum.setText(String.valueOf(products.size()));
        });
        if (category != null) {
            viewModel.loadProductsByCategory(category);
        }
    }
}