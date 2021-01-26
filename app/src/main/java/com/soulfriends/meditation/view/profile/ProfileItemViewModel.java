package com.soulfriends.meditation.view.profile;

import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.util.UtilAPI;

public class ProfileItemViewModel extends ViewModel {

    public MeditationContents meditationContents;

    public MutableLiveData<String> playtime = new MutableLiveData<>();
    public MutableLiveData<String> title = new MutableLiveData<>();

    private long mLastClickTime = 0;

    private ItemClickListener listener;

    public ProfileItemViewModel(MeditationContents entity_data, int category_subtype, ItemClickListener listener) {

        meditationContents = entity_data;

        this.listener = listener;

    }

    public void OnClicked_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.listener.onItemClick(view, 0);
    }
}
