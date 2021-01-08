package com.soulfriends.meditation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserProfile {
    public String uid;
    public String nickname;
    public String profileimg;
    public int longintype = 1; // 1 : email, 2: google
    public int gender = 1;  // 1 : man, 2 : woman
    public int age = 1;
    public int emotiontype = 0;  // emotion type
    public int chartype = 0;        // 나의 성격         // 2020.12.21
    public int sessionnum = 0;
    public int playtime = 0;

    public int psychologytime = 0; // 심리검사 시간 (초)

    public int  myemotiontype = 0;
    public int  donefirstpopup = 0;    // 처음 팝업여부

    public String finaltestdate;       // 심리 검사 시간
    public String finalchartestdate;   // 성격 검사 시간     // 2020.12.21
    public String finalvoicetestdate;  // 목소리 분석 시간   // 2020.12.21
    public boolean mIsDoneTest = false;

    public Map<String, Boolean> favoriteslist = new HashMap<>(); // 2020.11.25 처리
    public ArrayList<String> recentplaylist = new ArrayList<String>(); // 2020.12.21

    public void allClear()
    {
        uid = "";
        nickname = "";
        profileimg = "";

        longintype = 1;
        gender = 1;
        age = 1;
        emotiontype = 0;
        chartype = 0;
        sessionnum = 0;
        playtime = 0;

        psychologytime = 0;

        myemotiontype = 0;
        donefirstpopup = 0;

        finaltestdate = "";
        finalchartestdate = "";
        finalvoicetestdate = "";
        finalchartestdate = "";
        finalvoicetestdate = "";

        mIsDoneTest = false;

        favoriteslist.clear();
        recentplaylist.clear();

    }
}
