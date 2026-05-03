package com.example.allgoods.Data.repository.Card;

import com.example.allgoods.model.CardModel;
import java.util.List;

public interface CardRepository {
    void saveCard(CardModel card, OnCardChangeListener listener);
    void getCards(OnCardsFetchListener listener);
    void getPrimaryCard(OnCardFetchListener listener);
    void setPrimaryCard(String cardId, OnCardChangeListener listener);

    interface OnCardChangeListener {
        void onSuccess();
        void onFailure(String error);
    }

    interface OnCardFetchListener {
        void onSuccess(CardModel card);
        void onFailure(String error);
    }

    interface OnCardsFetchListener {
        void onSuccess(List<CardModel> cards);
        void onFailure(String error);
    }
}
