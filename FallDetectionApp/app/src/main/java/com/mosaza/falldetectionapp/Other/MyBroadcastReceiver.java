package com.mosaza.falldetectionapp.Other;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CustomFirebaseMessagingService.isNotificationClicked = true;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);

        boolean call = intent.getBooleanExtra("call", true);
        if(!call)
            return;

        Toast.makeText(context.getApplicationContext(), "Calling Help", Toast.LENGTH_SHORT).show();
        String latitude = intent.getStringExtra("latitude");
        String longitude = intent.getStringExtra("longitude");

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(
                    "911",
                    null,
                    "please help falling person at location: (latitude = " + latitude + ", longitude = " + longitude + ")",
                    null,
                    null);
            Toast.makeText(context.getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context.getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
