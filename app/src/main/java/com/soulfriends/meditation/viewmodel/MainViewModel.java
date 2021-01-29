package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class MainViewModel extends ViewModel {

    public MutableLiveData<String> getTitle() {
        return title;
    }

    public void setTitle(MutableLiveData<String> title) {
        this.title = title;
    }
    public void setTitleTo(String title) {
        this.title.setValue(title);
    }

    private MutableLiveData<String> title = new MutableLiveData<>();
    private ResultListener listener;

    public MutableLiveData<String> getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm.setValue(alarm);
    }

    private MutableLiveData<String> alarm = new MutableLiveData<>();

    private Context context;

    private long mLastClickTime = 0;

    public MainViewModel( Context mContext, ResultListener listener) {
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
