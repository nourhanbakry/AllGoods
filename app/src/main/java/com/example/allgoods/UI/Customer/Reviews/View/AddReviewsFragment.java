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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddReviewsFragment extends Fragment {

    private FragmentAddReviewsBinding binding;


    public static AddReviewsFragment newInstance(String param1, String param2) {
        AddReviewsFragment fragment = new AddReviewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}