package com.harshchourasiya.movies.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.harshchourasiya.movies.MainActivity;
import com.harshchourasiya.movies.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.harshchourasiya.movies.App.CHANNEL_ID;

public class PushNotificationService extends FirebaseMessagingService {

    // This run when someone send notification from admin panel

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // getting data from message
        toSendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getImageUrl());
    }

    private void toSendNotification(String title, String body, Uri url) {
        // Create a notification to notify user

        Bitmap b = null;
        if (url.toString().length() != 0 && url != null && url.toString() != "") {
            try {
                URLConnection connection = new URL(url.toString()).openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                BufferedInputStream bin = new BufferedInputStream(in);
                b = BitmapFactory.decodeStream(bin);
            } catch (Exception e) {

            }
        } else {
            b = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.logo)
                .setLargeIcon(b)
                .setAutoCancel(true)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        manager.notify(0, builder.build());
    }
}
