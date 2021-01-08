package com.soulfriends.meditation.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.util.UtilAPI;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;



public class AlertDialog extends Dialog {

    private Activity activity;
    private Context context;

    public AlertDialog(@NonNull Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    private void exitProgram() {



        // moveTaskToBack(true);



        ActivityCompat.finishAffinity(UtilAPI.scanForActivity(context));
        //Activity.finishAffinity();
        System.exit(0);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alert);

        // cancel
        TextView tv_cancel = findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(v->{

            this.dismiss();
            activity.finish();

        });

        // open setting
        TextView tv_open_setting = findViewById(R.id.tv_open_setting);
        tv_open_setting.setOnClickListener(v->{

            context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            this.dismiss();

            activity.finish();

        });
    }
}