package com.example.allgoods.UI.Auth.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.allgoods.Data.repository.Auth.AuthRepository;
import com.example.allgoods.model.User;
import com.example.allgoods.utils.Result;


public class SignUpViewModel extends ViewModel {
    private AuthRepository repository;
    public SignUpViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    private MutableLiveData<Result<User>> signUpState = new MutableLiveData<>();
    public LiveData<Result<User>> getSignUpState() {
        return signUpState;
    }

    public void signUp(String name, String email, String password) {

        signUpState.setValue(Result.loading());

        repository.signUp(name, email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                signUpState.setValue(Result.success(user));
            }

            @Override
            public void onFailure(String error) {
                signUpState.setValue(Result.error(error));
            }
        });
    }


    public void saveUserIfNeeded(boolean rememberMe, String name, String email) {
        if (rememberMe) {
            repository.saveUserLocally(name, email,"customer");
        }
    }
}
