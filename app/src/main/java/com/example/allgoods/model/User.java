package com.example.allgoods.model;

public class User {

    public String name;
    public String email;
    public String role;
    public String uid;

    public User() {}

    public User(String name, String email, String role,String uid) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.uid = uid;
    }
}