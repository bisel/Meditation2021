package com.soulfriends.meditation.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;


public class LocalNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {

        //Log.v("cry", "showLocalNotification  onReceive 01");

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        String ticker = intent.getStringExtra("ticker");
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        String s_icon = intent.getStringExtra("s_icon");
        String l_icon = intent.getStringExtra("l_icon");
        int color = intent.getIntExtra("color", 0);
        String bundle = intent.getStringExtra("bundle");
        Boolean sound = intent.getBooleanExtra("sound", false);
        String soundName = intent.getStringExtra("soundName");
        Boolean vibrate = intent.getBooleanExtra("vibrate", false);
        Boolean lights = intent.getBooleanExtra("lights", false);
        int id = intent.getIntExtra("id", 0);
        String channel = intent.getStringExtra("channel");

        Resources res = context.getResources();

        //Log.v("cry", "showLocalNotification onReceive 02");

        Intent notificationIntent = context.getPackageManager().getLaunchIntentForPackage(bundle);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (channel == null)
            channel = "default";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel);

        builder.setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.setColor(color);

        if (ticker != null && ticker.length() > 0)
            builder.setTicker(ticker);

        if (s_icon != null && s_icon.length() > 0)
            builder.setSmallIcon(res.getIdentifier(s_icon, "drawable", context.getPackageName()));

        if (l_icon != null && l_icon.length() > 0)
            builder.setLargeIcon(BitmapFactory.decodeResource(res, res.getIdentifier(l_icon, "drawable", context.getPackageName())));


        if (sound) {
            if (soundName != null) {
                int identifier = res.getIdentifier("raw/" + soundName, null, context.getPackageName());
                builder.setSound(Uri.parse("android.resource://" + bundle + "/" + identifier));
            } else
                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }

        if (vibrate)
            builder.setVibrate(new long[] {
                    1000L, 1000L
            });

        if (lights)
            builder.setLights(Color.GREEN, 3000, 3000);

        //Log.v("cry", "showLocalNotification onReceive 03");

        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }
}