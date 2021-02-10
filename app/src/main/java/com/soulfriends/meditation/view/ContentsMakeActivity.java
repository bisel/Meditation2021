package com.soulfriends.meditation.view;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.soulfriends.meditation.R;
import com.soulfriends.meditation.databinding.ContentsMakeBinding;
import com.soulfriends.meditation.dlg.AlertLineTwoPopup;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.ActivityStack;
import com.soulfriends.meditation.util.ResultListener;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.player.AudioPlayer;
import com.soulfriends.meditation.view.player.MeditationAudioManager;
import com.soulfriends.meditation.view.player.SoundPlayer;
import com.soulfriends.meditation.viewmodel.ContentsMakeViewModel;
import com.soulfriends.meditation.viewmodel.ContentsMakeViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ContentsMakeActivity extends PhotoBaseActivity implements ResultListener {

    private ContentsMakeBinding binding;
    private ContentsMakeViewModel viewModel;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private ContentsMakeViewModelFactory contentsMakeViewModelFactory;
    private AlertDialog.Builder alertDialog = null;


    public enum eAudioState {
        audio,
        ing,
        play,
    }

    private eAudioState audioState = eAudioState.audio;

    private boolean bCheck_TitleName = false;
    private boolean bCheck_Thumb = true;
    private boolean bCheck_Audio = false;
    private boolean bCheck_Background = true;

    private boolean bCheck_NextActive = false;

    private boolean bAudioIng = false;

    private boolean bPlayerImage_ButtonState = false;


    private String Upload_Audio_filePath = "";

    private int playtime_sec_audio = 0;

    private boolean bComplete_Audio_Record = false; // 녹음 완료 여부

    ArrayList<Integer> list_background = new ArrayList<>();
    ArrayList<String> list_background_string = new ArrayList<>();

    private boolean bServiceAudio = false;

    private long audio_complete_time_milisecond = 0;

    private boolean isKeyboardShowing = false;
    private int keypadBaseHeight = 0;

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

        // 배경음악과 오디오 재생 중이면 정지되도록 한다.

        bServiceAudio = false;
        if (MeditationAudioManager.isPlayingAndPause()) {

            if (MeditationAudioManager.getinstance().isTimerCount()) {
                MeditationAudioManager.getinstance().StopTimer();
            }
            MeditationAudioManager.stop_ext();
            bServiceAudio = true;
        } else {
            if (AudioPlayer.instance() != null) {
                AudioPlayer.instance().pause();
            }
        }

        // audio
        SetState_Audio(eAudioState.audio);

        binding.editTitle.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Toast.makeText(getApplicationContext(),"success db",Toast.LENGTH_SHORT).show();

                    hideKeyBoard();

                    Check_TitleEdit();

                    return true;
                }
                return false;
            }
        });


        UtilAPI.ClearActivity_Temp();

        checkPermission();

        //----------------------------------------------------
        // 썹네일, 배경 디폴트 이미지 설정 iv_background_image
        //----------------------------------------------------
        UtilAPI.setImage(this, binding.ivPicture, R.drawable.basic_img);

        UtilAPI.setImage(this, binding.ivBackgroundbt, R.drawable.bsleep_wall);

        //----------------------------------------------------
        // 키패드 처리
        //----------------------------------------------------
        binding.ll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                binding.ll.getWindowVisibleDisplayFrame(r);
                int screenHeight = binding.ll.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadBaseHeight == 0) {
                    keypadBaseHeight = keypadHeight;
                }

                if (keypadHeight > screenHeight * 0.15) {
                    // 키보드 열렸을 때
                    if (!isKeyboardShowing) {
                        isKeyboardShowing = true;

                        binding.ll.setPadding(0, 0, 0, (int) (keypadHeight * 0.5));
                        int height = keypadHeight - keypadBaseHeight;
                    }
                } else {
                    // 키보드가 닫혔을 때
                    if (isKeyboardShowing) {


                        binding.editTitle.clearFocus();

                        isKeyboardShowing = false;
                        binding.ll.setPadding(0, 0, 0, 0);


                    }
                }
            }
        });
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {
            // do your stuff
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    private void Check_TitleEdit() {
        binding.editTitle.clearFocus();

        if (viewModel.title.getValue() != null) {
            if (viewModel.title.getValue().length() > 0) {
                bCheck_TitleName = true;
            } else {
                bCheck_TitleName = false;
            }
        } else {
            bCheck_TitleName = false;
        }

        Check_NextButton();
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

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 버전과 같거나 이상이라면
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                //if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //    Toast.makeText(this, "외부 저장소 사용을 위해 읽기/쓰기 필요", Toast.LENGTH_SHORT).show();
                //}
                requestPermissions(new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        3);  //마지막 인자는 체크해야될 권한 갯수

            } else {
                //Toast.makeText(this, "권한 승인되었음", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Activity myActivity = this;
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
                alertDialog.setMessage("목소리 녹음을 위해서는 해당 권한 설정이 필요합니다.");
                alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:"+
                        //        getApplicationContext().getPackageName()));
                        //startActivity(intent);
                        //dialogInterface.dismiss();
                        //alertDialog = null;

//                        if (bServiceAudio) {
//                        } else {
//                            if (AudioPlayer.instance() != null) {
//                                AudioPlayer.instance().restart();
//                            }
//                        }

                        //finish();

                        ActivityStack.instance().OnBack(myActivity);
                        UtilAPI.ClearActivity_Temp();


                        //Intent intent2 = new Intent(getApplicationContext(),PsychologyListActivity.class);
                        //startActivity(intent2);
                    }
                });

                alertDialog.show();
            }
            return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // string

        list_background_string.clear();

        // string
        list_background_string.add("bsleep_wall");
        list_background_string.add("med_wall");
        list_background_string.add("music_wall");
        list_background_string.add("nature_wall");
        list_background_string.add("sleep_md_wall");
        list_background_string.add("sleep_ms_wall");

        if (UtilAPI.s_id_backimamge_makecontents > -1) {
            // 백그라운드 액티비티에서 이미지 id 받아옴
            binding.layoutBackgroundAdd.setVisibility(View.GONE);

            binding.layoutBackgroundImage.setVisibility(View.VISIBLE);

            // 음악 bg 넣도록 한다.
            list_background.clear();

            list_background.add(R.drawable.bsleep_wall);
            list_background.add(R.drawable.med_wall);
            list_background.add(R.drawable.music_wall);
            list_background.add(R.drawable.nature_wall);
            list_background.add(R.drawable.sleep_md_wall);
            list_background.add(R.drawable.sleep_ms_wall);

            binding.ivBackgroundImage.setImageResource(list_background.get(UtilAPI.s_id_backimamge_makecontents));

            bCheck_Background = true;

        } else {

            binding.layoutBackgroundAdd.setVisibility(View.VISIBLE);

            binding.layoutBackgroundImage.setVisibility(View.GONE);
        }

        Check_NextButton();
    }

    @Override
    public void OnSuccess_SelectAudioFile(String path_file, int duration) {

        // 오디오 파일 선택해서 파일 시간 표시하기


        playtime_sec_audio = (int) (duration / 1000);

        // 30 초 미만 15분 이상인 경우 문구 띄우고 리턴 처리를 한다.
        if (playtime_sec_audio < 30 || playtime_sec_audio > 60 * 15) {

            bCheck_Audio = false;

            String strText = this.getResources().getString(R.string.contentsmake_audio_exception);
            Toast.makeText(this, strText, Toast.LENGTH_SHORT).show();

            //binding.tvContext.setText(this.getResources().getString(R.string.contentsmake_audio_exception));
            return;
        }


        Upload_Audio_filePath = path_file;

        bCheck_Audio = true;


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
    public void OnSuccess_ImageCrop() {
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

            case R.id.ic_close: {
                onBackPressed();
            }
            break;

            case R.id.iv_background_image:
            case R.id.iv_backgroundbt: {

                if (audioState == eAudioState.ing) {
                    // 녹음 중이면 클릭이 안되도록 처리한다.
                    break;
                }

                // 배경 이미지 버튼 선택시

                Check_TitleEdit();

                Intent intent = new Intent(this, BackGroundActivity.class);
                intent.putExtra("title_text", viewModel.getTitle().getValue());
                startActivity(intent);
                this.overridePendingTransition(0, 0);

                // 종료하지 않는다.
                //finish();

            }
            break;

            case R.id.iv_picture_image:
            case R.id.layout_thumb_image:
            case R.id.iv_picture: {

                if (audioState == eAudioState.ing) {
                    // 녹음 중이면 클릭이 안되도록 처리한다.
                    break;
                }

                // 썹네일 이미지 선택시

                Check_TitleEdit();

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
            case R.id.iv_audio: {
                Check_TitleEdit();

                // 녹음

                bAudioIng = true;

                // 타이틀 입력 방지
                binding.editTitle.setEnabled(false);

                SetState_Audio(eAudioState.ing);

                // 녹음을 하면 업로드 파일명 초기화 처리
                Upload_Audio_filePath = "";

                //viewModel.setAudio_time("00:00");

                MediaPlayer mp;
                mp = MediaPlayer.create(this, R.raw.voice);
                mp.start();
                mp.setOnCompletionListener(m -> {

                    m.stop();

                    //bAudioIng = true;

                    // 타이틀 입력 방지
                    //binding.editTitle.setEnabled(false);

                    NetServiceManager.getinstance().startMyContentsRecord();

                    //SetState_Audio(eAudioState.ing);

                    // 녹음을 하면 업로드 파일명 초기화 처리
                    //Upload_Audio_filePath = "";

                    // 타이머 시작
                    StartTimer();

                    bCheck_Audio = false;

                    Check_NextButton();

                });

                //SoundPlayer.instance().playSound(0);


            }
            break;
            case R.id.iv_upload: {
                // 업로드
                Check_TitleEdit();

                doSelectAudio();
            }
            break;

            // 녹음 진행
            case R.id.iv_audio_stop: {
                // 정지
                if (binding.editTitle.isFocused()) {
                    binding.editTitle.clearFocus();
                }

                if (bStopButtonActive) {
                    // 정지 버튼 활성화 되면

                    Complete_Audio();

                    // 녹음된 시간을 다시 표시한다.
                    String strTime = GetString_time(audio_complete_time_milisecond);
                    viewModel.setAudio_time(strTime);
                } else {
                    // 타이틀 입력 방지
                    binding.editTitle.setEnabled(false);

                    // 녹음 진행하고 30 초 이전 이면 녹음 취소 팝업이 나오도록 한다.
                    if (accum_time_milisecond < 30 * 1000) {

                        //30초 이전에 정지 시 30초 이상 녹음해달라는 토스트 팝업 노출함
                        String strText = this.getResources().getString(R.string.contentsmake_audio_30);
                        Toast.makeText(this, strText, Toast.LENGTH_SHORT).show();

                        // NetServiceManager.getinstance().cancelMyContensRecord();
                    }
                }
            }
            break;

            //  재생 / 다시녹음
            case R.id.iv_audio_2: {
                // 재생 / 정지 =>

                Check_TitleEdit();

                bPlayerImage_ButtonState = !bPlayerImage_ButtonState;

                // 처음에 재생해야한다.
                if (bPlayerImage_ButtonState) {
                    // 오디오 플레이 상태
                    // 이미지는 정지 이미지
                    UtilAPI.setImage(this, binding.ivAudio2, R.drawable.social_create_stop);


                    // 오디오 정지
                    Stop_Audio();

                    Play_Audio(NetServiceManager.getinstance().mMyContentsPath);

                    // 타이머 시작
                    StartTimer();
                } else {
                    // 오디오 정지
                    // 이미지는 플레이 이미지
                    UtilAPI.setImage(this, binding.ivAudio2, R.drawable.social_create_play);


                    // 녹음된 시간을 다시 표시한다.
                    String strTime = GetString_time(audio_complete_time_milisecond);
                    viewModel.setAudio_time(strTime);
                    //binding.tvContext2.setText(strTime);

                    // 오디오 정지
                    Stop_Audio();

                    // 타이머 정지
                    StopTimer();


                }

            }
            break;

            case R.id.iv_re_audiobt: {
                // 다시녹음

                //SoundPlayer.instance().playSound(0);

                // 오디오 정지
                Stop_Audio();

                bStopButtonActive = false;

                binding.tvContext1.setText("00:00");

                Check_TitleEdit();

                bCheck_Audio = false;

                // 기존 파일을 삭제 처리한다.
                NetServiceManager.getinstance().delMyContentsRecordFile();

                NetServiceManager.getinstance().startMyContentsRecord();

                bComplete_Audio_Record = false;

                SetState_Audio(eAudioState.ing);


                // 초기화 재생 버튼으로
                UtilAPI.setImage(this, binding.ivAudio2, R.drawable.social_create_play);


                // 초기화 stop 비활성화
                UtilAPI.setImage(this, binding.ivAudioStop, R.drawable.social_create_stop_disabled);

                MediaPlayer mp;
                mp = MediaPlayer.create(this, R.raw.voice);
                mp.start();
                mp.setOnCompletionListener(m -> {

                    m.stop();


                    Check_NextButton();

                    // 타이머 시작
                    StartTimer();

                });
            }
            break;

            case R.id.iv_next: {

                if (audioState == eAudioState.ing) {
                    // 녹음 중이면 클릭이 안되도록 처리한다.
                    break;
                }

                if (bCheck_NextActive) {

                    // 오디오 정지
                    Stop_Audio();

                    // 다음 이동

                    // 현재 설정된 값을 다음 액티비티에 정보를 넘겨줘야 한다.

                    // 제작 - 치유 감정 선택 화면 이동
                    Intent intent = new Intent(this, ContentsEmotionSelActivity.class);

                    // String titleName, String thumnailImgName,String playtime, int IsSndFile, String SndFileName, String releasedate, String backgrroundImgName, String genre,String emotion){

                    // 타이틀
                    intent.putExtra("titleName", viewModel.title.getValue());


                    intent.putExtra("thumnailImgName", mCurrentPhotoPath);
                    intent.putExtra("playtime", playtime_sec_audio);

                    if (Upload_Audio_filePath.length() == 0) {
                        // 녹음 파일 이고
                        intent.putExtra("IsSndFile", 1);
                        intent.putExtra("SndFileName", NetServiceManager.getinstance().mMyContentsPath);
                    } else {
                        // 파일에서
                        intent.putExtra("IsSndFile", 0);
                        intent.putExtra("SndFileName", Upload_Audio_filePath);
                    }

                    SimpleDateFormat format_date = new SimpleDateFormat("yyyy-MM-dd");
                    Date date_now = new Date(System.currentTimeMillis());
                    String curdate = format_date.format(date_now);

                    intent.putExtra("releasedate", curdate);


                    if (UtilAPI.s_id_backimamge_makecontents > -1) {
                        if (list_background_string.size() > UtilAPI.s_id_backimamge_makecontents && UtilAPI.s_id_backimamge_makecontents > -1) {
                            intent.putExtra("backgrroundImgName", list_background_string.get(UtilAPI.s_id_backimamge_makecontents));
                        }
                    } else {
                        // -1 디폴트 인경우
                        intent.putExtra("backgrroundImgName", list_background_string.get(0));
                    }

                    this.startActivity(intent);
                    this.overridePendingTransition(0, 0);


                    UtilAPI.AddActivity_Temp(this);
                    //UtilAPI.s_activity_temp = this;
                }

                // 액티비티 저장을하고
                //finish();

            }
            break;
        }

    }

    @Override
    public void onFailure(Integer id, String message) {

    }


    private void Check_NextButton() {
        if (bCheck_TitleName && bCheck_Thumb && bCheck_Audio && bCheck_Background) {
            UtilAPI.setImage(this, binding.ivNext, R.drawable.social_btn);

            bCheck_NextActive = true;
        } else {
            UtilAPI.setImage(this, binding.ivNext, R.drawable.social_btn_disable);

            bCheck_NextActive = false;
        }

    }

    private void Complete_Audio() {
        // 녹음 완료 할 경우
        //SoundPlayer.instance().playSound(0);

        SetState_Audio(eAudioState.play);
        StopTimer();

        // 타이틀 입력 처리
        binding.editTitle.setEnabled(true);

        bAudioIng = false;

        audio_complete_time_milisecond = accum_time_milisecond;

        playtime_sec_audio = (int) (accum_time_milisecond / 1000);

        bComplete_Audio_Record = true;

        NetServiceManager.getinstance().doneMyContentsRecord();

        // 버튼 사운드
        MediaPlayer mp;
        mp = MediaPlayer.create(this, R.raw.voice);
        mp.start();
        mp.setOnCompletionListener(m -> {

            m.stop();

        });
    }

    private void SetState_Audio(eAudioState state) {
        audioState = state;
        switch (state) {
            case audio: {
                // 녹음 / 업로드 레이아웃

                binding.layoutAudioUpload.setVisibility(View.VISIBLE);
                binding.layoutAudioIng.setVisibility(View.GONE);
                binding.layoutAudioPlay.setVisibility(View.GONE);

                // 초기화 stop 비활성화
                //UtilAPI.setImage(this, binding.ivAudioStop, R.drawable.social_create_stop_disabled);
            }
            break;
            case ing: {

                //녹음 진행

                binding.layoutAudioUpload.setVisibility(View.GONE);
                binding.layoutAudioIng.setVisibility(View.VISIBLE);
                binding.layoutAudioPlay.setVisibility(View.GONE);
            }
            break;
            case play: {

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

    public void OnMessage_Handle(Message msg) {
        // ui 표시
        switch (audioState) {
            case ing: {
                // 녹음
                //tv_context_1
                binding.tvContext1.setText((String) msg.obj);

                // 30초 경과 후에는
                // 정지 버튼 활성화 하도록 한다.

                if (accum_time_milisecond > 30 * 1000) {

                    UtilAPI.setImage(this, binding.ivAudioStop, R.drawable.social_create_stop);

                    bStopButtonActive = true;
                }
            }
            break;

            case play: {
                // 플레이
                //tv_context_2
                binding.tvContext2.setText((String) msg.obj);
            }
            break;
        }

    }

    public void StartTimer() {
        StopTimer();

        Update_Time();

        switch (audioState) {
            case ing: {
                // 녹음
                // 15분 이면 second_time = 15 * 60
                // 30 이면 second_time = 30

                StartTimer(15 * 60, true, 30);
            }
            break;

            case play: {
                // 재생
                long play_time = audio_complete_time_milisecond;
                StartTimer_ex(play_time, false, 0);
            }
            break;
        }
    }

    public void StartTimer_ex(long time_milisecond, boolean bUseStopBt, long second_time_stopbt) {
        this.total_time_milisecond = time_milisecond;

        this.bUseStopBt = bUseStopBt;

        if (bUseStopBt) {
            this.second_timemilisecond_stopbt = second_time_stopbt * 1000;
        }

        this.accum_time_milisecond = 0;

        if (handlerUpdateLocation == null) {
            handlerUpdateLocation = new Handler(Looper.getMainLooper());
            handlerUpdateLocation.post(runnableUpdateLocation);
        }
    }

    public void StartTimer(long second_time, boolean bUseStopBt, long second_time_stopbt) {
        this.total_time_milisecond = second_time * 1000;

        this.bUseStopBt = bUseStopBt;

        if (bUseStopBt) {
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
                if (audioState == eAudioState.ing) {
                    // 자동으로 녹음 완료 처리

                    Complete_Audio();
                }

            } else {

                String strTime = GetString_time(accum_time_milisecond);

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

    private String GetString_time(long time) {
        long reminder_time = time;//total_time_milisecond - accum_time_milisecond;

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

        String strTime = (min + ":" + second);

        return strTime;
    }

    @Override
    protected void onStop() {
        super.onStop();

        // 녹음 중일 경우
        if (NetServiceManager.getinstance().isMyContentsRecording) {
            // 녹음 시작 진행 중이라면
            // 녹음 취소해야 한다.
            NetServiceManager.getinstance().cancelMyContensRecord();
        }
    }

    @Override
    public void onBackPressed() {

        if (audioState == eAudioState.ing) {
            // 팝업을 띄워준다.
            AlertLineTwoPopup alertDlg = new AlertLineTwoPopup(this, this, AlertLineTwoPopup.Dlg_Type.regiter_cancel);

            alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDlg.show();

            // 팝업에서 예를 누르면 소셜로 이동 처리
            alertDlg.iv_ok.setOnClickListener(v -> {

                // 오디오 정지
                Stop_Audio();

                // 녹음 중일 경우
                if (NetServiceManager.getinstance().isMyContentsRecording) {
                    // 녹음 시작 진행 중이라면
                    // 녹음 취소해야 한다.
                    NetServiceManager.getinstance().cancelMyContensRecord();
                }

                // 오디오 플레이 처리
                if (bServiceAudio) {
                } else {
                    if (AudioPlayer.instance() != null) {
                        AudioPlayer.instance().restart();
                    }
                }
                // 배경이미지 초기화
                UtilAPI.s_id_backimamge_makecontents = -1;

                // 마이 콘텐츠로 이동
                ActivityStack.instance().OnBack(this);
                UtilAPI.ClearActivity_Temp();

                alertDlg.dismiss();
            });
            return;
        }

        // 콘텐츠 제작 메인에서 정보를 입력한 상태에서
        // back 하면 안내 팝업 제공
        // - 한번 더 back 하거나 ‘예’ 선택 시 이전 화면(소셜)으로 이동
        // 녹음중일 경우
        if (bCheck_TitleName || bCheck_Thumb || bCheck_Audio || bCheck_Background || bAudioIng) {

            // 팝업을 띄워준다.
            AlertLineTwoPopup alertDlg = new AlertLineTwoPopup(this, this, AlertLineTwoPopup.Dlg_Type.regiter_cancel);

            alertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDlg.show();

            // 팝업에서 예를 누르면 소셜로 이동 처리
            alertDlg.iv_ok.setOnClickListener(v -> {

                // 오디오 정지
                Stop_Audio();

                // 녹음 중일 경우
                if (NetServiceManager.getinstance().isMyContentsRecording) {
                    // 녹음 시작 진행 중이라면
                    // 녹음 취소해야 한다.
                    NetServiceManager.getinstance().cancelMyContensRecord();
                }

                // 있으면 녹음 파일 삭제
                NetServiceManager.getinstance().delMyContentsRecordFile();


                // 오디오 플레이 처리
                if (bServiceAudio) {
                } else {
                    if (AudioPlayer.instance() != null) {
                        AudioPlayer.instance().restart();
                    }
                }


                // 배경이미지 초기화
                UtilAPI.s_id_backimamge_makecontents = -1;

                // 마이 콘텐츠로 이동
                ActivityStack.instance().OnBack(this);

                //ChangeActivity(MyContentsActivity.class);

                UtilAPI.ClearActivity_Temp();


                alertDlg.dismiss();
            });
        } else {
            // 마이 콘텐츠로 이동

            // 있으면 녹음 파일 삭제
            NetServiceManager.getinstance().delMyContentsRecordFile();

            // 녹음 중일 경우
            if (NetServiceManager.getinstance().isMyContentsRecording) {
                // 녹음 시작 진행 중이라면
                // 녹음 취소해야 한다.
                NetServiceManager.getinstance().cancelMyContensRecord();
            }

            // 오디오 정지
            Stop_Audio();

            // 오디오 플레이 처리
            if (bServiceAudio) {
            } else {
                if (AudioPlayer.instance() != null) {
                    AudioPlayer.instance().restart();
                }
            }

            // 배경이미지 초기화
            UtilAPI.s_id_backimamge_makecontents = -1;

            ActivityStack.instance().OnBack(this);

            //ChangeActivity(MyContentsActivity.class);


            UtilAPI.ClearActivity_Temp();

        }
    }
}