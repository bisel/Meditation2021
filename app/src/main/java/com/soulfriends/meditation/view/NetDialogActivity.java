package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.dlg.NetDialog;
import com.soulfriends.meditation.dlg.TimerDialog;
import com.soulfriends.meditation.util.UtilAPI;

public class NetDialogActivity extends AppCompatActivity {

    private NetDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_dialog);

        UtilAPI.SetFullScreen(getWindow());

        // 팝업 테스트
        alertDialog = new NetDialog(this, this);
        alertDialog.setCancelable(false);
        alertDialog.show();

        // 앱 종료
        alertDialog.iv_no.setOnClickListener(v -> {

            finish();

            alertDialog.dismiss();

            ActivityCompat.finishAffinity(UtilAPI.scanForActivity(this));
            //Activity.finishAffinity();
            System.exit(0);
        });

        // 세팅
        alertDialog.iv_ok.setOnClickListener(v -> {

            Intent intent= new Intent(Settings.ACTION_SETTINGS);
            startActivityForResult(intent,0);

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        UtilAPI.s_bShowNetConnection = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
            {
                finish();
                alertDialog.dismiss();

                UtilAPI.startActivity_NetConnection(this);

                this.overridePendingTransition(0, 0);

            }
            break;
        }

    }
}