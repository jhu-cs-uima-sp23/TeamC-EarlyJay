package com.example.empty;


import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 2;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);

        // Permission is granted, show the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "EarlyJay")
                .setSmallIcon(R.drawable.round_notifications_none_24)
                .setContentTitle("EarlyJay Task Reminder")
                .setContentText("You have a coming task!")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }
}