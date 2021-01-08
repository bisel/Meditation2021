package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.model.UserLogin;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class LoginViewModel extends ViewModel {

    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();

    private final UserLogin userLogin;
    private final ResultListener listener;

    public LoginViewModel(UserLogin userLogin, Context context, ResultListener listener) {
        this.userLogin = userLogin;
        this.listener = listener;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void OnClick_Close(View view) {
        listener.onSuccess(view.getId(), "onClick");
    }

    public void OnClick_Google(View view) {
        listener.onSuccess(view.getId(), "onClick");
    }

    public void OnClick_FindPassword(View view) {
        listener.onSuccess(view.getId(), "onClick");
    }

    private long mLastClickTime_1 = 0;
    private long mLastClickTime_2 = 0;
    private long mLastClickTime_3 = 0;

    public void OnClick_Login(View view) {

        if (SystemClock.elapsedRealtime() - mLastClickTime_1 < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime_1 = SystemClock.elapsedRealtime();

        userLogin.setEmail(email.getValue());
        userLogin.setPassword(password.getValue());
        listener.onSuccess(view.getId(), "onClick");
    }

    public void onFocusChange_Email(View view, boolean hasFocus) {

        if (SystemClock.elapsedRealtime() - mLastClickTime_2 < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime_2 = SystemClock.elapsedRealtime();

        if (!hasFocus) {
            userLogin.setEmail(email.getValue());
            if (userLogin.isValidEmail()) {
                //  유효하면 체크 표시
                listener.onSuccess(view.getId(), "onFocus_no");
            } else {
                // 경고
                listener.onFailure(view.getId(), "onFocus_no");
            }
        } else {
            listener.onSuccess(view.getId(), "onFocus_yes");
        }
    }

    public void onFocusChange_Password(View view, boolean hasFocus) {

        if (SystemClock.elapsedRealtime() - mLastClickTime_3 < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime_3 = SystemClock.elapsedRealtime();


        if (!hasFocus) {
            userLogin.setPassword(password.getValue());
            if (userLogin.isValidPassword()) {
                //  유효하면 체크 표시
                listener.onSuccess(view.getId(), "onFocus_no");
            } else {
                // 경고
                listener.onFailure(view.getId(), "onFocus_no");
            }
        } else {
            listener.onSuccess(view.getId(), "onFocus_yes");
        }
    }

    public void onTextChanged_Email(CharSequence s, int start, int before, int count) {

        userLogin.setEmail(email.getValue());
        listener.onSuccess(10, "onTextChanged_Email");
    }

    public void onTextChanged_Password(CharSequence s, int start, int before, int count) {

        userLogin.setPassword(password.getValue());
        listener.onSuccess(11, "onTextChanged_Password");
    }
}
