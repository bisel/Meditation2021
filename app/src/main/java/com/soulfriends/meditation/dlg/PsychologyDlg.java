package com.soulfriends.meditation.dlg;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.view.PsychologyListActivity;

public class PsychologyDlg extends Dialog implements View.OnClickListener{
    private final Context mContext;
    private ImageView iv_close;
    private ImageView iv_test;
    private TextView tv_feelstate;

    public PsychologyDlg(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_psychology);

        tv_feelstate = findViewById(R.id.tv_feel_quest);

        iv_close = findViewById(R.id.iv_close);
        iv_test = findViewById(R.id.iv_test);
        iv_close.setOnClickListener(this);
        iv_test.setOnClickListener(this);

        UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();

        // nickname feeling state
      
           if(userProfile.nickname != null) {
            String strQuest = userProfile.nickname + " " + mContext.getResources().getString(R.string.feel_state_quest);

            int end_nick = userProfile.nickname.length();
            if(end_nick > 0) {
		        Spannable wordtoSpan = new SpannableString(strQuest);
		        wordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(179, 179, 227)), 0, end_nick, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		        wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE), end_nick + 1, strQuest.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		        tv_feelstate.setText(wordtoSpan);
 			 }
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close: {
                dismiss();
            }
            break;

            case R.id.iv_test: {
                dismiss();

                Intent intent = new Intent();
                intent.setClass(mContext, PsychologyListActivity.class);
                mContext.startActivity(intent);
            }
            break;
        }
    }
}
