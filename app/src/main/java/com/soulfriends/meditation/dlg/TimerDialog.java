package com.soulfriends.meditation.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.util.ActivityStack;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.MainActivity;
import com.soulfriends.meditation.view.player.AudioPlayer;

import java.util.ArrayList;

public class TimerDialog extends Dialog {

    private Activity activity;
    private Context context;

    public TimerDialog(@NonNull Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    private void exitProgram() {
        // 종료

        // 태스크를 백그라운드로 이동
        // moveTaskToBack(true);

        ActivityCompat.finishAffinity(UtilAPI.scanForActivity(context));
        //Activity.finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timer);


        //  배경음악 플레이
        if (AudioPlayer.instance() != null) {
            AudioPlayer.instance().update();
        }

        // ok
        ImageView iv_ok = findViewById(R.id.iv_ok);
        iv_ok.setOnClickListener(v -> {

            if(ActivityStack.instance().Peek().equals("PlayerActivity"))
            {
                ActivityStack.instance().OnBack(this.activity);
            }
            else if(ActivityStack.instance().Peek().equals("TimerActivity"))
            {
                ActivityStack.instance().OnBack(this.activity);
                ActivityStack.instance().OnBack(this.activity);
            }
            else
            {

                // 팝업 닫기만 한다.
            }

//            activity.finish();
//
//            // player 이면 메인 액티비티로
//            // timer activity /  contentsinfo activity 일 경우에는 홈으로 이동 처리를 한다.
//
//            ArrayList<Activity> ListActivity = UtilAPI.GetActivityInPlayerList();
//            if(ListActivity.size() > 0)
//            {
//                // 홈으로 이동
//                Intent intent = new Intent(UtilAPI.GetActivity(), MainActivity.class);
//                UtilAPI.GetActivity().startActivity(intent);
//
//                for(int i = 0; i < ListActivity.size(); i++)
//                {
//                    ListActivity.get(i).finish();
//                }
//
//                UtilAPI.ClearActivityInPlayerList();
//            }

            this.dismiss();

        });

    }
}
