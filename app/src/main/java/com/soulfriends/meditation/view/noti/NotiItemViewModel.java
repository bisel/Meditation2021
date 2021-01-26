package com.soulfriends.meditation.view.noti;

import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;

import android.content.Context;

public class NotiItemViewModel extends ViewModel {

    private ItemClickListenerExt listener;

    private long mLastClickTime = 0;

    public int main_type = 0;
    public int sub_type = 0;

    public String nickName = "";
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

    public NotiItemViewModel(Context context, ItemClickListenerExt listener, String id, String nickName, int main_type, int sub_type) {

        this.listener = listener;
        this.context = context;

        this.id = id;

        this.main_type = main_type;
        this.sub_type = sub_type;

        this.nickName = nickName;

        String strText = nickName;

        if(main_type == 0)
        {
            switch(sub_type)
            {
                case 0:
                {
                    strText += context.getResources().getString(R.string.noti_a_0);
                }
                break;
                case 1:
                {
                    strText += context.getResources().getString(R.string.noti_a_1);
                }
                break;
                case 2:
                {
                    strText += context.getResources().getString(R.string.noti_a_2);
                }
                break;
                case 3:
                {
                    strText += context.getResources().getString(R.string.noti_a_3);
                }
                break;
            }
        }
        else
        {
            switch(sub_type)
            {
                case 0:
                {
                    strText += context.getResources().getString(R.string.noti_b_0);
                }
                break;
                case 1:
                {
                    strText += context.getResources().getString(R.string.noti_b_1);
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

        this.listener.onItemClick(view, this.id, position);
    }
}
