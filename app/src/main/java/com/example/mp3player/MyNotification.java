package com.example.mp3player;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyNotification extends Application {

    //initialize channel id name
    public static final String CHANNEL_ID = "ServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    //method to create notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "MP3Player Foreground",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
