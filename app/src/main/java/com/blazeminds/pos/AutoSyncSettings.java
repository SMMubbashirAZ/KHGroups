package com.blazeminds.pos;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AutoSyncSettings extends Fragment {
    
    public static final String TAG = AutoSyncSettings.class.getSimpleName();
    static int TimeInVal, TimeOutVal, autoSyncVal;
    static long SyncDurVal;
    static String FileName = "NotificationValues";
    static int Hour = 0;
    static int Minute = 0;
    static int Second = 0;
    CheckBox AutoSyncCheck;
    Button SaveSettingsBtn;
    TextView CheckIn, CheckOut;
    Spinner SyncDuration;
    SharedPreferences Results;
    SharedPreferences.Editor edit;
    int syncDur;
    boolean autoSync;
    
    
    public AutoSyncSettings() {
    }
    
    public static AutoSyncSettings newInstance() {
        return new AutoSyncSettings();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        
        View rootView = inflater.inflate(R.layout.autosync_settings, container, false);
        
        
        Results = getActivity().getSharedPreferences(FileName, 0);
        edit = Results.edit();
        
        syncDur = Results.getInt("syncDur", 0);
        autoSync = Results.getBoolean("autoSync", false);
        
        
        AutoSyncCheck = rootView.findViewById(R.id.AutoSyncCheck);
        
        CheckIn = rootView.findViewById(R.id.CheckInTimeTxt);
        CheckOut = rootView.findViewById(R.id.CheckOutTimeTxt);
        
        SyncDuration = rootView.findViewById(R.id.DurationTimeTxt);
        
        
        SaveSettingsBtn = rootView.findViewById(R.id.SaveSettings);
        
        String SettingsInfo = ShowMenuAsSettings();
        
        if (autoSync) {
            AutoSyncCheck.setChecked(true);
            
        }

/*

        if(SettingsInfo.contains("async1")) {
            AutoSyncCheck.setChecked(true);
            edit.putBoolean("autoSync", true);

        }

        if(SettingsInfo.contains("async0")) {
            AutoSyncCheck.setChecked(false);
            edit.putBoolean("autoSync",false);
        }
*/

//        edit.commit();
        
        if (SettingsInfo.contains("syncdurindex")) {
            
            String[] parts = SettingsInfo.split("syncdurindex");
            String data = parts[1];
            String[] dataAct = data.split("inv");
            final String dataIndex = dataAct[0];
            
            
            SyncDuration.post(new Runnable() {
                @Override
                public void run() {
                    SyncDuration.setSelection(Integer.parseInt(dataIndex));
                }
            });
            
        }
        
        
        PosDB db = PosDB.getInstance(getActivity());
        db.OpenDb();
        String DBChkIn = db.getTimeIn();
        String DBChkOut = db.getTimeOut();
        db.CloseDb();
        
        
        CheckIn.setText(Convert24HourTo12Hour(DBChkIn));
        CheckOut.setText(Convert24HourTo12Hour(DBChkOut));
        
        CheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                SelectCheckInTimeDialog();
                
            }
        });
        
        
        CheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectCheckOutTimeDialog();
                
                
            }
        });
        
        
        final long[] SyncDurIndex = {0};
        
        // DROP DOWN BOX STARTS
        
        // Spinner click listener
        SyncDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                
                SyncDurIndex[0] = i;
                
                if (i == 0) {
                    SyncDurVal = 0;
                }
                if (i == 1) {
                    //SyncDurVal = 20 * 60000;
                    SyncDurVal = 5;
                    
                    edit.putInt("syncDur", 5);
                    
                    
                }
                
                if (i == 2) {
                    SyncDurVal = 15;
                    
                    edit.putInt("syncDur", 15);
                }
                
                if (i == 3) {
                    SyncDurVal = 30;
                    edit.putInt("syncDur", 30);
                    
                }
                
                if (i == 4) {
                    SyncDurVal = 60;
                    
                    edit.putInt("syncDur", 60);
                }
                
                if (i == 5) {
                    SyncDurVal = 180;
                    edit.putInt("syncDur", 180);
                    
                }
                
                edit.apply();
                
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            
            }
        });
        
        // Spinner Drop down elements
        
        List<String> DurationCategories = new ArrayList<>();
        DurationCategories.add(getString(R.string.sync_spinner_title));
        DurationCategories.add(getString(R.string.t5));
        DurationCategories.add(getString(R.string.t15));
        DurationCategories.add(getString(R.string.t30));
        DurationCategories.add(getString(R.string.t1));
        DurationCategories.add(getString(R.string.t3));
        
        
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterDuration = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, DurationCategories);
        
        // Drop down layout style - editOrderList view with radio button
        dataAdapterDuration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        // attaching data adapter to spinner
        SyncDuration.setAdapter(dataAdapterDuration);
        
        
        // DROP DOWN BOX ENDS
        
        if (AutoSyncCheck.isChecked()) {
            edit.putBoolean("autoSync", true);
            
            autoSync = true;
            
        }
        
        if (!(AutoSyncCheck.isChecked())) {
            //else{
            edit.putBoolean("autoSync", false);
            autoSync = false;
        }
        edit.commit();
        
        SaveSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(AutoSyncCheck.isChecked())
                    autoSyncVal =1;
                else
                    autoSyncVal=0;
