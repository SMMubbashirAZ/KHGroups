package com.blazeminds.pos;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

/*
 * Created by Saad Kalim on 9/9/2015.
 */
public class MyService extends Service {
    
    private static final String TAG = " ";
    private static final String CHANNEL_ID = "11111";
    private static PendingIntent pendingIntent;
    private static AlarmManager manager;
    int /*syncDur,*/paidVal;
    String SyncDur;
    String TimeIn;
    String TimeOut;
    String /*EmailDB,*/ Emp_Name;
    
    @Override
    public IBinder onBind(Intent intent) {
        
        return null;
    }
    
    @Override
    public void onCreate() {
    
    }
    
    public void startAt10(int Hr, int Min, int dur) {
        
        Log.e("HR-=-MIN", Hr + " = " + Min);
        Log.e("StartServiceInBack", String.valueOf((dur * 60000)));
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            RemoteViews remoteViews = new RemoteViews(getPackageName(),
                    R.layout.custom_notification);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, TAG,
                    NotificationManager.IMPORTANCE_MIN);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            
            Notification notification = new Notification.Builder(getApplicationContext(),
                    CHANNEL_ID).setSmallIcon(R.drawable.ic_launcher).setContent(remoteViews).build();
            startForeground(1, notification);
        }
        assert manager != null;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),  (dur *
        60 * 1000), pendingIntent);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
         //   stopForeground(STOP_FOREGROUND_REMOVE);
        }
    }
    
    public void AlarmCancel() {
        
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            manager.cancel(pendingIntent);
        }
        
    }
    
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        
        super.onStartCommand(intent, flags, startId);

        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        Intent alarmIntent = new Intent(MyService.this, MyBroadcastReciever.class);
        
        pendingIntent = PendingIntent.getBroadcast(MyService.this, 0, alarmIntent, 0);
        PosDB db = PosDB.getInstance(getApplicationContext());
        db.OpenDb();
        String syncDurData = db.getSyncDuration();
        
        String TimeInData = db.getTimeIn();
        String TimeOutData = db.getTimeOut();
        
        String EmpNameData = db.getMobFName() + " " + db.getMobLName();
        
        db.CloseDb();
        
        SyncDur = syncDurData;
        TimeIn = TimeInData;
        TimeOut = TimeOutData;
        Emp_Name = EmpNameData;
        
        //syncDur = Results.getInt("syncDur", 0);
        /*paidVal = Results.getInt("paid",1); // FREE VERSION = 0 , Paid Version = 1*/
        
        paidVal = 1; // FREE VERSION = 0 , Paid Version = 1
        
        if (!TimeIn.equalsIgnoreCase("") && !TimeOut.equalsIgnoreCase("") && !Emp_Name.isEmpty()) {
            
            String[] TimeSet = TimeIn.split(":");
            String Hr = TimeSet[0];
            String Min = TimeSet[1];
            
            Log.d("Hr", Hr);
            Log.d("Min", Min);
            
            startAt10(Integer.parseInt(Hr.trim()), Integer.parseInt(Min.trim()), /*Integer.parseInt(SyncDur)*/Integer.parseInt(SyncDur));
            
            Log.e("SyncDuration", SyncDur);
            Log.d("localData", "broad Working");
            
        } else {
            Log.d("localData", "broad Not Working");
        }
        
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        //am.cancel(pi);
        
        super.onDestroy();
        Log.e("StopService", "Service Stop 1");
        try {
            if (pendingIntent != null) {
                
                Log.e("StopService", "Service Stop 2");

                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("restartservice");
                            broadcastIntent.setClass(getApplicationContext(), ServiceRestarter.class);
                            sendBroadcast(broadcastIntent);

                if (manager != null)
                    manager.cancel(pendingIntent);
                if (pendingIntent != null) {
                    pendingIntent.cancel();
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}