package com.example.allgoods.Data.repository.Card;

import com.example.allgoods.model.CardModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CardRepositoryImpl implements CardRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private String getUserId() {
        return auth.getUid();
    }

    @Override
    public void saveCard(CardModel card, OnCardChangeListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        if (card.isPrimary) {
            // Unset other primary cards first
            firestore.collection("Users")
                    .document(userId)
                    .collection("Cards")
                    .whereEqualTo("primary", true)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            doc.getReference().update("primary", false);
                        }
                        performSaveCard(userId, card, listener);
                    })
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        } else {
            performSaveCard(userId, card, listener);
        }
    }

    private void performSaveCard(String userId, CardModel card, OnCardChangeListener listener) {
        String cardId = card.id != null ? card.id : firestore.collection("Users").document(userId).collection("Cards").document().getId();
        card.id = cardId;

        firestore.collection("Users")
                .document(userId)
                .collection("Cards")
                .document(cardId)
                .set(card)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void getCards(OnCardsFetchListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        firestore.collection("Users")
                .document(userId)
                .collection("Cards")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CardModel> cards = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        CardModel card = doc.toObject(CardModel.class);
                        card.id = doc.getId();
                        cards.add(card);
                    }
                    listener.onSuccess(cards);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void getPrimaryCard(OnCardFetchListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        firestore.collection("Users")
                .document(userId)
                .collection("Cards")
                .whereEqualTo("primary", true)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        CardModel card = queryDocumentSnapshots.getDocuments().get(0).toObject(CardModel.class);
                        listener.onSuccess(card);
                    } else {
                        listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    @Override
    public void setPrimaryCard(String cardId, OnCardChangeListener listener) {
        String userId = getUserId();
        if (userId == null) {
            listener.onFailure("User not logged in");
            return;
        }

        // 1. Unset all
        firestore.collection("Users")
                .document(userId)
                .collection("Cards")
                .whereEqualTo("primary", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().update("primary", false);
                    }
                    // 2. Set new primary
                    firestore.collection("Users")
                            .document(userId)
                            .collection("Cards")
                            .document(cardId)
                            .update("primary", true)
                            .addOnSuccessListener(aVoid -> listener.onSuccess())
                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
}
