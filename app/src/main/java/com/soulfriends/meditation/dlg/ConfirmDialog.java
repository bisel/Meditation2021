package com.soulfriends.meditation.dlg;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.view.MainActivity;
import androidx.annotation.NonNull;



//  mEndDialog = new EndDialog(this);
//  mEndDialog.setCancelable(false)
//  mEndDialog.show();

public class ConfirmDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private TextView btn_cancel;
    private TextView btn_ok;

    public ConfirmDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);

        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;

            case R.id.btn_ok:
                ((MainActivity) mContext).finish();
                break;
        }
    }
}