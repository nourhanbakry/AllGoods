package com.example.allgoods.UI.Customer.Home.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.allgoods.R;
import com.example.allgoods.UI.Customer.ProductDetails.ProductDetails;
import com.example.allgoods.model.ProductModel;
import com.example.allgoods.utils.PriceUtils;

import java.util.List;

import com.example.allgoods.Data.repository.Wishlist.WishlistRepository;
import com.example.allgoods.Data.repository.Wishlist.WishlistRepositoryImpl;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductModel> productList;
    private final WishlistRepository wishlistRepository = new WishlistRepositoryImpl();

    public ProductAdapter(Context context, List<ProductModel> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.home_product_rv_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        ProductModel product = productList.get(position);

        holder.name.setText(product.getName());
        holder.price.setText("$" + PriceUtils.formatPrice(product.getPrice()));
        holder.ratingBar.setRating(product.getRating());
        holder.reviewCount.setText("(" + product.getReviewCount() + ")");

        String imageUrl = (product.getImages() != null && !product.getImages().isEmpty()) 
                ? product.getImages().get(0) 
                : product.getImage();

        Glide.with(context)
                .load(imageUrl)
                .into(holder.image);

        // Check if product is favorite
        wishlistRepository.isFavorite(product.getId(), isFavorite -> {
            product.setFav(isFavorite);
            if (isFavorite) {
                holder.favIcon.setImageResource(R.drawable.already_fav);
            } else {
                holder.favIcon.setImageResource(R.drawable.fav_icon);
            }
        });

        holder.favIcon.setOnClickListener(v -> {
            boolean newFavStatus = !product.isFav();
            product.setFav(newFavStatus);
            
            if (newFavStatus) {
                wishlistRepository.addToWishlist(product, new WishlistRepository.OnWishlistChangeListener() {
                    @Override
                    public void onSuccess() {
                        holder.favIcon.setImageResource(R.drawable.already_fav);
                    }

                    @Override
                    public void onFailure(String error) {
                        product.setFav(false);
                        holder.favIcon.setImageResource(R.drawable.fav_icon);
                    }
                });
            } else {
                wishlistRepository.removeFromWishlist(product.getId(), new WishlistRepository.OnWishlistChangeListener() {
                    @Override
                    public void onSuccess() {
                        holder.favIcon.setImageResource(R.drawable.fav_icon);
                    }

                    @Override
                    public void onFailure(String error) {
                        product.setFav(true);
                        holder.favIcon.setImageResource(R.drawable.already_fav);
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context,ProductDetails.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, reviewCount;
        ImageView image, favIcon;
        RatingBar ratingBar;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
            image = itemView.findViewById(R.id.productImage);
            favIcon = itemView.findViewById(R.id.favIcon);
            ratingBar = itemView.findViewById(R.id.productRating);
            reviewCount = itemView.findViewById(R.id.productReviewCount);
        }
    }
}