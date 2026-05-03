package com.example.allgoods.UI.Customer.Categories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.allgoods.model.ProductModel;
import com.example.allgoods.utils.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoriesViewModel extends ViewModel {

    private final MutableLiveData<List<ProductModel>> products = new MutableLiveData<>();

//    public LiveData<List<ProductModel>> getProductsByCategory(String category) {
//        MutableLiveData<List<ProductModel>> filtered = new MutableLiveData<>();
//
//        List<ProductModel> all = getProducts().getValue();
//        List<ProductModel> result = new ArrayList<>();
//
//        if (all != null) {
//            for (ProductModel product : all) {
//                if (product.getCategory().equals(category)) {
//                    result.add(product);
//                }
//            }
//        }
//
//        filtered.setValue(result);
//        return filtered;
//    }

    public LiveData<List<ProductModel>> getProducts() {
        return products;
    }

    public void loadProducts() {

        List<ProductModel> list = new ArrayList<>();

        list.add(new ProductModel(
                1,
                "Nike Sportswear Club Fleece",
                Arrays.asList("https://png.pngtree.com/png-vector/20210602/ourmid/pngtree-3d-beauty-cosmetics-product-design-png-image_3350323.jpg"),
                99.0,
                Category.PANTS
        ));

        list.add(new ProductModel(
                2,
                "Trail Running Jacket Nike",
                Arrays.asList("https://png.pngtree.com/png-vector/20210602/ourmid/pngtree-3d-beauty-cosmetics-product-design-png-image_3350323.jpg"),
                120.0,
                Category.PANTS
        ));

        list.add(new ProductModel(
                3,
                "Nike Sportswear Club Fleece",
                Arrays.asList("https://png.pngtree.com/png-vector/20210602/ourmid/pngtree-3d-beauty-cosmetics-product-design-png-image_3350323.jpg"),
                99.0,
                Category.PANTS
        ));
        list.add(new ProductModel(
                4,
                "Trail Running Jacket Nike",
                Arrays.asList("https://png.pngtree.com/png-vector/20210602/ourmid/pngtree-3d-beauty-cosmetics-product-design-png-image_3350323.jpg"),
                120.0,
                Category.PANTS
        ));

        list.add(new ProductModel(
                5,
                "Nike Sportswear Club Fleece",
                Arrays.asList("https://png.pngtree.com/png-vector/20210602/ourmid/pngtree-3d-beauty-cosmetics-product-design-png-image_3350323.jpg"),
                99.0,
                Category.PANTS
        ));



        products.setValue(list);
    }
}
