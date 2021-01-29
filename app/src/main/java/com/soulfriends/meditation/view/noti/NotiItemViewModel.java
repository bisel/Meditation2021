package com.soulfriends.meditation.view.noti;

import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.model.MeditationDetailAlarm;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

import android.content.Context;

public class NotiItemViewModel extends ViewModel {

    private ItemClickListenerExt listener;

    private long mLastClickTime = 0;

    public String id = "";

   private Context context;

    public int position = -1;

    public MutableLiveData<String> getContents() {
        return Contents;
    }

    public void setContents(MutableLiveData<String> contents) {
        Contents = contents;
    }

    public MutableLiveData<String> Contents = new MutableLiveData<>();     // 내용

    public MeditationDetailAlarm hAlarm;

    public NotiItemViewModel(Context context, ItemClickListenerExt listener, String id, MeditationDetailAlarm hAlarm) {

        this.listener = listener;
        this.context = context;

        this.id = id;

        this.hAlarm = hAlarm;

        String strText = hAlarm.otheruser.nickname;

        // alarm type : 1 : 간단 알람  2 : 수락,거절 알람
        //
        // alarm subtype
        //  1.  친구 신청 수락 (상대방이 수락)
        //  2.  친구 신청 거절 (상대방이 거절)
        //  3.  감정 공유 수락 (상다방이 나에게 수락)
        //  4.  감정 공유 거절 (상다방이 나에게 거절)
        //
        //  1 . 친구 신청 신청 (상대방이 나에게 신청)
        //  2.  감정 공유 신청 (상대방이 나에게 신청)
        if(hAlarm.entity.alarmtype == 1)
        {
            switch(hAlarm.entity.alarmsubtype)
            {
                case 1:
                {
                    strText += context.getResources().getString(R.string.noti_a_0);
                }
                break;
                case 2:
                {
                    strText += context.getResources().getString(R.string.noti_a_1);
                }
                break;
                case 3:
                {
                    strText += context.getResources().getString(R.string.noti_a_2);
                }
                break;
                case 4:
                {
                    strText += context.getResources().getString(R.string.noti_a_3);
                }
                break;
            }
        }
        else if(hAlarm.entity.alarmtype == 2)
        {
            switch(hAlarm.entity.alarmsubtype)
            {
                case 1:
                {
                    strText += context.getResources().getString(R.string.noti_b_1);

                }
                break;
                case 2:
                {
                    strText += context.getResources().getString(R.string.noti_b_0);
                }
                break;
            }
        }

        Contents.setValue(strText);
    }

    public void OnClicked_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.listener.onItemClick(view, hAlarm, position);
    }
}
