package com.example.allgoods.UI.Customer.MyCards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.allgoods.R;
import com.example.allgoods.UI.Customer.Home.HomeFragment;
import com.example.allgoods.UI.Customer.Cart.CartViewModel;
import com.example.allgoods.UI.Customer.MyCards.Adapter.CardsAdapter;
import com.example.allgoods.UI.Main.MainActivity;
import com.example.allgoods.databinding.FragmentMyCardsBinding;
import com.example.allgoods.model.CardModel;
import com.example.allgoods.utils.Network.NetworkListener;
import com.example.allgoods.utils.Network.NetworkManager;
import com.example.allgoods.utils.Network.NetworkOverlayController;
import com.example.allgoods.utils.SnackBarHelper;

import android.widget.Toast;
import java.util.List;

public class MyCardsFragment extends Fragment implements NetworkOverlayController {

    private FragmentMyCardsBinding binding;
    private CardsViewModel viewModel;

    private NetworkManager networkManager;
    private Boolean lastNetworkState = null;

    public MyCardsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyCardsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupViewModel();
        connection();

        binding.backButtonMyCard.setOnClickListener(v -> backToSource());
        binding.layoutAddPayment.setOnClickListener(v -> navigateToAddNewCard());
        binding.btnSaveCard.setOnClickListener(v -> saveSelectedCard());
    }

    private void saveSelectedCard() {
        String source = getArguments() != null ? getArguments().getString("source") : "";
        List<CardModel> cards = viewModel.getCards().getValue();
        if (cards != null && !cards.isEmpty()) {
            int currentPos = binding.viewPagerCards.getCurrentItem();
            CardModel selected = cards.get(currentPos);
            boolean makePrimary = binding.saveCardInfoSwitch.isChecked();

            if (makePrimary) {
                viewModel.setPrimaryCard(selected.id);
            }

            if ("cart".equals(source)) {
                CartViewModel cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
                cartViewModel.setSelectedCard(selected);
                getParentFragmentManager().popBackStack();
            } else if (makePrimary) {
                Toast.makeText(requireContext(), "Card set as default", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Card Information Saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).hideBottomBar();
        setSaveCardsEnabled(networkManager.isConnected(requireContext()));
        viewModel.loadCards();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) requireActivity()).showBottomBar();
        networkManager.unregister(requireContext());


        binding = null;
    }


    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CardsViewModel.class);

        // Register callback once
        binding.viewPagerCards.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                List<CardModel> currentCards = viewModel.getCards().getValue();
                if (currentCards != null && position >= 0 && position < currentCards.size()) {
                    bindCardData(currentCards.get(position));
                }
            }
        });

        viewModel.getCards().observe(getViewLifecycleOwner(), cards -> {
            if (cards == null || cards.isEmpty()) {
                binding.viewPagerCards.setVisibility(View.GONE);
                binding.noCardsAnimation.setVisibility(View.VISIBLE);
                binding.noCardsText.setVisibility(View.VISIBLE);
                binding.saveCardInfoTxt.setVisibility(View.GONE);
                binding.saveCardInfoSwitch.setVisibility(View.GONE);
                setSaveCardsEnabled(false);
                disableInputs();
            } else {
                binding.viewPagerCards.setVisibility(View.VISIBLE);
                binding.noCardsAnimation.setVisibility(View.GONE);
                binding.noCardsText.setVisibility(View.GONE);
                binding.saveCardInfoTxt.setVisibility(View.VISIBLE);
                binding.saveCardInfoSwitch.setVisibility(View.VISIBLE);
                setSaveCardsEnabled(true);
                enableInputs();

                CardsAdapter adapter = new CardsAdapter(cards);
                binding.viewPagerCards.setAdapter(adapter);

                binding.viewPagerCards.setClipToPadding(false);
                binding.viewPagerCards.setClipChildren(false);

                CompositePageTransformer transformer = new CompositePageTransformer();
                transformer.addTransformer((page, position) -> {
                    float absPos = Math.abs(position);
                    page.setScaleY(0.85f + (1 - absPos) * 0.15f);
                    page.setAlpha(0.5f + (1 - absPos) * 0.5f);
                    page.setTranslationX(-position * 40);
                });
                binding.viewPagerCards.setPageTransformer(transformer);

                // Initial bind
                bindCardData(cards.get(0));
            }
        });

        viewModel.loadCards();
    }

    private void connection() {

        networkManager = new NetworkManager();

        if (!networkManager.isConnected(requireContext())) {
            setSaveCardsEnabled(false);
            SnackBarHelper.showError(binding.getRoot(), "No Internet Connection");
        } else {
            setSaveCardsEnabled(true);
//            SnackBarHelper.showSuccess(binding.getRoot(), "Internet Connection Available");

        }


        networkManager.register(requireContext(), new NetworkListener() {

            @Override
            public void onConnected() {
                if (getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {

                        if (lastNetworkState != null && !lastNetworkState) SnackBarHelper.showSuccess(binding.getRoot(), "Internet Connection Available");


                        lastNetworkState = true;
                        setSaveCardsEnabled(true);
                    });
                }
            }

            @Override
            public void onDisconnected() {
                if (getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {

                        if (lastNetworkState == null || lastNetworkState) SnackBarHelper.showError(binding.getRoot(), "No Internet Connection");


                        lastNetworkState = false;
                        setSaveCardsEnabled(false);
                    });
                }
            }
        });
    }

    private void setSaveCardsEnabled(boolean enabled) {
        binding.btnSaveCard.setEnabled(enabled);
        binding.btnSaveCard.setAlpha(enabled ? 1f : 0.5f);
    }

    private void disableInputs() {
        binding.etCardOwner.setEnabled(false);
        binding.etCardNumber.setEnabled(false);
        binding.etEXP.setEnabled(false);
        binding.etCVV.setEnabled(false);
    }

    private void enableInputs() {
        binding.etCardOwner.setEnabled(true);
        binding.etCardNumber.setEnabled(true);
        binding.etEXP.setEnabled(true);
        binding.etCVV.setEnabled(true);
    }

    private void bindCardData(CardModel card) {
        binding.etCardOwner.setText(card.name);
        binding.etCardNumber.setText(card.number);
        binding.etEXP.setText(card.expiry);
        binding.etCVV.setText(card.cvv);
    }

    private void navigateToAddNewCard() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new AddCardsFragment())
                .addToBackStack(null)
                .commit();
    }

    private void backToSource() {
        String source = getArguments() != null
                ? getArguments().getString("source")
                : "";

        if ("cart".equals(source)) {
            getParentFragmentManager().popBackStack();
        } else {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, new HomeFragment())
                    .commit();
        }
    }

    @Override
    public boolean showNetworkOverlay() {
        return false;
    }
}