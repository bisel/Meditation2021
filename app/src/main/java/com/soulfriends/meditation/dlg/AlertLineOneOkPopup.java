package com.soulfriends.meditation.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.soulfriends.meditation.R;

public class AlertLineOneOkPopup extends Dialog {

    // 확인
    public enum Dlg_Type
    {
        voice_1_1,                  // 목소리 분석은 1일 1회만 가능합니다. 260, 144
        voice_restart,              // 녹음이 완료되지 않았습니다.\n처음부터 다시 진행해주세요. 260, 159
        contents_upload_failed,     // 콘텐츠 업로드에 실패했습니다. 300, 136
    }
    private Activity activity;
    private Context context;

    private Dlg_Type dlg_type;

    public AlertLineOneOkPopup(@NonNull Context context, Activity activity, Dlg_Type dlg_type) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.dlg_type = dlg_type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alertlineoneok);

        TextView textView = this.findViewById(R.id.tv_message);

        int text_id = 0;
        switch(dlg_type)
        {
            case voice_1_1:
            {
                text_id = R.string.dialog_voice_1_1;
            }
            break;
            case voice_restart:
            {
                text_id = R.string.dialog_voice_restart;
            }
            break;
            case contents_upload_failed:
            {
                text_id = R.string.dialog_contents_upload_failed;
            }
            break;
        }

        textView.setText(context.getResources().getString(text_id));

        // ok
        ImageView iv_ok = findViewById(R.id.iv_ok);
        iv_ok.setOnClickListener(v -> {

            this.dismiss();

        });

    }
}
