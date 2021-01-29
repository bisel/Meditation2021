package com.soulfriends.meditation.view.friend;

import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class FriendEditItemViewModel extends ViewModel {

    private ItemClickListenerExt listener;

    private long mLastClickTime = 0;

    public String id = "";

    public int position = -1;

    public UserProfile userProfile;

    public MutableLiveData<String> getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname.setValue(nickname);
    }

    public MutableLiveData<String> nickname = new MutableLiveData<>();     // 닉네임

    public FriendEditItemViewModel(ItemClickListenerExt listener, int position, UserProfile userProfile) {

        this.listener = listener;

        setNickname(userProfile.nickname);

        this.position = position;

        this.userProfile = userProfile;
    }

    public void OnClicked_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.listener.onItemClick(view, this.userProfile, position);
    }
}
