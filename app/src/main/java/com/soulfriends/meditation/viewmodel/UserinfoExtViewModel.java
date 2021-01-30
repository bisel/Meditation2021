package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class UserinfoExtViewModel extends ViewModel {


    public MutableLiveData<String> nickname = new MutableLiveData<>();
    public MutableLiveData<String> introduction = new MutableLiveData<>();
    private ResultListener listener;

    private Context context;

    private long mLastClickTime = 0;

    public UserinfoExtViewModel(Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;

        nickname.setValue("");
    }

    public MutableLiveData<String> getNickname() {
        return nickname;
    }

    public MutableLiveData<String> getIntroduction() {
        return introduction;
    }

    public void setNickname(String nickname) {
        this.nickname.setValue(nickname);
    }

    public void setIntroduction(String introduction) {
        this.introduction.setValue(introduction);
    }


    public void OnClick_Select(View view) {

        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.listener.onSuccess(view.getId(), "Success!");
    }
}
