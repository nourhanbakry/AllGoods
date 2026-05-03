package com.example.allgoods.UI.Customer.Reviews.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.allgoods.R;
import com.example.allgoods.databinding.FragmentAddReviewsBinding;
import com.example.allgoods.Data.repository.Review.ReviewRepository;
import com.example.allgoods.Data.repository.Review.ReviewRepositoryImpl;
import com.example.allgoods.model.ReviewModel;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddReviewsFragment extends Fragment {

    private static final String ARG_PRODUCT_ID = "product_id";
    private String productId;
    private FragmentAddReviewsBinding binding;
    private final ReviewRepository reviewRepository = new ReviewRepositoryImpl();


    public static AddReviewsFragment newInstance(String productId, String param2) {
        AddReviewsFragment fragment = new AddReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString(ARG_PRODUCT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddReviewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.backButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        binding.customSeek.setOnTouchListener((v, event) -> {

            float value = binding.customSeek.getProgressValue();

            binding.tvMinVal.setText(String.format("%.1f", value));

            return false;
        });

        binding.btnSave.setOnClickListener(v -> {
            saveReview();
        });

    }

    private void saveReview() {
        String name = binding.etName.getText().toString().trim();
        String experience = binding.etExperience.getText().toString().trim();
        float rating = binding.customSeek.getProgressValue();

        if (name.isEmpty() || experience.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(new Date());
        ReviewModel review = new ReviewModel(name, currentDate, rating, experience);

        if (productId != null && !productId.isEmpty()) {
            binding.btnSave.setEnabled(false);
            reviewRepository.addReview(productId, review, new ReviewRepository.OnReviewAddedListener() {
                @Override
                public void onSuccess() {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Review added successfully", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    }
                }

                @Override
                public void onFailure(String error) {
                    if (isAdded()) {
                        binding.btnSave.setEnabled(true);
                        Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(requireContext(), "Product ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}