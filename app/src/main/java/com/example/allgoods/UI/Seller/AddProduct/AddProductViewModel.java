package com.example.allgoods.UI.Seller.AddProduct;

import androidx.lifecycle.ViewModel;
import com.example.allgoods.model.ProductModel;
import android.net.Uri;
import androidx.lifecycle.MutableLiveData;
import com.example.allgoods.Data.repository.SellerProduct.ProductRepository;
import com.example.allgoods.Data.repository.SellerProduct.ProductRepositoryImpl;

import java.util.List;

public class AddProductViewModel extends ViewModel {
    private final ProductRepository repository = new ProductRepositoryImpl();
    public MutableLiveData<String> uploadStatus = new MutableLiveData<>();

    public void saveProduct(ProductModel product, List<Uri> imageUris) {
        if (imageUris == null || imageUris.isEmpty()) {
            uploadStatus.postValue("Please select at least one image");
            return;
        }

        repository.uploadProduct(product, imageUris, new ProductRepository.OnProductUploadListener() {
            @Override
            public void onSuccess() {
                uploadStatus.postValue("Success");
            }

            @Override
            public void onFailure(String error) {
                uploadStatus.postValue(error);
            }
        });
    }
}