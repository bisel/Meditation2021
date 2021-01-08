package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.dlg.AlertOneDialog;

public class AlertOneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_one);


        // 팝업 테스트
        AlertOneDialog alertDialog = new AlertOneDialog(this, this);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}