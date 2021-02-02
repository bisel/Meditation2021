package com.soulfriends.meditation.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.PreferenceManager;
import com.soulfriends.meditation.util.UtilAPI;

public class LoadingActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        //CallWithDelay(2000, this, progressBar);

        UtilAPI.SetFullScreen(getWindow());

        //  자동로그인 일 경우 정보 요청
        // uid 와 닉네임 저장


        String uid = PreferenceManager.getString(this,"uid");

        //if(UtilAPI.isNetworkAvailable(this)) {
            NetServiceManager.getinstance().setOnRecvProfileListener(new NetServiceManager.OnRecvProfileListener() {
                @Override
                public void onRecvProfile(boolean validate, int errorcode) {

                    DoRecvProfile(validate, errorcode);
                }
            });

            NetServiceManager.getinstance().recvUserProfile(uid);

            NetServiceManager.getinstance().setOnRecvContentsCharInfoListener(new NetServiceManager.OnRecvContentsCharInfoListener() {
                @Override
                public void onRecvContentsCharInfo(boolean validate) {
                    if(validate == true){

                        NetServiceManager.getinstance().recvContentsExt();
                     //   NetServiceManager.getinstance().recvSocialContentsExt();
                    }
                }
            });

//            NetServiceManager.getinstance().setOnSocialRecvContentsListener(new NetServiceManager.OnSocialRecvContentsListener() {
//                @Override
//                public void onSocialRecvContents(boolean validate) {
//                    if(validate == true){
//                        NetServiceManager.getinstance().recvContentsExt();
//                    }
//                }
//            });

            NetServiceManager.getinstance().setOnRecvContentsListener(new NetServiceManager.OnRecvContentsListener() {
                @Override
                public void onRecvContents(boolean validate) {

                    DoRecvContents(validate);
                }
            });

    }

    private void DoRecvProfile(boolean validate, int errorcode)
    {
        if(validate) {

            UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
            //userProfile.uid = PreferenceManager.getString(this,"uid");
            //userProfile.nickname = PreferenceManager.getString(this,"nickname");

            NetServiceManager.getinstance().recvContentsCharInfo(); // 2020.12.21
        }
        else
        {
            //if(validate == false && errorcode == 0){
            // 2020.12.05 실패하면 로그인쪽으로 가야 한다.
            this.startActivity(new Intent(this, LoginActivity.class));
            //}
        }
    }

    private void DoRecvContents(boolean validate)
    {
        if(validate) {

            //NetServiceManager.getinstance().reqSocialEmotionAllContents(); // 2021.02.01
            NetServiceManager.getinstance().reqEmotionAllContents(); // 2020.12.05 2st
            NetServiceManager.getinstance().reqChartypeAllContents(false);  // 2020.12.22

            this.startActivity(new Intent(this, MainActivity.class));

            progressBar.setVisibility(View.GONE);

            finish();
        }
        else
        {

        }

    }

    public static void CallWithDelay(long miliseconds, final Activity activity, ProgressBar progressBar) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    progressBar.setVisibility(View.GONE);

                    //activity.startActivity(new Intent(activity, MainActivity.class));

                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }, miliseconds);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}