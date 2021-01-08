package com.soulfriends.meditation.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.soulfriends.meditation.R;

public class VoiceDialog extends Dialog {

    private Activity activity;
    private Context context;

    private boolean bVoiceAnalysis;

    public VoiceDialog(@NonNull Context context, Activity activity, boolean bVoiceAnalysis) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.bVoiceAnalysis = bVoiceAnalysis;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_voice);

        TextView textView = this.findViewById(R.id.tv_message);

        if(bVoiceAnalysis)
        {
            textView.setText(context.getResources().getString(R.string.dialog_voice_analysis_warning));
        }
        else
        {
            textView.setText(context.getResources().getString(R.string.dialog_voice_warning));
        }

//        // ok
//        ImageView iv_ok = findViewById(R.id.iv_ok);
//        iv_ok.setOnClickListener(v -> {
//
//            this.dismiss();
//
//        });

    }
}
