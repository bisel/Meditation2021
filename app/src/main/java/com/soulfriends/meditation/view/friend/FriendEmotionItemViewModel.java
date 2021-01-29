package com.soulfriends.meditation.view.friend;

import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class FriendEmotionItemViewModel extends ViewModel {

    private ItemClickListenerExt listener;

    private long mLastClickTime = 0;

    public int emotion_state = 0;

    public UserProfile userProfile;

    public MutableLiveData<String> getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname.setValue(nickname);
    }

    public MutableLiveData<String> nickname = new MutableLiveData<>();

    public FriendEmotionItemViewModel(ItemClickListenerExt listener, UserProfile userProfile, int emotion_state) {

        this.listener = listener;

        this.emotion_state = emotion_state;

        this.userProfile = userProfile;

        setNickname(this.userProfile.nickname);


    }

    public void OnClicked_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.listener.onItemClick(view, userProfile, this.emotion_state);
    }
}
