package com.soulfriends.meditation.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.util.ResultListener;


public class ProfileViewModelFactory extends ViewModelProvider.NewInstanceFactory{

    private ResultListener listener;
    private Context context;
    private UserProfile userProfile;

    public ProfileViewModelFactory(UserProfile userProfile, Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;
        this.userProfile = userProfile;
    }

    // ctrl+o
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new ProfileViewModel(userProfile, context, listener);
    }
}