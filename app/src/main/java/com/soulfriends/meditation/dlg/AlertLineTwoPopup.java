package com.soulfriends.meditation.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.soulfriends.meditation.R;

public class AlertLineTwoPopup extends Dialog {

    //아니오 / 예

    public enum Dlg_Type
    {
        contents_existing_delete,   //최대 10개의 콘텐츠를 제작할 수 있습니다.\n기존 컨텐츠를 삭제하러 이동하시겠습니까?
        regiter_cancel,             //입력한 내용이 모두 삭제됩니다.\n등록을 취소하시겠습니까?
    }

    private Activity activity;
    private Context context;

    private Dlg_Type dlg_type;

    public ImageView iv_ok;

    public AlertLineTwoPopup(@NonNull Context context, Activity activity, Dlg_Type dlg_type) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.dlg_type = dlg_type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alertlinetwo);

        TextView textView = this.findViewById(R.id.tv_message);

        int text_id = 0;
        switch(dlg_type) {
            case contents_existing_delete: {
                text_id = R.string.dialog_contents_existing_delete;
            }
            break;
            case regiter_cancel: {
                text_id = R.string.dialog_regiter_cancel;
            }
            break;
        }

        textView.setText(context.getResources().getString(text_id));

        // ok
        iv_ok = findViewById(R.id.iv_ok);
//        iv_ok.setOnClickListener(v -> {
//
//            switch(dlg_type) {
//                case contents_existing_delete: {
//
//
//                }
//                break;
//                case regiter_cancel: {
//
//                }
//                break;
//            }
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
