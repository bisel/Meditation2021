package com.soulfriends.meditation.view.player;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.soulfriends.meditation.R;
import com.soulfriends.meditation.util.UtilAPI;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.greenrobot.eventbus.EventBus;

public class MeditationService extends Service implements Player.EventListener, AudioManager.OnAudioFocusChangeListener {

    public static final String ACTION_PLAY = "com.soulfriends.meditation.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.soulfriends.meditation.ACTION_PAUSE";
    public static final String ACTION_STOP = "com.soulfriends.meditation.ACTION_STOP";
    public static final String ACTION_REW = "com.soulfriends.meditation.ACTION_REW";
    public static final String ACTION_FFWD = "com.soulfriends.meditation.ACTION_FFWD";

    private final IBinder iBinder = new LocalBinder();

    private Context context;

    public Context getContext() {
        return context;
    }

    private Handler handler;
    private final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private SimpleExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    private boolean onGoingCall = false;
    private TelephonyManager telephonyManager;

    private WifiManager.WifiLock wifiLock;

    private AudioManager audioManager;

    private MeditationNotificationMgr notificationManager;

    private String status;

    private String strAppName;
    private String strLiveBroadcast;
    private String streamUrl;

    private String thumbnail_url;
    private String title;

    public void SetThumbnail_Url(String url) {
        this.thumbnail_url = url;
    }

    public void SetTitle_Url(String title) {
        this.title = title;
    }

