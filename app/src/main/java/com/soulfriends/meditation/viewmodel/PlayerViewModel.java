package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class PlayerViewModel extends ViewModel {

    private ResultListener listener;

    private Context context;
    private MeditationContents meditationContents;


    public MutableLiveData<String> title = new MutableLiveData<>();     // 타이틀
    public MutableLiveData<String> audio = new MutableLiveData<>();     // audio 파일
    public MutableLiveData<String> narrtor = new MutableLiveData<>();   // 나레이터
    public MutableLiveData<String> author = new MutableLiveData<>();    // 저자
    public MutableLiveData<String> artist = new MutableLiveData<>();    // 아티스트

    public MutableLiveData<String> getTimer_time() {
        return timer_time;
    }

    public void setTimer_time(String timer_time) {
        this.timer_time.setValue(timer_time);
    }

    public MutableLiveData<String> timer_time = new MutableLiveData<>();    // 타이머 타임

    public MutableLiveData<String> getNickname_friend() {
        return nickname_friend;
    }

    public void setNickname_friend(String nickname_friend) {
        this.nickname_friend.setValue(nickname_friend);
    }

    public MutableLiveData<String> nickname_friend = new MutableLiveData<>();    // 친구 닉네임

    private long mLastClickTime = 0;

    public PlayerViewModel(MeditationContents meditationContents, Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;
        this.meditationContents = meditationContents;

        title.setValue(meditationContents.title);
        audio.setValue(meditationContents.audio);
        narrtor.setValue(meditationContents.narrtor);
        author.setValue(meditationContents.author);
        artist.setValue(meditationContents.artist);
    }

    public void OnClick_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        //Toast myToast = Toast.makeText(context,"stop success", Toast.LENGTH_SHORT);
        //myToast.show();
        this.listener.onSuccess(view.getId(),"Success!");
    }
}
