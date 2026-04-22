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
import com.example.allgoods.UI.Customer.Reviews.Adapter.ReviewAdapter;
import com.example.allgoods.databinding.FragmentReviews2Binding;
import com.example.allgoods.model.ReviewModel;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ReviewsFragment extends Fragment {

    private com.example.allgoods.databinding.FragmentReviews2Binding binding;

    public static ReviewsFragment newInstance(String param1, String param2) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    .replace(android.R.id.content, new AddReviewsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        List<ReviewModel> reviews = new ArrayList<>();
        reviews.add(new ReviewModel("Ronald Richards", "13 Sep, 2020", 4.8f, getString(R.string.discription)));
        reviews.add(new ReviewModel("John Doe", "14 Sep, 2020", 4.5f, getString(R.string.discription)));
        reviews.add(new ReviewModel("Jane Smith", "15 Sep, 2020", 5.0f, getString(R.string.discription)));
        reviews.add(new ReviewModel("Ronald Richards", "13 Sep, 2020", 4.8f, getString(R.string.discription)));
        reviews.add(new ReviewModel("John Doe", "14 Sep, 2020", 4.5f, getString(R.string.discription)));
        reviews.add(new ReviewModel("Jane Smith", "15 Sep, 2020", 5.0f, getString(R.string.discription)));
        reviews.add(new ReviewModel("Ronald Richards", "13 Sep, 2020", 4.8f, getString(R.string.discription)));
        reviews.add(new ReviewModel("John Doe", "14 Sep, 2020", 4.5f, getString(R.string.discription)));
        reviews.add(new ReviewModel("Jane Smith", "15 Sep, 2020", 5.0f, getString(R.string.discription)));

        ReviewAdapter adapter = new ReviewAdapter(reviews);
        binding.reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.reviewsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}