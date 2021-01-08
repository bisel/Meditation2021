package com.soulfriends.meditation.viewmodel;

import android.view.View;

import com.soulfriends.meditation.util.ResultListener;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PasswordChangeViewModel extends ViewModel {

    public MutableLiveData<String> strCurPassword;
    public MutableLiveData<String> strNewPassword;
    public MutableLiveData<String> strConfirmPassword;
    private ResultListener listener;

    public void setListener(ResultListener listener) {
        this.listener = listener;
    }

    public PasswordChangeViewModel(String strCurPassword, String strNewPassword, String strConfirmPassword) {

        if(this.strCurPassword == null) {
            this.strCurPassword = new MutableLiveData<>();
            this.strCurPassword.setValue(strCurPassword);
        }

        if(this.strNewPassword == null) {
            this.strNewPassword = new MutableLiveData<>();
            this.strNewPassword.setValue(strNewPassword);
        }

        if(this.strConfirmPassword == null) {
            this.strConfirmPassword = new MutableLiveData<>();
            this.strConfirmPassword.setValue(strConfirmPassword);
        }
    }

    public MutableLiveData<String> getStrCurPassword() {
        return strCurPassword;
    }

    public MutableLiveData<String> getStrNewPassword() {
        return strNewPassword;
    }

    public MutableLiveData<String> getStrConfirmPassword() {
        return strConfirmPassword;
    }

    // 변경하기
    public void OnClicked_Change(View view)
    {
        this.listener.onSuccess(view.getId(), "Success!");
    }
}
