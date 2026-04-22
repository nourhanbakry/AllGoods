package com.example.allgoods.UI.Customer.Wishlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.allgoods.model.ProductModel;
import com.example.allgoods.utils.Category;

import java.util.ArrayList;
import java.util.List;

public class WishlistViewModel extends ViewModel {
    private final MutableLiveData<List<ProductModel>> products = new MutableLiveData<>();

    public LiveData<List<ProductModel>> getProducts() {
        return products;
    }

    public void loadProducts() {

        List<ProductModel> list = new ArrayList<>();

        list.add(new ProductModel(
                1,
                "Nike Sportswear Club Fleece",
                "https://png.pngtree.com/png-vector/20210602/ourmid/pngtree-3d-beauty-cosmetics-product-design-png-image_3350323.jpg",
                99.0,
                Category.PANTS,
                true
        ));

        list.add(new ProductModel(
                2,
                "Trail Running Jacket Nike",
                "https://png.pngtree.com/png-vector/20210602/ourmid/pngtree-3d-beauty-cosmetics-product-design-png-image_3350323.jpg",
                120.0,
                Category.PANTS,
                true

        ));

        list.add(new ProductModel(
                3,
                "Nike Sportswear Club Fleece",
                "https://cdn.isa-aydin.com/wp-content/uploads/2023/05/creative-volume-swatch-makeup-highlighter-dark-golden.jpg.jpg",
                99.0,
                Category.PANTS,
                true

        ));
        list.add(new ProductModel(
                4,
                "Trail Running Jacket Nike",
                "https://png.pngtree.com/png-vector/20210602/ourmid/pngtree-3d-beauty-cosmetics-product-design-png-image_3350323.jpg",
                120.0,
                Category.PANTS,
                true

        ));

        list.add(new ProductModel(
                5,
                "Nike Sportswear Club Fleece",
                "https://images.rawpixel.com/image_800/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDIyLTExL3BmLXMxMDgtcG0tNDExMy1tb2NrdXAuanBn.jpg",
                99.0,
                Category.PANTS,
                true

        ));



        products.setValue(list);
    }
}
