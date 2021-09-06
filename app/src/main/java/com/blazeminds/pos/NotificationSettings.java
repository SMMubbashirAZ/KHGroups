package com.blazeminds.pos;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;

public class NotificationSettings extends Fragment {
    
    public static final String TAG = NotificationSettings.class.getSimpleName();
    static String FileName = "NotificationValues";
    SharedPreferences SavedValues;
    SharedPreferences.Editor edit;
    CheckBox VibChk, SoundChk, LightChk, AlertChk, EmailChk, PhoneChk;
    boolean vib, sound, light, alert, email, phone_start;
    Button Save;
    
    public NotificationSettings() {
    }
    
    public static NotificationSettings newInstance() {
        return new NotificationSettings();
    }
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        
        View rootView = inflater.inflate(R.layout.notifications_settings, container, false);
        
        SavedValues = getActivity().getSharedPreferences(FileName, Context.MODE_PRIVATE);
        
        edit = SavedValues.edit();
        
        VibChk = rootView.findViewById(R.id.VibCheck);
        SoundChk = rootView.findViewById(R.id.SoundCheck);
        LightChk = rootView.findViewById(R.id.LightCheck);
        AlertChk = rootView.findViewById(R.id.AlertCheck);
        EmailChk = rootView.findViewById(R.id.EmailCheck);
        PhoneChk = rootView.findViewById(R.id.PhoneCheck);
        
        vib = SavedValues.getBoolean("vibration", true);
        sound = SavedValues.getBoolean("sound", true);
        light = SavedValues.getBoolean("light", true);
        alert = SavedValues.getBoolean("alert", true);
        email = SavedValues.getBoolean("email", true);
        phone_start = SavedValues.getBoolean("phone_start", true);

//EmailChk.setEnabled(false);
        Save = rootView.findViewById(R.id.SaveSettings);

//        EmailChk.setEnabled(false);
        
        if (vib)
            VibChk.setChecked(true);
        
        if (sound)
            SoundChk.setChecked(true);
        
        if (light)
            LightChk.setChecked(true);
        
        if (alert)
            AlertChk.setChecked(true);
        
        if (email)
            EmailChk.setChecked(true);
        if (phone_start)
            PhoneChk.setChecked(true);
        
        
        PhoneChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (PhoneChk.isChecked())
                    GetPhoneDialog();
            }
        });
        
        
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VibChk.isChecked()) {
                    
                    edit.putBoolean("vibration", true);
                    
                    
                }
                
                if (!(VibChk.isChecked())) {
                    
                    edit.putBoolean("vibration", false);
                    
                }
                
                if (SoundChk.isChecked()) {
                    
                    edit.putBoolean("sound", true);
                    
                }
                
                if (!(SoundChk.isChecked())) {
                    
                    edit.putBoolean("sound", false);
                    
                }
                
                if (LightChk.isChecked()) {
                    
                    edit.putBoolean("light", true);
                    
                }
                
                if (!(LightChk.isChecked())) {
                    
                    edit.putBoolean("light", false);
                    
                }
                
                if (AlertChk.isChecked()) {
                    
                    edit.putBoolean("alert", true);
                    
                }
                
                if (!(AlertChk.isChecked())) {
                    
                    edit.putBoolean("alert", false);
                    
                }
                
                if (EmailChk.isChecked()) {
                    
                    edit.putBoolean("email", true);
                    
                }
                
                if (!(EmailChk.isChecked())) {
                    
                    edit.putBoolean("email", false);
                    
                }
                
                if (PhoneChk.isChecked()) {
                    
                    edit.putBoolean("phone_start", true);
                    
                }
                
                if (!(PhoneChk.isChecked())) {
                    
                    edit.putBoolean("phone_start", false);
                    
                }
                
                edit.apply();
                
                Toast.makeText(AppContextProvider.getContext(), "Settings Saved", Toast.LENGTH_SHORT).show();
            }
        });
        
        return rootView;
    }
    
    
    public boolean GetPhoneDialog() {
        
        final boolean[] ans = {false};
        
        final Dialog dialog = new Dialog(getActivity());
        
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_settings_setphone);
        //dialog.getWindow().setBac(getResources().getColor(R.color.login_bg));
        // dialog.getWindow().
        
        
        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        
        
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview
        
        
        //  dialog.setTitle(ItemName);
        final EditText AdminContact = dialog.findViewById(R.id.SettingAdminPhone);
/*
        final EditText PassSetting = (EditText)dialog.findViewById(R.id.SettingPass);
        final EditText PassSetting2 = (EditText)dialog.findViewById(R.id.SettingPass1);
*/
        Button Login = dialog.findViewById(R.id.SettingLogin);

//        Login.setText("Confirm Login");
        
        PosDB db = PosDB.getInstance(getActivity());
        db.OpenDb();
        String PhoneDB = db.getSettingsAdminPhone();
        db.CloseDb();
        
        AdminContact.setText(PhoneDB);
        
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        
                        if (AdminContact.getText().toString().isEmpty()) {
                            Toast.makeText(AppContextProvider.getContext(), "Contact Required", Toast.LENGTH_LONG).show();
                            
                            AdminContact.setError("REQUIRED");
                            
                            
                        } else {
                            
                            PosDB db = PosDB.getInstance(getActivity());
                            db.OpenDb();
                            db.updateSettingsPhone(AdminContact.getText().toString());
                            db.CloseDb();
                            
                            Toast.makeText(AppContextProvider.getContext(), "Phone Notification is Set", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            
                            // Settings_Pass.setChecked(true);
                            
                            db.OpenDb();
                            String PhoneDB = db.getSettingsAdminPhone();
                            AdminContact.setText(PhoneDB);
                            db.CloseDb();
                            
                            if (PhoneChk.isChecked()) {
                                
                                edit.putBoolean("phone_start", true);
                                
                            }
                            
                            if (!(PhoneChk.isChecked())) {
                                
                                edit.putBoolean("phone_start", false);
                                
                            }
                            
                            edit.commit();
                            
                            
                            ans[0] = true;
                        }
                        
                    }
                });
                
                
            }
        });
        
        
        dialog.show();
        
        return ans[0];
    }
    
    
}
