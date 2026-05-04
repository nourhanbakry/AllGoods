package com.example.allgoods.Data.repository.Auth;

import android.content.Context;

import com.example.allgoods.Data.local.SharedPrefManager;
import com.example.allgoods.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthRepositoryImpl implements AuthRepository{

    private FirebaseAuth firebaseAuth;
    private SharedPrefManager prefManager;
    private FirebaseFirestore firestore;

    public AuthRepositoryImpl(Context context) {
        firebaseAuth = FirebaseAuth.getInstance();
        prefManager = new SharedPrefManager(context);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void signUp(String name, String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            com.google.firebase.auth.UserProfileChangeRequest profileUpdates =
                                    new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                            firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        User user = new User(
                                                name,
                                                email,
                                                "customer",
                                                firebaseUser.getUid()
                                        );

                                        // Save user to Firestore
                                        firestore.collection("Users")
                                                .document(user.uid)
                                                .set(user)
                                                .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                                    });
                        }
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
                        if (firebaseUser != null) {
                            firestore.collection("Users")
                                    .document(firebaseUser.getUid())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            User user = documentSnapshot.toObject(User.class);
                                            callback.onSuccess(user);
                                        } else {
                                            callback.onFailure("User not found");
                                        }
                                    })
                                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }
    /*@Override
    public void login(String email, String password, AuthCallback callback) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        String role = "customer";

                        if (email.equals("admin@gmail.com")) {
                            role = "seller";
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
*/
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
