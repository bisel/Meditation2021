package com.soulfriends.meditation.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.soulfriends.meditation.R;

public class ErrorCodePopup extends Dialog {

    private Activity activity;
    private Context context;


    public ImageView iv_ok;

    public TextView textView;

    public ErrorCodePopup(@NonNull Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_errorcode);

        textView = this.findViewById(R.id.tv_message);
        //textView.setText(context.getResources().getString(text_id));

        // ok
        iv_ok = findViewById(R.id.iv_ok);
    }
}
