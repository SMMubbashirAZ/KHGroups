package com.blazeminds.pos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Saad Kalim on 8/25/2015.
 */
public class AutoBootApp extends BroadcastReceiver {
    
    
    @Override
    public void onReceive(Context context, Intent intent) {

        
        Log.e("AutoBootCheck", intent.getAction());
        
        // Only perform this code if the BroadcastReceiver received the ACTION_BOOT_COMPLETED action.
        
        String autoSync = "";
        
        Intent i = new Intent(context, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Important because the activity is launched from a context outside the activity. Without this, the activity will not start.
        
        DateFormat dfProper = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm a");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        String dateProper = dfProper.format(Calendar.getInstance().getTime());
       
        PosDB db =PosDB.getInstance(context);
        db.OpenDb();
        db.createPhoneEntry(date);
        autoSync= db.getSettingsAutoSync();
        db.CloseDb();
        
        
        if (autoSync.equals("1")) {
            
            try {
                Log.e("StartServiceInBack", "AutoBoot");
                ContextCompat.startForegroundService(context, new Intent(context, MyService.class));
                Log.e("StartServiceInBack", "AutoBoot");
                
            } catch (IllegalStateException e) {
                Log.d("AutoBootCheck", "AppCrashing IllegalStateExcep");
                e.printStackTrace();
                
            } catch (RuntimeException e) {
                Log.d("AutoBootCheck", "AppCrashing RuntimeExcep");
                e.printStackTrace();
                
            }
            
        }
        
    }
    
}
