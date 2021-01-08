package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class PsychologyCharacterDetailViewModel extends ViewModel {

    public MutableLiveData<String> strTitle = new MutableLiveData<>();
    public MutableLiveData<String> strResult = new MutableLiveData<>();
    public MutableLiveData<String> strNick = new MutableLiveData<>();

    private ResultListener listener;
    private Context context;


    public MutableLiveData<String> getStrNick() {
        return strNick;
    }

    public void setStrNick(String strNick) {
        this.strNick.setValue(strNick);
    }

    public MutableLiveData<String> getTitle() {
        return strTitle;
    }

    public void setStrTitle(String strTitle) {
        this.strTitle.setValue(strTitle);
    }

    public MutableLiveData<String> getStrResult() {
        return strResult;
    }

    public void setStrResult(String strResult) {
        this.strResult.setValue(strResult);
    }

    private long mLastClickTime = 0;

    public PsychologyCharacterDetailViewModel(Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;
    }

    // 닫기 버튼
    public void OnClicked_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.listener.onSuccess(view.getId(), "Success!");
    }
}
