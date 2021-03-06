package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class MyContentsViewModel extends ViewModel {

    private ResultListener listener;
    private Context context;

    private MutableLiveData<String> title = new MutableLiveData<>();

    private long mLastClickTime = 0;

    public MyContentsViewModel(Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;
    }

    public MutableLiveData<String> getTitle() {
        return title;
    }

    public void setTitle(String title)
    {
        this.title.setValue(title);
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
