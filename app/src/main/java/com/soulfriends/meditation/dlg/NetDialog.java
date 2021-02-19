package com.soulfriends.meditation.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.util.ActivityStack;
import com.soulfriends.meditation.view.player.AudioPlayer;

public class NetDialog extends Dialog {

    private Activity activity;
    private Context context;

    public ImageView iv_ok;
    public ImageView iv_no;

    public NetDialog(@NonNull Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_net);

        // 앱 종료
        iv_ok = findViewById(R.id.iv_ok);
//        iv_ok.setOnClickListener(v -> {
//
//            activity.finish();
//            activity.overridePendingTransition(0, 0);
//
//            this.dismiss();
//
//        });
        
        // 세팅
        iv_no = findViewById(R.id.iv_no);
//        iv_no.setOnClickListener(v -> {
//
//            Intent intent= new Intent(Settings.ACTION_SETTINGS);
//            startActivityForResult(intent,0);   //startActivityForResult() 는 호출한 Activity로 부터 결과를 받을 경우 사용.
//
//            activity.finish();
//            activity.overridePendingTransition(0, 0);
//
//            this.dismiss();
//
//        });

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//
//    }
}
