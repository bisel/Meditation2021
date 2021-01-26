package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.ContentsMakeBinding;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.viewmodel.ContentsMakeViewModel;
import com.soulfriends.meditation.viewmodel.ContentsMakeViewModelFactory;

import java.util.ArrayList;

public class ContentsMakeActivity extends PhotoBaseActivity implements ResultListener {

    private ContentsMakeBinding binding;
    private ContentsMakeViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private ContentsMakeViewModelFactory contentsMakeViewModelFactory;


    public enum eAudioState
    {
        audio,
        ing,
        play,
    }

    private eAudioState audioState = eAudioState.audio;

    private boolean bCheck_TitleName = false;
    private boolean bCheck_Thumb = false;
    private boolean bCheck_Audio = false;
    private boolean bCheck_Background = false;

    private boolean bCheck_NextActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_contents_make);
        binding.setLifecycleOwner(this);

        if (contentsMakeViewModelFactory == null) {
            contentsMakeViewModelFactory = new ContentsMakeViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), contentsMakeViewModelFactory).get(ContentsMakeViewModel.class);
        binding.setViewModel(viewModel);

        // default
        binding.layoutThumbAdd.setVisibility(View.VISIBLE);

        binding.layoutThumbImage.setVisibility(View.GONE);

        // audio
        SetState_Audio(eAudioState.audio);

        binding.editTitle.setOnEditorActionListener(new EditText.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    //Toast.makeText(getApplicationContext(),"success db",Toast.LENGTH_SHORT).show();

                    hideKeyBoard();
                    binding.editTitle.clearFocus();

                    if(binding.editTitle.getText().length() > 0)
                    {
                        bCheck_TitleName = true;
                    }
                    else
                    {
                        bCheck_TitleName = false;
                    }

                    Check_NextButton();

                    return true;
                }
                return false;
            }
        });

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

    @Override
    protected void onStart() {
        super.onStart();

        if(UtilAPI.s_id_background_image > -1)
        {
            // 백그라운드 액티비티에서 이미지 id 받아옴
            binding.layoutBackgroundAdd.setVisibility(View.GONE);

            binding.layoutBackgroundImage.setVisibility(View.VISIBLE);

            ArrayList<Integer> list = new ArrayList<>();

            // 추후 음악 bg 넣도록 한다.
            list.add(R.drawable.bg_1);
            list.add(R.drawable.mask_group_8);
            list.add(R.drawable.music_wall);
            list.add(R.drawable.nature_wall);
            list.add(R.drawable.sleep_ms_wall);

            binding.ivBackgroundImage.setImageResource(list.get(UtilAPI.s_id_background_image));

            bCheck_Background = true;
        }
        else
        {
            binding.layoutBackgroundAdd.setVisibility(View.VISIBLE);

            binding.layoutBackgroundImage.setVisibility(View.GONE);
        }

        Check_NextButton();
    }

    @Override
    public void OnSuccess_SelectAudioFile(int duration) {

        // 오디오 파일 선택해서 파일 시간 표시하기

        // 시간단위
        String hour = String.valueOf(duration / (60 * 60 * 1000));

        // 분단위
        long getMin = duration - (duration / (60 * 60 * 1000));

        int int_hour_loc = Integer.parseInt(hour);
        long getMin_loc = getMin - (int_hour_loc * 60 * 60 * 1000);
        String min = String.valueOf(getMin_loc / (60 * 1000)); // 몫

        // 초단위
        String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지

        // 밀리세컨드 단위
        String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // 몫

        // 시간이 한자리면 0을 붙인다
        if (hour.length() == 1) {
            hour = "0" + hour;
        }

        // 분이 한자리면 0을 붙인다
        if (min.length() == 1) {
            min = "0" + min;
        }

        // 초가 한자리면 0을 붙인다
        if (second.length() == 1) {
            second = "0" + second;
        }

        String strTime = (min + ":" + second);

        binding.tvContext.setText(strTime);
    }


    @Override
    public void OnSuccess_ImageCrop()
    {
        // 썹네일 성공시
        // 이미지 show
        binding.layoutThumbAdd.setVisibility(View.GONE);

        binding.layoutThumbImage.setVisibility(View.VISIBLE);

        binding.ivPictureImage.setImageURI(albumURI);

        bCheck_Thumb = true;

        Check_NextButton();
    }

    @Override
    public void onSuccess(Integer id, String message) {

        switch (id) {
            case R.id.iv_backgroundbt: {

                // 배경 이미지 버튼 선택시

                Intent intent = new Intent(this, BackGroundActivity.class);
                intent.putExtra("title_text",binding.editTitle.getText());
                startActivity(intent);
                this.overridePendingTransition(0, 0);

                // 종료하지 않는다.
                //finish();

            }
            break;

            case R.id.layout_thumb_image:
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

            //녹음 / 업로드 레이아웃
            case R.id.iv_audio:
            {
                // 녹음

                SetState_Audio(eAudioState.ing);

                // 타이머 시작
                StartTimer();

            }
            break;
            case R.id.iv_upload:
            {
                // 업로드

                doSelectAudio();
            }
            break;

            // 녹음 진행
            case R.id.iv_audio_stop:
            {
                // 정지
                if(bStopButtonActive) {
                    // 정지 버튼 활성화 되면
                    Complete_Audio();
                }
            }
            break;

            //  재생 / 다시녹음
            case R.id.iv_audio_2:
            {
                // 재생
                SetState_Audio(eAudioState.play);

                // 타이머 시작
                StartTimer();

            }
            break;

            case R.id.iv_re_audiobt:
            {
                // 다시녹음

                SetState_Audio(eAudioState.ing);

                // 타이머 시작
                StartTimer();

            }
            break;

            case R.id.iv_next: {

                if (bCheck_NextActive) {
                }
                // 다음 이동

                // 제작 - 치유 감정 선택 화면 이동
                this.startActivity(new Intent(this, ContentsEmotionSelActivity.class));
                this.overridePendingTransition(0, 0);
                finish();


            }
            break;
        }

    }

    @Override
    public void onFailure(Integer id, String message) {

    }


    private void Check_NextButton()
    {
        if(bCheck_TitleName && bCheck_Thumb && bCheck_Audio && bCheck_Background)
        {
            UtilAPI.setImage(this, binding.ivNext, R.drawable.social_btn);

            bCheck_NextActive = true;
        }
        else
        {
            UtilAPI.setImage(this,binding.ivNext, R.drawable.social_btn_disable);

            bCheck_NextActive = false;
        }

    }

    private void Complete_Audio()
    {
        // 녹음 완료 할 경우
        SetState_Audio(eAudioState.play);
        StopTimer();
    }

    private void SetState_Audio(eAudioState state)
    {
        audioState = state;
        switch(state)
        {
            case audio:
            {
                // 녹음 / 업로드 레이아웃

                binding.layoutAudioUpload.setVisibility(View.VISIBLE);
                binding.layoutAudioIng.setVisibility(View.GONE);
                binding.layoutAudioPlay.setVisibility(View.GONE);

                // 초기화 stop 비활성화
                UtilAPI.setImage(this, binding.ivAudioStop, R.drawable.social_create_stop_disable);
            }
            break;
            case ing:
            {

                //녹음 진행

                binding.layoutAudioUpload.setVisibility(View.GONE);
                binding.layoutAudioIng.setVisibility(View.VISIBLE);
                binding.layoutAudioPlay.setVisibility(View.GONE);
            }
            break;
            case play:
            {

                //재생 / 다시녹음

                binding.layoutAudioUpload.setVisibility(View.GONE);
                binding.layoutAudioIng.setVisibility(View.GONE);
                binding.layoutAudioPlay.setVisibility(View.VISIBLE);


                bCheck_Audio = true;

                Check_NextButton();
            }
            break;
        }
    }
    
    //-------------------------------------------------------------
    // 녹음 타이머
    //-------------------------------------------------------------
    private Handler handlerUpdateLocation;
    private long total_time_milisecond = 0;
    private long accum_time_milisecond = 0;
    private long time_interval = 1000;

    private boolean bUseStopBt = false;
    private long second_timemilisecond_stopbt = 0;

    // 15분 이면 second_time = 15 * 60 * 60

    private boolean bStopButtonActive = false;

    public Handler mHandler;
    public Runnable mRunnable;

    public void Update_Time() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                OnMessage_Handle(msg);
            }
        };
    }

    public void OnMessage_Handle(Message msg)
    {
        // ui 표시
        switch (audioState)
        {
            case ing:
            {
                // 녹음
                //tv_context_1
                binding.tvContext1.setText((String)msg.obj);

                // 30초 경과 후에는
                // 정지 버튼 활성화 하도록 한다.
                if(accum_time_milisecond > 30 * 1000) {
                    UtilAPI.setImage(this, binding.ivAudioStop, R.drawable.social_create_stop);

                    bStopButtonActive = true;
                }
            }
            break;

            case play:
            {
                // 플레이
                //tv_context_2
                binding.tvContext2.setText((String)msg.obj);
            }
            break;
        }

    }

    public void StartTimer()
    {
        StopTimer();

        Update_Time();

        switch (audioState)
        {
            case ing:
            {
                // 녹음
                // 15분 이면 second_time = 15 * 60
                // 30 이면 second_time = 30

                StartTimer(15 * 60 , true, 30);
            }
            break;

            case play:
            {
                // 재생
                long play_time = 50; // 테스트
                StartTimer(play_time, false, 0);
            }
            break;
        }
    }


    public void StartTimer(long second_time, boolean bUseStopBt, long second_time_stopbt) {
        this.total_time_milisecond = second_time * 1000;

        this.bUseStopBt = bUseStopBt;

        if(bUseStopBt)
        {
            this.second_timemilisecond_stopbt = second_time_stopbt * 1000;
        }

        this.accum_time_milisecond = 0;

        if (handlerUpdateLocation == null) {
            handlerUpdateLocation = new Handler(Looper.getMainLooper());
            handlerUpdateLocation.post(runnableUpdateLocation);
        }
    }

    public void StopTimer() {
        if (handlerUpdateLocation != null) {
            handlerUpdateLocation.removeMessages(0);
            handlerUpdateLocation = null;
        }

        if (mHandler != null) {
            mHandler.removeMessages(0);
            mHandler = null;
        }
    }

    private Runnable runnableUpdateLocation = new Runnable() {
        @Override
        public void run() {
            // TODO: You can send location here
            accum_time_milisecond += time_interval;

            if (accum_time_milisecond >= total_time_milisecond) {
                
                // 15 분 경과 된 경우
                StopTimer();
                
                // 
                if(audioState == eAudioState.ing)
                {
                    // 자동으로 녹음 완료 처리

                    Complete_Audio();
                }
                
            } else {

                long reminder_time = accum_time_milisecond;//total_time_milisecond - accum_time_milisecond;

                // 시간단위
                String hour = String.valueOf(reminder_time / (60 * 60 * 1000));

                // 분단위
                long getMin = reminder_time - (reminder_time / (60 * 60 * 1000));

                int int_hour_loc = Integer.parseInt(hour);
                long getMin_loc = getMin - (int_hour_loc * 60 * 60 * 1000);
                String min = String.valueOf(getMin_loc / (60 * 1000)); // 몫

                // 초단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지

                // 밀리세컨드 단위
                String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // 몫

                // 시간이 한자리면 0을 붙인다
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }

                // 분이 한자리면 0을 붙인다
                if (min.length() == 1) {
                    min = "0" + min;
                }

                // 초가 한자리면 0을 붙인다
                if (second.length() == 1) {
                    second = "0" + second;
                }

//                // 시간이 0보다 크면 올림할 필요가 없음.
//                // 분만 있다면 올림 처리를 한다.
//                int int_hour = Integer.parseInt(hour);
//                if(int_hour == 0)
//                {
//                    // 시간이 0일때만 분에 대한 올림 처리
//                    int int_second = Integer.parseInt(second);
//                    if(int_second > 0) {
//                        int int_min = Integer.parseInt(min) + 1;
//
//                        min = String.valueOf(int_min);
//                        if (min.length() == 1) {
//                            min = "0" + min;
//                        }
//                    }
//                }

                // 테스트
                //String strTime = (hour + ":" + min + ":" + second);

                String strTime = (min + ":" + second);

                if (mHandler != null) {
                    //Message msg = mHandler.obtainMessage(count++);
                    //mHandler.sendEmptyMessage(strTime);

                    Message msg = mHandler.obtainMessage(0, strTime);
                    mHandler.sendMessage(msg);  // 메인 스레드로 메시지를 보냄
                }


                //status = PlaybackStatus.TIMER_COUNT; 이것 처럼 status에 값을 넣고 이벤트 보내면 안됨!!!
                //onPlayerStateChanged 이벤트가 발생하게 됨

                handlerUpdateLocation.postDelayed(this, time_interval); // 1초 마다 호출
            }
        }
    };


}