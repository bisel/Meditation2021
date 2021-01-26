package com.soulfriends.meditation.model;

//  notitype : 1 (간단알람)
//  notisubtype
//  1.  친구 신청 수락 (누가 유저의 친구신청을 수락/거절했습니다.)
//  2.  친구 신청 거절
//  3.  감정 공유 수락 (누가 유저의 친구신청을 수락/거절했습니다.)
//  4.  감정 공유 거절
//
//  notisubtype
//  1 . 친구 신청 시청 (누가 유저에게 친구 공류를 신청했습니다.)
//  2.  감정 공유 신청 (누가 유저에게 감정 공유를 신청했습니다.)

public class MeditationNotify {
    public String uid;    // Noti를 발생시킨자
    public int notitype;  // 1 : 간단 알람  2 : 수락,거절 알람
    public int notisubtype;
    public String notidate;  // yymmddhhmmss
}
