package com.soulfriends.meditation.model;
// alarm type : 1 : 간단 알람  2 : 수락,거절 알람
//
// alarm subtype
//  1.  친구 신청 수락 (상대방이 수락)
//  2.  친구 신청 거절 (상대방이 거절)
//  3.  감정 공유 수락 (상다방이 나에게 수락)
//  4.  감정 공유 거절 (상다방이 나에게 거절)
//
//  1 . 친구 신청 신청 (상대방이 나에게 신청)
//  2.  감정 공유 신청 (상대방이 나에게 신청)
public class MeditationAlarm {
    public String uid;     // 알람을 보낸사람 uid
    public String releasedate;  // yymmddhhmmss
    public int alarmtype;
    public int alarmsubtype;
    public boolean doneshow = false;    // 보았는지 여부
}
