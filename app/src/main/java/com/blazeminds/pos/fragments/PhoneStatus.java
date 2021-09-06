package com.blazeminds.pos.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blazeminds.pos.BuildConfig;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Blazeminds on 12/26/2017.
 */

public class PhoneStatus extends Fragment {
    
    public final static String TAG = PhoneStatus.class.getSimpleName();
    
    TextView mobileNameTV, androidVersionTV, appBuildVersionTV, appVersionCodeTV,
            currentDateTimeTV, systemDateTimeTV, locationEnabledTV, locationPermissionTV, internetEnableTV, internetPermissionTV, availableDiskSpaceTV, ramSizeTV, userIdTV, userNameTV, totalRamSizeTextView;
    
    public static PhoneStatus newInstance() {
        return new PhoneStatus();
    }
    
    private static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy, hh:mm aa", Locale.getDefault());
        
        //DateFormat df = DateFormat.getDateTimeInstance();
        
        //SelectedDate = dateFormat.format(new Date());
        
        return dateFormat.format(new Date());
        //return df.format(new Date());
    }
    
    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            int availBlocks = stat.getAvailableBlocks();
            int blockSize = stat.getBlockSize();
            long free_memory = (long) availBlocks * (long) blockSize;
            
            return free_memory;
        } else {
            return 0;
        }
    }
    
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
    
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }
    
    public static String formatSize(double size) {
        String suffix;
        StringBuilder resultBuffer;
        if (size >= 1024) {
            size = size / 1024;
            suffix = " GB";
            resultBuffer = new StringBuilder(String.valueOf((float) size).substring(0, 4));
        } else {
            suffix = " MB";
            resultBuffer = new StringBuilder(String.valueOf(size));
        }
        
        
        resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
    
    public static String formatSize(long size) {
        String suffix = null;
        double size1 = size;
        StringBuilder resultBuffer;
        if (size >= 1024L) {
            size1 = size1 / 1024L;
            size1 = size1 / 1024L;
            size1 = size1 / 1024L;
            suffix = " GB";
            resultBuffer = new StringBuilder(String.valueOf(size1).substring(0,
                    4));
        } else {
            suffix = " MB";
            resultBuffer = new StringBuilder(String.valueOf(size1));
        }
        
        
        resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
    
    private long getTotalRam() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager =
                (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;
        return availableMegs;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View rootView = inflater.inflate(R.layout.fragment_phone_status, container, false);
        
        ScrollView lay = rootView.findViewById(R.id.phoneStatusFrag);
        
        // load the animation
        Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
        lay.startAnimation(enter);
        
        //lay.setVisibility(View.VISIBLE);
        
        
        mobileNameTV = rootView.findViewById(R.id.mobileNameTextView);
        androidVersionTV = rootView.findViewById(R.id.androidVersionTextView);
        appBuildVersionTV = rootView.findViewById(R.id.applicationVersionNameTextView);
        appVersionCodeTV = rootView.findViewById(R.id.applicationVersionCodeTextView);
        currentDateTimeTV = rootView.findViewById(R.id.currentDateTimeTextView);
        systemDateTimeTV = rootView.findViewById(R.id.systemDateTimeTextView);
        locationEnabledTV = rootView.findViewById(R.id.locationEnableTextView);
        locationPermissionTV = rootView.findViewById(R.id.locationPermissionTextView);
        internetEnableTV = rootView.findViewById(R.id.internetEnableTextView);
        internetPermissionTV = rootView.findViewById(R.id.internetPermissionTextView);
        availableDiskSpaceTV = rootView.findViewById(R.id.availableDiskSpaceTextView);
        ramSizeTV = rootView.findViewById(R.id.availableRamSizeTextView);
        totalRamSizeTextView = rootView.findViewById(R.id.totalRamSizeTextView);
        userIdTV = rootView.findViewById(R.id.userIDTextView);
        userNameTV = rootView.findViewById(R.id.userNameTextView);
        
        PosDB db = PosDB.getInstance(getActivity());
        db.OpenDb();
        String empID = db.getMobEmpId();
        String fName = db.getMobFName();
        String lName = db.getMobLName();
        String lastSycedDate = db.getMobEmpLastSynced();
        db.CloseDb();
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //DateFormat dfProper = new SimpleDateFormat("dd-MMM-yy\nK:mm a");
        DateFormat dfProper = new SimpleDateFormat("d MMM yyyy, hh:mm a");
        Date dt = null;
        try {
            dt = df.parse(lastSycedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        //holder.txtDate.setText( dfProper.format(dt) );
        
        String str = android.os.Build.MODEL;
        String myVersion = android.os.Build.VERSION.RELEASE;
        String versionName = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;
        String currentDateTime = getDateTime();
        long diskSpaceAvailable = getAvailableExternalMemorySize();
        Log.e("DISK_SPACE", String.valueOf(diskSpaceAvailable));
        String diskSpaceStr = formatSize(diskSpaceAvailable);
        double internalMemory = freeRamMemorySize();
        String ramMemoryStr = formatSize(internalMemory);
        
        long tRAM = totalRamMemorySize();
        String totalRamMemory = formatSize(tRAM);
        
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {
        }
        
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }
        
        if (!gps_enabled && !network_enabled) {
            // notify user
            locationPermissionTV.setText("Denied");
            locationEnabledTV.setText("Disabled");
        } else {
            locationPermissionTV.setText("Granted");
            locationEnabledTV.setText("Enable");
        }
        
        if (isConnectingToInternet()) {
            internetEnableTV.setText("Enabled");
            internetPermissionTV.setText("Granted");
        } else {
            internetEnableTV.setText("Disabled");
            internetPermissionTV.setText("Denied");
        }
        userIdTV.setText(empID);
        userNameTV.setText(fName + " " /*+lName*/);
        mobileNameTV.setText(str);
        androidVersionTV.setText(myVersion);
        appBuildVersionTV.setText(versionName);
        appVersionCodeTV.setText(String.valueOf(versionCode));
        currentDateTimeTV.setText(currentDateTime);
        systemDateTimeTV.setText(dfProper.format(dt));
        availableDiskSpaceTV.setText(diskSpaceStr);
        ramSizeTV.setText(ramMemoryStr);
        totalRamSizeTextView.setText(totalRamMemory);
        
        return rootView;
    }
    
    private long freeRamMemorySize() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;
        
        return availableMegs;
    }
    
    private long totalRamMemorySize() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager =
                (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.totalMem / 1024L;
        availableMegs = availableMegs / 1024L;
        
        return availableMegs;
    }
    
    
    private boolean checkCoarseLocationPermission() {
        
        String permission = "android.permission.ACCESS_COARSE_LOCATION";
        int res = getActivity().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    
    private boolean checkFineLocationPermission() {
        
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = getActivity().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo networkInfo : info)
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
            
        }
        return false;
    }
    
    
}
