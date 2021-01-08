package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class PsychologyVoiceTestViewModel extends ViewModel {

    private ResultListener listener;
    private Context context;
    private long mLastClickTime = 0;

    public MutableLiveData<String> getStrSentence() {
        return strSentence;
    }

//    public void setStrSentence(MutableLiveData<String> strSentence) {
//        this.strSentence = strSentence;
//    }

    public void setStrSentence(String strSentence) {
        this.strSentence.setValue(strSentence);
    }

    public MutableLiveData<String> strSentence = new MutableLiveData<>();

    public MutableLiveData<String> getStrTimer() {
        return strTimer;
    }

    public void setStrTimer(String strTimer) {
        this.strTimer.setValue(strTimer);;
    }

    public MutableLiveData<String> strTimer = new MutableLiveData<>();

    public PsychologyVoiceTestViewModel(Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;
    }

    public void OnClicked_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.listener.onSuccess(view.getId(), "Success!");
    }
}
