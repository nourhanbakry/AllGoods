package com.example.allgoods.UI.Auth.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.allgoods.Data.repository.Auth.AuthRepository;
import com.example.allgoods.model.User;
import com.example.allgoods.utils.Result;

public class LoginViewModel extends ViewModel {
    private AuthRepository repository;
    public LoginViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    private MutableLiveData<Result<User>> loginState = new MutableLiveData<>();

    public LiveData<Result<User>> getLoginState() {
        return loginState;
    }

    public void login(String email, String password) {

        loginState.setValue(Result.loading());

        repository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                loginState.postValue(Result.success(user));
            }

            @Override
            public void onFailure(String message) {
                loginState.postValue(Result.error(message));
            }
        });
    }
}
