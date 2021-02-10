package com.soulfriends.meditation.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.MainBinding;
import com.soulfriends.meditation.dlg.PsychologyDlg;
import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.model.MeditationDetailAlarm;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.parser.ResultData;
import com.soulfriends.meditation.util.ActivityStack;
import com.soulfriends.meditation.util.Notification;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.player.AudioPlayer;
import com.soulfriends.meditation.view.player.MeditationAudioManager;
import com.soulfriends.meditation.view.player.PlaybackStatus;
import com.soulfriends.meditation.viewmodel.MainViewModel;
import com.soulfriends.meditation.viewmodel.MainViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class MainActivity extends BaseActivity implements ResultListener {

    private MainBinding binding;
    private MainViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private MainViewModelFactory mainViewModelFactory;

    private HomeFragment homeFragment = new HomeFragment();
    private SleepFragment sleepFragment = new SleepFragment();
    private MeditationFragment meditationFragment = new MeditationFragment();
    private MusicFragment musicFragment = new MusicFragment();
    private ProfileFragment profileFragment = new ProfileFragment();

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;

    private MeditationContents meditationContents;


    MeditationAudioManager meditationAudioManager;

    private ArrayList<ResultData> resultData_list;

    private boolean bShowMiniPlayer;

    private String strPlaybackStatus;

    private boolean miniPlaying;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onStart() {
        super.onStart();

        bCallAlarm = false;

        // 메인에서는 액티비티 스택 초기화 처리한다.

        ActivityStack.instance().Clear();

        if (UtilAPI.s_bEvent_service_player_timer_stop) {

            //타이머에 의한 이벤트 발생 된 경우

            UtilAPI.s_bEvent_service_player_timer_stop = false;

            // 플레이 위치 초기화
            MeditationAudioManager.idle_start();

            // 프레임 레이어 조절
            UtilAPI.setMarginBottom(this, binding.container, 0);

            binding.miniLayout.setVisibility(View.GONE);
        }


        UtilAPI.s_bEvent_service_player_stop = false;

        // service onevent PlaybackStatus.STOPPED_END 체크
        if (UtilAPI.s_bEvent_service) {
            UtilAPI.s_bEvent_service = false;

            // 플레이 위치 초기화
            MeditationAudioManager.idle_start();

            // 프레임 레이어 조절
            UtilAPI.setMarginBottom(this, binding.container, 0);

            binding.miniLayout.setVisibility(View.GONE);

            // 세션으로 이동
            startActivity(new Intent(this, SessioinActivity.class));
            return;
        }

        UtilAPI.s_bEvent_service_main = true;

        strPlaybackStatus = PlaybackStatus.IDLE;

        //EventBus.getDefault().register(this);

        binding.miniLayout.setVisibility(View.GONE);

        bShowMiniPlayer = false;

        miniPlaying = false;


        if (meditationAudioManager.isPlayingAndPause()) {
            miniPlaying = true;

            start_mini_progressbar();

            bShowMiniPlayer = true;

            // 프레임 레이어 조절
            UtilAPI.setMarginBottom(this, binding.container, 54);

            meditationContents = NetServiceManager.getinstance().getCur_contents();

            String title = "";
            if (meditationContents != null) {
                title = meditationContents.title;
            }

            viewModel.setTitleTo(title);
            binding.miniLayout.setVisibility(View.VISIBLE);


            if(meditationContents.thumbnail == null)
            {
                UtilAPI.setImage(this, binding.ivMiniTitleIcon, R.drawable.basic_img);
            }
            else {
                UtilAPI.load_image(this, meditationContents.thumbnail, binding.ivMiniTitleIcon);
            }

            if (meditationAudioManager.isPlaying()) {
                UtilAPI.setImage(this, binding.ivPlay, R.drawable.miniplay_pause_btn);
            } else {
                UtilAPI.setImage(this, binding.ivPlay, R.drawable.miniplay_play_btn);
            }

            int progress = (int) ((MeditationAudioManager.getCurrentPosition() / (float) MeditationAudioManager.getDuration()) * 100.0f);

            binding.miniProgressBar.setProgress(progress);

        } else {
            // 프레임 레이어 조절
            UtilAPI.setMarginBottom(this, binding.container, 0);

            //  배경음악 플레이
            if (AudioPlayer.instance() != null) {
                AudioPlayer.instance().update();
            }
        }

        //---------------------------------------------------------
        // 알람 정보 얻기
        //---------------------------------------------------------

        binding.ivAlarmBg.setVisibility(View.GONE);
        binding.tvAlarmCount.setVisibility(View.GONE);


    }

    private boolean bCallAlarm = false;
    private void update_alarm()
    {
        if(bCallAlarm)
        {
            return;
        }

        NetServiceManager.getinstance().setOnRecvMyAlarmListListener(new NetServiceManager.OnRecvMyAlarmListListener() {
            @Override
            public void onRecvMyAlarmList(boolean validate) {

                if(validate)
                {


                    ArrayList<MeditationDetailAlarm> list_alarm = NetServiceManager.getinstance().mDetailAlarmDataList;

                    // 새로운 알림 정보 갯수 확인
                    int total_alarm = NetServiceManager.getinstance().calcNewAlarmNum();

                    String str_alarm = "";

                    binding.ivAlarmBg.setVisibility(View.VISIBLE);
                    binding.tvAlarmCount.setVisibility(View.VISIBLE);

                    if(total_alarm > 99)
                    {
                        str_alarm= "99+";
                    }
                    else
                    {
                        if(total_alarm == 0)
                        {
                            binding.ivAlarmBg.setVisibility(View.GONE);
                            binding.tvAlarmCount.setVisibility(View.GONE);
                        }
                        else {
                            str_alarm = String.valueOf(total_alarm);
                        }
                    }
                    viewModel.setAlarm(str_alarm);
                }
                else
                {

                }
                bCallAlarm = false;
            }
        });

        // null exception으로 인해서 주석처리함 dlsmdla
        NetServiceManager.getinstance().recvMyAlarmList();

        bCallAlarm = true;
    }

    @Override // 2020.12.20
    public void onBackPressed() {
        switch (UtilAPI.s_StrMainFragment) {
            case UtilAPI.FRAGMENT_HOME: {
                moveTaskToBack(true);
            }
            break;
            case UtilAPI.FRAGMENT_SLEEP:
            case UtilAPI.FRAGMENT_MEDITATION:
            case UtilAPI.FRAGMENT_MUSIC:
            case UtilAPI.FRAGMENT_PROFILE:
            {
                changeFragment(homeFragment, "HomeFragment");
                bottomNavigationView.setSelectedItemId(R.id.home_fragment);
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        if (mainViewModelFactory == null) {
            mainViewModelFactory = new MainViewModelFactory(this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), mainViewModelFactory).get(MainViewModel.class);
        binding.setViewModel(viewModel);

        bCallAlarm = false;

        //Notification.with(this, this);

        //Notification.instance().Register();


        binding.ivAlarmBg.setVisibility(View.GONE);
        binding.tvAlarmCount.setVisibility(View.GONE);

        meditationAudioManager = MeditationAudioManager.with(getApplicationContext());

        setUpNavigation();

        changeFragment(UtilAPI.s_StrMainFragment);
        //changeFragment(homeFragment, "HomeFragment");

        resultData_list = NetServiceManager.getinstance().xmlParser(R.xml.resultdata_result, this.getResources());

        // mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();

        UserProfile userProfile = NetServiceManager.getinstance().getUserProfile();
        if (userProfile.donefirstpopup == 0) {
            // 첫 로그인 일 경우
            PsychologyDlg psychologyDlg = new PsychologyDlg(MainActivity.this);
            psychologyDlg.show();

            userProfile.donefirstpopup = 1;

            NetServiceManager.getinstance().setOnRecvValProfileListener(new NetServiceManager.OnRecvValProfileListener() {
                @Override
                public void onRecvValProfile(boolean validate) {
                    if (validate == true) {
                        int xx = 0;
                    } else {
                        int yy = 0;
                    }
                }
            });

            NetServiceManager.getinstance().sendValProfile(userProfile);
        }
    }

    public void setUpNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        //NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_fragment:
                        changeFragment(homeFragment, "HomeFragment");
                        return true;
                    case R.id.sleep_fragment:
                        changeFragment(sleepFragment, "SleepFragment");
                        return true;
                    case R.id.meditation_fragment:
                        changeFragment(meditationFragment, "MeditationFragment");
                        return true;
                    case R.id.music_fragment:
                        changeFragment(musicFragment, "MusicFragment");
                        return true;
                    case R.id.profile_fragment:
                        changeFragment(profileFragment, "ProfileFragment");
                        return true;

                }
                return false;
            }
        });
    }


    public void changeFragment(String strfragment) {
        switch (strfragment) {
            case UtilAPI.FRAGMENT_HOME: {
                changeFragment(homeFragment, "HomeFragment");

                bottomNavigationView.setSelectedItemId(R.id.home_fragment);
            }
            break;
            case UtilAPI.FRAGMENT_SLEEP: {
                changeFragment(sleepFragment, "SleepFragment");

                bottomNavigationView.setSelectedItemId(R.id.sleep_fragment);
            }
            break;
            case UtilAPI.FRAGMENT_MEDITATION: {
                changeFragment(meditationFragment, "MeditationFragment");

                bottomNavigationView.setSelectedItemId(R.id.meditation_fragment);
            }
            break;
            case UtilAPI.FRAGMENT_MUSIC: {
                changeFragment(musicFragment, "MusicFragment");

                bottomNavigationView.setSelectedItemId(R.id.music_fragment);
            }
            break;
            case UtilAPI.FRAGMENT_PROFILE: {
                changeFragment(profileFragment, "ProfileFragment");

                bottomNavigationView.setSelectedItemId(R.id.profile_fragment);
            }
            break;
        }
    }

    public void changeFragment(Fragment fragment, String tagFragmentName) {

        update_alarm();

        UtilAPI.s_StrMainFragment = tagFragmentName;

        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fragment currentFragment = mFragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.detach(currentFragment);
        }

        Fragment fragmentTemp = mFragmentManager.findFragmentByTag(tagFragmentName);
        if (fragmentTemp == null) {
            fragmentTemp = fragment;
            fragmentTransaction.add(R.id.container, fragmentTemp, tagFragmentName);
        } else {
            fragmentTransaction.attach(fragmentTemp);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNowAllowingStateLoss();

        if (tagFragmentName.equals("HomeFragment")) {
            binding.imageView12.setImageResource(R.drawable.home_bg);

            binding.notiBtn.setVisibility(View.VISIBLE);
            binding.profileBtn.setVisibility(View.GONE);

        } else if (tagFragmentName.equals("MeditationFragment")) {
            binding.imageView12.setImageResource(R.drawable.meditation_bg);

            binding.notiBtn.setVisibility(View.VISIBLE);
            binding.profileBtn.setVisibility(View.GONE);

        } else if (tagFragmentName.equals("SleepFragment")) {
            binding.imageView12.setImageResource(R.drawable.sleep_bg);

            binding.notiBtn.setVisibility(View.VISIBLE);
            binding.profileBtn.setVisibility(View.GONE);

        } else if (tagFragmentName.equals("MusicFragment")) {
            binding.imageView12.setImageResource(R.drawable.music_bg);

            binding.notiBtn.setVisibility(View.VISIBLE);
            binding.profileBtn.setVisibility(View.GONE);

        } else if (tagFragmentName.equals("ProfileFragment")) {
            binding.imageView12.setImageResource(R.drawable.my_bg);

            binding.notiBtn.setVisibility(View.GONE);
            binding.profileBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSuccess(Integer id, String message) {
        switch (id) {
            case R.id.iv_close: {
                // 미니 플레이 닫기

                // 프레임 레이어 조절
                UtilAPI.setMarginBottom(this, binding.container, 0);

                MeditationAudioManager.stop();
                binding.miniLayout.setVisibility(View.GONE);
                meditationAudioManager.unbind();

                //  배경음악 플레이
                if (AudioPlayer.instance() != null) {
                    AudioPlayer.instance().update();
                }
            }
            break;
            case R.id.iv_playcontroler: {

                // 플레이 버튼
                if (MeditationAudioManager.isPlaying()) {
                    // 플레이 -> 정지
                    //Toast.makeText(getApplicationContext(),"플레이 -> 정지",Toast.LENGTH_SHORT).show();

                    UtilAPI.setImage(this, binding.ivPlay, R.drawable.miniplay_play_btn);

                    MeditationAudioManager.pause();
                } else {
                    // 정지 -> 플레이
                    //Toast.makeText(getApplicationContext(),"정지 -> 플레이",Toast.LENGTH_SHORT).show();
                    UtilAPI.setImage(this, binding.ivPlay, R.drawable.miniplay_pause_btn);

                    if (strPlaybackStatus == PlaybackStatus.STOPPED_END) {
                        MeditationAudioManager.resume();
                    } else {
                        MeditationAudioManager.play();
                    }
                }
            }
            break;
            case R.id.iv_Minibg: {
                // 미니 bg 선택시
                if (strPlaybackStatus == PlaybackStatus.STOPPED_END) {

                    // 플레이 위치 초기화
                    MeditationAudioManager.idle_start();

                    // 프레임 레이어 조절
                    UtilAPI.setMarginBottom(this, binding.container, 0);

                    MeditationAudioManager.stop();
                    binding.miniLayout.setVisibility(View.GONE);
                    meditationAudioManager.unbind();

                    //  배경음악 플레이
                    //AudioPlayer.instance().update();

                    // 세션으로 이동
                    ActivityStack.instance().Push(this, "");
                    startActivity(new Intent(this, SessioinActivity.class));

                    this.overridePendingTransition(0, 0);

                    finish();
                } else {

                    ActivityStack.instance().Push(this, "");

                    this.startActivity(new Intent(this, PlayerActivity.class));

                    this.overridePendingTransition(0, 0);

                    finish();
                }
            }
            break;
            case R.id.meun_btn: {

                this.startActivity(new Intent(this, SettingActivity.class));
                this.overridePendingTransition(0, 0);
                finish();

//                Intent intent = new Intent(UtilAPI.GetActivity() , TimerDialogActivity.class);
//                UtilAPI.GetActivity().startActivity(intent);

                //ActivityStack.instance().Push(MyContentsActivity.this, ""); // 메인액티비티여야 된다.

                //ActivityStack.instance().Push(this, "");
                //ChangeActivity(ContentsMakeActivity.class);


                // 테스트
//
//                this.startActivity(new Intent(this, UserinfoExtActivity.class));
//                this.overridePendingTransition(0, 0);
//                finish();
//
//                this.startActivity(new Intent(this, MyContentsActivity.class));
//                //this.startActivity(new Intent(this, FriendEditActivity.class));
//                //this.startActivity(new Intent(this, NotiActivity.class));
//
//                //this.startActivity(new Intent(this, FriendFindActivity.class));
//                //this.startActivity(new Intent(this, ProfileActivity.class));
//
//                //this.startActivity(new Intent(this, UserinfoExtActivity.class));
//
//                //this.startActivity(new Intent(this, InAppActivity.class));
//                this.overridePendingTransition(0, 0);
//
//                finish();
            }
            break;
            case R.id.noti_btn: {

                ActivityStack.instance().Push(this, "");

                ChangeActivity(NotiActivity.class);
            }
            break;
            case R.id.profile_btn: {

                ActivityStack.instance().Push(this, "");

                ChangeActivity(ProfileActivity.class);
            }
            break;
        }
    }

    @Override
    public void onEvent(String status) {

        if (strPlaybackStatus == status) return;

        strPlaybackStatus = status;

        switch (status) {

            case PlaybackStatus.ERROR: {

                miniPlaying = false;

                // 프레임 레이어 조절
                UtilAPI.setMarginBottom(this, binding.container, 0);

                MeditationAudioManager.stop();
                meditationAudioManager.unbind();

                binding.miniLayout.setVisibility(View.GONE);

                //  배경음악 플레이
                CallWithDelay_FormStopNoti(400, this);
            }
            break;
            case PlaybackStatus.TRACK_CHANGE: {
            }
            break;
            case PlaybackStatus.PAUSED_NOTI: {

                // 플레이 -> 정지
                UtilAPI.setImage(this, binding.ivPlay, R.drawable.miniplay_play_btn);

            }
            break;

            case PlaybackStatus.PLAYING_NOTI: {

                // 정지 -> 플레이
                UtilAPI.setImage(this, binding.ivPlay, R.drawable.miniplay_pause_btn);

            }
            break;
            case PlaybackStatus.STOPPED_END: {

                miniPlaying = false;

                // 플레이 완료가 되어 stop가 되면 play 버튼 상태로 변경
                // 플레이 -> 정지
                //Toast.makeText(getApplicationContext(),"[메인] 플레이 -> 정지", Toast.LENGTH_SHORT).show();
                UtilAPI.setImage(this, binding.ivPlay, R.drawable.miniplay_play_btn);

                //------------------------------------------------------------------
                // 미니 플레이어 플레이 완료 시 결과 화면 표시되게 수정
                //------------------------------------------------------------------
                // 플레이 위치 초기화
                MeditationAudioManager.idle_start();

                // 프레임 레이어 조절
                UtilAPI.setMarginBottom(this, binding.container, 0);

                binding.miniLayout.setVisibility(View.GONE);

                // 세션으로 이동
                //startActivity(new Intent(this, SessioinActivity.class));
            }
            break;
            case PlaybackStatus.STOP_NOTI: {

                // 노티에서 정지 이벤트  발생된 경우

                // 프레임 레이어 조절
                UtilAPI.setMarginBottom(this, binding.container, 0);

                MeditationAudioManager.stop();
                meditationAudioManager.unbind();

                binding.miniLayout.setVisibility(View.GONE);

                //  배경음악 플레이
                CallWithDelay_FormStopNoti(400, this);
            }
            break;
            case PlaybackStatus.STOP_TIMER: {

                // 프레임 레이어 조절
                UtilAPI.setMarginBottom(this, binding.container, 0);

                binding.miniLayout.setVisibility(View.GONE);//mini_layout

                //  배경음악 플레이
                if (AudioPlayer.instance() != null) {
                    AudioPlayer.instance().update();
                }
            }
            break;
        }

        super.onEvent(status);
    }

    public static void CallWithDelay_FormStopNoti(long miliseconds, final Activity activity) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    //  배경음악 플레이
                    if (AudioPlayer.instance() != null) {

                        AudioPlayer.instance().update();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }, miliseconds);
    }


    public void start_mini_progressbar() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (MeditationAudioManager.isPlaying()) {
                    int progress = (int) ((MeditationAudioManager.getCurrentPosition() / (float) MeditationAudioManager.getDuration()) * 100.0f);

                    binding.miniProgressBar.setProgress(progress);
                }

            }
        };

        // 새로운 스레드 실행 코드. 1초 단위로 현재 시각 표시 요청.
        class NewRunnable implements Runnable {
            @Override
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // 메인 스레드에 runnable 전달.
                    runOnUiThread(runnable);
                }
            }
        }

        NewRunnable nr = new NewRunnable();
        Thread t = new Thread(nr);
        t.start();
    }

    @Override
    protected void onStop() {

        //EventBus.getDefault().unregister(this);

        miniPlaying = false;

        UtilAPI.s_bEvent_service_main = false;

        super.onStop();
    }

    @Override
    public void onFailure(Integer id, String message) {

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}