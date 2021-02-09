package com.soulfriends.meditation.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Notification {

    private static Notification instance = null;

    private Context context;
    private Activity activity;

    private boolean s_test = true;

    public static Notification instance(){
        return instance;
    }

    public static Notification with(Context context, Activity activity) {

        if (instance == null)
            instance = new Notification(context, activity);

        return instance;
    }
    public Notification(Context context, Activity activity) {

        this.context = context;
        this.activity = activity;
    }

    private int getRandomNumber(int min,int max) {
        return (new Random()).nextInt((max - min) + 1) + min;
    }

    public void Register() {
        // 기획상 30일 만 등록하도록 한다.
        // 현재 게임 시간 기준으로


        if (s_test)
        {
            // 테스트 목적

            ClearNotifications();

            String str_title = "힐링 타이틀";

            String str_msg_0 = "제목 1";
            String str_msg_1 = "제목 2";
            String str_msg_2 = "제목 3";


            float daytime_01 = 20;// 시간 * 분 * 초 * 밀리
            SendNotification(1, daytime_01, str_title, str_msg_0);

            float daytime_02 = 40;// 시간 * 분 * 초 * 밀리
            SendNotification(2, daytime_02, str_title, str_msg_1);

            float daytime_03 = 60;// 시간 * 분 * 초 * 밀리
            SendNotification(3, daytime_03, str_title, str_msg_2);

            float daytime_04 = 80;// 시간 * 분 * 초 * 밀리
            SendNotification(4, daytime_04, str_title, str_msg_0);

            float daytime_05 = 100;// 시간 * 분 * 초 * 밀리
            SendNotification(5, daytime_05, str_title, str_msg_1);

            return;
        }

        float curtime = 0;

        float daytime = 24 * 60 * 60;// 시간 * 분 * 초 * 밀리

        // 10초 마다 테스트
        //float daytime = 10 ;// 시간 * 분 * 초 * 밀리

        String str_msg_0 = "";
        String str_msg_1 = "";
        String str_msg_2 = "";

        String str_title = "";

        for (int i = 0; i < 30; i++) {
            curtime += 1.0f;

            //curtime += daytime;

            int rand = getRandomNumber(0, 2 + 1); // 0 ~ 2
            switch (rand) {
                case 0: {
                    SendNotification(i, curtime, str_title, str_msg_0);
                }
                break;
                case 1: {
                    SendNotification(i, curtime, str_title, str_msg_1);
                }
                break;
                case 2: {
                    SendNotification(i, curtime, str_title, str_msg_2);
                }
                break;
                default:
                    break;
            }
        }
    }

    private void SendNotification(int id, float delay, String title, String message)
    {
        int sound = 1;
        int vibrate = 1;
        int lights = 1;
        int bgColor = 0;

        String channel = "default";
        String largeIconResource = "logo_icon.png";//""l_icon";//s_icon.png 확장자 빼고 이름만
        String smallIconResource = "logo_icon.png";//"s_icon"; //s_icon.png 확장자 빼고 이름만
        String bundle = "com.soulfriends.meditation";

        SetNotification(channel, bundle, largeIconResource, smallIconResource, title, message, id, delay, sound, vibrate, lights, bgColor);
    }

    private Set<String> channels = new HashSet<>();

    public void CreateChannel(String identifier, String name, String description, int importance, String soundName, int enableLights, int lightColor, int enableVibration, long[] vibrationPattern, String bundle) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        channels.add(identifier);

        NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(identifier, name, importance);
        channel.setDescription(description);
        if (soundName != null) {
            Resources res = context.getResources();
            int id = res.getIdentifier("raw/" + soundName, null, activity.getPackageName());
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            channel.setSound(Uri.parse("android.resource://" + bundle + "/" + id), audioAttributes);
        }
        channel.enableLights(enableLights == 1);
        channel.setLightColor(lightColor);
        channel.enableVibration(enableVibration == 1);
        if (vibrationPattern == null)
            vibrationPattern = new long[] { 1000L, 1000L };
        channel.setVibrationPattern(vibrationPattern);
        nm.createNotificationChannel(channel);
    }

    @TargetApi(24)
    private void createChannelIfNeeded(String identifier, String name, String soundName, boolean enableLights, boolean enableVibration, String bundle) {
        if (channels.contains(identifier))
            return;
        channels.add(identifier);

        CreateChannel(identifier, name, identifier + " notifications", NotificationManager.IMPORTANCE_DEFAULT, soundName, enableLights ? 1 : 0, Color.GREEN, enableVibration ? 1 : 0, null, bundle);
    }

    private int sound = 1;
    private int vibrate = 1;
    private int lights = 1;
    private int bgColor = 0;

    private String channel = "default";
    private String big_Icon = "l_icon";
    private String small_Icon = "s_icon";
    private String str_bundle = "com.soulfriends.meditation";

    public void register(String title, String message, int id, float _delayMs)
    {
        SetNotification(channel, str_bundle, big_Icon, small_Icon, title, message, id, _delayMs, sound, vibrate, lights, bgColor);
    }

    public void SetNotification(String channel, String bundle, String largeIconResource, String smallIconResource, String title, String message, int id, float _delayMs, int sound, int vibrate, int lights, int bgColor)
    {
        //Log.v("01", "SetNotification 01");
        //long delayMs = (long)_delayMs;


        long delayMs = 0;
        if(s_test)
        {
            //delayMs = 60 * (long)_delayMs;
            delayMs = (long)_delayMs;
        }
        else
        {
            delayMs = 24 * 60 * 60 * (long)_delayMs;
        }
        //long delayMs = 24 * 60 * 60 * (long)_delayMs;
        //long delayMs = 60 * (long)_delayMs;

        delayMs *= 1000;

        String soundName = null;
        String ticker = channel;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (channel == null)
                channel = "default";
            createChannelIfNeeded(channel, title, soundName, lights == 1, vibrate == 1, bundle);
        }

        Activity currentActivity = activity;
        AlarmManager am = (AlarmManager)currentActivity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(currentActivity, LocalNotificationReceiver.class);
        intent.putExtra("ticker", ticker);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("id", id);
        intent.putExtra("color", bgColor);
        intent.putExtra("sound", sound == 1);
        //intent.putExtra("soundName", soundName);
        intent.putExtra("vibrate", vibrate == 1);
        intent.putExtra("lights", lights == 1);

        //intent.putExtra("l_icon", "l_icon.png");//largeIconResource);
        //intent.putExtra("s_icon", "l_icon.png");//smallIconResource);

        intent.putExtra("l_icon", largeIconResource);
        intent.putExtra("s_icon", smallIconResource);

        intent.putExtra("bundle", bundle);
        intent.putExtra("channel", channel);
        //Bundle b = new Bundle();
        //b.putParcelableArrayList("actions", actions);
        //intent.putExtra("actionsBundle", b);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMs, PendingIntent.getBroadcast(currentActivity, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        else
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMs, PendingIntent.getBroadcast(currentActivity, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public void CancelPendingNotification(int id)
    {
        Activity currentActivity = activity;
        AlarmManager am = (AlarmManager)currentActivity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(currentActivity, LocalNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(currentActivity, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pendingIntent);
    }

    public void ClearNotifications()
    {
        Activity currentActivity = activity;
        NotificationManager nm = (NotificationManager)currentActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }
}