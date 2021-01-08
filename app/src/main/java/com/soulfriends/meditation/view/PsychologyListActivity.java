package com.soulfriends.meditation.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.PsychologyListBinding;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
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

        // 목소리 분석 검사는 1일 1회만 가능하며 00시 기준으로
        // 리셋됨, 완료 시 완료된 내용으로 검사 카드 변경되고 비
        // 활성화 처리함

        UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();

        String voice_result_time = userProfile.finalvoicetestdate; // 보이스 테스트 시간이 들어 있어야 한다.

        if (voice_result_time != null) {
            if (voice_result_time.length() > 0) {
                // 1일 차이 여부
                long day = UtilAPI.GetDay_Date(voice_result_time);
                if (day > 0) {
                    // 1일 차이가 난다면
                    // 목소리 표시 -> 활성화

                    bVoiceEvent = true;

                    UtilAPI.setImage(this, binding.ivVoiceButton, R.drawable.test_voice);
                }
                else
                {
                    // 목소리 표시 -> 비활성화
                    // 클릭 이벤트 처리 하지 않도록 한다.

                    bVoiceEvent = false;

                    UtilAPI.setImage(this, binding.ivVoiceButton, R.drawable.test_voice_com);
                }
            }
        }
        else
        {
            bVoiceEvent = true;

            UtilAPI.setImage(this, binding.ivVoiceButton, R.drawable.test_voice);
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
                // 목소리 검사

                // 일단 주석 처리
                if(bVoiceEvent)
                {
                    Intent intent = new Intent(this, PsychologyVoiceTestActivity.class);
                    startActivity(intent);
                    finish(); // 2020.12.08
                }
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