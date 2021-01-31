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

        // 이미지
        UtilAPI.load_image(this, meditationContents.thumbnail, binding.ivContentsImage);

        //String uid = PreferenceManager.getString(this,"uid");

        String uid = NetServiceManager.getinstance().getUserProfile().uid;
        reactiionCode = NetServiceManager.getinstance().reqContentsFavoriteEvent(uid, meditationContents.uid);

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

                // func 2. 해당 콘텐츠의 좋아요, 싫어요 결정. reactiionCode 0: Default, 1 : 좋아요, 2: 싫어요

//                if(reactiionCode_orig != reactiionCode) {
//                    //String uid = PreferenceManager.getString(this, "uid");
//
//                    String uid = NetServiceManager.getinstance().getUserProfile().uid;
//                    NetServiceManager.getinstance().sendFavoriteEvent(uid, meditationContents.uid, reactiionCode);
//                }

                if(bFinish)
                {
                    finish();
                }
                else {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);

                    this.overridePendingTransition(0, 0);

                    // 나가기 버튼
                    finish();
                }
            }
            break;
            case R.id.iv_good_button: {
                // 좋아요

                reactiionCode = 1;

                Select_Good();

                String uid = NetServiceManager.getinstance().getUserProfile().uid;

                if(meditationContents.ismycontents == 1){ // social
                    NetServiceManager.getinstance().sendFavoriteLocalEventExt(uid, meditationContents.uid, reactiionCode,true);
                    NetServiceManager.getinstance().sendSocialFavoriteEventExt(uid, meditationContents.uid, reactiionCode,true);
                }else{
                    NetServiceManager.getinstance().sendFavoriteLocalEvent(uid, meditationContents.uid, reactiionCode);
                    NetServiceManager.getinstance().sendFavoriteEvent(uid, meditationContents.uid, reactiionCode);
                }
            }
            break;
            case R.id.iv_bad_button: {
                // 별로예요

                reactiionCode = 2;

                Select_Bad();

                String uid = NetServiceManager.getinstance().getUserProfile().uid;

                if(meditationContents.ismycontents == 1){ // social
                    NetServiceManager.getinstance().sendFavoriteLocalEventExt(uid, meditationContents.uid, reactiionCode,true);
                    NetServiceManager.getinstance().sendSocialFavoriteEventExt(uid, meditationContents.uid, reactiionCode,true);
                }else{
                    NetServiceManager.getinstance().sendFavoriteLocalEvent(uid, meditationContents.uid, reactiionCode);
                    NetServiceManager.getinstance().sendFavoriteEvent(uid, meditationContents.uid, reactiionCode);
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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.overridePendingTransition(0, 0);
            finish();
        }
    }

//    public void close()
//    {
//        ActivityManager manager = (ActivityManager)this.getSystemService(Activity.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> list = manager.getRunningTasks(1);
//        ActivityManager.RunningTaskInfo info = list.get(0);
//
//        String ss = info.topActivity.getClassName();
//
//        if (info.topActivity.getClassName().equals("com.example.MainActivity")) {
//
//
//        } else {
//
//        }
//    }
}