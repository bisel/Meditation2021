package com.soulfriends.meditation.view.profile;

import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.model.MeditationContentsCharInfo;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.parser.BgTagData;
import com.soulfriends.meditation.util.ItemClickListener;
import com.soulfriends.meditation.util.ItemClickListenerExt;
import com.soulfriends.meditation.util.UtilAPI;

import java.util.ArrayList;

public class ProfileItemViewModel extends ViewModel {

    public MeditationContents meditationContents;

    public MutableLiveData<String> playtime = new MutableLiveData<>();
    public MutableLiveData<String> title = new MutableLiveData<>();

    private long mLastClickTime = 0;

    private ItemClickListenerExt listener;

    // 콘텐츠 종류
    public int contentskind;

    // 가위 보여야 할지 여부
    public int bShow_ivModify = 0;

    // 무료, 유료 여부
    public int paid;

    public ProfileItemViewModel(MeditationContents entity_data, int category_subtype, ItemClickListenerExt listener) {

        meditationContents = entity_data;

        this.listener = listener;

        // 타이틀
        title.setValue(entity_data.title);

        // 초
        float play_time_second = Float.valueOf(entity_data.playtime);
        float calc_minute = play_time_second / 60.0f;
        float final_minute = Math.round(calc_minute);
        int minute = (int)final_minute;

        String str_play_time = "";
        if(minute == 0)
        {
            str_play_time = "1분";
        }
        else
        {
            str_play_time =String.valueOf(minute) + "분";
        }
        playtime.setValue(str_play_time);



        //  0 : 기본 제공  1 : 소셜 콘텐츠
        if(meditationContents.ismycontents == 0)
        {
            // 0 : 기본 제공
            bShow_ivModify = 0;

            MeditationContentsCharInfo info = NetServiceManager.getinstance().getMeditationContentsCharInfo(meditationContents.uid);

            if(info == null)
            {
                int xx = 0;
            }
            else
            {
                paid = Integer.parseInt(info.paid);
            }
        }
        else
        {
            // 1 : 소셜 콘텐츠
            // 2021.02.02
            if(meditationContents.authoruid.equals(NetServiceManager.getinstance().getUserProfile().uid)){
                //UtilAPI.s_playerMode = UtilAPI.PlayerMode.my;

                bShow_ivModify = 1;

            }else{
                //UtilAPI.s_playerMode = UtilAPI.PlayerMode.friend;

                bShow_ivModify = 0;
            }
        }


        // 콘텐츠 상태
        // 콘텐츠 인덱스를 받아서 콘텐츠 상태 표시를 해야 한다.

        // 1. 콘텐츠별로 달모양 오류와 관련해서
        // - 소셜콘텐츠와 소셜콘텐츠가 아닐경우에따라 처리 분리 필요
        // 1) 소셜콘텐츠 : MeditationContents Class의 contentkind에 저장되어 있으므로 이용.
        // 2) 일반콘텐츠 : getMeditationContentsCharInfo 함수를 이용해서 해당 ID의 MeditationContentsCharInfo의 kind
        // 정보를 얻고 다시 GetKindByBgTagName을 이용해서 해당 id를 받아서
        // 다시 int로 변환해서 사용해야 한다.


        if(meditationContents.ismycontents == 0)
        {
            //  0 : 기본 제공
            MeditationContentsCharInfo info = NetServiceManager.getinstance().getMeditationContentsCharInfo(meditationContents.uid);

            if(info == null)
            {
                int xx = 0;
            }
            else
            {
                contentskind = Integer.parseInt(NetServiceManager.getinstance().GetKindByBgTagName(info.kind));
            }
        }
        else
        {
            //  1 : 소셜 콘텐츠
            contentskind = meditationContents.contentskind;
        }
    }

    public void OnClicked_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < UtilAPI.button_delay_time){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.listener.onItemClick(view,  (Object)this.meditationContents, 0);
    }
}
