package com.example.omi.niggachatdemo.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.omi.niggachatdemo.application.NiggaChatApplication;
import com.example.omi.niggachatdemo.R;
import com.example.omi.niggachatdemo.activity.ChatRoomActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by omi on 11/9/2016.
 */

public class CustomFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        System.out.println("on Message received");
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        System.out.println("notification: " + title + "  " + body);
        //sendNotification(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            final String from = remoteMessage.getData().get("fromName");
            if (((NiggaChatApplication) getApplication()).getFull_name().equals(from)) {
                return;
            }
            final String message = remoteMessage.getData().get("message");
            final String time = remoteMessage.getData().get("time");
            Intent intent = new Intent("receive_message");
            intent.putExtra("from", from);
            intent.putExtra("message", message);
            intent.putExtra("time", time);
            sendBroadcast(intent);
        }


    }


    private void sendNotification(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

// this is a my insertion looking for a solution
        int icon = R.drawable.icon;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
