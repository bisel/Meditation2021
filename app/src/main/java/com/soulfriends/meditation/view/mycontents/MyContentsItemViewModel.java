package com.soulfriends.meditation.view.mycontents;

import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.UtilAPI;

public class MyContentsItemViewModel extends ViewModel {

    public MeditationContents meditationContents;

    public MutableLiveData<String> playtime = new MutableLiveData<>();
    public MutableLiveData<String> title = new MutableLiveData<>();

    private long mLastClickTime = 0;

    private ItemClickListenerExt listener;


    public MyContentsItemViewModel(MeditationContents entity_data, int category_subtype, ItemClickListenerExt listener) {

        this.listener = listener;

        if(entity_data == null)
        {
            // 새로 추가할 항목

            meditationContents = null;

        }
        else {

            // 내가 만든 콘텐츠 항목
            meditationContents = entity_data;

            title.setValue(entity_data.title);

            // 초
            float play_time_second = Float.valueOf(entity_data.playtime);
            float calc_minute = play_time_second / 60.0f;
            float final_minute = Math.round(calc_minute);
            int minute = (int) final_minute;

            String str_play_time = "";
            if (minute == 0) {
                str_play_time = "1분";
            } else {
                str_play_time = String.valueOf(minute) + "분";
            }
            playtime.setValue(str_play_time);
        }

    }

    public void OnClicked_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.listener.onItemClick(view,  (Object)this.meditationContents, 0);
    }
}
