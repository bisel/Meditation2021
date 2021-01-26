package com.soulfriends.meditation.model;

public class MeditationAlarm {
    public String uid;     // 알람을 보낸사람 uid
    public String releaseDate;  // yymmddhhmmss
    public int alarmtype;
    public int alarmsubtype;
    public boolean doneshow = false;    // 보았는지 여부
}
