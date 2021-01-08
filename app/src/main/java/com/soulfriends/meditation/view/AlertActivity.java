package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.dlg.AlertDialog;

public class AlertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        // 팝업 테스트
        AlertDialog alertDialog = new AlertDialog(this, this);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}