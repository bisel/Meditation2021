package com.soulfriends.meditation.view;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.UserinfoBinding;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.PreferenceManager;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.viewmodel.UserinfoViewModel;
import com.soulfriends.meditation.viewmodel.UserinfoViewModelFactory;


public class UserinfoActivity extends PhotoBaseActivity implements ResultListener {

    private UserinfoBinding binding;
    private UserinfoViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private UserinfoViewModelFactory userinfoViewModelFactory;

    private FirebaseAuth mAuth;

    private UserProfile userProfile;

    private boolean bSuccess_nickname;
    private boolean bSuccess_gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_userinfo);
        binding.setLifecycleOwner(this);

        userProfile = new UserProfile();

        if (userinfoViewModelFactory == null) {
            userinfoViewModelFactory = new UserinfoViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), userinfoViewModelFactory).get(UserinfoViewModel.class);
        binding.setViewModel(viewModel);

        bSuccess_nickname = false;
        bSuccess_gender = false;

        // 자동로그인 시 uid 초기화 처리
        PreferenceManager.setString(this,"uid", "");


        //---------------------------------------------
        // NetServiceManager
        //---------------------------------------------
        //NetServiceManager.getinstance().init();
        NetServiceManager.getinstance().setOnRecvValNickNameListener(new NetServiceManager.OnRecvValNickNameListener(){
            @Override
            public void onRecvValNickName(boolean validate) {

                DoRecvValNickName(validate);
            }
        });

        NetServiceManager.getinstance().setOnRecvValProfileListener(new NetServiceManager.OnRecvValProfileListener() {
            @Override
            public void onRecvValProfile(boolean validate) {

                if(validate) {
                    DoRecvValProfile(validate);
                }
                else
                {
                    Toast.makeText(getBaseContext(), "onRecvValProfile 실패 ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        NetServiceManager.getinstance().setOnRecvContentsListener(new NetServiceManager.OnRecvContentsListener() {
            @Override
            public void onRecvContents(boolean validate) {

                DoRecvContents(validate);
            }
        });

        NetServiceManager.getinstance().setOnRecvContentsCharInfoListener(new NetServiceManager.OnRecvContentsCharInfoListener() {
            @Override
            public void onRecvContentsCharInfo(boolean validate) {
                if(validate == true){

                    // dlsmdla 2021_02_01 임시
                    //NetServiceManager.getinstance().recvContentsExt();
                    NetServiceManager.getinstance().recvSocialContentsExt();
                }
            }
        });

        NetServiceManager.getinstance().setOnSocialRecvContentsListener(new NetServiceManager.OnSocialRecvContentsListener() {
            @Override
            public void onSocialRecvContents(boolean validate) {
                if(validate == true){
                    NetServiceManager.getinstance().recvContentsExt();
                }
            }
        });


        binding.editNickname.setOnEditorActionListener(new EditText.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    Check_EditFocus_OnButton();

                    return true;
                }
                return false;
            }
        });

        binding.editIntrodution.setOnEditorActionListener(new EditText.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    Check_EditFocus_OnButton();

                    return true;
                }
                return false;
            }
        });

    }

    private void DoRecvContents(boolean validate)
    {
        binding.progressBar.setVisibility(View.GONE);

        if(validate) {

            // uid 와 닉네임 저장

            //String nickname = NetServiceManager.getinstance().getUserProfile().nickname;

            //PreferenceManager.setString(this,"uid", uid);
            //PreferenceManager.setString(this,"nickname", nickname);

            //String key = uid + "##" + nickname;
            //PreferenceManager.setString(this,"uid_nickname", key);

            // 성공한 경우때 uid를 저장해야 한다.
            String uid = NetServiceManager.getinstance().getUserProfile().uid;
            PreferenceManager.setString(this,"uid", uid);

            this.startActivity(new Intent(this, MainActivity.class));

            finish();
        }
        else
        {

        }

    }

    private void DoRecvValProfile(boolean validate)
    {
        if(validate)
        {
            // 성공을 하면
            //NetServiceManager.getinstance().recvContentsExt();

            UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
            //userProfile.uid = PreferenceManager.getString(this,"uid");

            NetServiceManager.getinstance().setOnRecvProfileListener(new NetServiceManager.OnRecvProfileListener() {
                @Override
                public void onRecvProfile(boolean validate, int errorcode) {

                    if(errorcode == 0) {
                        NetServiceManager.getinstance().recvContentsCharInfo();
                        //NetServiceManager.getinstance().recvContentsExt();
                    }
                }
            });

            NetServiceManager.getinstance().recvUserProfile(userProfile.uid);

            //NetServiceManager.getinstance().recvContentsExt();
        }
        else
        {
            // 다시 보낸다.
            UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
            //userProfile.uid = mAuth.getCurrentUser().getUid();
            //userProfile.nickname = viewModel.getNickname().getValue();
            NetServiceManager.getinstance().sendValProfile(userProfile);
        }
    }

    private void DoRecvValNickName(boolean validate) {

        // 닉네임을 보내고 이벤트 받아서 아래 로직으로 ui 처리해야 한다.
        if (validate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                binding.buttonNickname.setBackground(ContextCompat.getDrawable(this, R.drawable.nickcheck_btn));
            } else {
                binding.buttonNickname.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.nickcheck_btn));
            }

            // 사용 가능한 닉네임 입니다.
            binding.tvCheckNickname.setText(getString(R.string.use_ok_nickname));

            bSuccess_nickname = true;

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                binding.buttonNickname.setBackground(ContextCompat.getDrawable(this, R.drawable.check_btn_disabled));
            } else {
                binding.buttonNickname.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.check_btn_disabled));
            }

            // 이미 사용중인 닉네임 입니다.
            binding.tvCheckNickname.setText(getString(R.string.used_nickname));

            bSuccess_nickname = false;
        }

        Check_IsButton();

        binding.progressBar.setVisibility(View.GONE);
    }

    private void hideKeyBoard() {

        InputMethodManager imm;
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText())
        {
            View view = this.getCurrentFocus();
            if(view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void Check_IsButton()
    {
        // 회원정보 저장 버튼 활성화 여부 체크
        if(bSuccess_nickname && bSuccess_gender) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                binding.buttonOk.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_member_able));
            } else {
                binding.buttonOk.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_member_able));
            }
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                binding.buttonOk.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_member_disable));
            } else {
                binding.buttonOk.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_member_disable));
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onSuccess(Integer id, String message) {
        switch (id) {
            case R.id.button_nickname: {

                Check_EditFocus_OnButton();
                // 중복 검사 버튼

                // 예외처리
                //1 ~ 6자로 제한
                String nickname = viewModel.getNickname().getValue();
                if(nickname.length() < 1)
                {

                    break;
                }

                binding.progressBar.setVisibility(View.VISIBLE);

                NetServiceManager.getinstance().sendValNickName(viewModel.getNickname().getValue());
            }
            break;
            case R.id.button_man: {

                Check_EditFocus_OnButton();


                userProfile.gender = 1;

                bSuccess_gender = true;

                // man
                UtilAPI.setButtonBackground(this, binding.buttonMan, R.drawable.man_selected);

                // woman
                UtilAPI.setButtonBackground(this, binding.buttonWoman, R.drawable.man);

                Check_IsButton();
            }
            break;
            case R.id.button_woman: {

                Check_EditFocus_OnButton();

                userProfile.gender = 2;

                bSuccess_gender = true;

                // woman
                UtilAPI.setButtonBackground(this, binding.buttonWoman, R.drawable.man_selected);

                // man
                UtilAPI.setButtonBackground(this, binding.buttonMan, R.drawable.man);

                Check_IsButton();

            }
            break;

            case R.id.button_ok: {

                Check_EditFocus_OnButton();
                if(!bSuccess_nickname) {
                    break;
                }

                if(!bSuccess_gender) {
                    binding.tvGenderSelect.setVisibility(View.VISIBLE);
                    break;
                }

                binding.progressBar.setVisibility(View.VISIBLE);

                UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
                userProfile.uid = mAuth.getCurrentUser().getUid();
                userProfile.nickname = viewModel.getNickname().getValue();

                //userProfile.profileimg = mCurrentPhotoPath;
                userProfile.profileIntro = viewModel.getIntroduction().getValue();

                //NetServiceManager.getinstance().sendValProfile(userProfile);

                // 안됨 dlsmdla
                NetServiceManager.getinstance().sendValNewProfileExt(userProfile, null, null, mCurrentPhotoPath);
            }
            break;
            case R.id.iv_picture: {

                Check_EditFocus_OnButton();
                // 썹네일 이미지 선택시

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
                bottomSheetDialog.setContentView(R.layout.layout_photo_sheetdialog);
                bottomSheetDialog.show();

                View view_gallery = bottomSheetDialog.findViewById(R.id.layer_gallery);

                view_gallery.setOnClickListener(v -> {

                    getAlbum();

                    bottomSheetDialog.dismiss();

                });

                View view_photo = bottomSheetDialog.findViewById(R.id.layer_photo);

                view_photo.setOnClickListener(v -> {

                    captureCamera();

                    bottomSheetDialog.dismiss();

                });
            }
            break;
        }
    }


    // 버튼들을 눌렀을때 에디트 포커스 해제 처리
    private void Check_EditFocus_OnButton()
    {
        // 우선순위 주의!!
        hideKeyBoard();

        if(binding.editNickname.isFocused()) {
            binding.editNickname.clearFocus();
        }

        if(binding.editIntrodution.isFocused()) {
            binding.editIntrodution.clearFocus();
        }
    }

    @Override
    public void onFailure(Integer id, String message) {
        switch (id) {
            case R.id.button_nickname: {

            }
            break;

            case R.id.button_ok: {
            }
            break;
        }
    }

    @Override
    public void OnSuccess_ImageCrop()
    {
        // 썹네일 성공시
        // 이미지 show

        binding.ivPicture.setImageURI(albumURI);

    }

}