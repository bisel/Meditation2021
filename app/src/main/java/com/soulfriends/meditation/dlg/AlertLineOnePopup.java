package com.soulfriends.meditation.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.soulfriends.meditation.R;


public class AlertLineOnePopup extends Dialog {

    // 예  /  아니오

    public enum Dlg_Type
    {
        emotion_request,            //감정공유를 요청하시겠습니까?
        emotion_cancel,             //감정공유를 취소하시겠습니까?
        emotion_request_cancel,     //감정공유 요청을 취소하시겠습니까?
        contents_delete,            //콘텐츠를 정말 삭제하시겠습니까?
        friend_add,                 //친구로 추가하시겠습니까?
        friend_delete,              //친구 삭제하시겠습니까?
        friend_cancel               //친구 요청을 취소하시겠습니까?
    }

    private Activity activity;
    private Context context;

    private boolean bVoiceAnalysis;

    private Dlg_Type dlg_type;

    public ImageView iv_ok;

    public AlertLineOnePopup(@NonNull Context context, Activity activity, Dlg_Type dlg_type) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.dlg_type = dlg_type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alertlineone);

        TextView textView = this.findViewById(R.id.tv_message);

        iv_ok = findViewById(R.id.iv_ok);

        int text_id = 0;
        switch(dlg_type)
        {
            case emotion_request:
            {
                text_id = R.string.dialog_emotion_request;
            }
            break;
            case emotion_cancel:
            {
                text_id = R.string.dialog_emotion_cancel;
            }
            break;
            case emotion_request_cancel:
            {
                text_id = R.string.dialog_emotion_request_cancel;
            }
            break;
            case contents_delete:
            {
                text_id = R.string.dialog_contents_delete;
            }
            break;
            case friend_add:
            {
                text_id = R.string.dialog_friend_add;
            }
            break;
            case friend_delete:
            {
                text_id = R.string.dialog_friend_delete;
            }
            break;
            case friend_cancel:
            {
                text_id = R.string.dialog_friend_cancel;
            }
            break;
        }

        textView.setText(context.getResources().getString(text_id));

        // ok 개별적으로 처리하도록 한다.
//        // ok
//        ImageView iv_ok = findViewById(R.id.iv_ok);
//        iv_ok.setOnClickListener(v -> {
//
//            switch(dlg_type)
//            {
//                case emotion_request:
//                {
//
//                }
//                break;
//                case emotion_cancel:
//                {
//
//                }
//                break;
//                case emotion_request_cancel:
//                {
//
//                }
//                break;
//                case contents_delete:
//                {
//
//                }
//                break;
//                case friend_add:
//                {
//
//                }
//                break;
//                case friend_delete:
//                {
//
//                }
//                break;
//                case friend_cancel:
//                {
//
//                }
//                break;
//            }
//
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
