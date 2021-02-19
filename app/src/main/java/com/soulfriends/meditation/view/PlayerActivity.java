package com.soulfriends.meditation.view;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.PlayerBinding;
import com.soulfriends.meditation.dlg.AlertLineOneOkPopup;
import com.soulfriends.meditation.dlg.MenuModifyPopup;
import com.soulfriends.meditation.dlg.MenuReportPopup;
import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ActivityStack;
import com.soulfriends.meditation.util.PreferenceManager;
import com.soulfriends.meditation.util.RecvEventListener;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.player.AudioPlayer;
import com.soulfriends.meditation.view.player.MeditationAudioManager;
import com.soulfriends.meditation.view.player.PlaybackStatus;
import com.soulfriends.meditation.viewmodel.PlayerViewModel;
import com.soulfriends.meditation.viewmodel.PlayerViewModelFactory;

import java.util.ArrayList;

public class PlayerActivity extends BaseActivity implements RecvEventListener, ResultListener {

    private PlayerControlView playerView;
    //private ProgressBar progressBar;
    //private SimpleExoPlayer simpleExoPlayer;

    MeditationAudioManager meditationAudioManager;

    private PlayerBinding binding;
    private PlayerViewModel viewModel;

    private ViewModelStore viewModelStore = new ViewModelStore();
    private PlayerViewModelFactory playerViewModelFactory;

    private MeditationContents meditationContents;

    private boolean bBookmark_state;

    private boolean bOneEntry_Stopped;


    private long mLastClickTime = 0;

    private String strPlaybackStatus;

    private int reactionCode = 0;

    private boolean bEvent_service_interior = false;

    private UserProfile userProfile_friend;

    public AlertLineOneOkPopup alertDlg_ok;

    private boolean bEntryFromMyContentsUpload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        binding.setLifecycleOwner(this);

        UtilAPI.AddActivityInPlayer(this);

        meditationContents = NetServiceManager.getinstance().getCur_contents();

        if (playerViewModelFactory == null) {
            playerViewModelFactory = new PlayerViewModelFactory(meditationContents, this, this);
        }
        viewModel = new ViewModelProvider(this.getViewModelStore(), playerViewModelFactory).get(PlayerViewModel.class);
        binding.setViewModel(viewModel);

        // network
        UtilAPI.SetNetConnection_Activity(this);

        // string

        ArrayList<Integer> list_background = new ArrayList<>();
        list_background.add(R.drawable.bsleep_wall);
        list_background.add(R.drawable.med_wall);
        list_background.add(R.drawable.music_wall);
        list_background.add(R.drawable.nature_wall);
        list_background.add(R.drawable.sleep_md_wall);
        list_background.add(R.drawable.sleep_ms_wall);

        ArrayList<String> list_background_string = new ArrayList<>();
        list_background_string.add("bsleep_wall");
        list_background_string.add("med_wall");
        list_background_string.add("music_wall");
        list_background_string.add("nature_wall");
        list_background_string.add("sleep_md_wall");
        list_background_string.add("sleep_ms_wall");

        int bg_index = list_background_string.indexOf(meditationContents.bgimg);

        if (bg_index < 0) {
            for (int i = 0; i < list_background_string.size(); i++) {
                if (meditationContents.bgimg.contains(list_background_string.get(i))) {
                    bg_index = i;
                    break;
                }
            }
        }

        binding.ivBg1.setImageResource(list_background.get(bg_index));

        //UtilAPI.load_image(this, meditationContents.bgimg, binding.ivBg1);

        // 제작 , 마이, 친구 분리