    public class LocalBinder extends Binder {
        public MeditationService getService() {
            return MeditationService.this;
        }
    }

    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            pause();
        }
    };

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (state == TelephonyManager.CALL_STATE_OFFHOOK
                    || state == TelephonyManager.CALL_STATE_RINGING) {

                if (!isPlaying()) return;

                onGoingCall = true;
                //stop();
                pause();

                EventBus.getDefault().post(PlaybackStatus.PAUSED_NOTI);

            } else if (state == TelephonyManager.CALL_STATE_IDLE) {

                if (!onGoingCall) return;

                onGoingCall = false;
                //resume();
                //play();
            }
        }
    };

    private MediaSessionCompat.Callback mediasSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPause() {
            super.onPause();

            pause();
        }

        @Override
        public void onStop() {
            super.onStop();

            stop();

            EventBus.getDefault().post(PlaybackStatus.STOP_NOTI);

            notificationManager.cancelNotify();
        }

        @Override
        public void onPlay() {
            super.onPlay();

            resume();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        strAppName = getResources().getString(R.string.app_name);
        strLiveBroadcast = getResources().getString(R.string.live_broadcast);

        context = getApplicationContext();

        onGoingCall = false;

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        notificationManager = new MeditationNotificationMgr(MeditationService.this);

        wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mcScPAmpLock");

        mediaSession = new MediaSessionCompat(this, getClass().getSimpleName());
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "...")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, strAppName)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, strLiveBroadcast)
                .build());
        mediaSession.setCallback(mediasSessionCallback);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        handler = new Handler();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        AdaptiveTrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
        exoPlayer.addListener(this);

        registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

        status = PlaybackStatus.IDLE;
    }

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:            //알수없는 기간동안 오디오 포커스 잃은 경우
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:  //일시적으로 오디오 포커스 빼앗긴 경우
                    pause();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:           //오디오 포커스를 얻은 경우
                    //play();
                    // setAudioFocus();
                    break;
            }
        }
    };

    //오디오 포커스 관련
    private void setAudioFocus() {
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);  //0이면 실패, 1이면 성공

        if (result == AudioManager.AUDIOFOCUS_GAIN) {  //성공시
            //play();
            //setAudioFocus();
        } else {  //실패시

            pause();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        if (TextUtils.isEmpty(action))
            return START_NOT_STICKY;

        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

            stop();

            return START_NOT_STICKY;
        }

        if (action.equalsIgnoreCase(ACTION_PLAY)) {

            //transportControls.play();

            play();
            EventBus.getDefault().post(PlaybackStatus.PLAYING_NOTI);

        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {

            transportControls.pause();
            EventBus.getDefault().post(PlaybackStatus.PAUSED_NOTI);

        } else if (action.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();

            // 노티에서 정지 클릭 이벤트 일 경우
            if (UtilAPI.s_bEvent_service_main == false && UtilAPI.s_bEvent_service_player == false) {
                UtilAPI.s_bEvent_service_player_stop = true;
            }

            StopTimer();

        } else if (action.equalsIgnoreCase(ACTION_FFWD)) {
            transportControls.fastForward();
        } else if (action.equalsIgnoreCase(ACTION_REW)) {
            transportControls.rewind();
        }


        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        if (status.equals(PlaybackStatus.IDLE))
            stopSelf();

        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(final Intent intent) {

    }

    @Override
    public void onDestroy() {

        pause();

        exoPlayer.release();
        exoPlayer.removeListener(this);

        if (telephonyManager != null)
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);

        notificationManager.cancelNotify();

        mediaSession.release();

        unregisterReceiver(becomingNoisyReceiver);

        super.onDestroy();


    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:

                exoPlayer.setVolume(0.8f);

                resume();

                break;

            case AudioManager.AUDIOFOCUS_LOSS:

                stop();

                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:

                if (isPlaying()) pause();

                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                if (isPlaying())
                    exoPlayer.setVolume(0.1f);

                break;
        }

    }



    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch (playbackState) {
            case Player.STATE_BUFFERING:
                status = PlaybackStatus.LOADING;
                break;
            case Player.STATE_ENDED: {
                status = PlaybackStatus.STOPPED;

                MeditationAudioManager.getinstance().SendUserProFile_Play((int)(getDuration() / 1000), 1);

                MeditationAudioManager.stop();
                MeditationAudioManager.getinstance().unbind();

                if (UtilAPI.s_bEvent_service_main == false && UtilAPI.s_bEvent_service_player == false) {
                    UtilAPI.s_bEvent_service = true;
                }
            }
            break;
            case Player.STATE_IDLE:
                status = PlaybackStatus.IDLE;
                break;
            case Player.STATE_READY:
                status = playWhenReady ? PlaybackStatus.PLAYING : PlaybackStatus.PAUSED;
                break;
            default:
                status = PlaybackStatus.IDLE;
                break;
        }

        if (!status.equals(PlaybackStatus.IDLE))
            notificationManager.startNotify(status, thumbnail_url, title);

        if (playbackState == Player.STATE_ENDED) {
            status = PlaybackStatus.STOPPED_END;
        }

        EventBus.getDefault().post(status);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        // 반복
        if (exoPlayer.getCurrentPosition() > 10) {
            EventBus.getDefault().post(PlaybackStatus.TRACK_CHANGE);

            MeditationAudioManager.getinstance().SendUserProFile_Play((int)(getDuration() / 1000), 1);
        }
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

        EventBus.getDefault().post(PlaybackStatus.ERROR);
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    private MediaSource buildMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(this, "exoplayerfirebase");

        //new ExtractorMediaSource
        return new ProgressiveMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri);
    }


    public void play(String streamUrl) {

        this.streamUrl = streamUrl;

        if (wifiLock != null && !wifiLock.isHeld()) {

            wifiLock.acquire();

        }

        MediaSource mediaSource = buildMediaSource(Uri.parse(streamUrl));

        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);



    }

    public SimpleExoPlayer getplayerInstance() {
        return exoPlayer;
    }

    public void resume() {

        if (streamUrl != null)
            play(streamUrl);
    }

    public void play() {
        exoPlayer.setPlayWhenReady(true);
    }


    public void pause() {

        exoPlayer.setPlayWhenReady(false);

        audioManager.abandonAudioFocus(this);
        wifiLockRelease();
    }

    public void stop() {

        exoPlayer.stop();

        audioManager.abandonAudioFocus(this);
        wifiLockRelease();

        StopTimer();
    }

    public void idle_start() {
        exoPlayer.seekTo(0);
        exoPlayer.setPlayWhenReady(false);

        audioManager.abandonAudioFocus(this);
        wifiLockRelease();
    }

//    public void setRepeatMode()
//    {
//        exoPlayer.getRepeatMode();//.setRepeatMode
//    }
//
//    public int getRepeatMode()
//    {
//        exoPlayer.getRepeatMode();//.setRepeatMode
//    }

