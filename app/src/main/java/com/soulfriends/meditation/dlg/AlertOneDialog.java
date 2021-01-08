package com.soulfriends.meditation.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.util.UtilAPI;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class AlertOneDialog extends Dialog {

    private Activity activity;
    private Context context;

    public AlertOneDialog(@NonNull Context context, Activity activity) {
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
        setContentView(R.layout.dialog_alertone);

        // ok
        TextView tv_ok = findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(v -> {
            this.dismiss();
            activity.finish();
        });

    }
}