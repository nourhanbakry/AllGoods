package com.example.allgoods.UI.Seller.Inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allgoods.R;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    List<String> list;

    OnDeleteClickListener listener;
    public InventoryAdapter(List<String> list, OnDeleteClickListener listener) {
        this.listener = listener;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.deleteBtn.setOnClickListener(v -> {

            if (listener != null) {

                int currentPosition = holder.getAdapterPosition();

                if (currentPosition != RecyclerView.NO_POSITION) {

                    listener.onDeleteClick(currentPosition);

                }

            }

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView deleteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteBtn = itemView.findViewById(R.id.imgDelete);
        }

    }

    public interface OnDeleteClickListener {

        void onDeleteClick(int position);

    }
}
