package com.blazeminds.pos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class ServiceRestarter extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i("Broadcast Listened", "Service tried to stop");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                PosDB db = PosDB.getInstance(context);
                db.OpenDb();
                if (db.getSettingsAutoSync().equals("1")) {
                    Log.d("TAG", "run: Service restarted");
//                    Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, MyService.class));
        } else {
            context.startService(new Intent(context, MyService.class));
        } }
            }},14000);
    }
}