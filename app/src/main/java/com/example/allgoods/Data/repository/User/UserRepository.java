package com.example.allgoods.Data.repository.User;

import com.example.allgoods.model.AddressModel;
import java.util.List;

public interface UserRepository {
    void saveAddress(AddressModel address, OnAddressChangeListener listener);
    void getPrimaryAddress(OnAddressFetchListener listener);
    void getAllAddresses(OnAddressesFetchListener listener);

    interface OnAddressChangeListener {
        void onSuccess();
        void onFailure(String error);
    }

    interface OnAddressFetchListener {
        void onSuccess(AddressModel address);
        void onFailure(String error);
    }

    interface OnAddressesFetchListener {
        void onSuccess(List<AddressModel> addresses);
        void onFailure(String error);
    }
}
