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
        contents_error,             // 콘텐츠에 오류가 발생하여 재생할 수 없습니다.
        friend_request,             // 친구 요청 했습니다.
        friend_cancelled,           // 친구가 취소되었습니다.
        error_retry                 // 지금은 이 요청을 처리할 수 없습니다. \n 다시 시도해주세요
    }
    private Activity activity;
    private Context context;

    private Dlg_Type dlg_type;

    public ImageView iv_ok;

    private boolean bUse_ButtonOk = true;

    public AlertLineOneOkPopup(@NonNull Context context, Activity activity, Dlg_Type dlg_type) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.dlg_type = dlg_type;

        bUse_ButtonOk = true;

        if(dlg_type == Dlg_Type.contents_upload_failed)
        {
            bUse_ButtonOk = false;
        }
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
            case contents_error:
            {
                text_id = R.string.dialog_contents_error;
            }
            break;
            case friend_request:
            {
                text_id = R.string.dialog_friend_request;
            }
            break;
            case friend_cancelled:
            {
                text_id = R.string.dialog_friend_cancelled;
            }
            break;
            case error_retry:
            {
                text_id = R.string.dialog_error_retry;
            }
            break;
        }

        textView.setText(context.getResources().getString(text_id));

        // ok
        iv_ok = findViewById(R.id.iv_ok);

        if(bUse_ButtonOk) {
            iv_ok.setOnClickListener(v -> {

                this.dismiss();

            });
        }

    }
}
