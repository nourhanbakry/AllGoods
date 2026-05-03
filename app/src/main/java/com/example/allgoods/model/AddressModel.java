package com.example.allgoods.model;

import java.io.Serializable;

public class AddressModel implements Serializable {
    private String id;
    private String name;
    private String country;
    private String city;
    private String phoneNumber;
    private String address;
    private boolean isPrimary;

    public AddressModel() {}

    public AddressModel(String name, String country, String city, String phoneNumber, String address, boolean isPrimary) {
        this.name = name;
        this.country = country;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isPrimary = isPrimary;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean primary) { isPrimary = primary; }
}