//    public void rew()
//    {
//        exoPlayer.();
//
//        audioManager.abandonAudioFocus(this);
//        wifiLockRelease();
//    }
//
//    public void ffwd()
//    {
//
//        exoPlayer.stop();
//
//        audioManager.abandonAudioFocus(this);
//        wifiLockRelease();
//    }


    public void playOrPause(String url) {

        if (streamUrl != null && streamUrl.equals(url)) {

            if (!isPlaying()) {

                play(streamUrl);

            } else {

                pause();
            }

        } else {

            if (isPlaying()) {

                pause();

            }

            play(url);
        }
    }

    public String getStatus() {

        return status;
    }

    public long getDuration() {
        return exoPlayer.getDuration();
    }

    public long getContentPosition() {
        return exoPlayer.getContentPosition();
    }

    public long getCurrentPosition() {
        return exoPlayer.getCurrentPosition();
    }


    public MediaSessionCompat getMediaSession() {

        return mediaSession;
    }

    public boolean isPlaying() {

        return this.status.equals(PlaybackStatus.PLAYING);
    }

    public boolean isPlayingAndPause() {
        boolean res = false;

        if (this.status.equals(PlaybackStatus.PLAYING)) {
            res = true;
        }
        if (this.status.equals(PlaybackStatus.PAUSED)) {
            res = true;
        }
        return res;
    }

    private void wifiLockRelease() {

        if (wifiLock != null && wifiLock.isHeld()) {

            wifiLock.release();
        }
    }

    private String getUserAgent() {

        return Util.getUserAgent(this, getClass().getSimpleName());
    }

    private Handler handlerUpdateLocation;
    private long total_time_milisecond = 0;
    private long accum_time_milisecond = 0;
    private long time_interval = 1000;

    CountDownTimer countDownTimer = null;

    public boolean isTimerCount() {
        if (handlerUpdateLocation != null) {
            return true;
        }
        return false;
    }

    public void StartTimer(long second_time) {
        this.total_time_milisecond = second_time * 1000;

        this.accum_time_milisecond = 0;

        if (handlerUpdateLocation == null) {
            handlerUpdateLocation = new Handler(Looper.getMainLooper());
            handlerUpdateLocation.post(runnableUpdateLocation);
        }

        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);

    }

    public void StopTimer() {
        if (handlerUpdateLocation != null) {
            handlerUpdateLocation.removeMessages(0);
            handlerUpdateLocation = null;

            exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        }
    }

    private Runnable runnableUpdateLocation = new Runnable() {
        @Override
        public void run() {
            // TODO: You can send location here
            accum_time_milisecond += time_interval;

            if (accum_time_milisecond >= total_time_milisecond) {
                if (UtilAPI.s_bEvent_service_main == false && UtilAPI.s_bEvent_service_player == false) {
                    UtilAPI.s_bEvent_service_player_timer_stop = true;
                } else {
                    EventBus.getDefault().post(PlaybackStatus.STOP_TIMER);
                }

                MeditationAudioManager.getinstance().ShowTimerPopup();
                MeditationAudioManager.stop();
                MeditationAudioManager.getinstance().unbind();

                StopTimer();
            } else {

                long reminder_time = total_time_milisecond - accum_time_milisecond;

                // 시간단위
                String hour = String.valueOf(reminder_time / (60 * 60 * 1000));

                // 분단위
                long getMin = reminder_time - (reminder_time / (60 * 60 * 1000));

                int int_hour_loc= Integer.parseInt(hour);
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

                // 시간이 0보다 크면 올림할 필요가 없음.
                // 분만 있다면 올림 처리를 한다.
                int int_hour = Integer.parseInt(hour);
                if(int_hour == 0)
                {
                    // 시간이 0일때만 분에 대한 올림 처리
                    int int_second = Integer.parseInt(second);
                    if(int_second > 0) {
                        int int_min = Integer.parseInt(min) + 1;

                        min = String.valueOf(int_min);
                        if (min.length() == 1) {
                            min = "0" + min;
                        }
                    }
                }

                // 테스트
                //String strTime = (hour + ":" + min + ":" + second);

                String strTime = (hour + ":" + min);

                MeditationAudioManager.setStrTimerCount(strTime);

                //status = PlaybackStatus.TIMER_COUNT; 이것 처럼 status에 값을 넣고 이벤트 보내면 안됨!!!
                //onPlayerStateChanged 이벤트가 발생하게 됨
                EventBus.getDefault().post(PlaybackStatus.TIMER_COUNT);

                handlerUpdateLocation.postDelayed(this, time_interval); // 1초 마다 호출
            }
        }
    };
}