*/
                
                if (AutoSyncCheck.isChecked()) {
                    
                    edit.putBoolean("autoSync", true);
                    
                    autoSync = true;
                    
                }
                
                if (!(AutoSyncCheck.isChecked())) {
                    
                    edit.putBoolean("autoSync", false);
                    autoSync = false;
                }
                edit.commit();
                
                
                String ChkIn = Convert12HourTo24Hour(CheckIn.getText().toString());
                String ChkOut = Convert12HourTo24Hour(CheckOut.getText().toString());


//                if( autoSyncVal==1 && (ChkIn.trim().isEmpty() || ChkOut.trim().isEmpty() || SyncDurVal==0) ){
                if (autoSync && (ChkIn.trim().isEmpty() || ChkOut.trim().isEmpty() || SyncDurVal == 0)) {
                    
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AppContextProvider.getContext(), "Time In, Time Out & Sync Duration\n REQUIRED", Toast.LENGTH_LONG).show();
                        }
                    });
                    
                }

//                else if( autoSyncVal ==1 && ( !(ChkIn.trim().isEmpty()) || !(ChkOut.trim().isEmpty()) || SyncDurVal !=0) ) {
                else if (autoSync && (!(ChkIn.trim().isEmpty()) || !(ChkOut.trim().isEmpty()) || SyncDurVal != 0)) {
                    PosDB db = PosDB.getInstance(getActivity());
                    db.OpenDb();
                    db.updateAutoSyncSettings(1, ChkIn, ChkOut, SyncDurVal, SyncDurIndex[0]);
                    
                    String res = db.getSettingsData();
                    db.CloseDb();
                    Log.d("Setings DB Data UPDATED", res);
                    
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AppContextProvider.getContext(), "Auto Sync Enabled", Toast.LENGTH_SHORT).show();
                        }
                    });


