package com.soulfriends.meditation.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.soulfriends.meditation.R;

public class AlertAlreadyPopup extends Dialog {

    //아니오 / 예

    public enum Dlg_Type
    {
        friend,              // 닉네임 님이 이미 친구 신청을 했습니다.
        emotion,             // 닉네임 님이 이미 감정공유를 요청했습니다.
    }

    private Activity activity;
    private Context context;

    private AlertAlreadyPopup.Dlg_Type dlg_type;

    public ImageView iv_ok;
    public ImageView iv_no;

    private String nickname;

    public AlertAlreadyPopup(@NonNull Context context, Activity activity, AlertAlreadyPopup.Dlg_Type dlg_type, String nickname) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.dlg_type = dlg_type;

        this.nickname = nickname;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alertalready);

        TextView textView = this.findViewById(R.id.tv_message);

        int text_id = 0;
        switch(dlg_type) {
            case friend: {
                text_id = R.string.dialog_already_friend;
            }
            break;
            case emotion: {
                text_id = R.string.dialog_already_emotion;
            }
            break;
        }

        textView.setText(this.nickname + " " + context.getResources().getString(text_id));

        // ok
        iv_ok = findViewById(R.id.iv_ok);

        // no
        iv_no = findViewById(R.id.iv_no);
    }
}
