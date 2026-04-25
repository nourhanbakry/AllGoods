package com.example.allgoods.Data.repository.Auth;

import com.example.allgoods.model.User;

public interface AuthRepository {

    void signUp(String name, String email, String password, AuthCallback callback);

    void login(String email, String password, AuthCallback callback);

    void saveUserLocally(String name, String email, String role);

    void logout();

    interface AuthCallback {
        void onSuccess(User user);
        void onFailure(String error);
    }
}

