package com.soulfriends.meditation.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.soulfriends.meditation.util.ResultListener;


public class SessionViewModelFactory extends ViewModelProvider.NewInstanceFactory{

    private ResultListener listener;
    private Context context;
    private String title;

    public SessionViewModelFactory(String title, Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;
        this.title = title;
    }

    // ctrl+o
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new SessionViewModel(title, context, listener);
    }
}
