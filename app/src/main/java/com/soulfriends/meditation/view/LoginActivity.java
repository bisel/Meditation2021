package com.soulfriends.meditation.view;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.LoginBinding;
import com.soulfriends.meditation.model.UserLogin;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.AuthManager;
import com.soulfriends.meditation.util.PreferenceManager;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.viewmodel.LoginViewModel;
import com.soulfriends.meditation.viewmodel.LoginViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements ResultListener {

    private final AuthManager authManager = new AuthManager();

    private boolean bEmail_Success;
    private boolean bPassword_Success;
    private boolean isKeyboardShowing = false;
    private int keypadBaseHeight = 0;
    private UserLogin userLogin;
    private InputMethodManager imm;
    private LoginBinding binding;
    private LoginViewModelFactory loginViewModelFactory;
    private boolean bBackEventUse;

//    @Override
//    protected void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        authManager.onStop();
//    }

    @Override
    public void onBackPressed() {
        // 2020.12.20
        //if(bBackEventUse) {
        //    super.onBackPressed();
        //}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bEmail_Success = false;
        bPassword_Success = false;
        bBackEventUse = false;

        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            String activity_name = intent.getExtras().getString("activity_name");
            if (activity_name.length() > 0) {
                // 설정에서 로그아웃으로 온 경우임
                bBackEventUse = true;
            }
        }

       // FirebaseAuth.getInstance().signOut();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setLifecycleOwner(this);

        // network
        UtilAPI.SetNetConnection_Activity(this);

        userLogin = new UserLogin();

        if (loginViewModelFactory == null) {
            loginViewModelFactory = new LoginViewModelFactory(userLogin, this, this);
        }
        LoginViewModel viewModel = new ViewModelProvider(this.getViewModelStore(), loginViewModelFactory).get(LoginViewModel.class);

        binding.setViewModel(viewModel);

        authManager.init(this);
        authManager.setResultListener(this);

        binding.ivClose.setVisibility(View.GONE);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        UtilAPI.SetFullScreen(getWindow());

        binding.mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                binding.mainLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = binding.mainLayout.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadBaseHeight == 0) {
                    keypadBaseHeight = keypadHeight;
                }

                if (keypadHeight > screenHeight * 0.15) {
                    // 키보드 열렸을 때
                    if (!isKeyboardShowing) {
                        isKeyboardShowing = true;

                        binding.mainLayout.setPadding(0, 0, 0, (int) (keypadHeight * 0.5));
                        int height = keypadHeight - keypadBaseHeight;
                    }
                } else {
                    // 키보드가 닫혔을 때
                    if (isKeyboardShowing) {

                        binding.footerLayout.setVisibility(View.VISIBLE);
                        binding.ivClose.setVisibility(View.GONE);
                        binding.email.clearFocus();
                        binding.password.clearFocus();

                        isKeyboardShowing = false;
                        binding.mainLayout.setPadding(0, 0, 0, 0);

                        UtilAPI.SetFullScreen(getWindow());


                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        authManager.onActivityResult(this, requestCode, resultCode, data);
    }

    public void DoOnRecvProfile(boolean validate, int errorcode)
    {
        if(validate) {

            // 성공인 경우
            // 정상인지 확인용
            UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
            String nickname = userProfile.nickname;

            if(nickname == null || nickname.isEmpty())
            {
                // 실패 한 경우
                startActivity(new Intent(this, UserinfoActivity.class));

                binding.progressBar.setVisibility(View.GONE);

                finish();
                return;
            }

            // 2020.12.05 start 로그인이 성공했으므로 콘텐츠들을 새롭게 갱신해야 한다.
            //NetServiceManager.getinstance().reqEmotionAllContents(); // 2020.12.05 2st
            // 2020.12.05 end

            //Toast.makeText(this, nickname, Toast.LENGTH_SHORT).show();

            PreferenceManager.setString(this, "uid", userProfile.uid);

            binding.progressBar.setVisibility(View.GONE);
            finish();
            startActivity(new Intent(this, LoadingActivity.class));




        }
        else
        {
            // 실패 한 경우
            startActivity(new Intent(this, UserinfoActivity.class));

            binding.progressBar.setVisibility(View.GONE);

            finish();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onSuccess(Integer id, String message) {
        switch (id) {
            case 10:{
                // onTextChanged_Email
                // 이메일 텍스트 입력마다 들어옴
                checkIsValidEmail();
                
            }
            break;
            case 11:{
                // onTextChanged_Password
                // 패스워드 텍스트 입력마다 들어옴
                checkIsValidPassword();
            }
            break;

            case 50: {
                // 구글 / 이메일 인증
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
                userProfile.uid = uid;

                NetServiceManager.getinstance().recvUserProfile(uid);

                NetServiceManager.getinstance().setOnRecvProfileListener(new NetServiceManager.OnRecvProfileListener() {
                    @Override
                    public void onRecvProfile(boolean validate,int errorcode) {

                        DoOnRecvProfile(validate, errorcode);
                    }
                });


                //PreferenceManager.setString(this,"uid", uid);


                // dlsmdla

//                String nickname = PreferenceManager.getString(this, "nickname");
//                if (nickname.isEmpty()) {
//                    startActivity(new Intent(this, UserinfoActivity.class));
//
//                    binding.progressBar.setVisibility(View.GONE);
//
//                    finish();
//                } else {
//
//                    this.startActivity(new Intent(this, LoadingActivity.class));
//
//                    binding.progressBar.setVisibility(View.GONE);
//
//                    finish();
//                }
            }
            break;
            case 0: {
                // 계정 성공해서 로그인 된 경우

                // 닉네임이 있는 경우에는 메인메뉴로 이동하도록 한다.
//                String key = PreferenceManager.getString(this,"uid_nickname");
//                if(key.length() > 0)
//                {
//                    String[] array = key.split("##");
//
//                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                    if(array[0].contains(uid))
//                    {
//                        String nickname = array[1];
//
//                        PreferenceManager.setString(this,"uid", uid);
//                        PreferenceManager.setString(this,"nickname", nickname);
//
//                        UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
//                        userProfile.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                        userProfile.nickname = nickname;
//
//                        binding.progressBar.setVisibility(View.GONE);
//                        finish();
//                        this.startActivity(new Intent(this, MainActivity.class));
//                        break;
//                    }
//                }

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
                userProfile.uid = uid;

                //PreferenceManager.setString(this,"uid", uid);

                startActivity(new Intent(this, UserinfoActivity.class));

                binding.progressBar.setVisibility(View.GONE);

                finish();

            }
            break;
            case 100: {
                // 비밀번호 재설정 성공 한 경우
                binding.ivPasswordFindBalloon.setVisibility(View.VISIBLE);
                binding.tvPasswordFindBalloon.setText(getString(R.string.login_passwordfind_balloon));
                CallWithDelay_send_balloon(2000, this);
            }
            break;
            case R.id.iv_close: {
                // x 버튼 선택할 경우
                binding.footerLayout.setVisibility(View.VISIBLE);
                View view = this.getCurrentFocus();
                if(view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                binding.email.clearFocus();
                binding.password.clearFocus();
            }
            break;

            case R.id.bt_account: {
                // 회원 가입
                if (userLogin.isValidEmail()) {
                    binding.ivEmailCheck.setVisibility(View.VISIBLE);
                    binding.ivEmailCheck.setImageResource(R.drawable.icon_true);
                    bEmail_Success = true;

                    if(binding.ivEmailBalloon.getVisibility() == View.VISIBLE) {
                        binding.ivEmailBalloon.setVisibility(View.GONE);
                    }
                }
                else
                {
                    bEmail_Success = false;
                }

                if (userLogin.isValidPassword()) {
                    binding.ivPasswordCheck.setVisibility(View.VISIBLE);
                    binding.ivPasswordCheck.setImageResource(R.drawable.icon_true);
                    bPassword_Success = true;

                    if(binding.ivPasswordBalloon.getVisibility() == View.VISIBLE) {
                        binding.ivPasswordBalloon.setVisibility(View.GONE);
                    }
                }
                else
                {
                    bPassword_Success = false;
                }

                if (!bEmail_Success) {
                    binding.ivEmailCheck.setVisibility(View.VISIBLE);
                    binding.ivEmailCheck.setImageResource(R.drawable.icon_false);

                    if(binding.ivEmailBalloon.getVisibility() == View.GONE) {
                        binding.ivEmailBalloon.setVisibility(View.VISIBLE);
                        CallWithDelay_email_balloon(2000, this);
                    }
                }

                if (!bPassword_Success) {
                    binding.ivPasswordCheck.setVisibility(View.VISIBLE);
                    binding.ivPasswordCheck.setImageResource(R.drawable.icon_false);

                    if(binding.ivPasswordBalloon.getVisibility() == View.GONE) {
                        binding.ivPasswordBalloon.setVisibility(View.VISIBLE);
                        CallWithDelay_password_balloon(2000, this);
                    }
                }
                if (bEmail_Success && bPassword_Success) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    //authManager.DoAccountCreate(this, userLogin.getEmail(), userLogin.getPassword());

                    authManager.DoAccountCreate(this, userLogin.getEmail(), userLogin.getPassword());
                }

                if (isKeyboardShowing) {
                    View view = this.getCurrentFocus();
                    if(view != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                
            }
            break;
            case R.id.bt_login: {
                // 로그인 버튼 클릭한 경우
                if (userLogin.isValidEmail()) {
                    binding.ivEmailCheck.setVisibility(View.VISIBLE);
                    binding.ivEmailCheck.setImageResource(R.drawable.icon_true);
                    bEmail_Success = true;

                    if(binding.ivEmailBalloon.getVisibility() == View.VISIBLE) {
                        binding.ivEmailBalloon.setVisibility(View.GONE);
                    }
                }
                else
                {
                    bEmail_Success = false;
                }

                if (userLogin.isValidPassword()) {
                    binding.ivPasswordCheck.setVisibility(View.VISIBLE);
                    binding.ivPasswordCheck.setImageResource(R.drawable.icon_true);
                    bPassword_Success = true;

                    if(binding.ivPasswordBalloon.getVisibility() == View.VISIBLE) {
                        binding.ivPasswordBalloon.setVisibility(View.GONE);
                    }
                }
                else
                {
                    bPassword_Success = false;
                }

                if (!bEmail_Success) {
                    binding.ivEmailCheck.setVisibility(View.VISIBLE);
                    binding.ivEmailCheck.setImageResource(R.drawable.icon_false);

                    if(binding.ivEmailBalloon.getVisibility() == View.GONE) {
                        binding.ivEmailBalloon.setVisibility(View.VISIBLE);
                        CallWithDelay_email_balloon(2000, this);
                    }
                }

                if (!bPassword_Success) {
                    binding.ivPasswordCheck.setVisibility(View.VISIBLE);
                    binding.ivPasswordCheck.setImageResource(R.drawable.icon_false);

                    if(binding.ivPasswordBalloon.getVisibility() == View.GONE) {
                        binding.ivPasswordBalloon.setVisibility(View.VISIBLE);
                        CallWithDelay_password_balloon(2000, this);
                    }
                }
                if (bEmail_Success && bPassword_Success) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    authManager.DoEmail(this, userLogin.getEmail(), userLogin.getPassword());
                }

                if (isKeyboardShowing) {
                    View view = this.getCurrentFocus();
                    if(view != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
            break;
            case R.id.sign_in_google: {
                // 구글 버튼 클릭한 경우
                authManager.DoGoogle(this);
            }
            break;
            case R.id.tv_passwordFind: {
                // 비밀번호 찾기

                String uid = PreferenceManager.getString(this,"uid");
                if(uid.length() > 0) {
                    if (userLogin.isValidEmail()) {
                        // 이메일 주소가 정상일 경우
                        authManager.sendPasswordResetEmail(userLogin.email);
                    } else {
                        // 이메일 주소가 잘못된 경우
                        binding.ivPasswordFindBalloon.setVisibility(View.VISIBLE);
                        binding.tvPasswordFindBalloon.setText(getString(R.string.password_find_yestono));
                        CallWithDelay_send_balloon(2000, this);
                    }
                }
                else
                {
                    // 회원가입을 진행주세요
                    binding.ivPasswordFindBalloon.setVisibility(View.VISIBLE);
                    binding.tvPasswordFindBalloon.setText(getString(R.string.password_find_no));
                    CallWithDelay_send_balloon(2000, this);
                }
            }
            break;
            case R.id.email: {

                // 이메일 유효한 경우
                if (message.contains("onFocus_no")) {
                    // 포커스 해제된 경우
                    binding.footerLayout.setVisibility(View.VISIBLE);
                    binding.ivClose.setVisibility(View.GONE);

                    binding.ivEmailCheck.setVisibility(View.VISIBLE);
                    binding.ivEmailCheck.setImageResource(R.drawable.icon_true);

                    bEmail_Success = true;

                    if(binding.ivEmailBalloon.getVisibility() == View.VISIBLE) {
                        binding.ivEmailBalloon.setVisibility(View.GONE);
                    }
                } else {
                    // 포커스 된 경우
                    binding.footerLayout.setVisibility(View.GONE);
                    binding.ivClose.setVisibility(View.VISIBLE);


                }
            }
            break;
            case R.id.password: {

                // 패스워드 유효한 경우
                if (message.contains("onFocus_no")) {
                    // 포커스 해제된 경우

                    binding.footerLayout.setVisibility(View.VISIBLE);
                    binding.ivClose.setVisibility(View.GONE);

                    binding.ivPasswordCheck.setVisibility(View.VISIBLE);
                    binding.ivPasswordCheck.setImageResource(R.drawable.icon_true);

                    bPassword_Success = true;


                    // 말풍선
                    if(binding.ivPasswordBalloon.getVisibility() == View.VISIBLE) {
                        binding.ivPasswordBalloon.setVisibility(View.GONE);
                    }

                } else {
                    // 포커스 된 경우

                    binding.footerLayout.setVisibility(View.GONE);
                    binding.ivClose.setVisibility(View.VISIBLE);
                }
            }
            break;
        }
    }

    //@SuppressLint("NonConstantResourceId")
    @Override
    public void onFailure(Integer id, String message) {
        switch (id) {
            case 0: {
                // 이메일 계정 생성 실패일 경우
                binding.progressBar.setVisibility(View.GONE);

                CallWithDelay_account_error_balloon(2000, this);
            }
            break;
            case 50: {
                // 로그인 실패 한 경우
                binding.progressBar.setVisibility(View.GONE);

                CallWithDelay_login_error_balloon(2000, this);
            }
            break;
            case 100: {
                // 비밀번호 재설정 실패한 경우
                binding.ivPasswordFindBalloon.setVisibility(View.VISIBLE);
                binding.tvPasswordFindBalloon.setText(getString(R.string.password_find_retry));
                CallWithDelay_send_balloon(2000, this);
            }
            break;
            case R.id.email: {
                // 이메일 부적합 한 경우
                binding.ivEmailCheck.setVisibility(View.VISIBLE);
                binding.ivEmailCheck.setImageResource(R.drawable.icon_false);
                bEmail_Success = false;

                // 말풍선
                binding.ivEmailBalloon.setVisibility(View.VISIBLE);
                CallWithDelay_email_balloon(2000, this);

            }
            break;
            case R.id.password: {
                // 패스워드 부적합 한 경우
                // 말풍선
                binding.ivPasswordCheck.setVisibility(View.VISIBLE);
                binding.ivPasswordCheck.setImageResource(R.drawable.icon_false);

                // 말풍선
                binding.ivPasswordBalloon.setVisibility(View.VISIBLE);
                CallWithDelay_password_balloon(2000, this);

            }
            break;
        }
    }

    public void checkIsValidEmail()
    {
        if (userLogin.isValidEmail()) {
            binding.ivEmailCheck.setVisibility(View.VISIBLE);
            binding.ivEmailCheck.setImageResource(R.drawable.icon_true);
            bEmail_Success = true;
        }
        else
        {
            binding.ivEmailCheck.setVisibility(View.VISIBLE);
            binding.ivEmailCheck.setImageResource(R.drawable.icon_false);
            bEmail_Success = false;
        }
    }

    public void checkIsValidPassword()
    {
        if (userLogin.isValidPassword()) {
            binding.ivPasswordCheck.setVisibility(View.VISIBLE);
            binding.ivPasswordCheck.setImageResource(R.drawable.icon_true);
            bPassword_Success = true;
        }
        else
        {
            binding.ivPasswordCheck.setVisibility(View.VISIBLE);
            binding.ivPasswordCheck.setImageResource(R.drawable.icon_false);
            bPassword_Success = false;
        }
    }

    public void CallWithDelay_email_balloon(long miliseconds, final Activity activity) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    binding.ivEmailBalloon.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }, miliseconds);
    }

    public void CallWithDelay_password_balloon(long miliseconds, final Activity activity) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    binding.ivPasswordBalloon.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }, miliseconds);
    }

    public void CallWithDelay_send_balloon(long miliseconds, final Activity activity) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    binding.ivPasswordFindBalloon.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }, miliseconds);
    }

    public void CallWithDelay_login_error_balloon(long miliseconds, final Activity activity) {

        binding.ivEmailErrorBalloon.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    binding.ivEmailErrorBalloon.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }, miliseconds);
    }

    public void CallWithDelay_account_error_balloon(long miliseconds, final Activity activity) {

        binding.ivAccountErrorBalloon.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    binding.ivAccountErrorBalloon.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }, miliseconds);
    }
}

