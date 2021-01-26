package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.dlg.AlertLineOneOkPopup;
import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.netservice.NetServiceManager;

public class ContentsUploadActivity extends BaseActivity {

    // value
    private String titleName = "";
    private String thumnailImgName = "";
    private int playtime = 0;
    private int IsSndFile = 0;
    private String SndFileName = "";
    private String releasedate = "";
    private String backgrroundImgName = "";
    private String genre = "";
    private String emotion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents_upload);

        // 정보 받기
        Intent intent = getIntent();

        titleName = intent.getStringExtra("titleName");
        thumnailImgName = intent.getStringExtra("thumnailImgName");
        playtime = intent.getIntExtra("playtime", 0);
        IsSndFile = intent.getIntExtra("IsSndFile", 0);
        SndFileName = intent.getStringExtra("SndFileName");
        releasedate = intent.getStringExtra("releasedate");
        backgrroundImgName = intent.getStringExtra("backgrroundImgName");
        genre = intent.getStringExtra("명상");//genre
        emotion = intent.getStringExtra("기쁨");//emotion

        // 추후 수정일 경우에는 null 이 아니므로 고려해야함.

        NetServiceManager.getinstance().setOnRecvValMeditationContentsListener(new NetServiceManager.OnRecvValMeditationContentsListener() {
            @Override
            public void onRecvValMeditationContentsListener(boolean validate, MeditationContents successContents) {

               if(validate)
               {
                   // 업로드 성공

                   NetServiceManager.getinstance().getUserProfile().mycontentslist.add(successContents.uid);

                   NetServiceManager.getinstance().sendValProfile(NetServiceManager.getinstance().getUserProfile());

                   NetServiceManager.getinstance().mSocialContentsList.add(successContents);



                   OnEvent_Success();


                   Toast.makeText(getApplicationContext(),"업로드 성공",Toast.LENGTH_SHORT).show();
               }
               else
               {
                   // 업로드 실패

                   OnEvent_Faild();

                   Toast.makeText(getApplicationContext(),"업로드 실패",Toast.LENGTH_SHORT).show();
               }
            }
        });

        NetServiceManager.getinstance().sendValSocialMeditationContents(null,
                titleName,
                thumnailImgName,
                String.valueOf(playtime),
                IsSndFile,
                SndFileName,
                releasedate,
                backgrroundImgName,
                genre,
                emotion);

        // 애니메이션 처리
        final AnimationDrawable drawable = (AnimationDrawable) this.findViewById(R.id.iv_upload).getBackground();

        drawable.start();

        // 실패 팝업 테스트
//        ImageView iv_bg = this.findViewById(R.id.iv_bg);
//        iv_bg.setOnClickListener(v -> {
//
//            OnEvent_Faild();
//        });
    }

    private void OnEvent_Success()
    {
        Intent intent = new Intent(this, MyContentsActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        finish();
    }

    private void OnEvent_Faild()
    {
        AlertLineOneOkPopup alertDlg = new AlertLineOneOkPopup(this, this, AlertLineOneOkPopup.Dlg_Type.contents_upload_failed);

        alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDlg.show();

        alertDlg.iv_ok.setOnClickListener(v -> {

            finish();

            alertDlg.dismiss();
        });
    }
}