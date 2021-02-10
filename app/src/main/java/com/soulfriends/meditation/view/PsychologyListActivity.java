package com.soulfriends.meditation.view;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.PsychologyListBinding;
import com.soulfriends.meditation.dlg.AlertLineOneOkPopup;
import com.soulfriends.meditation.dlg.AlertTermsPopup;
import com.soulfriends.meditation.dlg.CopyrightDialog;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.PreferenceManager;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.viewmodel.PsychologyListViewModel;
import com.soulfriends.meditation.viewmodel.PsychologyListViewModelFactory;

public class PsychologyListActivity extends BaseActivity implements ResultListener {

    private PsychologyListBinding binding;
    private PsychologyListViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private PsychologyListViewModelFactory psychologyListViewModelFactory;

    private boolean bVoiceEvent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_psychology_list);
        binding.setLifecycleOwner(this);

        if (psychologyListViewModelFactory == null) {
            psychologyListViewModelFactory = new PsychologyListViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), psychologyListViewModelFactory).get(PsychologyListViewModel.class);
        binding.setViewModel(viewModel);

        // service onevent PlaybackStatus.STOPPED_END 체크
        UtilAPI.s_bEvent_service_main = false;
        UtilAPI.s_bEvent_service_player = false;

        // 목소리 유료 여부에 따라 분리

        //boolean bPayUser = false; //<-- 유료 유저 인 여부 / 값을 받아서 처리를 해야 한다.
        //if(bPayUser)

        UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
        if(userProfile.isPayUser == 1)
        {
            // layout_voice_enable;
            // layout_voice_lock;
            // layout_voice_1day;

            // 목소리 분석 검사는 1일 1회만 가능하며 00시 기준으로
            // 리셋됨, 완료 시 완료된 내용으로 검사 카드 변경되고 비
            // 활성화 처리함

            binding.layoutVoiceEnable.setVisibility(View.VISIBLE);
            binding.layoutVoiceLock.setVisibility(View.GONE);
            binding.layoutVoice1day.setVisibility(View.GONE);

            //UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();

            String voice_result_time = userProfile.finalvoicetestdate; // 보이스 테스트 시간이 들어 있어야 한다.

            if (voice_result_time != null) {
                if (voice_result_time.length() > 0) {
                    // 1일 차이 여부
                    long day = UtilAPI.GetDay_Date(voice_result_time);
                    if (day > 0) {
                        // 1일 차이가 난다면
                        // 목소리 표시 -> 활성화

                        bVoiceEvent = true;

                        //UtilAPI.setImage(this, binding.ivVoiceButton, R.drawable.test_voice);

                        binding.layoutVoiceEnable.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        // 목소리 표시 -> 비활성화
                        // 클릭 이벤트 처리 하지 않도록 한다.

                        bVoiceEvent = false;

                        binding.layoutVoiceEnable.setVisibility(View.GONE);
                        binding.layoutVoice1day.setVisibility(View.VISIBLE);

                        //UtilAPI.setImage(this, binding.ivVoiceButton, R.drawable.test_voice_com);
                    }
                }
            }
            else
            {
                bVoiceEvent = true;

                //UtilAPI.setImage(this, binding.ivVoiceButton, R.drawable.test_voice);
            }

        }
        else
        {
            // 잠금 처리
            binding.layoutVoiceEnable.setVisibility(View.GONE);
            binding.layoutVoice1day.setVisibility(View.GONE);

            binding.layoutVoiceLock.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSuccess(Integer id, String message) {
        switch (id) {
            case R.id.ic_close: {
                // 닫기 버튼

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                this.overridePendingTransition(0, 0);

                finish();

            }
            break;
            case R.id.iv_feeling_button: {
                // 기분 어떤신가요?

                Intent intent = new Intent(this, PsychologyFeelingTestActivity.class);
                startActivity(intent);
                finish();  // 2020.12.08
            }
            break;
            case R.id.iv_color_button: {
                // 컬러 심리 검사

                Intent intent = new Intent(this, PsychologyColorTestActivity.class);
                startActivity(intent);
                finish(); // 2020.12.08
            }
            break;
            case R.id.iv_voice_button: {
                // 목소리 검사 가능

                // 일단 주석 처리
                if(bVoiceEvent)
                {
                    // 최초 한번
                    // 약관 동의
                    String str_voice_terms_agree = PreferenceManager.getString(this, "voice_terms_agree");
                    if(str_voice_terms_agree.equals("yes")) {
                        // 동의 한 경우

                        Intent intent = new Intent(this, PsychologyVoiceTestActivity.class);
                        startActivity(intent);
                        finish(); // 2020.12.08
                    }
                    else
                    {
                        // 동의 하지 않는 경우
                        // 약관 팝업 띄운다.

                        AlertTermsPopup alertDlg = new AlertTermsPopup(this, this);

                        alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        alertDlg.show();

                        ImageView iv_ok = alertDlg.findViewById(R.id.iv_ok);
                        iv_ok.setOnClickListener(v -> {

                            // 동의 한 경우
                            PreferenceManager.setString(this, "voice_terms_agree", "yes");

                            Intent intent = new Intent(this, PsychologyVoiceTestActivity.class);
                            startActivity(intent);
                            finish(); // 2020.12.08

                            alertDlg.dismiss();

                        });
                    }
                }
            }
            break;
            case R.id.iv_voice_button_lock: {
                // 목소리 검사

                // 잠금 상태임
                // 결제 화면으로 이동함
                Intent intent = new Intent(this, InAppActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.iv_voice_button_1day: {
                // 목소리 검사 가능
                // 1일 한번
                // 터치시 팝업
                AlertLineOneOkPopup alertDlg = new AlertLineOneOkPopup(this, this, AlertLineOneOkPopup.Dlg_Type.voice_1_1);

                alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDlg.show();

            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }
	
	
	@Override // 2020.12.20 , Close 버튼과 동일
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        finish();
    }
}