        //UtilAPI.s_playerMode = UtilAPI.PlayerMode.friend;
        switch (UtilAPI.s_playerMode) {
            case base: {
                binding.layoutBase.setVisibility(View.VISIBLE);
                binding.layoutBaseMy.setVisibility(View.GONE);
                binding.layoutBaseFriend.setVisibility(View.GONE);
            }
            break;
            case my: {
                binding.layoutBase.setVisibility(View.GONE);
                binding.layoutBaseMy.setVisibility(View.VISIBLE);
                binding.layoutBaseFriend.setVisibility(View.GONE);

                // 내가 만든 콘텐츠 업로드에서 들어온 경우에는
                // 4. My에서 생성 및 수정된 소셜 콘텐츠가 업로드 성공되면 자동 플레이가 되는데 Back키를 눌렀거나,
                //    Back UI를 눌렀을 경우 플레이되는 소셜 재생을 멈춰야 함.
                // 정보 받기
                Intent intent = getIntent();
                bEntryFromMyContentsUpload = intent.getBooleanExtra("contents_upload", false);

            }
            break;
            case friend: {
                binding.layoutBase.setVisibility(View.GONE);
                binding.layoutBaseMy.setVisibility(View.GONE);
                binding.layoutBaseFriend.setVisibility(View.VISIBLE);

                SetFriendState();

                if (UtilAPI.s_userProfile_friend != null) {
                    viewModel.setNickname_friend(UtilAPI.s_userProfile_friend.nickname);
                }
                //userProfile_friend
            }
            break;
        }

        // 플레이 화면에서 "나레이터 저자"순으로 보이면 1, "아티스트"만 보이면 2, 아무것도 안보이면 ,0
        if (meditationContents.showtype == 1) {
            binding.layoutTextTwo.setVisibility(View.VISIBLE);
            binding.layoutArtist.setVisibility(View.GONE);
        } else if (meditationContents.showtype == 2) {
            binding.layoutTextTwo.setVisibility(View.GONE);
            binding.layoutArtist.setVisibility(View.VISIBLE);
        } else {
            binding.layoutTextTwo.setVisibility(View.GONE);
            binding.layoutArtist.setVisibility(View.GONE);
        }

        meditationAudioManager = MeditationAudioManager.with(this);

        meditationAudioManager.setReceivedEvent(this);

        playerView = findViewById(R.id.player_view);


        // 음악이 플레이중이면 정지하고 나가기
        FrameLayout ivStopEx = playerView.findViewById(R.id.stop_ex);
        ivStopEx.setOnClickListener(view -> {

            if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            meditationAudioManager.stop();
            meditationAudioManager.unbind();

            // MainActivity 이동
            //ChangeActivity(MainActivity.class);
            ActivityStack.instance().OnBack(this);
        });

        binding.playerView.setVisibility(View.GONE);

        // 좋아요 표시
        String uid = NetServiceManager.getinstance().getUserProfile().uid;

        // 2021.02.17
        if(meditationContents.ismycontents == 1){
            reactionCode = NetServiceManager.getinstance().reqSocialContentsFavoriteEvent(uid, meditationContents.uid);
        }else{
            reactionCode = NetServiceManager.getinstance().reqContentsFavoriteEvent(uid, meditationContents.uid);
        }

