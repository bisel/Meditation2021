package com.soulfriends.meditation.viewmodel;

import android.content.Context;

import com.soulfriends.meditation.model.UserLogin;
import com.soulfriends.meditation.util.ResultListener;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LoginViewModelFactory extends ViewModelProvider.NewInstanceFactory{

    private UserLogin userLogin;
    private Context context;
    private ResultListener listener;


    public LoginViewModelFactory(UserLogin userLogin, Context context, ResultListener listener) {
        this.userLogin = userLogin;
        this.context = context;
        this.listener = listener;
    }

    // ctrl+o
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new LoginViewModel(userLogin, context, listener);
    }
}
