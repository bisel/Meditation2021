package com.soulfriends.meditation.view.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.soulfriends.meditation.R;
import com.soulfriends.meditation.view.MainActivity;

public class MeditationNotificationMgr {

    public static final int NOTIFICATION_ID = 555;
    private final String PRIMARY_CHANNEL = "PRIMARY_CHANNEL_ID";
    private final String PRIMARY_CHANNEL_NAME = "PRIMARY";

    private MeditationService service;

    private String strAppName;
    //strLiveBroadcast;

    private Resources resources;

    private NotificationManagerCompat notificationManager;

    private Context context;


    public MeditationNotificationMgr(MeditationService service) {

        this.service = service;
        this.resources = service.getResources();

        this.context = service.getContext();

        strAppName = resources.getString(R.string.app_name);
        //strLiveBroadcast = resources.getString(R.string.live_broadcast);

        notificationManager = NotificationManagerCompat.from(service);
    }

    public void startNotify(String playbackStatus, String thumbnail_url, String title) {

        Bitmap largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);

       // Bitmap largeIcon = BitmapFactory.decodeResource(resources, R.drawable.main_video);
        //emotion_1
        int icon = R.drawable.pause_noti_btn; // pause_btn  ic_player_pause
        Intent playbackAction = new Intent(service, MeditationService.class);
        playbackAction.setAction(MeditationService.ACTION_PAUSE);
        PendingIntent action = PendingIntent.getService(service, 1, playbackAction, 0);

        if(playbackStatus.equals(PlaybackStatus.PAUSED)){

            icon = R.drawable.play_noti_btn; // ic_player_play
            playbackAction.setAction(MeditationService.ACTION_PLAY);
            action = PendingIntent.getService(service, 2, playbackAction, 0);

        }

        Intent stopIntent = new Intent(service, MeditationService.class);
        stopIntent.setAction(MeditationService.ACTION_STOP);
        PendingIntent stopAction = PendingIntent.getService(service, 3, stopIntent, 0);

        Intent intent = new Intent(service, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, 0);

        notificationManager.cancel(NOTIFICATION_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL, PRIMARY_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(service, PRIMARY_CHANNEL)


                .setAutoCancel(false)
                .setContentTitle(title)
               // .setContentText(strAppName)
                //.setLargeIcon(largeIcon)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.statusbaricon)
//                //.addAction(R.drawable.ic_player_rew,"skip prev", action)
                .addAction(icon, "pause", action)
                .addAction(R.drawable.stop_noti_btn, "stop", stopAction) // rectangle_14 ic_player_stop
//                //.addAction(R.drawable.ic_player_forward,"skip next", action) // ic_player_forward
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(service.getMediaSession().getSessionToken())
                        .setShowActionsInCompactView(0, 1)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopAction));

//         .setShowActionsInCompactView(0, 1)



        Glide.with(context)
                .asBitmap()
                .load(thumbnail_url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        builder.setLargeIcon(resource);
                        service.startForeground(NOTIFICATION_ID, builder.build());
                    }
                });

        int xx= 0;
    }

    public void cancelNotify() {

        service.stopForeground(true);
    }

}
