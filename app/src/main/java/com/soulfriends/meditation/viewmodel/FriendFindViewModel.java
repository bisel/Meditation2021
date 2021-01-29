package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class FriendFindViewModel extends ViewModel {

    private ResultListener listener;
    private Context context;

    private long mLastClickTime = 0;

    public MutableLiveData<String> getInputword() {
        return inputword;
    }

    public void setInputword(MutableLiveData<String> inputword) {
        this.inputword = inputword;
    }

    public MutableLiveData<String> inputword = new MutableLiveData<>();

    public FriendFindViewModel(Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;
    }

    public void OnClick_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.listener.onSuccess(view.getId(),"Success!");
    }
}
