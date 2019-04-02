package com.example.user.cashearingapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMassagingService extends FirebaseMessagingService {

    public MyFirebaseMassagingService() {

    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        sendNotification(remoteMessage.getNotification().getBody());


    }

    private void sendNotification (String messageBody){


        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,122,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defueldSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBulder = new NotificationCompat.Builder(this);
        notificationBulder.setSmallIcon(R.drawable.cashearninglogo);
        notificationBulder.setContentTitle("CashEarning");
        notificationBulder.setContentText(messageBody);
        notificationBulder.setAutoCancel(true);
        notificationBulder.setSound(defueldSoundUri);
        notificationBulder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(122,notificationBulder.build());



    }


}
