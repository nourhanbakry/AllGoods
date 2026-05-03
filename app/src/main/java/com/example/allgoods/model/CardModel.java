package com.example.allgoods.model;

public class CardModel {

    public String id;
    public String name;
    public String number;
    public String expiry;
    public String cvv;
    public boolean isPrimary;


    public CardModel() {}

    public CardModel(String name, String number, String expiry, String cvv) {
        this(name, number, expiry, cvv, false);
    }

    public CardModel(String name, String number, String expiry, String cvv, boolean isPrimary) {
        this.name = name;
        this.number = number;
        this.expiry = expiry;
        this.cvv = cvv;
        this.isPrimary = isPrimary;
    }
}
