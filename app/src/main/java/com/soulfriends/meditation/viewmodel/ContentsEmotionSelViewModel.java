package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class ContentsEmotionSelViewModel extends ViewModel {

    private ResultListener listener;
    private Context context;

    private long mLastClickTime = 0;

    public View getView() {
        return view;
    }

    private View view;

    public MutableLiveData<String> getContents_state() {
        return contents_state;
    }

    public void setContents_state(String contents_state) {
        this.contents_state.setValue(contents_state);
    }

    public MutableLiveData<String> contents_state = new MutableLiveData<>();


    public ContentsEmotionSelViewModel(Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;
    }

    public void OnClick_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.view = view;
        this.listener.onSuccess(view.getId(),"Success!");
    }
}
