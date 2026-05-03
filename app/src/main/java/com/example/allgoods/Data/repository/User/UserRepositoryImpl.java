package com.example.allgoods.Data.repository.User;

import com.example.allgoods.model.AddressModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private String getUserId() {
        return auth.getUid();
    }

    @Override
    public void saveAddress(AddressModel address, OnAddressChangeListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        if (address.isPrimary()) {
            // Unset other primary addresses first
            firestore.collection("Users")
                    .document(userId)
                    .collection("Addresses")
                    .whereEqualTo("primary", true)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            doc.getReference().update("primary", false);
                        }
                        // Now save the new primary address
                        saveNewAddress(userId, address, listener);
                    })
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        } else {
            saveNewAddress(userId, address, listener);
        }
    }

    private void saveNewAddress(String userId, AddressModel address, OnAddressChangeListener listener) {
        String addressId = address.getId() != null ? address.getId() : firestore.collection("Users").document(userId).collection("Addresses").document().getId();
        address.setId(addressId);

        firestore.collection("Users")
                .document(userId)
                .collection("Addresses")
                .document(addressId)
                .set(address)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void getPrimaryAddress(OnAddressFetchListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        firestore.collection("Users")
                .document(userId)
                .collection("Addresses")
                .whereEqualTo("primary", true)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        AddressModel address = queryDocumentSnapshots.getDocuments().get(0).toObject(AddressModel.class);
                        listener.onSuccess(address);
                    } else {
                        listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void getAllAddresses(OnAddressesFetchListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        firestore.collection("Users")
                .document(userId)
                .collection("Addresses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<AddressModel> addresses = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        addresses.add(doc.toObject(AddressModel.class));
                    }
                    listener.onSuccess(addresses);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
}
