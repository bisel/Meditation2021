package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class ContentsEditViewModel extends ViewModel {

    private ResultListener listener;
    private Context context;

    private long mLastClickTime = 0;

    public MutableLiveData<String> getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title.setValue(title);
    }

    public MutableLiveData<String> title = new MutableLiveData<>();

    public MutableLiveData<String> getAudio_time() {
        return audio_time;
    }

    public void setAudio_time(String audio_time) {
        this.audio_time.setValue(audio_time);
    }

    public MutableLiveData<String> audio_time = new MutableLiveData<>();

    public ContentsEditViewModel(Context mContext, ResultListener listener) {
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
