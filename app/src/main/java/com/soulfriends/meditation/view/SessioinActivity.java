package com.soulfriends.meditation.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.google.firebase.auth.FirebaseAuth;
import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.SessionBinding;
import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ActivityStack;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.player.AudioPlayer;
import com.soulfriends.meditation.viewmodel.SessionViewModel;
import com.soulfriends.meditation.viewmodel.SessionViewModelFactory;


public class SessioinActivity extends BaseActivity implements ResultListener {

    private SessionBinding binding;
    private SessionViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private SessionViewModelFactory sessionViewModelFactory;

    private FirebaseAuth mAuth;

    private UserProfile userProfile;

    private MeditationContents meditationContents;

    private int reactiionCode = 0;
    private int reactiionCode_orig = 0;

    private boolean bFinish = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sessioin);
        binding.setLifecycleOwner(this);

        meditationContents = NetServiceManager.getinstance().getCur_contents();

        userProfile = NetServiceManager.getinstance().getUserProfile();

        if (sessionViewModelFactory == null) {
            sessionViewModelFactory = new SessionViewModelFactory(meditationContents.title,this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), sessionViewModelFactory).get(SessionViewModel.class);
        binding.setViewModel(viewModel);

        // network
        UtilAPI.SetNetConnection_Activity(this);

        // 이미지

        if(meditationContents.thumbnail == null) {

            UtilAPI.setImage(this,binding.ivContentsImage, R.drawable.basic_img);
        }
        else {
            UtilAPI.load_image(this, meditationContents.thumbnail, binding.ivContentsImage);
        }

        //String uid = PreferenceManager.getString(this,"uid");

        String uid = NetServiceManager.getinstance().getUserProfile().uid;

        if(meditationContents.ismycontents == 1){
            reactiionCode = NetServiceManager.getinstance().reqSocialContentsFavoriteEvent(uid, meditationContents.uid);
        }else{
            reactiionCode = NetServiceManager.getinstance().reqContentsFavoriteEvent(uid, meditationContents.uid);
        }

        reactiionCode_orig = reactiionCode;

        //  배경음악 플레이
        if(AudioPlayer.instance() != null ) {
            AudioPlayer.instance().update();
        }

        if(reactiionCode == 1)
        {
            // 좋아요
            // good 활성화
            Select_Good();
        }
        else if(reactiionCode == 2)
        {
            // 별로예요
            Select_Bad();
        }

        bFinish = false;
        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            String strValue = intent.getExtras().getString("isfinish");
            if (strValue.equals("true")) {

                bFinish = true;
            }
        }

        int xx = 0;
    }

    private void Select_Good()
    {
        // good 활성화
        UtilAPI.setImage(this, binding.ivGoodButton, R.drawable.grading_like_abled);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            binding.ivGoodButton.setBackground(ContextCompat.getDrawable(this, R.drawable.grading_like_abled));
//        } else {
//            binding.ivGoodButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.grading_like_abled));
//        }

        // bad 비활성화
        UtilAPI.setImage(this, binding.ivBadButton, R.drawable.grading_dislike_abled);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            binding.ivBadButton.setBackground(ContextCompat.getDrawable(this, R.drawable.grading_dislike_abled));
//        } else {
//            binding.ivBadButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.grading_dislike_abled));
//        }
    }

    private void Select_Bad()
    {
        // bad 활성화
        UtilAPI.setImage(this, binding.ivBadButton, R.drawable.grading_dislike_disabled);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            binding.ivBadButton.setBackground(ContextCompat.getDrawable(this, R.drawable.grading_dislike_disabled));
//        } else {
//            binding.ivBadButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.grading_dislike_disabled));
//        }

        // good 비활성화
        UtilAPI.setImage(this, binding.ivGoodButton, R.drawable.grading_like_disabled);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            binding.ivGoodButton.setBackground(ContextCompat.getDrawable(this, R.drawable.grading_like_disabled));
//        } else {
//            binding.ivGoodButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.grading_like_disabled));
//        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onSuccess(Integer id, String message) {
        switch (id) {
            case R.id.iv_close: {
                onBackPressed();
            }
            break;
            case R.id.iv_good_button: {
                // 좋아요

                //--------------------------------------------------
                //  인터넷 연결 안되는 상황일 때 앱 실행 시
                //--------------------------------------------------
                if(UtilAPI.isConnected(this) == 0) {
                    Intent intent = new Intent(this, NetDialogActivity.class);
                    this.startActivity(intent);

                    this.overridePendingTransition(0, 0);

                    // this.finish();
                }
                else {
                    reactiionCode = 1;

                    Select_Good();

                    String uid = NetServiceManager.getinstance().getUserProfile().uid;

                    if (meditationContents.ismycontents == 1) { // social
                        NetServiceManager.getinstance().sendFavoriteLocalEventExt(uid, meditationContents.uid, reactiionCode, true);
                        NetServiceManager.getinstance().sendSocialFavoriteEventExt(uid, meditationContents.uid, reactiionCode, true);
                    } else {
                        NetServiceManager.getinstance().sendFavoriteLocalEvent(uid, meditationContents.uid, reactiionCode);
                        NetServiceManager.getinstance().sendFavoriteEvent(uid, meditationContents.uid, reactiionCode);
                    }
                }
            }
            break;
            case R.id.iv_bad_button: {
                // 별로예요

                //--------------------------------------------------
                //  인터넷 연결 안되는 상황일 때 앱 실행 시
                //--------------------------------------------------
                if(UtilAPI.isConnected(this) == 0) {
                    Intent intent = new Intent(this, NetDialogActivity.class);
                    this.startActivity(intent);

                    this.overridePendingTransition(0, 0);

                    // this.finish();
                }
                else {
                    reactiionCode = 2;

                    Select_Bad();

                    String uid = NetServiceManager.getinstance().getUserProfile().uid;

                    if (meditationContents.ismycontents == 1) { // social
                        NetServiceManager.getinstance().sendFavoriteLocalEventExt(uid, meditationContents.uid, reactiionCode, true);
                        NetServiceManager.getinstance().sendSocialFavoriteEventExt(uid, meditationContents.uid, reactiionCode, true);
                    } else {
                        NetServiceManager.getinstance().sendFavoriteLocalEvent(uid, meditationContents.uid, reactiionCode);
                        NetServiceManager.getinstance().sendFavoriteEvent(uid, meditationContents.uid, reactiionCode);
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    @Override // 2020.12.20 , Close 막기
    public void onBackPressed() {

        if(bFinish)
        {
            finish();
        }
        else {

            ActivityStack.instance().OnBack(this);

//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            this.overridePendingTransition(0, 0);
//            finish();
        }
    }
}