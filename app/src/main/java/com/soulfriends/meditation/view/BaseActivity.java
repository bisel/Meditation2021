package com.soulfriends.meditation.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.soulfriends.meditation.dlg.TimerDialog;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.player.PlaybackStatus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class BaseActivity extends AppCompatActivity {

    protected boolean bShowTimerPopup = false;

    protected boolean bOnStart = false;

    public boolean GetOnStart(){return bOnStart;}

    @Override
    protected void onStart() {
        super.onStart();

        bShowTimerPopup = false;

        UtilAPI.SetActivityFocus(this);

        if(UtilAPI.s_bShowTimerPopup)
        {

            // 타이머 팝업
            TimerDialog alertDialog = new TimerDialog(this, this);
            alertDialog.setCancelable(false);
            alertDialog.show();
            
            //Intent intent = new Intent(UtilAPI.GetActivity() , TimerDialogActivity.class);
            //UtilAPI.GetActivity().startActivity(intent);

            UtilAPI.s_bShowTimerPopup = false;

            bShowTimerPopup = true;
        }

        bOnStart = true;

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        bOnStart = false;

        EventBus.getDefault().unregister(this);
    }

    public void ChangeActivity(java.lang.Class<?> cls)
    {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        finish();
    }

    @Subscribe
    public void onEvent(String status)
    {
        // 세션으로 이동
        switch (status) {
            case PlaybackStatus.STOPPED_END:
            {

                UtilAPI.s_bEvent_service = false;

                ActivityManager manager = (ActivityManager)this.getSystemService(Activity.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> list = manager.getRunningTasks(1);
                ActivityManager.RunningTaskInfo info = list.get(0);

                String strClassName = info.topActivity.getClassName();

                Intent intent = new Intent(this, SessioinActivity.class);
                if(strClassName.contains("Psychology"))
                {
                    intent.putExtra("isfinish","true");
                }
                else
                {
                    intent.putExtra("isfinish","false");
                }

                startActivity(intent);
            }
            break;
        }

    }
}
