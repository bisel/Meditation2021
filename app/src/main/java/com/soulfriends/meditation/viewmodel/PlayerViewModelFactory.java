package com.soulfriends.meditation.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.util.ResultListener;



public class PlayerViewModelFactory extends ViewModelProvider.NewInstanceFactory{

    private ResultListener listener;
    private Context context;
    private MeditationContents meditationContents;

    public PlayerViewModelFactory(MeditationContents meditationContents, Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;
        this.meditationContents = meditationContents;
    }

    // ctrl+o
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new PlayerViewModel(meditationContents, context, listener);
    }
}