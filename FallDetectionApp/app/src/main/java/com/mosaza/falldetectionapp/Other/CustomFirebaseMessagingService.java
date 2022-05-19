package com.mosaza.falldetectionapp.Other;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mosaza.falldetectionapp.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CustomFirebaseMessagingService extends FirebaseMessagingService {

    public static Boolean isNotificationClicked = false;
    private String latitude = "33.3333", longitude = "44.4444";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getData().size() > 0){
            latitude = remoteMessage.getData().get("latitude");
            longitude = remoteMessage.getData().get("longitude");
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
        }
    }

    private void showNotification(String title, String body) {
        isNotificationClicked = false;

        NotificationManager notificationManager =  (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "FALL_DETECTED";
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.alarm_sound);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Custom Notification",
                    NotificationManager.IMPORTANCE_HIGH);

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            notificationChannel.setDescription("Notification to alert the user that a fall has been detected");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[] {0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationChannel.setSound(alarmSound, attributes);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this,
                NOTIFICATION_CHANNEL_ID);

        builder
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.fall_help)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("warning")
                .setSound(alarmSound)
                .setTicker("Fall Detected")
                .addAction(R.drawable.phone_big, "Call Help", doCallHelp())
                .addAction(R.drawable.dismiss, "Dismiss", doDismiss());

        notificationManager.notify(1, builder.build());
        MediaPlayer mp = MediaPlayer. create (getApplicationContext(), alarmSound);
        mp.start();

        waitToCall();
    }

    private PendingIntent doCallHelp() {
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        intent.putExtra("call", true);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private PendingIntent doDismiss() {
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        intent.putExtra("call", false);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }

    private void waitToCall() {
        ScheduledExecutorService worker =
                Executors.newSingleThreadScheduledExecutor();

        Runnable task = new Runnable() {
            public void run() {
                if (!CustomFirebaseMessagingService.isNotificationClicked) {
                    Intent intent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);;
                    intent.putExtra("call", true);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    sendBroadcast(intent);
                }
            }
        };

        worker.schedule(task, 30, TimeUnit.SECONDS);
    }
}