/*
                   if( isMyServiceRunning(MyService.class) ) {
                       getActivity().stopService(new Intent(getActivity(), MyService.class));
                   }
                    else{}
*/
                    
                    getActivity().stopService(new Intent(getActivity(), MyService.class));
                    
                    getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().startService(new Intent(getActivity(), MyService.class));
                } else if (!autoSync) {
                    
                    PosDB db = PosDB.getInstance(getActivity());
                    db.OpenDb();
                    db.updateAutoSyncSettings(0, ChkIn.trim(), ChkOut.trim(), 0, 0);
                    
                    String res = db.getSettingsData();
                    db.CloseDb();
                    Log.d("Setings DB Data UPDATED", res);
                    
                    getActivity().stopService(new Intent(getActivity(), MyService.class));
                    
                    
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AppContextProvider.getContext(), "Auto Sync Disabled", Toast.LENGTH_SHORT).show();
                        }
                    });
                    
                }
                
                
            }
        });
        
        
        return rootView;
    }
    
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            return serviceClass.getName().equals(service.service.getClassName());
        }
        return false;
    }
    
    public String ShowMenuAsSettings() {
        
        String Options = "";
        ArrayList<HashMap<String, String>> DBVal;
        
        PosDB db = PosDB.getInstance(getActivity());
        db.OpenDb();
        DBVal = db.SettingsDataHash();
        db.CloseDb();
        
        
        Log.d("DBSetSize", DBVal.size() + "");
        
        String timing = "", route = "", ASync = "", SyncDur = "", SyncDurIndex = "", Inv = "", Cust = "", PO = "", Sumry = "", SyncPg = "", EPass = "";
        String TI = "", TO = "", Pass = "";
        
        HashMap<String, String> f;
        
        if (DBVal.size() > 0) {
            
            for (int i = 0; i < DBVal.size(); i++) {
                
                f = DBVal.get(i);
                
                timing = f.get("timing");
                route = f.get("route");
                ASync = f.get("async");
                SyncDur = f.get("syncdur");
                SyncDurIndex = f.get("syncdurindex");
                Inv = f.get("inv");
                Cust = f.get("cust");
                PO = f.get("po");
                Sumry = f.get("sumry");
                SyncPg = f.get("syncpg");
                TI = f.get("ti");
                TO = f.get("to");
                Pass = f.get("pass");
                EPass = f.get("epass");
                
                
            }
        }
        
        Log.d("Timing", timing);
        Log.d("ROute", route);
        Log.d("ASync", ASync);
        Log.d("TI", TI);
        Log.d("TO", TO);
        Log.d("SyncDur", SyncDur);
        Log.d("SyncDurIndex", SyncDurIndex);
        Log.d("Inv", Inv);
        Log.d("Cust", Cust);
        Log.d("Sumry", Sumry);
        Log.d("SyncPg", SyncPg);
        Log.d("Pass", Pass);
        Log.d("EPass", EPass);
        Log.d("PO", PO);
        
        
        return "timing" + timing + "route" + route + "async" + ASync + "syncdur" + SyncDur + "syncdurindex" + SyncDurIndex + "inv" + Inv + "cust" + Cust + "sumry" + Sumry + "syncpg" + SyncPg + "pass" + Pass + "epass" + EPass + "po" + PO + "timein" + TI + "timeout" + TO;
    }
    
    public void SelectCheckInTimeDialog() {
        
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        
        dialog.setContentView(R.layout.activity_select_time);
        
        final Button SelectBtn = dialog.findViewById(R.id.SelectTime);
        Button CancelBtn = dialog.findViewById(R.id.CancelTime);
        final TimePicker timePicker = dialog.findViewById(R.id.TimeSelected);
        
        SelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                
                timePicker.clearFocus();
                Hour = timePicker.getCurrentHour();
                Minute = timePicker.getCurrentMinute();
                
                
                onTimeSet(timePicker, Hour, Minute, CheckIn);
                dialog.dismiss();
                
            }
        });
        
        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                dialog.dismiss();
            }
        });
//		dialog.setTitle("Select Date & Time");
        
        //Set Size of Dialog Starts
/*
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
*/
        //Set Size of Dialog Ends
        
        dialog.show();
    }
    
    
    public void SelectCheckOutTimeDialog() {
        
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        
        dialog.setContentView(R.layout.activity_select_time);
        
        Button SelectBtn = dialog.findViewById(R.id.SelectTime);
        Button CancelBtn = dialog.findViewById(R.id.CancelTime);
        final TimePicker timePicker = dialog.findViewById(R.id.TimeSelected);
        
        SelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                
                timePicker.clearFocus();
                Hour = timePicker.getCurrentHour();
                Minute = timePicker.getCurrentMinute();
                
                //CheckOut.setText( Hour + ":" + Minute) ;
                onTimeSet(timePicker, Hour, Minute, CheckOut);
                dialog.dismiss();
                
            }
        });
        
        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                dialog.dismiss();
            }
        });
//		dialog.setTitle("Select Date & Time");
        
        //Set Size of Dialog Starts
/*
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
*/
        //Set Size of Dialog Ends
        
        dialog.show();
    }
    
    
    public String Convert12HourTo24Hour(String SelectedDate) {
        DateFormat df = new SimpleDateFormat("hh:mm aa");
        
        //Desired format: 24 hour format: Change the pattern as per the need
        DateFormat outputformat = new SimpleDateFormat("HH:mm");
        Date date;
        String output = null;
        try {
            //Converting the input String to Date
            date = df.parse(SelectedDate);
            //Changing the format of date and storing it in String
            output = outputformat.format(date);
            //Displaying the date
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        
        return output;
    }
    
    public String Convert24HourTo12Hour(String SelectedDate) {
        DateFormat df = new SimpleDateFormat("HH:mm");
        
        //Desired format: 24 hour format: Change the pattern as per the need
        DateFormat outputformat = new SimpleDateFormat("hh:mm aa");
        Date date;
        String output = null;
        try {
            //Converting the input String to Date
            date = df.parse(SelectedDate);
            //Changing the format of date and storing it in String
            output = outputformat.format(date);
            //Displaying the date
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        
        return output;
    }
    
    public void onTimeSet(TimePicker view, int hourOfDay, int minute, TextView TimeView) {
        String am_pm = "";
        
        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);
        
        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            am_pm = "PM";
        
        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
        
        TimeView.setText(strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);
    }
    
    
}
