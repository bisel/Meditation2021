package com.soulfriends.meditation.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.soulfriends.meditation.util.ResultListener;

public class PsychologyVoiceTestViewModelFactory extends ViewModelProvider.NewInstanceFactory{
    private ResultListener listener;
    private Context context;

    public PsychologyVoiceTestViewModelFactory(Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;
    }

    // ctrl+o
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new PsychologyVoiceTestViewModel( context, listener);
    }
}
