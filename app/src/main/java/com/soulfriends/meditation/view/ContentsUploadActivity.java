package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.dlg.AlertLineOneOkPopup;
import com.soulfriends.meditation.dlg.AlertLineOnePopup;
import com.soulfriends.meditation.dlg.AlertLineTwoPopup;
import com.soulfriends.meditation.dlg.CopyrightDialog;

public class ContentsUploadActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents_upload);



        // 애니메이션 처리
        final AnimationDrawable drawable = (AnimationDrawable) this.findViewById(R.id.iv_upload).getBackground();

        drawable.start();

        // 실패 팝업 테스트
        ImageView iv_bg = this.findViewById(R.id.iv_bg);
        iv_bg.setOnClickListener(v -> {

            OnEvent_Faild();
        });
    }


    private void OnEvent_Faild()
    {
        AlertLineOneOkPopup alertDlg = new AlertLineOneOkPopup(this, this, AlertLineOneOkPopup.Dlg_Type.contents_upload_failed);

        alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDlg.show();
    }
}