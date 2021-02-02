package com.soulfriends.meditation.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.PsychologyVoiceTestBinding;
import com.soulfriends.meditation.dlg.VoiceDialog;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.parser.VoiceData;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.view.player.AudioPlayer;
import com.soulfriends.meditation.view.player.MeditationAudioManager;
import com.soulfriends.meditation.viewmodel.PsychologyVoiceTestViewModel;
import com.soulfriends.meditation.viewmodel.PsychologyVoiceTestViewModelFactory;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class PsychologyVoiceTestActivity extends BaseActivity implements ResultListener {

    private PsychologyVoiceTestBinding binding;
    private PsychologyVoiceTestViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private PsychologyVoiceTestViewModelFactory psychologyVoiceTestViewModelFactory;

    private int step_voice = 0;

    private CountDownTimer m_countDownTimer = null;
    private AlertDialog.Builder alertDialog = null;

    private boolean bServiceAudio = false;

    private boolean bShowVoicePopup = false;

    private boolean bShowVoicePopUpInExceptionVoiceTest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_psychology_voice_test);
        binding.setLifecycleOwner(this);

        if (psychologyVoiceTestViewModelFactory == null) {
            psychologyVoiceTestViewModelFactory = new PsychologyVoiceTestViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), psychologyVoiceTestViewModelFactory).get(PsychologyVoiceTestViewModel.class);
        binding.setViewModel(viewModel);

        bShowVoicePopup = false;
        bShowVoicePopUpInExceptionVoiceTest = false;

        // 단계
        step_voice = 0;

        SetStep(step_voice);

        // 보이스 체크 확인
        checkPermission();


        // 배경음악 / 음악  정지

        bServiceAudio = false;
        if(MeditationAudioManager.isPlayingAndPause()) {
            MeditationAudioManager.pause();
            bServiceAudio = true;
        }
        else
        {
            if (AudioPlayer.instance() != null) {
                AudioPlayer.instance().pause();
            }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 버전과 같거나 이상이라면
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            {

                //if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //    Toast.makeText(this, "외부 저장소 사용을 위해 읽기/쓰기 필요", Toast.LENGTH_SHORT).show();
                //}
                requestPermissions(new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},
                                3);  //마지막 인자는 체크해야될 권한 갯수

            } else {
                //Toast.makeText(this, "권한 승인되었음", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //위 예시에서 requestPermission 메서드를 썼을시 , 마지막 매개변수에 0을 넣어 줬으므로, 매칭
        if (requestCode == 3) {
            // requestPermission의 두번째 매개변수는 배열이므로 아이템이 여러개 있을 수 있기 때문에 결과를 배열로 받는다.
            // 해당 예시는 요청 퍼미션이 한개 이므로 i=0 만 호출한다.
            int grantResultsNum = 0;
            grantResultsNum = grantResults.length;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                //해당 권한이 승낙된 경우.

            } else {
                // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                // Theme에 영향을 받아서 버튼 색깔이 안바뀌었음. colorOnPrimary 수정하닌깐 되었음.
                alertDialog = new AlertDialog.Builder(this, R.style.AlertMeditationDialog);
                alertDialog.setTitle("권한설정");
                alertDialog.setMessage("목소리 분석을 위해서는 해당 권한 설정이 필요합니다.");
                alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:"+
                        //        getApplicationContext().getPackageName()));
                        //startActivity(intent);
                        //dialogInterface.dismiss();
                        //alertDialog = null;

                        if (bServiceAudio) {
                        } else {
                            if (AudioPlayer.instance() != null) {
                                AudioPlayer.instance().restart();
                            }
                        }

                        finish();
                        Intent intent2 = new Intent(getApplicationContext(),PsychologyListActivity.class);
                        startActivity(intent2);
                    }
                });

                alertDialog.show();
            }
            return;
        }
    }

    private void SetStep(int step)
    {
        step_voice = step;

        switch(step)
        {
            case 0:
            {
                binding.icClose.setVisibility(View.VISIBLE);

                // 녹음버튼을 누르고 글을 읽어주세요.
                binding.layoutVoiceAudio.setVisibility(View.VISIBLE);
                binding.layoutVoiceAnalysis.setVisibility(View.GONE);

                binding.layoutVoiceStep1.setVisibility(View.VISIBLE);
                binding.layoutVoiceStep2.setVisibility(View.GONE);
                binding.layoutVoiceStep3.setVisibility(View.GONE);

                if(m_countDownTimer != null)
                {
                    m_countDownTimer.cancel();
                    m_countDownTimer = null;
                }

                //--------------------------------
                // 4개 문장 중 랜덤으로 한개를 선택한다.
                //--------------------------------

                ArrayList<VoiceData> list_voicedata = NetServiceManager.getinstance().getVoiceDataList();
                Random random = new Random();
                int random_index = random.nextInt(list_voicedata.size());


                VoiceData voiceData = list_voicedata.get(random_index);

                String strSentence = voiceData.desc;

                viewModel.setStrSentence(strSentence);

            }
            break;
            case 1:
            {
                // 녹음 진행 중...
                binding.layoutVoiceStep1.setVisibility(View.GONE);
                binding.layoutVoiceStep2.setVisibility(View.VISIBLE);
                binding.layoutVoiceStep3.setVisibility(View.GONE);

                // - 팝업 1종 추가(음성 녹음 중 기타 사유로 녹음 중단 시)
                // - 녹음 게이지 시작 위치 92px~(720 기준)
                int offset_percent = 15;

                binding.progressBar2.setProgress(offset_percent);

                // 프로그래스 바 진행률
                // 시간 표시

                // 완료가 되면

                // 1분 20 초
                long total_time = 80;//80;
                CountDownTimer countDownTimer = new CountDownTimer(total_time * 1000, 1000) {
                    public void onTick(long millisUntilFinished) {

                        long cur_second = millisUntilFinished / 1000L;

                        long mins = cur_second / 60;
                        long secs = cur_second - mins * 60;

                        String strTime = String.format(Locale.getDefault(), "%02d : %02d", mins, secs);

                        // 시간 표시
                        viewModel.setStrTimer(strTime);

                        // 프로그래스바 진행 표시

                        float f_curSecond = (float)cur_second;
                        float f_totalTime = (float)total_time;

                        //float progress = 100 - (f_curSecond / f_totalTime) * 100;
                        float progress = (100 - offset_percent) - (f_curSecond / f_totalTime) * (100 - offset_percent);

                        if(progress < 0 ) progress = 0;

                        binding.progressBar2.setProgress((int)(progress + offset_percent));
                    }

                    public void onFinish() {

                        SetStep(2);
                    }
                }.start();

                m_countDownTimer = countDownTimer;

                // 보이스 시작
                NetServiceManager.getinstance().StartVoiceTest();

            }
            break;
            case 2:
            {
                // 녹음한 내용을 전송 중입니다.
                binding.layoutVoiceStep1.setVisibility(View.GONE);
                binding.layoutVoiceStep2.setVisibility(View.GONE);
                binding.layoutVoiceStep3.setVisibility(View.VISIBLE);


                // 전송이 완료가 되면
                // 전송 진행 아이콘 회전 처리

                binding.progressBarIcon.setVisibility(View.VISIBLE);

                // SetStep(3);


                NetServiceManager.getinstance().setOnRecvDoneVoiceTestListener(new NetServiceManager.OnRecvDoneVoiceTestListener() {
                    @Override
                    public void onRecvDoneVoiceTest(boolean validate) {

                        if(validate == false)
                        {
                            ShowVoiceDialog();
                        }
                        else {
                            SetStep(3);
                        }
                    }
                });

                NetServiceManager.getinstance().DoneVoiceTest();

//                // 6초 테스트
//                CountDownTimer countDownTimer = new CountDownTimer(6 * 1000, 1000) {
//                    public void onTick(long millisUntilFinished) {
//                    }
//                    public void onFinish() {
//                        SetStep(3);
//                    }
//                }.start();

            }
            break;
            case 3:
            {
                // 목소리 감정 분석 중입니다.


                binding.icClose.setVisibility(View.GONE);

                binding.layoutVoiceAudio.setVisibility(View.GONE);
                binding.layoutVoiceAnalysis.setVisibility(View.VISIBLE);

                binding.layoutVoiceStep1.setVisibility(View.GONE);
                binding.layoutVoiceStep2.setVisibility(View.GONE);
                binding.layoutVoiceStep3.setVisibility(View.GONE);

                final AnimationDrawable drawable = (AnimationDrawable) binding.ivAnimAnalysis.getBackground();

                drawable.start();
                
                // 분석 완료가 되면 
                // drawable.stop();
                // SetStep(4);
                // 처리

                // 테스트로 4초 있다가 결과 화면 이동 처리한다.

                NetServiceManager.getinstance().setRecvDoneVoiceAnalysisListener(new NetServiceManager.OnRecvDoneVoiceAnalysisListener() {
                    @Override
                    public void onRecvDoneVoiceAnalysis(boolean validate) {
                        if(NetServiceManager.getinstance().isAppOnForeground() == true){
                            Log.d("NetServiceManager","onRecvDoneVoiceAnalysis forground ");
                            if(step_voice == 3 && !bShowVoicePopup){
                                if(validate == false)
                                {
                                    ShowVoiceDialog();
                                }
                                else {
                                    SetStep(4);
                                }
                            }

                        }else{
                            Log.d("NetServiceManager","onRecvDoneVoiceAnalysis backround ");
                            //ShowVoiceDialog();
                            bShowVoicePopUpInExceptionVoiceTest = true;
                        }

                    }
                });

                NetServiceManager.getinstance().StartVoiceAnalysis();

//                // 6초 테스트
//                CountDownTimer countDownTimer = new CountDownTimer(6 * 1000, 1000) {
//                    public void onTick(long millisUntilFinished) {
//                    }
//                    public void onFinish() {
//
//                        drawable.stop();
//                        SetStep(4);
//                    }
//                }.start();

            }
            break;
            case 4:
            {
                // 목소리 결과 화면으로 이동 처리
                //NetServiceManager.getinstance().checkColorTest(list_page_selectid);

                if (bServiceAudio) {
                } else {
                    if (AudioPlayer.instance() != null) {
                        AudioPlayer.instance().restart();
                    }
                }

                Intent intent = new Intent(this, PsychologyResultActivity.class);
                intent.putExtra("activity_name","voice");
                startActivity(intent);

                finish();
            }
            break;
        }
    }

    @Override
    public void onSuccess(Integer id, String message) {
        switch (id) {
            case R.id.ic_close: {

                // 현재 상태에 따라서 나갈 경우 stop 처리를 해야한다.

                if(step_voice == 1 ||  step_voice == 2)
                {
                    NetServiceManager.getinstance().StopVoiceTest();
                }
                else if(step_voice == 3)
                {
                    NetServiceManager.getinstance().StopVoiceAnalysis();
                }

                if(step_voice == 0) {

                    if (bServiceAudio) {
                    } else {
                        if (AudioPlayer.instance() != null) {
                            AudioPlayer.instance().restart();
                        }
                    }

                    Intent intent = new Intent(this, PsychologyListActivity.class);
                    startActivity(intent);

                    finish();
                }
                else {
                    //SetStep(0);
                    ShowVoiceDialog();
                }
            }
            break;
            case R.id.iv_voice_button1: {

                // 녹음 버튼 클릭한 경우
                // 이동 -> 녹음 진행 중...
                SetStep(1);

            }
            break;

            case R.id.iv_voice_x_button2: {

                // 취소
                ShowVoiceDialog();
                //SetStep(0);

                //NetServiceManager.getinstance().StopVoiceTest();

            }
            break;

            case R.id.iv_voice_x_button3: {

                // 취소
                ShowVoiceDialog();
                //SetStep(0);

                //NetServiceManager.getinstance().StopVoiceTest();
            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {

        super.onStart();

        if(step_voice == 1 || step_voice == 2 || step_voice == 3 ) {
            ShowVoiceDialog();
        }

        if(bShowVoicePopUpInExceptionVoiceTest == true){
            ShowVoiceDialog();
            bShowVoicePopUpInExceptionVoiceTest = false;
        }

    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(step_voice == 1 || step_voice == 2 || step_voice == 3 ) {
//            ShowVoiceDialog();
//        }
    }

    @Override
    protected void onUserLeaveHint()
    {
        super.onUserLeaveHint();
    }

    public void ShowVoiceDialog()
    {

        if(bShowVoicePopup)
        {
            return;
        }

        bShowVoicePopup = true;

        boolean bVoiceAnalysis = false;
        if(step_voice == 1 ||  step_voice == 2)
        {
            NetServiceManager.getinstance().StopVoiceTest();
        }
        else if(step_voice == 3)
        {
            bVoiceAnalysis = true;
            NetServiceManager.getinstance().StopVoiceAnalysis();
        }

        if(m_countDownTimer != null)
        {
            m_countDownTimer.cancel();
            m_countDownTimer = null;
        }

        VoiceDialog alertDialog = new VoiceDialog(this, this, bVoiceAnalysis);
        alertDialog.setCancelable(false);
        alertDialog.show();

        // ok
        ImageView iv_ok = alertDialog.findViewById(R.id.iv_ok);
        iv_ok.setOnClickListener(v -> {

            SetStep(0);
            alertDialog.dismiss();

            bShowVoicePopup = false;

        });

    }


    @Override // 2020.12.20 , Close 버튼과 동일
    public void onBackPressed() {

        if(step_voice == 0) {

            if (bServiceAudio) {
            } else {
                if (AudioPlayer.instance() != null) {
                    AudioPlayer.instance().restart();
                }
            }

            Intent intent = new Intent(this, PsychologyListActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            //SetStep(0);
            ShowVoiceDialog();
        }
    }

}