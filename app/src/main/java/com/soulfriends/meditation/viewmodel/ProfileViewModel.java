package com.soulfriends.meditation.viewmodel;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulfriends.meditation.model.UserProfile;
import com.soulfriends.meditation.netservice.NetServiceManager;
import com.soulfriends.meditation.parser.PersonResultData;
import com.soulfriends.meditation.parser.ResultData;
import com.soulfriends.meditation.util.ResultListener;

public class ProfileViewModel extends ViewModel {

    public MutableLiveData<String> nickname = new MutableLiveData<>();
    public MutableLiveData<String> totalSession = new MutableLiveData<>();
    public MutableLiveData<String> hour = new MutableLiveData<>();
    public MutableLiveData<String> minute = new MutableLiveData<>();
    public MutableLiveData<String> feelstate = new MutableLiveData<>();
    public MutableLiveData<String> charstate = new MutableLiveData<>();  // 2020.12.24

    // 3 / 10
    public MutableLiveData<String> contents_make_number = new MutableLiveData<>();

    public MutableLiveData<String> getIntroduction() {
        return introduction;
    }

    public void setIntroduction(MutableLiveData<String> introduction) {
        this.introduction = introduction;
    }

    public MutableLiveData<String> introduction = new MutableLiveData<>();


    private ResultListener listener;

    private Context context;
    private UserProfile userProfile;

    private long mLastClickTime = 0;

    public ProfileViewModel(UserProfile userProfile, Context mContext, ResultListener listener) {
        this.context = mContext;
        this.listener = listener;
        this.userProfile = userProfile;
    }

    public void Update()
    {
        // nick
        nickname.setValue(userProfile.nickname);

        // 소개글
        introduction.setValue(userProfile.profileIntro);

        // 총 세션수
        if(userProfile.sessionnum > 9999)
        {
            totalSession.setValue("9999+");
        }
        else
        {
            totalSession.setValue(String.valueOf(userProfile.sessionnum));
        }

        // 마음 점검 시간
        int _second = userProfile.playtime;
        int _hour = _second/3600; //1시간 = 60분 = 3600초
        _second %= 3600;
        int _minute = _second/60;

        hour.setValue(String.valueOf(_hour));
        minute.setValue(String.valueOf(_minute));

        // 마음 상태
        ResultData resultData = NetServiceManager.getinstance().getResultData(userProfile.emotiontype);
        feelstate.setValue(resultData.state);

        // 2020.12.24
        PersonResultData charData  = NetServiceManager.getinstance().getPersonResultDataList().get(userProfile.chartype);
        charstate.setValue(charData.title);
    }

    public void OnClick_Select(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 100){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        this.listener.onSuccess(view.getId(),"Success!");
    }
}
