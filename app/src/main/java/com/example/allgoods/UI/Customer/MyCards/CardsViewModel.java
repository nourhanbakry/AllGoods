package com.example.allgoods.UI.Customer.MyCards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.allgoods.model.CardModel;

import java.util.ArrayList;
import java.util.List;

public class CardsViewModel extends ViewModel {

    private final MutableLiveData<List<CardModel>> cards = new MutableLiveData<>();

    public LiveData<List<CardModel>> getCards() {
        return cards;
    }

    public void loadCards() {

        List<CardModel> list = new ArrayList<>();

        // test data
        list.add(new CardModel("Nourhan Bakry", "5254763487347690", "12/28","129"));
        list.add(new CardModel("Ali Ahmed", "4111111111111111", "11/26","139"));

        cards.setValue(list);
    }
}