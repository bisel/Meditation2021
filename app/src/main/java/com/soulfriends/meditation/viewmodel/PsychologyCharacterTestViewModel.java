package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.util.ResultListener;

public class PsychologyCharacterTestViewModel extends ViewModel {

    private ResultListener listener;
    private Context context;
    private long mLastClickTime = 0;

    public MutableLiveData<String> getTitle() {
        return title;
    }

    public void setTitle(MutableLiveData<String> title) {
        this.title = title;
    }

    public void setTitle_data(String title) {
        this.title.setValue(title);
    }

    public void setText_1_data(String text_1) {
        this.text_1.setValue(text_1);
    }
    public void setText_2_data(String text_2) {
        this.text_2.setValue(text_2);
    }
    public void setText_3_data(String text_3) {
        this.text_3.setValue(text_3);
    }

    public MutableLiveData<String> getText_1() {
        return text_1;
    }

    public void setText_1(MutableLiveData<String> text_1) {
        this.text_1 = text_1;
    }

    public MutableLiveData<String> getText_2() {
        return text_2;
    }

    public void setText_2(MutableLiveData<String> text_2) {
        this.text_2 = text_2;
    }

    public MutableLiveData<String> getText_3() {
        return text_3;
    }

    public void setText_3(MutableLiveData<String> text_3) {
        this.text_3 = text_3;
    }

    public MutableLiveData<String> title = new MutableLiveData<>();     // title
    public MutableLiveData<String> text_1 = new MutableLiveData<>();    // text_1
    public MutableLiveData<String> text_2 = new MutableLiveData<>();    // text_2
    public MutableLiveData<String> text_3= new MutableLiveData<>();     // text_3


    public MutableLiveData<String> getStrButtontext() {
        return strButtontext;
    }

    public void setStrButtontext(String strButtontext) {
        this.strButtontext.setValue(strButtontext);
    }

    public MutableLiveData<String> strButtontext= new MutableLiveData<>();     // button text


    public PsychologyCharacterTestViewModel(Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;
    }

    public void OnClicked_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 100){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.view = view;
        this.listener.onSuccess(view.getId(), "Success!");
    }

    public Button getView() {
        return (Button)view;
    }

    private View view;
}
