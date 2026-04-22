package com.example.allgoods.UI.Customer.MyCards.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allgoods.R;
import com.example.allgoods.model.CardModel;

import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardViewHolder> {

    private List<CardModel> cards;

    public CardsAdapter(List<CardModel> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

        CardModel card = cards.get(position);

        holder.name.setText(card.name);
        holder.number.setText(formatCardNumber(card.number));
        holder.expiry.setText(card.expiry);

        holder.itemView.setOnClickListener(v -> {
            holder.itemView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start();
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView name, number, expiry;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvCardName);
            number = itemView.findViewById(R.id.tvCardNumber);
            expiry = itemView.findViewById(R.id.tvExpiry);
        }
    }

    // فورمات الرقم
    private String formatCardNumber(String number) {
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}
