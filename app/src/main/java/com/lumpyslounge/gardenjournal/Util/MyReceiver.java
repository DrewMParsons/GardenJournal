package com.lumpyslounge.gardenjournal.Util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.lumpyslounge.gardenjournal.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyReceiver extends BroadcastReceiver
{
    static int notificationID;
    String channel_id = "Alerts";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Alert channel";
            String description = "Alerts set for important Garden Events";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(channel_id,name,importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Notification notification = new NotificationCompat.Builder(context,channel_id)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("Event Type: " + intent.getStringExtra(Intent.EXTRA_SUBJECT))
                .setContentTitle("Event: "+intent.getStringExtra(Intent.EXTRA_TITLE)+ " Notification: "+ notificationID)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID++,notification);
    }
}
