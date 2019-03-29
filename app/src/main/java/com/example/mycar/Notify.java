package com.example.mycar;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notify extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    "Notificationchannel",
                    "Park Expiry",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.setDescription("Your Parking Time expires soon");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}
