package com.example.allgoods.UI.Customer.Wishlist.Adapter;

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

import com.example.allgoods.Data.repository.Wishlist.WishlistRepository;
import com.example.allgoods.Data.repository.Wishlist.WishlistRepositoryImpl;

public class WishlistProductAdapter extends RecyclerView.Adapter<WishlistProductAdapter.WishlistProductViewHolder> {

    private Context context;
    private List<ProductModel> productList;
    private final WishlistRepository wishlistRepository = new WishlistRepositoryImpl();

    public WishlistProductAdapter(Context context, List<ProductModel> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public WishlistProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.home_product_rv_item, parent, false);
        return new WishlistProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistProductViewHolder holder, int position) {

        ProductModel product = productList.get(position);

        holder.name.setText(product.getName());
        holder.price.setText(String.valueOf(product.getPrice()));

        String imageUrl = (product.getImages() != null && !product.getImages().isEmpty()) 
                ? product.getImages().get(0) 
                : product.getImage();

        Glide.with(context)
                .load(imageUrl)
                .into(holder.image);

        // In wishlist, it's always fav initially
        holder.favIcon.setImageResource(R.drawable.already_fav);

        holder.favIcon.setOnClickListener(v -> {
            wishlistRepository.removeFromWishlist(product.getId(), new WishlistRepository.OnWishlistChangeListener() {
                @Override
                public void onSuccess() {
                    productList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, productList.size());
                }

                @Override
                public void onFailure(String error) {
                    // Handle error
                }
            });
        });

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, ProductDetails.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    public static class WishlistProductViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView image, favIcon;

        public WishlistProductViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
            image = itemView.findViewById(R.id.productImage);
            favIcon = itemView.findViewById(R.id.favIcon);
        }
    }
}
