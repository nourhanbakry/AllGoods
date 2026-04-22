package com.example.allgoods.model;

public class CardModel {

    public String name;
    public String number;
    public String expiry;


    public CardModel() {}

    public CardModel(String name, String number, String expiry) {
        this.name = name;
        this.number = number;
        this.expiry = expiry;
    }
}
