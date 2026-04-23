package com.example.allgoods.model;

public class CardModel {

    public String name;
    public String number;
    public String expiry;
    public String cvv;


    public CardModel() {}

    public CardModel(String name, String number, String expiry,String cvv) {
        this.name = name;
        this.number = number;
        this.expiry = expiry;
        this.cvv = cvv;
    }
}
