package com.blazeminds.pos.resources;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.blazeminds.pos.R;


/**
 * Created by Saad Kalim on 03-Aug-16.
 */
public class Loader {
    
    private com.wang.avi.AVLoadingIndicatorView ldr;
    
    private Dialog dialog;
    private ProgressDialog nDialog;
    private ProgressDialog progressDoalog;
    
    public void showDialog(Context activity) {

       /* nDialog = new ProgressDialog(activity);

        nDialog.setMessage("Loading...");
        nDialog.setTitle("Synchronizing");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(false);
        nDialog.show();*/
        
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_custom_loader);
        dialog.setOwnerActivity((Activity) activity);
        
        //spinner = (ProgressBar) dialog.findViewById(R.id.progressBar);
        
        ldr = dialog.findViewById(R.id.avloadingIndicatorView);
        
        ldr.setVisibility(View.VISIBLE);
        
        //spinner.setVisibility(View.VISIBLE);
        //if (!((Activity) activity).isDestroyed())
        dialog.show();
        
    }
    
    public void HideLoader() {
        
      
        Activity activity = dialog.getOwnerActivity();
        
        if (dialog != null && activity != null && !activity.isDestroyed()) {
            //ldr.setVisibility(View.GONE);
            
            dialog.dismiss();
        }
        
    }
    
}
