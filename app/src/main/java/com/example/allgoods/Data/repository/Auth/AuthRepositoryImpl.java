package com.example.allgoods.Data.repository.Auth;

import android.content.Context;

import com.example.allgoods.Data.local.SharedPrefManager;
import com.example.allgoods.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepositoryImpl implements AuthRepository{

    private FirebaseAuth firebaseAuth;
    private SharedPrefManager prefManager;

    public AuthRepositoryImpl(Context context) {
        firebaseAuth = FirebaseAuth.getInstance();
        prefManager = new SharedPrefManager(context);
    }

    @Override
    public void signUp(String name, String email, String password, AuthCallback callback) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        User user = new User(
                                name,
                                email,
                                "customer",
                                firebaseUser != null ? firebaseUser.getUid() : ""
                        );

                        callback.onSuccess(user);

                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    @Override
    public void login(String email, String password, AuthCallback callback) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        String role;

                        if (email.equals("admin@gmail.com")) {
                            role = "seller";
                        } else {
                            role = "customer";
                        }

                        User user = new User(
                                firebaseUser.getDisplayName(),
                                email,
                                role,
                                firebaseUser.getUid()
                        );


                        callback.onSuccess(user);

                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    @Override
    public void saveUserLocally(String name, String email,String role) {
        prefManager.saveUser(name, email, role);
    }


    @Override
    public void logout() {
        firebaseAuth.signOut();
        prefManager.logout();
    }

}
