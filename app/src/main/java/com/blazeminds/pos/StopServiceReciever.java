package com.blazeminds.pos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Blazeminds on 9/24/2018.
 */

public class StopServiceReciever extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        
        String action = intent.getAction();
        if (action != null && action.equalsIgnoreCase("com.blazeminds.pos.stopservice")) {
            context.stopService(new Intent(context, MyService.class));
            
        }
    }
}
