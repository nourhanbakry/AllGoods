package com.example.allgoods.UI.Customer.Categories.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.allgoods.R;
import com.example.allgoods.UI.Customer.ProductDetails.ProductDetails;
import com.example.allgoods.model.ProductModel;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    private Context context;
    private List<ProductModel> productList;

    public CategoriesAdapter(Context context, List<ProductModel> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.home_product_rv_item, parent, false);
        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {

        ProductModel product = productList.get(position);

        holder.name.setText(product.getName());
        holder.price.setText(String.valueOf(product.getPrice()));

        Glide.with(context)
                .load(product.getImage())
                .into(holder.image);

        if (product.isFav()) {
            holder.favIcon.setImageResource(R.drawable.already_fav);
        } else {
            holder.favIcon.setImageResource(R.drawable.fav_icon);
        }

        holder.favIcon.setOnClickListener(v -> {
            product.setFav(!product.isFav());
            notifyItemChanged(position);
        });

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, ProductDetails.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    public static class CategoriesViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView image, favIcon;

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
            image = itemView.findViewById(R.id.productImage);
            favIcon = itemView.findViewById(R.id.favIcon);
        }
    }
}
