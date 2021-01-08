package com.soulfriends.meditation.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.TimerBinding;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.player.MeditationAudioManager;
import com.soulfriends.meditation.view.player.PlaybackStatus;
import com.soulfriends.meditation.viewmodel.TimerViewModel;
import com.soulfriends.meditation.viewmodel.TimerViewModelFactory;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class TimerActivity extends BaseActivity implements ResultListener {

    private TimerBinding binding;
    private TimerViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private TimerViewModelFactory timerViewModelFactory;


    private int picker_hour_index = 0;
    private int picker_minute_index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_timer);
        binding.setLifecycleOwner(this);

        if (timerViewModelFactory == null) {
            timerViewModelFactory = new TimerViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), timerViewModelFactory).get(TimerViewModel.class);
        binding.setViewModel(viewModel);

        // 시간
        binding.textViewHour.setTextColor(Color.WHITE);

        binding.npHour.setMinValue(0);
        binding.npHour.setMaxValue(9);

        List<String> displayedValues = new ArrayList<>();
        for (int i = 0; i < 10; i += 1) {
            displayedValues.add(String.format("%d시간", i));
        }
        binding.npHour.setDisplayedValues(displayedValues.toArray(new String[displayedValues.size()]));

        binding.npHour.setWrapSelectorWheel(true);

        binding.npHour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                picker_hour_index = newVal;
            }
        });

        // 분 -> 5분 부터 시작
        binding.textViewMinute.setTextColor(Color.WHITE);

        binding.npMinute.setMinValue(0);//0);
        binding.npMinute.setMaxValue((60 / 5) - 2);
        List<String> displayedValues_minute = new ArrayList<>();

        for (int i = 0; i < 60; i += 5) {
            displayedValues_minute.add(String.format("%d분", i + 5));
        }
        binding.npMinute.setDisplayedValues(displayedValues_minute.toArray(new String[displayedValues_minute.size()]));
        binding.npMinute.setWrapSelectorWheel(true);
        binding.npMinute.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                picker_minute_index = newVal;
            }
        });

        viewModel.setStrCurTime("00 : 00");

        // button state
        binding.buttonTimerEndActivity.setVisibility(View.GONE);
        binding.buttonTimerEndDeactivity.setVisibility(View.VISIBLE);
        binding.buttonTimerNew.setVisibility(View.GONE);
        binding.buttonTimerStart.setVisibility(View.VISIBLE);

        UtilAPI.AddActivityInPlayer(this);

    }

    protected void onStart() {
        super.onStart();

        if(MeditationAudioManager.getinstance().isTimerCount()) {

            binding.buttonTimerEndActivity.setVisibility(View.VISIBLE); // 타이머 종료 활성화  -> 보이게
            binding.buttonTimerEndDeactivity.setVisibility(View.GONE);  // 타이머 종료 비활성화  ->  안보이게
            binding.buttonTimerNew.setVisibility(View.VISIBLE); // 새타이머 설정 -> 보이게
            binding.buttonTimerStart.setVisibility(View.GONE);  // 시작 -> 안보이게

            //binding.tvHourminute.setTextColor(Color.parseColor("#40aeff"));
            binding.tvHourminute.setTextColor(Color.WHITE);

            String strTimerCount = MeditationAudioManager.getinstance().GetStrTimerCount();
            viewModel.setStrCurTime(strTimerCount);
        }

        //EventBus.getDefault().register(this);


    }

    @Override
    protected void onStop() {

        //EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        UtilAPI.RemoveActivityInPlayer(this);
    }

    @Subscribe
    public void onEvent(String status) {

        switch (status) {
            case PlaybackStatus.TIMER_COUNT: {


                String strTimerCount = MeditationAudioManager.getinstance().GetStrTimerCount();
                viewModel.setStrCurTime(strTimerCount);
            }
            break;
            case PlaybackStatus.STOPPED: {

                // 음악이 끝난 경우 발생되는 이벤트

                // 플레이 위치 초기화
                MeditationAudioManager.getinstance().idle_start();

                // MainActivity 이동
                ChangeActivity(MainActivity.class);

            }
            break;
            case PlaybackStatus.STOP_NOTI: {

                // 노티에서 정지 이벤트  발생된 경우
                MeditationAudioManager.getinstance().stop();
                MeditationAudioManager.getinstance().unbind();

                // MainActivity 이동
                ChangeActivity(MainActivity.class);
            }
            break;
        }
    }

    private void ChangeActivity(java.lang.Class<?> cls)
    {
        Intent intent = new Intent(this, cls);
        startActivity(intent);

        this.overridePendingTransition(0, 0);

        finish();
    }


    @Override
    public void onSuccess(Integer id, String message) {

        switch (id) {

            case R.id.iv_close: {

                Intent intent = new Intent(this, PlayerActivity.class);
                startActivity(intent);

                finish();

            }
            break;
            case R.id.button_timer_end_activity: {

                // 타이머 종료 활성화
                binding.buttonTimerEndActivity.setVisibility(View.GONE);
                binding.buttonTimerEndDeactivity.setVisibility(View.VISIBLE);
                binding.buttonTimerNew.setVisibility(View.GONE);
                binding.buttonTimerStart.setVisibility(View.VISIBLE);

                // 타이머 종료 로직 처리

                MeditationAudioManager.getinstance().StopTimer();

                NetServiceManager.getinstance().setCur_contents_timer(null);

                viewModel.setStrCurTime("00 : 00");
                binding.tvHourminute.setTextColor(Color.parseColor("#000000"));

            }
            break;
            case R.id.button_timer_end_deactivity: {
                // 타이머 종료 비활성화

            }
            break;
            case R.id.button_timer_new: {

                // 새 타이머 설정

                // 이전 스탑 처리
                MeditationAudioManager.getinstance().StopTimer();

                NetServiceManager.getinstance().setCur_contents_timer(null);
                
                binding.buttonTimerEndActivity.setVisibility(View.GONE);
                binding.buttonTimerEndDeactivity.setVisibility(View.VISIBLE);
                binding.buttonTimerNew.setVisibility(View.GONE);
                binding.buttonTimerStart.setVisibility(View.VISIBLE);

                viewModel.setStrCurTime("00 : 00");
                binding.tvHourminute.setTextColor(Color.parseColor("#000000"));

            }
            break;
            case R.id.button_timer_start: {
                // 시작

                binding.buttonTimerEndActivity.setVisibility(View.VISIBLE); // 타이머 종료 활성화  -> 보이게
                binding.buttonTimerEndDeactivity.setVisibility(View.GONE);  // 타이머 종료 비활성화  ->  안보이게
                binding.buttonTimerNew.setVisibility(View.VISIBLE); // 새타이머 설정 -> 보이게
                binding.buttonTimerStart.setVisibility(View.GONE);  // 시작 -> 안보이게

                viewModel.setStrCurTime("00 : 00");
                binding.tvHourminute.setTextColor(Color.parseColor("#40aeff"));

                // 카운트 시작 처리

                long total_second_time = 0;
                total_second_time += picker_hour_index * 60 * 60; // 60분 60 초
                total_second_time += (picker_minute_index + 1) * 5 * 60; // 여기서 5는 5분 간격띄우기 때문에 순수 인덱스에다 5를 곱함.


                MeditationAudioManager.getinstance().StartTimer(total_second_time);

                // 테스트 5초 후에 팝업이 나와야 한다.
                //MeditationAudioManager.getinstance().StartTimer(10);

                NetServiceManager.getinstance().setCur_contents_timer(NetServiceManager.getinstance().getCur_contents());

            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {


        Intent intent = new Intent(getApplicationContext(), TimerDialogActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        finish();
    }
}
