package com.soulfriends.meditation.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.soulfriends.meditation.R;

public class AlertTermsPopup extends Dialog {

       // 목소리 / 약관동의 dialog

    private Activity activity;
    private Context context;

    public AlertTermsPopup(@NonNull Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alertterms);

// ok
//        ImageView iv_ok = findViewById(R.id.iv_ok);
//        iv_ok.setOnClickListener(v -> {
//
//            this.dismiss();
//
//        });

        // no
        ImageView iv_no = findViewById(R.id.iv_no);
        iv_no.setOnClickListener(v -> {

            this.dismiss();

        });

    }
}
