package com.soulfriends.meditation.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.AuthManager;
import com.soulfriends.meditation.util.PreferenceManager;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.player.AudioPlayer;
import com.soulfriends.meditation.view.player.MeditationAudioManager;

public class IntroActivity extends AppCompatActivity {


    AuthManager authManager = new AuthManager();

    Boolean bLogin = false;

    @Override
    protected void onStart() {
        super.onStart();
        authManager.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        authManager.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        bLogin = authManager.IsLogin(this);

        CallWithDelay(2000, this, bLogin);

        NetServiceManager.getinstance().allClear();
        NetServiceManager.getinstance().init(getResources(),getApplicationContext());

        UtilAPI.Init();

        UtilAPI.SetFullScreen(getWindow());

        //mAuth = FirebaseAuth.getInstance();
        //FirebaseAuth.getInstance().signOut();

        // 배경음

        boolean sound_off = PreferenceManager.getBoolean(this,"sound_off");

        AudioPlayer.with(getApplicationContext());

        if(sound_off) {
            // 음악 정지 상태
        }
        else {

            // 음악 플레이 상태
            AudioPlayer.instance().playSound(R.raw.bgm, 1.0f);
        }

        UtilAPI.SetActivity(this);
    }

    public void Do(Boolean bLogin)
    {
        if (bLogin) {

            String uid = PreferenceManager.getString(this,"uid");
            if(uid.isEmpty() || uid.length() == 0)
            {
                this.startActivity(new Intent(this, LoginActivity.class));

                //this.finish();
            }
            else {

                this.startActivity(new Intent(this, LoadingActivity.class));

                //this.overridePendingTransition(0, 0);

                //this.finish();
            }

        } else {

            this.startActivity(new Intent(this, LoginActivity.class));

            //this.finish();
        }
    }


    public void CallWithDelay(long miliseconds, final Activity activity, Boolean bLogin) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    Do(bLogin);

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



    @Override
    protected void onDestroy() {

        MeditationAudioManager.getinstance().unbind();
        MeditationAudioManager.getinstance().release_service(); // 2020.12.20

        if (AudioPlayer.instance() != null) {
            AudioPlayer.instance().release();
        }

        super.onDestroy();
    }

}