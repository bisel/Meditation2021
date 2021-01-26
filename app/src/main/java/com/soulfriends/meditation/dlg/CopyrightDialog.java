package com.soulfriends.meditation.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.view.ContentsUploadActivity;

public class CopyrightDialog extends Dialog {

    //<!-- 콘텐츠 약관동의 dialog  -->

    private Activity activity;
    private Context context;

    public CopyrightDialog(@NonNull Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_copyright);

        // ok
        ImageView iv_ok = findViewById(R.id.iv_ok);
        iv_ok.setOnClickListener(v -> {

            Intent intent = new Intent(activity, ContentsUploadActivity.class);
            activity.startActivity(intent);

            activity.finish();
            this.dismiss();

        });

        // no
        ImageView iv_no = findViewById(R.id.iv_no);
        iv_no.setOnClickListener(v -> {

            this.dismiss();

        });

    }
}
