package com.example.allgoods.UI.Customer.MyCards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.allgoods.Data.repository.Card.CardRepository;
import com.example.allgoods.Data.repository.Card.CardRepositoryImpl;
import com.example.allgoods.model.CardModel;

import java.util.ArrayList;
import java.util.List;

public class CardsViewModel extends ViewModel {

    private final MutableLiveData<List<CardModel>> cards = new MutableLiveData<>();
    private final CardRepository cardRepository = new CardRepositoryImpl();

    public LiveData<List<CardModel>> getCards() {
        return cards;
    }

    public void loadCards() {
        cardRepository.getCards(new CardRepository.OnCardsFetchListener() {
            @Override
            public void onSuccess(List<CardModel> cardList) {
                cards.setValue(cardList);
            }

            @Override
            public void onFailure(String error) {
                cards.setValue(new ArrayList<>());
            }
        });
    }

    public void setPrimaryCard(String cardId) {
        cardRepository.setPrimaryCard(cardId, new CardRepository.OnCardChangeListener() {
            @Override
            public void onSuccess() {
                // Optionally reload to sync UI
                loadCards();
            }

            @Override
            public void onFailure(String error) {
                // Handle error
            }
        });
    }
}