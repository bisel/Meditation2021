package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class SessionViewModel extends ViewModel {

    private MutableLiveData<String> title = new MutableLiveData<>();
    private ResultListener listener;

    private Context context;

    private long mLastClickTime = 0;

    public SessionViewModel(String title, Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;
        this.title.setValue(title);

        // 추후 contents_Session 내용에 대해서 정보를 받아서 처리를 해야 한다.
    }

    public MutableLiveData<String> getTitle() {
        return title;
    }

    public void setTitle(MutableLiveData<String> title) {
        this.title = title;
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
