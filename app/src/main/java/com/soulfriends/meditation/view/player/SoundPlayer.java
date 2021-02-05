package com.soulfriends.meditation.view.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.soulfriends.meditation.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SoundPlayer {

    private static SoundPlayer instance = null;

    public static SoundPlayer instance(){
        return instance;
    }

    public static SoundPlayer with(Context context) {

        if (instance == null)
            instance = new SoundPlayer(context);

        return instance;
    }

    private SoundPool soundPool;
    private AudioManager mAudioManager;
    private Context mContext;

    private int mStreamId;

    public SoundPlayer(Context mContext) {

        this.mContext = mContext;

        //롤리팝 이상 버전일 경우
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().build();
        } else {
            //롤리팝 이하 버전일 경우 new SoundPool(1번,2번,3번)
            // 1번 - 음악 파일 갯수
            // 2번 - 스트림 타입
            // 3번 - 음질
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public int playSound(int soundId) {

        soundPool.load(mContext, R.raw.voice, 1);

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                {
                    int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    mStreamId = soundPool.play(sampleId, streamVolume, streamVolume, 1, 0, 1.0f); // 실행
                }
            }
        });

        return 0;
    }

    public void stopSound(int streamId) {
        soundPool.stop(streamId);
    }

    public void pauseSound(int streamId) {
        soundPool.pause(streamId);
    }

    public void resumeSound(int streamId) {
        soundPool.resume(streamId);
    }
}
