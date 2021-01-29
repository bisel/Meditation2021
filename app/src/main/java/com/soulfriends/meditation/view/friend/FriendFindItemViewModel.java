package com.soulfriends.meditation.view.friend;

import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class FriendFindItemViewModel extends ViewModel {

    private ItemClickListenerExt listener;

    private long mLastClickTime = 0;

    public int friend_state = 0;

    public String id = "";

    public UserProfile userProfile;

    public FriendFindItemViewModel(ItemClickListenerExt listener, String id, UserProfile userProfile,  int friend_state) {

        this.listener = listener;

        this.friend_state = friend_state;

        this.id = id;

        this.userProfile = userProfile;

    }

    public void OnClicked_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.listener.onItemClick(view, this.id, this.friend_state);
    }
}
