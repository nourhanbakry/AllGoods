package com.example.allgoods.UI.Customer.MyCards;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.CompositePageTransformer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.allgoods.R;
import com.example.allgoods.UI.Customer.Home.HomeFragment;
import com.example.allgoods.UI.Customer.MyCards.Adapter.CardsAdapter;
import com.example.allgoods.UI.Main.MainActivity;
import com.example.allgoods.databinding.FragmentMyCardsBinding;
import com.example.allgoods.utils.Network.NetworkListener;
import com.example.allgoods.utils.Network.NetworkManager;
import com.example.allgoods.utils.SnackBarHelper;

public class MyCardsFragment extends Fragment {

    private FragmentMyCardsBinding binding;

    private CardsViewModel viewModel;

    private NetworkManager networkManager;
    private boolean lastNetworkState = true;


    public MyCardsFragment() {}


    public static MyCardsFragment newInstance(String param1, String param2) {
        MyCardsFragment fragment = new MyCardsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyCardsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setupViewModel();
        connection();
        binding.backButtonMyCard.setOnClickListener(v -> backToSource());

        binding.layoutAddPayment.setOnClickListener(v -> navigateToAddNewCard());
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).hideBottomBar();
        setSaveCardsEnabled(networkManager.isConnected(requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) requireActivity()).showBottomBar();
        networkManager.unregister(requireContext());
        binding = null;
    }

    private void navigateToAddNewCard(){
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new AddCardsFragment())
                .addToBackStack(null)
                .commit();
    }

    private void backToSource(){
        String source = getArguments() != null
                ? getArguments().getString("source")
                : "";

        if ("cart".equals(source)) {
            getParentFragmentManager().popBackStack();
        } else {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, new HomeFragment())
                    .commit();
        }
    }

    private void setupViewModel(){
        viewModel = new ViewModelProvider(this).get(CardsViewModel.class);

        viewModel.getCards().observe(getViewLifecycleOwner(), cards -> {

            if (cards == null || cards.isEmpty()) {

                binding.viewPagerCards.setVisibility(View.GONE);
                binding.noCardsAnimation.setVisibility(View.VISIBLE);
                binding.noCardsText.setVisibility(View.VISIBLE);
            } else {

                binding.viewPagerCards.setVisibility(View.VISIBLE);
                binding.noCardsAnimation.setVisibility(View.GONE);
                binding.noCardsText.setVisibility(View.GONE);

                CardsAdapter adapter = new CardsAdapter(cards);
                binding.viewPagerCards.setAdapter(adapter);

                //slider
                binding.viewPagerCards.setClipToPadding(false);
                binding.viewPagerCards.setClipChildren(false);

                CompositePageTransformer transformer = new CompositePageTransformer();

                transformer.addTransformer((page, position) -> {
                    float r = 1 - Math.abs(position);
                    page.setScaleY(0.85f + r * 0.15f);
                });

                binding.viewPagerCards.setPageTransformer(transformer);
            }
        });

        viewModel.loadCards();
    }

    private void setSaveCardsEnabled(boolean enabled){
        binding.btnSaveCard.setEnabled(enabled);
        binding.btnSaveCard.setAlpha(enabled ? 1f : 0.5f);
    }

    private void connection(){
        networkManager = new NetworkManager();

        if (!networkManager.isConnected(requireContext())) {
            setSaveCardsEnabled(false);
            SnackBarHelper.showError(binding.getRoot(), "No Internet Connection");
        } else {
            setSaveCardsEnabled(true);
            SnackBarHelper.showSuccess(binding.getRoot(), "Internet Connection Available");

        }

        networkManager.register(requireContext(), new NetworkListener() {
            @Override
            public void onConnected() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (!lastNetworkState) SnackBarHelper.showSuccess(binding.getRoot(), "Internet Connection Available");

                        lastNetworkState = true;
                        setSaveCardsEnabled(true);
                    });
                }
            }

            @Override
            public void onDisconnected() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (lastNetworkState) SnackBarHelper.showError(binding.getRoot(), "No Internet Connection");

                        lastNetworkState = false;
                        setSaveCardsEnabled(false);
                    });
                }
            }
        });
    }

}