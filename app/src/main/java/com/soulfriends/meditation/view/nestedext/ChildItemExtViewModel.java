package com.soulfriends.meditation.view.nestedext;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.model.MeditationContents;
import com.soulfriends.meditation.model.MeditationContentsCharInfo;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.parser.BgTagData;

import java.util.ArrayList;

public class ChildItemExtViewModel extends ViewModel {
    public MutableLiveData<MeditationContents> entity = new MutableLiveData<>();

    public MutableLiveData<String> playtime = new MutableLiveData<>();
    public MutableLiveData<String> title = new MutableLiveData<>();

    public MeditationContents meditationContents;

    private int count = 0;

    public int GetCount()
    {
        return count;
    }

    private int category_subtype = 0;

    public int GetSubType()
    {
        return category_subtype;
    }

    // 콘텐츠 종류
    public int contentskind;

    // 무료, 유료 여부
    public int paid;

    public ChildItemExtViewModel(MeditationContents entity_data, int category_subtype, int count) {

        meditationContents = entity_data;

        this.category_subtype = category_subtype;
        this.count = count;

        //thumb_url.setValue("");
        entity.setValue(entity_data);
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

                paid = Integer.parseInt(info.paid);
            }
        }
        else
        {
            //  1 : 소셜 콘텐츠
            contentskind = meditationContents.contentskind;
        }

    }

    public MutableLiveData<MeditationContents> getEntity() {
        return entity;
    }

    public void setEntity(MutableLiveData<MeditationContents> entity) {
        this.entity = entity;
    }
}
