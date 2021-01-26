package com.soulfriends.meditation.view.friend;

import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

public class FriendEmotionItemViewModel extends ViewModel {

    private ItemClickListenerExt listener;

    private long mLastClickTime = 0;

    public int emotion_state = 0;

    public String id = "";

    public FriendEmotionItemViewModel(ItemClickListenerExt listener, String id, int emotion_state) {

        this.listener = listener;

        this.emotion_state = emotion_state;

        this.id = id;

    }

    public void OnClicked_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.listener.onItemClick(view, this.id, this.emotion_state);
    }
}
