package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class ContentsinfoViewModel extends ViewModel {

    private ResultListener listener;
    private Context context;

    public MutableLiveData<String> title = new MutableLiveData<>(); // 타이틀
    public MutableLiveData<String> intro = new MutableLiveData<>(); // 콘텐츠 상세
    public MutableLiveData<String> publisher = new MutableLiveData<>(); // 제작사
    public MutableLiveData<String> publisherintro = new MutableLiveData<>(); // 제작사 소개

    private long mLastClickTime = 0;

    public ContentsinfoViewModel(MeditationContents meditationContents, Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;

        title.setValue(meditationContents.title);
        intro.setValue(meditationContents.intro);
        publisher.setValue(meditationContents.publisher);
        publisherintro.setValue(meditationContents.publisherintro);
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
