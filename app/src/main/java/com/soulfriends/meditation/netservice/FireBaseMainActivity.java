package com.soulfriends.meditation.netservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.model.UserProfile;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class FireBaseMainActivity extends AppCompatActivity {
    Button mFBBtn;
    Button mPrintFB;
    UserProfile otherUserVal = new UserProfile();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base_main);

        mFBBtn = (Button) findViewById(R.id.firebaseBtn);
        mFBBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TestOneOtherPlayer();
                TestMultipleOtherPlayer();
            }
        });

        mPrintFB = (Button) findViewById(R.id.printFB);
        mPrintFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Test","OthertestValue : "+otherUserVal.age);
            }
        });
    }

    public void TestOneOtherPlayer()
    {
        NetServiceManager.getinstance().setOnRecvOtherProfileListener(new NetServiceManager.OnRecvOtherProfileListener() {
            @Override
            public void onRecvOtherProfileListener(boolean validate, UserProfile otherUser) {
                otherUserVal = otherUser;
                otherUserVal.age = 2;
                Log.d("Test","testValue");
            }
        });

        NetServiceManager.getinstance().getOtherUserProfile("0IyCCCIhIxW0B0veAGKzzmhOmeL2");
    }

    public int cntOther = 0;

    // 여러개의 값을 얻는 서버로부터 얻는 방법
    public void TestMultipleOtherPlayer()
    {
        NetServiceManager.getinstance().setOnRecvOtherProfileListener(new NetServiceManager.OnRecvOtherProfileListener() {
            @Override
            public void onRecvOtherProfileListener(boolean validate, UserProfile otherUser) {
                otherUserVal = otherUser;
                Log.d("Test","multiple ID : "+cntOther+" returnVal : "+ otherUser.uid);
                cntOther++;
            }
        });

        List<String> inputContentsCategorysIds = asList("0IyCCCIhIxW0B0veAGKzzmhOmeL2","0jUNvcuYiLM7QedwCozeKFlYBpr1","2S710gUJjWTuNjVPLisjQO4g0Cn1");
        int sizeNum = inputContentsCategorysIds.size();
        for(int i = 0; i < sizeNum; i++){
            NetServiceManager.getinstance().getOtherUserProfile(inputContentsCategorysIds.get(i));
        }
    }
}