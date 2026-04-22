package com.example.allgoods.UI.Customer.Cart.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.allgoods.databinding.ItemInCartBinding;
import com.example.allgoods.model.ProductModel;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    List<ProductModel> list;
    private Context context;


    public CartAdapter(Context context, List<ProductModel> list) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInCartBinding binding = ItemInCartBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductModel item = list.get(position);

        holder.binding.cartProductName.setText(item.getName());
        holder.binding.cartProductPrice.setText("$ " + item.getPrice());
        holder.binding.cartProductQuantity.setText(String.valueOf(item.getQuantity()));

        Glide.with(holder.itemView.getContext())
                .load(item.getImage())
                .into(holder.binding.cartProductImage);

        holder.binding.increaseProductsQuantity.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
        });

        holder.binding.decreaseProductsQuantity.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
            }
        });

        holder.binding.deleteBtn.setOnClickListener(v -> {
            list.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateData(List<ProductModel> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }


    class CartViewHolder extends RecyclerView.ViewHolder {
        ItemInCartBinding binding;

        public CartViewHolder(ItemInCartBinding itemInCartBinding) {
            super(itemInCartBinding.getRoot());
            binding = itemInCartBinding;
        }
    }
}