package com.soulfriends.meditation.view.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.soulfriends.meditation.dlg.TimerDialog;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.util.UtilAPI;
import com.soulfriends.meditation.view.TimerDialogActivity;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.soulfriends.meditation.util.RecvEventListener;

import org.greenrobot.eventbus.EventBus;

public class MeditationAudioManager {

    private RecvEventListener recvEventListener;

    private static MeditationAudioManager instance = null;

    private static MeditationService service;

    private Context context;

    private boolean serviceBound;

    private boolean serviceBind = false;



    public boolean getServiceBound() {
        return serviceBound;
    }

    public void setReceivedEvent(RecvEventListener listener) {
        recvEventListener = listener;
    }

    private MeditationAudioManager(Context context) {
        this.context = context;
        serviceBound = false;
    }

    public static MeditationAudioManager getinstance(){

        return instance;
    }

    public static MeditationAudioManager with(Context context) {

        if (instance == null)
            instance = new MeditationAudioManager(context);

        return instance;
    }

    public void release_service()
    {
        service = null;
    }


    private static String strTimerCount = "00  : 00";
    public static void setStrTimerCount(String count)
    {
        strTimerCount = count;
    }

    public static boolean isTimerCount()
    {
        if (service == null) return false;
        return service.isTimerCount();
    }

    public static String GetStrTimerCount()
    {
        return strTimerCount;
    }

    public void SetThumbnail_Url(String url)
    {
        if (service == null) return;
        service.SetThumbnail_Url(url);
    }

    public void SetTitle_Url(String title)
    {
        if (service == null) return;
        service.SetTitle_Url(title);
    }

    public void ShowTimerPopup() {

        // this.activity -> intro activity 임.
        if(UtilAPI.GetActivity() != null) {

            if(UtilAPI.GetActivityFocus() != null)
            {
                if(UtilAPI.GetActivityFocus().GetOnStart())
                {
                    // 활성화 된 경우
                    // 타이머 팝업
                    //TimerDialog alertDialog = new TimerDialog(UtilAPI.GetActivity(), UtilAPI.GetActivity());
                    //alertDialog.setCancelable(false);
                    //alertDialog.show();
                    Intent intent = new Intent(UtilAPI.GetActivity() , TimerDialogActivity.class);
                    UtilAPI.GetActivity().startActivity(intent);
                }
                else
                {
                    UtilAPI.s_bShowTimerPopup = true;
                }
            }
            else
            {
                UtilAPI.s_bShowTimerPopup = true;
            }
        }
    }

    public void SendUserProFile_Play(int playtime, int count)
    {
        if(NetServiceManager.getinstance() == null) return;

        NetServiceManager.getinstance().SendUserProFile_Play(playtime, count);
    }


    public static MeditationService getService() {
        return service;
    }

    public static SimpleExoPlayer getplayerInstance() {
        return service.getplayerInstance();
    }

    public static void playOrPause(String streamUrl) {

        if (service == null) return;
        service.playOrPause(streamUrl);
    }

//    public void rew(){
//
//        service.playOrPause(streamUrl);
//    }
//
//    public void ffwd(){
//
//        service.playOrPause(streamUrl);
//    }

    public static boolean isPlaying() {

        if (service == null) return false;
        return service.isPlaying();
    }

    public static boolean isPlayingAndPause()
    {
        if (service == null) return false;
        return service.isPlayingAndPause();
    }

    public static void pause() {
        if (service == null) return;
        service.pause();
    }

    public static long getDuration() {
        if (service == null) return 0;
        return service.getDuration();
    }

    public static long getContentPosition() {
        if (service == null) return 0;
        return service.getContentPosition();
    }

    public static long getCurrentPosition() {
        if (service == null) return 0;
        return service.getCurrentPosition();
    }


    public static void resume() {

        if (service == null) return;
        service.resume();
    }

    public static void play() {
        if (service == null) return;
        service.play();
    }

    public static void stop() {
        if (service == null) return;
        service.stop();
    }

    // 이 함수 호출 하면 정지가 된다
    public static void stop_ext()
    {
        stop();
        if(getinstance().getServiceBound()) {
            getinstance().unbind();
        }
    }



    public static void idle_start() {
        if (service == null) return;
        service.idle_start();
    }

    public void bind() {

        Intent intent = new Intent(context, MeditationService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        if (service != null)
            EventBus.getDefault().post(service.getStatus());
    }

    public void unbind() {

        if(serviceBind)
        {
            if (service != null) {
                StopTimer();
            }
            context.unbindService(serviceConnection);
            serviceBind = false;

            serviceBound = false;

        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {

            service = ((MeditationService.LocalBinder) binder).getService();
            serviceBound = true;

            serviceBind = true;
            recvEventListener.onReceivedEvent();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            serviceBound = false;
        }
    };

    public static void StartTimer(long second_time) {
        if (service == null) return;
        service.StartTimer(second_time);
    }

    public static void StopTimer() {
        if (service == null) return;
        service.StopTimer();
    }

}
