package com.example.mycar;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class Remind extends BroadcastReceiver {

    private NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        long remind_time = intent.getLongExtra("remind_time",0);
        String address = intent.getStringExtra("address");
        notificationManager = NotificationManagerCompat.from(context);

        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);

        Notification notification = new NotificationCompat.Builder(context, "Notificationchannel")
                .setSmallIcon(R.drawable.ic_place_black_24dp)
                .setContentTitle("Parking Time Expiry")
                .setContentText("Your Parking expires in " + (remind_time / 60000) + " minutes")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Your Parking expires in " + (remind_time / 60000) + " minutes. Your car is at\n"+address))
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);
    }
}
