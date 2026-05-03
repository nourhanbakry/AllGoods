package com.example.allgoods.Data.repository.Wishlist;

import com.example.allgoods.model.ProductModel;
import java.util.List;

public interface WishlistRepository {
    void addToWishlist(ProductModel product, OnWishlistChangeListener listener);
    void removeFromWishlist(String productId, OnWishlistChangeListener listener);
    void getWishlist(OnWishlistFetchListener listener);
    void isFavorite(String productId, OnFavoriteCheckListener listener);

    interface OnWishlistChangeListener {
        void onSuccess();
        void onFailure(String error);
    }

    interface OnWishlistFetchListener {
        void onSuccess(List<ProductModel> products);
        void onFailure(String error);
    }

    interface OnFavoriteCheckListener {
        void onResult(boolean isFavorite);
    }
}