        if (reactionCode == 1) {
            // 좋아요
            // good 활성화
            UtilAPI.setButtonBackground(this, binding.btLike, R.drawable.like_btn_com);
        } else if (reactionCode == 2) {
            // 별로예요
            UtilAPI.setButtonBackground(this, binding.btLike, R.drawable.dislike_btn_com);
        }
    }

    private void play(boolean bPlay) {
        meditationAudioManager.SetTitle_Url(meditationContents.title);
        meditationAudioManager.SetThumbnail_Url(meditationContents.thumbnail_uri);



        if(UtilAPI.isConnected(this) == 0)
        {
            //인터넷 연결 안될 경우

            // 인터넷 연결 끊었을 경우 해당
            AlertLineOneOkPopup alertDlg = new AlertLineOneOkPopup(this, this, AlertLineOneOkPopup.Dlg_Type.error_retry);
            alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDlg.show();

            alertDlg.iv_ok.setOnClickListener(v -> {

                ActivityStack.instance().OnBack(this);

                alertDlg.dismiss();
            });
            return;
        }


        FirebaseStorage storage = FirebaseStorage.getInstance();

        //StorageReference storageRef = storage.getReferenceFromUrl("gs://meditation-m.appspot.com/test/play_bgm5.mp3");

        //String strTest = "gs://meditation-m.appspot.com/meditation/Sound/bookbros_aud.mp3";

        // 내가 만든 사운드 
        //gs://meditation-m.appspot.com/meditation/MyContentsSound/f5FL3rBPAbO1obZADqDzoFYwzvx2_20210202_test_1minute.mp3 <-- 파이어베이스 경로
        //gs://meditation-m.appspot.com/meditation/Sound/f5FL3rBPAbO1obZADqDzoFYwzvx2_20210202_test_1minute.mp3 <---- 디버깅 했을때 경로

        StorageReference storageRef = storage.getReferenceFromUrl(meditationContents.audio);
        //StorageReference storageRef = storage.getReferenceFromUrl(strTest);

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Download url of file

                String url = uri.toString();
                // String url = "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_asianet_mf_p";

                if (meditationAudioManager.getServiceBound()) {
                    SimpleExoPlayer player = meditationAudioManager.getplayerInstance();
                    playerView.setPlayer(player);
                    if (bPlay) {
                        meditationAudioManager.playOrPause(url);
                    } else {
                        meditationAudioManager.pause();
                    }
                } else {
                    meditationAudioManager.unbind();
                    meditationAudioManager.bind();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                int xx = 0;
            }
        });
    }

    public void StopState() {
        if (meditationAudioManager.getServiceBound()) {
            SimpleExoPlayer player = meditationAudioManager.getplayerInstance();
            playerView.setPlayer(player);

            //Toast.makeText(getApplicationContext(),"서비스 바인딩 성공",Toast.LENGTH_SHORT).show();

            play(false);
        }
    }

    @Override
    public void onReceivedEvent() {

        if (meditationAudioManager.getServiceBound()) {
            SimpleExoPlayer player = meditationAudioManager.getplayerInstance();
            playerView.setPlayer(player);

            if (meditationAudioManager.isPlayingAndPause()) {
            } else {
                play(true);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 소셜 콘텐츠 재생 후 닫기 눌러서 복귀 시 콘텐츠로 오게 수정
        UtilAPI.s_StrFragment_Profile_Tab = UtilAPI.TAB_CONTENTS;

        // 초기화
        viewModel.setTimer_time("");

        //플레이어 최초 실행시 안내 문구 제공
        boolean bool_balloon_timer = PreferenceManager.getBoolean(this.getBaseContext(), "bool_player_timer_balloon");
        if (bool_balloon_timer == false) {

            CallWithDelay_balloon_Timer(10000); // 2020.12.24 timer 10s처리

            PreferenceManager.setBoolean(this.getBaseContext(), "bool_player_timer_balloon", true);
        }

        // 타이머 정지된 경우
        if (UtilAPI.s_bEvent_service_player_timer_stop) {
            UtilAPI.s_bEvent_service_player_timer_stop = false;

            bEvent_service_interior = true;

            // 타이머 팝업에서 MainActivity 이동 처리함.
            return;
        }

        // 노티에서 정지 이벤트 클릭 한 경우
        if (UtilAPI.s_bEvent_service_player_stop) {
            UtilAPI.s_bEvent_service_player_stop = false;

            bEvent_service_interior = true;

            // MainActivity 이동
            //ChangeActivity(MainActivity.class);

            ActivityStack.instance().OnBack(this);
            return;
        }

        // 플레이 한번 완료 된 경우
        if (UtilAPI.s_bEvent_service) {
            UtilAPI.s_bEvent_service = false;

            bEvent_service_interior = true;

            // SessioinActivity 이동
            // 플레이어 액티비티를 넣줄 필요가 없다.
            ChangeActivity(SessioinActivity.class);

            return;
        }


        if (meditationAudioManager.isPlayingAndPause()) {
            onReceivedEvent();
        } else {
            StopState();
        }

        playerView.setRepeatToggleModes(1);
        playerView.setVisibility(View.VISIBLE);

        UtilAPI.s_bEvent_service_player = true;

        mLastClickTime = 0;

        strPlaybackStatus = PlaybackStatus.IDLE;

        //UtilAPI.player_track_count = 0;

        //EventBus.getDefault().register(this);

        //즐겨찾기 여부 : false - 즐겨찾기 안되어 있음. true - 즐겨찾기 되어있음

        bBookmark_state = NetServiceManager.getinstance().reqFavoriteContents(meditationContents.uid);
        if (bBookmark_state) {
            UtilAPI.setButtonBackground(this, binding.btBookmark, R.drawable.bookmark_btn_com);
        } else {
            UtilAPI.setButtonBackground(this, binding.btBookmark, R.drawable.bookmark_btn);
        }

        bOneEntry_Stopped = false;

        // 배경음 정지
        if (AudioPlayer.instance() != null) {
            AudioPlayer.instance().pause();
        }

        // 다르다면 타이머 종료 처리한다.
        if (MeditationAudioManager.getinstance().isTimerCount()) {
            if (NetServiceManager.getinstance().getCur_contents() != NetServiceManager.getinstance().getCur_contents_timer()) {
                MeditationAudioManager.getinstance().StopTimer();
            }
        }
    }

    public void OnEvent_Delete_Contents(MeditationContents meditationContents) {
        NetServiceManager.getinstance().setOnDelMyContentsListener(new NetServiceManager.OnDelMyContentsListener() {
            @Override
            public void onDelMyContentsListener(boolean validate) {
                if (validate) {
                    // 삭제 성공
                    NetServiceManager.getinstance().delLocalSocialContents(meditationContents.uid);
                    NetServiceManager.getinstance().delUserProfileMyContents(meditationContents.uid);

                    // 플레이어 강제 종료처리
                    MeditationAudioManager.stop_ext();
                    //meditationAudioManager.stop();
                    //meditationAudioManager.unbind();

                    onBackPressed();
                    //Toast.makeText(getApplicationContext(),"삭제 성공",Toast.LENGTH_SHORT).show();

                } else {
                    // 삭제 실패
                    //Toast.makeText(getApplicationContext(),"삭제 실패",Toast.LENGTH_SHORT).show();
                }
            }
        });
        NetServiceManager.getinstance().delMyContents(meditationContents);
    }

    @Override
    protected void onStop() {

        //EventBus.getDefault().unregister(this);

        UtilAPI.s_bEvent_service_player = false;

        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (bEvent_service_interior) {

            bEvent_service_interior = false;
        } else {
            meditationAudioManager.bind();
        }

        if (MeditationAudioManager.getinstance().isTimerCount()) {
            String strTimerCount = MeditationAudioManager.getinstance().GetStrTimerCount();
            viewModel.setTimer_time(strTimerCount);
            UtilAPI.setButtonBackground(this, binding.btTimer, R.drawable.timer_btn_com); // 2020.12.24

        } else {
            viewModel.setTimer_time("");
            UtilAPI.setButtonBackground(this, binding.btTimer, R.drawable.timer_btn); // 2020.12.24
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (alertDlg_ok != null) {
            if (alertDlg_ok.isShowing()) {
                alertDlg_ok.dismiss();
            }
        }

        UtilAPI.RemoveActivityInPlayer(this);

    }

    @Override
    public void onEvent(String status) {

        if (status == PlaybackStatus.TIMER_COUNT) {
            String strTimerCount = MeditationAudioManager.getinstance().GetStrTimerCount();
            viewModel.setTimer_time(strTimerCount);

            if (strTimerCount.equals("")) {
                UtilAPI.setButtonBackground(this, binding.btTimer, R.drawable.timer_btn); // 2020.12.24
            } else {
                UtilAPI.setButtonBackground(this, binding.btTimer, R.drawable.timer_btn_com); // 2020.12.24
            }
        }

        if (strPlaybackStatus == status) return;

        strPlaybackStatus = status;

        switch (status) {
            case PlaybackStatus.ERROR: {

                // 인터넷 연결 끊었을 경우 해당
                AlertLineOneOkPopup alertDlg = new AlertLineOneOkPopup(this, this, AlertLineOneOkPopup.Dlg_Type.error_retry);
                alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDlg.show();

                alertDlg.iv_ok.setOnClickListener(v -> {

                    MeditationAudioManager.stop_ext();
                    ActivityStack.instance().OnBack(this);

                    alertDlg.dismiss();
                });

            }
            break;
            case PlaybackStatus.IDLE:
                break;

            case PlaybackStatus.LOADING:
                break;

            case PlaybackStatus.STOPPED: {

                // 음악이 끝난 경우 발생되는 이벤트

                // 플레이 위치 초기화
                MeditationAudioManager.idle_start();

                // MainActivity 이동
                //ChangeActivity(MainActivity.class);
                ActivityStack.instance().OnBack(this);
            }
            break;
            case PlaybackStatus.STOP_NOTI: {

                // 노티에서 정지 이벤트  발생된 경우
                MeditationAudioManager.stop();
                meditationAudioManager.unbind();

                // MainActivity 이동
                //ChangeActivity(MainActivity.class);
                ActivityStack.instance().OnBack(this);
            }
            break;

            case PlaybackStatus.TRACK_CHANGE: {
            }
            break;


            case PlaybackStatus.STOPPED_END: {

                // 플레이 위치 초기화
                meditationAudioManager.idle_start();

                //this.overridePendingTransition(0, 0);

                // SessioinActivity 이동
                //ChangeActivity(SessioinActivity.class);
            }
            break;

            case PlaybackStatus.STOP_TIMER: {
                // 타이머 시간이 끝날때 발생되는 이벤트
                // MainActivity 이동
                //ChangeActivity(MainActivity.class);
                ActivityStack.instance().OnBack(this);
            }
            break;
        }

        super.onEvent(status);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onSuccess(Integer id, String message) {
        switch (id) {
            case R.id.bt_close: {

                // MainActivity 이동
                //ChangeActivity(MainActivity.class);

                if(bEntryFromMyContentsUpload)
                {
                    // 마이 콘텐츠 생성, 수정 업로드 일 경우 플레이어 종료하도록 한다.
                    MeditationAudioManager.stop_ext();
                }

                ActivityStack.instance().OnBack(this);
            }
            break;
//            case R.id.bt_download: {
//                // 다운로드
//
//            }
//            break;

            case R.id.bt_timer: {
                // 타이머

                // 현재 액티비티를 넣준다.
                ActivityStack.instance().Push(this, "");
                ChangeActivity(TimerActivity.class);
            }
            break;
            case R.id.bt_info: {

                // 정보
                ActivityStack.instance().Push(this, "");
                ChangeActivity(ContentsinfoActivity.class);
            }
            break;
            case R.id.bt_bookmark: {
                // 북마크

                if (bBookmark_state) {
                    NetServiceManager.getinstance().sendFavoriteContents(meditationContents.uid, false);
                    bBookmark_state = false;

                    UtilAPI.setButtonBackground(this, binding.btBookmark, R.drawable.bookmark_btn);
                } else {
                    // 말풍선 띄워준다.
                    NetServiceManager.getinstance().sendFavoriteContents(meditationContents.uid, true);
                    bBookmark_state = true;

                    UtilAPI.setButtonBackground(this, binding.btBookmark, R.drawable.bookmark_btn_com);

                    CallWithDelay_balloon(2000, this);
                }
            }
            break;
            case R.id.bt_like: {
                if (reactionCode == 1) {
                    // 좋아요 -> 별로예요
                    UtilAPI.setButtonBackground(this, binding.btLike, R.drawable.dislike_btn_com);

                    reactionCode = 2;

                    CallWithDelay_balloon_like(2000, this, binding.layoutDislikeBalloon);
                } else if (reactionCode == 2 || reactionCode == 0) {
                    // 별로예요 -> 좋아요
                    UtilAPI.setButtonBackground(this, binding.btLike, R.drawable.like_btn_com);

                    reactionCode = 1;

                    CallWithDelay_balloon_like(2000, this, binding.layoutLikeBalloon);
                }

                String uid = NetServiceManager.getinstance().getUserProfile().uid;
                //String uid_11 = NetServiceManager.getinstance().getUserProfile().uid;

                // 2020.12.04 로컬 콘텐츠 List 자체에 대한 수정도 같이 이루어져야 한다.
                if (meditationContents.ismycontents == 1) { // social
                    NetServiceManager.getinstance().sendFavoriteLocalEventExt(uid, meditationContents.uid, reactionCode, true);
                    NetServiceManager.getinstance().sendSocialFavoriteEventExt(uid, meditationContents.uid, reactionCode, true);
                } else {
                    NetServiceManager.getinstance().sendFavoriteLocalEvent(uid, meditationContents.uid, reactionCode);
                    NetServiceManager.getinstance().sendFavoriteEvent(uid, meditationContents.uid, reactionCode);
                }
            }
            break;

            //-------------------------------------------------
            // 친구
            //-------------------------------------------------
            case R.id.layout_nickname: {
                // 친구 닉네임 또는 썹네일 선택
                // 친구 프로필으로 이동 처리

                ActivityStack.instance().Push(this, "");
                ChangeActivity(ProfileFriendActivity.class);

            }
            break;
            case R.id.iv_friend_state: {
                // 친구 상태 아이콘 선택
                // 친구 프로필으로 이동 처리

                // 친구가 아닌 경우 : 프로필이미지 + 닉네임 + 친구추가 버튼 표시되며 선택 시
                // 친구 추가됨(안내 문구 제공 : 친구로 추가되었습니다.)
                // - 친구인 경우 : 프로필이미지 + 닉네임 + 친구 표시, 터치하면 친구삭제(안내 문
                //         구 제공 : 친구가 취소되었습니다.)
                // - 친구 요청중인 경우 : 프로필 이미지 + 닉네임 + 친구요청중 표시, 터치하면 친
                // 구 요청이 취소됨(안내 문구 제공 : 친구가 취소되었습니다.)
                // - 친구 프로필, 닉네임 선택 시 해당 친구의 프로필 페이지로 이동

                RequestFriendState();

            }
            break;
            case R.id.iv_modify_my: {

                View vModifyMy = findViewById(R.id.iv_modify_my);

                MenuModifyPopup menuPopup = new MenuModifyPopup(PlayerActivity.this, PlayerActivity.this);
                menuPopup.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                menuPopup.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                menuPopup.setCancelable(true);

                int[] outLocation = new int[2];
                vModifyMy.getLocationInWindow(outLocation);

                ViewGroup.LayoutParams params_cut = vModifyMy.getLayoutParams();

                WindowManager.LayoutParams params = menuPopup.getWindow().getAttributes();
                params.gravity = Gravity.TOP | Gravity.LEFT;
                params.x = outLocation[0] - params_cut.width / 2;
                params.y = outLocation[1] + params_cut.height + 8;
                menuPopup.getWindow().setAttributes(params);

                menuPopup.show();
                menuPopup.iv_modify.setOnClickListener(v1 -> {

                    // 콘텐츠 수정 액티비티로 이동

                    // 플레이어 강제 종료처리
                    MeditationAudioManager.stop_ext();
                    //meditationAudioManager.stop();
                    //meditationAudioManager.unbind();

                    UtilAPI.s_MeditationContents_temp = meditationContents;
                    //ActivityStack.instance().Push(PlayerActivity.this, ""); // 메인액티비티여야 된다.
                    ChangeActivity(ContentsEditActivity.class);
                    menuPopup.dismiss();
                });

                menuPopup.iv_delete.setOnClickListener(v1 -> {
                    // 콘텐츠 삭제
                    OnEvent_Delete_Contents(meditationContents);
                    menuPopup.dismiss();
                });

            }
            break;
            case R.id.iv_friend_report: {
                // 친구 신고하기

                View iv_Report = findViewById(R.id.iv_friend_report);
                MenuReportPopup menuPopup = new MenuReportPopup(PlayerActivity.this, PlayerActivity.this);
                menuPopup.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                menuPopup.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                menuPopup.setCancelable(true);

                int[] outLocation = new int[2];
                iv_Report.getLocationInWindow(outLocation);

                ViewGroup.LayoutParams params_cut = iv_Report.getLayoutParams();

                WindowManager.LayoutParams params = menuPopup.getWindow().getAttributes();
                params.gravity = Gravity.TOP | Gravity.LEFT;
                params.x = outLocation[0] - params_cut.width;
                params.y = outLocation[1] + params_cut.height + 8;
                menuPopup.getWindow().setAttributes(params);

                menuPopup.show();
                menuPopup.iv_report.setOnClickListener(v1 -> {

                    NetServiceManager.getinstance().reportedSocialContents(meditationContents);
                    menuPopup.dismiss();
                });
            }
            break;
        }
    }

    @Override
    public void onFailure(Integer id, String message) {

    }


    public void CallWithDelay_balloon(long miliseconds, final Activity activity) {

        binding.layoutFavoriteBalloon.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    binding.layoutFavoriteBalloon.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }, miliseconds);
    }

    public void CallWithDelay_balloon_Timer(long miliseconds) {

        binding.layoutTimerBalloon.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    binding.layoutTimerBalloon.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }, miliseconds);
    }


    public void CallWithDelay_balloon_like(long miliseconds, final Activity activity, RelativeLayout layout) {

        layout.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    layout.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }, miliseconds);
    }

    // 플레이어 프렌드 상태 처리
    //- 친구가 아닌 경우 : 프로필이미지 + 닉네임 + 친구추가 버튼 표시되며 선택 시 친구 추가됨(안내 문구 제공 : 친구로 추가되었습니다.)
    //- 친구인 경우 : 프로필이미지 + 닉네임 + 친구 표시, 터치하면 친구삭제(안내 문구 제공 : 친구가 취소되었습니다.)
    //- 친구 요청중인 경우 : 프로필 이미지 + 닉네임 + 친구요청중 표시, 터치하면 친구 요청이 취소됨(안내 문구 제공 : 친구가 취소되었습니다.)


    private int friend_state = -1;

    public void SetFriendState() {
        // 친구 추가 friend_state == 0
        // 친구  friend_state == 1
        // 친구 요청 중 friend_state == 2
        if(UtilAPI.s_userProfile_friend == null){  // s_userProfile_friend처리 추가 2021.02.03
            friend_state = 0;
            UtilAPI.setImage(this, binding.ivFriendState, R.drawable.player_addfriend);
        }else{
            // 친구 인지 아닌지 판별
            if (NetServiceManager.getinstance().findFriends(UtilAPI.s_userProfile_friend.uid) != null) {
                // 친구 임
                friend_state = 1;
                UtilAPI.setImage(this, binding.ivFriendState, R.drawable.player_friend);
            } else {

                // 현재 친구 요청 리스트
                if (NetServiceManager.getinstance().findFriendsRequest(UtilAPI.s_userProfile_friend.uid)) {
                    // 친구 요청중에 있는지 확인
                    friend_state = 2;
                    UtilAPI.setImage(this, binding.ivFriendState, R.drawable.player_requested);
                }
                else {
                    // 친구 아닌 상태
                    // 친구 추가
                    friend_state = 0;
                    UtilAPI.setImage(this, binding.ivFriendState, R.drawable.player_addfriend);
                }
            }

            // 현재 친구 요청 리스트
//            if (NetServiceManager.getinstance().findFriendsRequest(UtilAPI.s_userProfile_friend.uid)) {
//                // 친구 요청중에 있는지 확인
//                friend_state = 2;
//                UtilAPI.setImage(this, binding.ivFriendState, R.drawable.player_requested);
//            }
        }
        // 상태에 따라서 ui 표시를 하고
    }

    public void SendFriendState()
    {
        NetServiceManager.getinstance().setOnRecvFriendsListener(new NetServiceManager.OnRecvFriendsListener() {
            @Override
            public void onRecvFriends(boolean validate) {

                SetFriendState();
            }
        });

        NetServiceManager.getinstance().recvFriendsList();
    }

    public void RequestFriendState() {
        switch (friend_state) {
            case 0: {
                // 3. 친구 추가
                // sendFriendRequest(보내는 사람 uid,  받는 사람 uid)
                //        => 성공이 되면 "친구요청중"이라고 ux 변경

                NetServiceManager.getinstance().setOnSendFriendRequestListener(new NetServiceManager.OnSendFriendRequestListener() {
                    @Override
                    public void onSendFriendRequest(boolean validate) {

                        if (validate) {
                            alertDlg_ok = new AlertLineOneOkPopup(PlayerActivity.this, PlayerActivity.this, AlertLineOneOkPopup.Dlg_Type.friend_request);

                            alertDlg_ok.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            alertDlg_ok.show();
                            alertDlg_ok.iv_ok.setOnClickListener(v -> {

                                SendFriendState();

                                //UtilAPI.setImage(PlayerActivity.this, binding.ivFriendState, R.drawable.player_requested);
                                alertDlg_ok.dismiss();
                            });

                            //UtilAPI.setImage(PlayerActivity.this, binding.ivFriendState, R.drawable.player_requested);


                            //Toast.makeText(PlayerActivity.this, "친구로 요청중입니다.", Toast.LENGTH_SHORT).show();
                        } else {

                        }
                    }
                });

                NetServiceManager.getinstance().sendFriendRequest(NetServiceManager.getinstance().getUserProfile().uid, UtilAPI.s_userProfile_friend.uid);

            }
            break;
            case 1: {
                // 친구 삭제
                NetServiceManager.getinstance().setOnRemoveFriendListener(new NetServiceManager.OnRemoveFriendListener() {
                    @Override
                    public void onRemoveFriend(boolean validate) {

                        if (validate) {
                            alertDlg_ok = new AlertLineOneOkPopup(PlayerActivity.this, PlayerActivity.this, AlertLineOneOkPopup.Dlg_Type.friend_cancelled);

                            alertDlg_ok.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            alertDlg_ok.show();
                            alertDlg_ok.iv_ok.setOnClickListener(v -> {

                                SendFriendState();
                                alertDlg_ok.dismiss();
                            });

                           // UtilAPI.setImage(PlayerActivity.this, binding.ivFriendState, R.drawable.player_addfriend);
                            //Toast.makeText(PlayerActivity.this, "친구가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {

                        }
                    }
                });

                NetServiceManager.getinstance().removeFriend(NetServiceManager.getinstance().getUserProfile().uid, UtilAPI.s_userProfile_friend.uid);

            }
            break;
            case 2: {

                // 친구 취소
                NetServiceManager.getinstance().setOnCancelFriendRequestListener(new NetServiceManager.OnCancelFriendRequestListener() {
                    @Override
                    public void onCancelFriendRequest(boolean validate) {

                        if (validate) {
                            alertDlg_ok = new AlertLineOneOkPopup(PlayerActivity.this, PlayerActivity.this, AlertLineOneOkPopup.Dlg_Type.friend_cancelled);

                            alertDlg_ok.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            alertDlg_ok.show();
                            alertDlg_ok.iv_ok.setOnClickListener(v -> {

                                NetServiceManager.getinstance().removeLocalFriendsRequest(UtilAPI.s_userProfile_friend.uid);


                                SetFriendState();

                                alertDlg_ok.dismiss();
                                //friend_state = 0;
                                //UtilAPI.setImage(PlayerActivity.this, binding.ivFriendState, R.drawable.player_addfriend);

                            });
                        } else {

                        }
                    }
                });

                NetServiceManager.getinstance().cancelFriendRequest(NetServiceManager.getinstance().getUserProfile().uid, UtilAPI.s_userProfile_friend.uid);

            }
            break;
        }
    }


    @Override // 2020.12.20 , Close 버튼과 동일
    public void onBackPressed() {

        if(bEntryFromMyContentsUpload)
        {
            // 마이 콘텐츠 생성, 수정 업로드 일 경우 플레이어 종료하도록 한다.
            MeditationAudioManager.stop_ext();
        }

        ActivityStack.instance().OnBack(this);
        //ChangeActivity(MainActivity.class);
    }
}
