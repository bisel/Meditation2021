package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;


public class UserinfoViewModel extends ViewModel {

    public MutableLiveData<String> nickname = new MutableLiveData<>();

    private ResultListener listener;

    private Context context;

    private long mLastClickTime = 0;

    public UserinfoViewModel(Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;

        nickname.setValue("");
    }

//    public void OnNicknameDoubleClicked(View view)
//    {
//        if (!TextUtils.isEmpty(nickname.getValue())) {
//            this.listener.onSuccess(view.getId(),"Success!");
//        }
//        else
//        {
//            this.listener.onFailure(view.getId(), "Failure!");
//        }
//    }

    public MutableLiveData<String> getNickname() {
        return nickname;
    }

//    public void OnClick_Man(View view) {
//        this.listener.onSuccess(view.getId(), "Success!");
//    }
//
//    public void OnClick_Woman(View view) {
//        this.listener.onSuccess(view.getId(), "Success!");
//    }
//
//    public void OnOKClicked(View view) {
//        this.listener.onSuccess(view.getId(), "Success!");
//    }

    public void OnClick_Select(View view) {

        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.listener.onSuccess(view.getId(), "Success!");
    }
}
