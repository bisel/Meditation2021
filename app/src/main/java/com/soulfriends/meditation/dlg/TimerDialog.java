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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timer);


//        //  배경음악 플레이
//        if (AudioPlayer.instance() != null) {
//            AudioPlayer.instance().update();
//        }

        // ok
        ImageView iv_ok = findViewById(R.id.iv_ok);
        iv_ok.setOnClickListener(v -> {

            if(ActivityStack.instance().Peek() != null) {
                if (ActivityStack.instance().Peek().equals("PlayerActivity")) {
                    ActivityStack.instance().OnBack(this.activity);
                } else if (ActivityStack.instance().Peek().equals("TimerActivity")) {
                    ActivityStack.instance().OnBack(this.activity);
                    ActivityStack.instance().OnBack(this.activity);
                } else {

                    activity.finish();
                    activity.overridePendingTransition(0, 0);
                }
            }
            else
            {

                activity.finish();
                activity.overridePendingTransition(0, 0);
            }

            this.dismiss();

            //  배경음악 플레이
            if (AudioPlayer.instance() != null) {
                AudioPlayer.instance().update();
            }

        });

    }
}
