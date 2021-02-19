package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.UserinfoExtBinding;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.HeightProvider;
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

    private boolean bChange_ProfileImage = false;

    private boolean isKeyboardShowing = false;
    private int keypadBaseHeight = 0;

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

        // network
        UtilAPI.SetNetConnection_Activity(this);

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


        //---------------------------------------------
        // 초기 설정 처리
        //---------------------------------------------

        UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();


        // 프로필 사진
        if(userProfile.profileimg_uri !=null && userProfile.profileimg_uri.length() > 0) {

            String image_uri = userProfile.profileimg_uri;

            UtilAPI.load_image_circle(this, image_uri, binding.ivPicture);
        }
        // 프로필 사진
        //UtilAPI.load_image_circle(this, userProfile.profileimg, binding.ivPicture);

        // 닉네임
        viewModel.setNickname(userProfile.nickname);
        
        // 성별
        SetGender(userProfile.gender);

        // 자기 소개
        viewModel.setIntroduction(userProfile.profileIntro);


        // 버튼 활성화 처리
        bSuccess_nickname = true;
        bSuccess_gender = true;

        Check_IsButton();


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


        binding.editIntrodution.addTextChangedListener(new TextWatcher() {
            String textBeforeEdit = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textBeforeEdit = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();

                if (binding.editIntrodution.getLineCount() > 4)
                {
                    binding.editIntrodution.setText(textBeforeEdit);
                    binding.editIntrodution.setSelection(binding.editIntrodution.length());
                    String str_res = getResources().getString(R.string.userinfo_editline_exception);
                    Toast.makeText(UserinfoExtActivity.this, str_res, Toast.LENGTH_SHORT).show();
                }
            }
        });


        UpdateKey();

    }

    private void SetGender(int gender)
    {
        if(gender == 1) {
            
            // 남성 선택
            
            // man
            UtilAPI.setButtonBackground(this, binding.buttonMan, R.drawable.man_selected);

            // woman
            UtilAPI.setButtonBackground(this, binding.buttonWoman, R.drawable.man);
        }
        else
        {
            // 여성 선택

            // woman
            UtilAPI.setButtonBackground(this, binding.buttonWoman, R.drawable.man_selected);

            // man
            UtilAPI.setButtonBackground(this, binding.buttonMan, R.drawable.man);
            
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
            case R.id.ic_close:
            {
                onBackPressed();
            }
            break;
            case R.id.button_nickname: {
                // 중복 검사 버튼

                Check_EditFocus_OnButton();

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

                Check_EditFocus_OnButton();
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

                Check_EditFocus_OnButton();
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

                Check_EditFocus_OnButton();

                if (!bSuccess_nickname) {
                    break;
                }

                if (!bSuccess_gender) {
                    binding.tvGenderSelect.setVisibility(View.VISIBLE);
                    break;
                }

                binding.progressBar.setVisibility(View.VISIBLE);

                UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();

                //userProfile.profileimg = mCurrentPhotoPath;
                //userProfile.nickname = viewModel.getNickname().getValue();
                //userProfile.profileIntro = viewModel.getIntroduction().getValue();
                //NetServiceManager.getinstance().sendValProfile(userProfile);


                String str_nickname = null;
                if(!userProfile.nickname.equals(viewModel.getNickname().getValue()))
                {
                    str_nickname = viewModel.getNickname().getValue();
                }

                String str_intro = null;
                if(!userProfile.profileIntro.equals(viewModel.getIntroduction().getValue()))
                {
                    str_intro = viewModel.getIntroduction().getValue();
                }

                String str_photopath = null;
                if(bChange_ProfileImage)
                {
                    str_photopath = mCurrentPhotoPath;
                }

                NetServiceManager.getinstance().setOnRecvValProfileListener(new NetServiceManager.OnRecvValProfileListener() {
                    @Override
                    public void onRecvValProfile(boolean validate) {

                        binding.progressBar.setVisibility(View.GONE);

                        // 수정하면 finish
                        finish();
                    }
                });

                // 유저 이미지를 보내야만 되는 이벤트 오는거 같음 - dlsmdla
                NetServiceManager.getinstance().sendValNewProfileExt(userProfile, str_nickname, str_intro, str_photopath);
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

    private void UpdateKey()
    {
        new HeightProvider(this, getWindowManager(), binding.layoutScrollEx, new HeightProvider.KeyboardHeightListener() {
            @Override
            public void onKeyboardHeightChanged(int keyboardHeight, boolean keyboardOpen, boolean isLandscape) {

                if(binding.editNickname.isFocused()) {
                }
                else
                {
                    ViewGroup.LayoutParams layoutParams = binding.scrollView.getLayoutParams();
                    Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int height = point.y;

                    final int height_top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 86, UserinfoExtActivity.this.getResources().getDisplayMetrics());
                    layoutParams.height = height - keyboardHeight - height_top;
                    binding.scrollView.setLayoutParams(layoutParams);

                    binding.scrollView.fullScroll(View.FOCUS_DOWN);
                    binding.scrollView.invalidate();
                }

                if(keyboardOpen) {

                }
                else
                {
                    binding.editNickname.clearFocus();
                    binding.editIntrodution.clearFocus();
                }
            }
        });
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

        bChange_ProfileImage = true;

        binding.ivPicture.setImageURI(albumURI);

    }

    @Override
    public void onBackPressed() {

        finish();
    }

}