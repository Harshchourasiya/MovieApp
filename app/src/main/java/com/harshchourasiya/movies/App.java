package com.harshchourasiya.movies;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class App extends Application {

    public static final String CHANNEL_ID = "Channel_Id";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        // Creating Notification Channel
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID , "Channelid", NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("For Latest Updates");
        channel.enableVibration(true);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }
}
