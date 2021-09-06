package com.blazeminds.pos.resources;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.blazeminds.pos.MainActivity;
import com.blazeminds.pos.R;

public class ReminderBroadcastReciever extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        long when = System.currentTimeMillis();
        String text = bundle.getString("event");
        String date = " " + bundle.getString("time");
//        Uri sound = Uri. parse (ContentResolver. SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/alarm_warning.mp3" ) ;

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Uri notification = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.phone_funny_bell);
//        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//        AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
//                .setUsage(AudioAttributes. USAGE_ALARM )
//                .build() ;
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,"KhalilGroups")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(text)
                .setContentText(date).setSound(alarmSound)
                .setAutoCancel(true).setWhen(when)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager= NotificationManagerCompat.from(context);
        notificationManager.notify(200,builder.build());
    }
}
