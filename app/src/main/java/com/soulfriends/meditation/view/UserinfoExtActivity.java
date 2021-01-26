package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.UserinfoExtBinding;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.PreferenceManager;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.viewmodel.UserinfoExtViewModel;
import com.soulfriends.meditation.viewmodel.UserinfoExtViewModelFactory;

public class UserinfoExtActivity extends PhotoBaseActivity implements ResultListener {

    private UserinfoExtBinding binding;
    private UserinfoExtViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private UserinfoExtViewModelFactory userinfoExtViewModelFactory;


    private UserProfile userProfile;

    private boolean bSuccess_nickname;
    private boolean bSuccess_gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_userinfo_ext);
        binding.setLifecycleOwner(this);

        userProfile = new UserProfile();

        if (userinfoExtViewModelFactory == null) {
            userinfoExtViewModelFactory = new UserinfoExtViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), userinfoExtViewModelFactory).get(UserinfoExtViewModel.class);
        binding.setViewModel(viewModel);

        bSuccess_nickname = false;
        bSuccess_gender = false;

        //---------------------------------------------
        // NetServiceManager
        //---------------------------------------------
        //NetServiceManager.getinstance().init();
        NetServiceManager.getinstance().setOnRecvValNickNameListener(new NetServiceManager.OnRecvValNickNameListener() {
            @Override
            public void onRecvValNickName(boolean validate) {

                DoRecvValNickName(validate);
            }
        });

        NetServiceManager.getinstance().setOnRecvValProfileListener(new NetServiceManager.OnRecvValProfileListener() {
            @Override
            public void onRecvValProfile(boolean validate) {
                DoRecvValProfile(validate);
            }
        });

    }

    private void DoRecvContents(boolean validate) {
        binding.progressBar.setVisibility(View.GONE);

        if (validate) {

            this.startActivity(new Intent(this, MainActivity.class));

            finish();
        } else {

        }

    }

    private void DoRecvValProfile(boolean validate) {
        if (validate) {
            // 성공을 하면

            UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
            //userProfile.uid = PreferenceManager.getString(this,"uid");

            NetServiceManager.getinstance().setOnRecvProfileListener(new NetServiceManager.OnRecvProfileListener() {
                @Override
                public void onRecvProfile(boolean validate, int errorcode) {

                    if (errorcode == 0) {
                        NetServiceManager.getinstance().recvContentsCharInfo();
                    }
                }
            });

            NetServiceManager.getinstance().recvUserProfile(userProfile.uid);
        } else {
            // 다시 보낸다.
            UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();

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

        if (imm.isAcceptingText()) {
            View view = this.getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void Check_IsButton() {
        // 회원정보 저장 버튼 활성화 여부 체크
        if (bSuccess_nickname && bSuccess_gender) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                binding.buttonOk.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_member_able));
            } else {
                binding.buttonOk.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_member_able));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                binding.buttonOk.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_member_disable));
            } else {
                binding.buttonOk.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_member_disable));
            }
        }
    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public void onSuccess(Integer id, String message) {
        switch (id) {
            case R.id.button_nickname: {
                // 중복 검사 버튼

                hideKeyBoard();
                binding.editNickname.clearFocus();

                // 예외처리
                //1 ~ 6자로 제한
                String nickname = viewModel.getNickname().getValue();
                if (nickname.length() < 1) {

                    break;
                }

                binding.progressBar.setVisibility(View.VISIBLE);

                NetServiceManager.getinstance().sendValNickName(viewModel.getNickname().getValue());
            }
            break;
            case R.id.button_man: {

                //--------------------------------------------------
                // 회원 정보 수정에서는 성별 변경하지 않도록 한다.
                //--------------------------------------------------
                if(false) {
                    userProfile.gender = 1;

                    bSuccess_gender = true;

                    // man
                    UtilAPI.setButtonBackground(this, binding.buttonMan, R.drawable.man_selected);

                    // woman
                    UtilAPI.setButtonBackground(this, binding.buttonWoman, R.drawable.man);

                    Check_IsButton();
                }
            }
            break;
            case R.id.button_woman: {

                //--------------------------------------------------
                // 회원 정보 수정에서는 성별 변경하지 않도록 한다.
                //--------------------------------------------------
                if(false) {
                    userProfile.gender = 2;

                    bSuccess_gender = true;

                    // woman
                    UtilAPI.setButtonBackground(this, binding.buttonWoman, R.drawable.man_selected);

                    // man
                    UtilAPI.setButtonBackground(this, binding.buttonMan, R.drawable.man);

                    Check_IsButton();
                }

            }
            break;

            case R.id.button_ok: {

                if (!bSuccess_nickname) {
                    break;
                }

                if (!bSuccess_gender) {
                    binding.tvGenderSelect.setVisibility(View.VISIBLE);
                    break;
                }

                binding.progressBar.setVisibility(View.VISIBLE);

                UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();

                userProfile.nickname = viewModel.getNickname().getValue();
                NetServiceManager.getinstance().sendValProfile(userProfile);
            }
            break;
            case R.id.iv_picture: {

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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

}