package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class FriendEditViewModel extends ViewModel {

    private ResultListener listener;
    private Context context;

    private long mLastClickTime = 0;

    public FriendEditViewModel(Context mContext, ResultListener listener) {
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
