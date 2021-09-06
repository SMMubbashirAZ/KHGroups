package com.blazeminds.pos;

/*
 * Created by Saad Kalim on 10/22/2015.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.blazeminds.pos.webservice_url.RetrofitWebService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.content.Context.POWER_SERVICE;
import static com.blazeminds.pos.Constant.NOTIFICATION_ID;

public class MyBroadcastReciever extends BroadcastReceiver implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {
    
    private static double Latitude;
    private static double Longitude;
    /**
     * Location Works Starts
     **/
    
    // My GPS states
    private final int GPS_PROVIDER_DISABLED = 1;
    private final int GPS_GETTING_COORDINATES = 2;
    private final int GPS_GOT_COORDINATES = 3;
    //public  final int GPS_PROVIDER_UNAVIALABLE = 4;
    //public  final int GPS_PROVIDER_OUT_OF_SERVICE = 5;
    private final int GPS_PAUSE_SCANNING = 6;
    private final LocationListener locListener = this;
    /*GPSTracker gps;*/
    String autoSync;
    LocationSettingsRequest locationSettingsRequest;
    LocationSettingsRequest.Builder builder;
    SettingsClient settingsClient;
    private LocationCallback mLocationCallBack = new LocationCallback() {
        
        @Override
        public void onLocationResult(LocationResult locationResult) {
            
            
            for (Location location : locationResult.getLocations()) {
                
                if (location != null) {
                    
                    Latitude = location.getLatitude();
                    
                    Longitude = location.getLongitude();
                    
                    
                    //  Log.d(TAG, "Current Location " + latis + " , " + longis);
                }
            }
            
            Location locationLast = locationResult.getLastLocation();
            if (locationLast != null) {
                
                Latitude = locationLast.getLatitude();
                Longitude = locationLast.getLongitude();
                
                
                Log.e("onLocationResult", "Last Known Location " + Latitude + " , " + Longitude);
                
            }
            stopLocationUpdates();
        }
        
        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            
            
        }
    };
    /*
        static String FileName = "NotificationValues";
        SharedPreferences Results;
    */
    private String vib;
    private String sound;
    private String lights;
    private String alert;
    private String email;
    private int /*syncDur,*/paidVal;
    private String SyncDur;
    private String EmpID;
    private int ChkAutoSync;
    private int ChkOfflineSync;
    private String TimeIn;
    private String TimeOut;
    private String EmailDB;
    private String Emp_Name;
    private Context ctx;
    private PosDB db;
    
    
    //Location
    private NotificationCompat.Builder mNotifyBuilder;
    private int defaults;
    private PowerManager.WakeLock wakeLock;
    //Power manager
    // Location manager
    private LocationManager manager;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 500; /* 2 sec */
    
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        
        Log.e("TrackData ", "run");
        this.ctx = context;
        
        
        db = PosDB.getInstance(context);
        
        db.OpenDb();
        SyncDur = db.getSyncDuration();
        EmpID = db.getMobEmpId();
        //  Log.e("StartServiceInBack", String.valueOf((Integer.parseInt(SyncDur) * 60000)));
        ChkAutoSync = db.CheckAutoSync();
        ChkOfflineSync = db.CheckOfflineAutoSync();
        TimeIn = db.getTimeIn();
        TimeOut = db.getTimeOut();
        
        EmailDB = db.getSettingsAdminEmail();
        Emp_Name = db.getMobFName() + " " + db.getMobLName();
        
        vib = db.getVibration();
        sound = db.getSound();
        lights = db.getLights();
        alert = db.getAlert();
        email = db.getEmailNotify();
        
        db.CloseDb();
        
        paidVal = 1; // FREE VERSION = 0 , Paid Version = 1
        
        Log.e("In Broadcast", "In Broadcast Reciever");
        
        if (!EmpID.equals("")) {
            
            PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
            if (powerManager != null) {
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mywakelocktag");
            }
            wakeLock.acquire(10 * 60 * 1000 /*10 minutes*/);
            
            manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            
            buildGoogleApiClient();
            //startLocationUpdates();
            //startLocationUpdates();
            //startLocation();
            
            Log.e("TrackLatLon", Latitude + " : " + Longitude);
            
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                
                if (checkLocationPermission(context)) {
                    MainFunctionality(context);
                }
                
            } else {
                MainFunctionality(context);
                Log.e("BroadCastRec", "done");
            }
        } else Log.e("TrackLogOut", "Logout");
    }
    
    private boolean checkLocationPermission(Context c) {
        
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = c.checkCallingOrSelfPermission(permission);
        Log.e("CheckLocationStatus", String.valueOf(res == PackageManager.PERMISSION_GRANTED));
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    
    private void MainFunctionality(Context context) {
        /*gps = new GPSTracker(context);*/
        if (!TimeIn.equalsIgnoreCase("") && !TimeOut.equalsIgnoreCase("") && !EmpID.isEmpty()) {
            
            int TI = Integer.parseInt(TimeIn.replaceAll(":", ""));
            int TO = Integer.parseInt(TimeOut.replaceAll(":", ""));

            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            final int t = cal.get(Calendar.HOUR_OF_DAY) * 100 + cal.get(Calendar.MINUTE);
            //final boolean isBetween = (TO > TI && t >= TI) && (t <= TO || TO < TI && (t >= TI || t <= TO) );
            final boolean isBetween = (t < TO && t > TI);
            
            
            //autoSync = Results.getBoolean("autoSync", false);
            
            if (networkAvailable() && (ChkAutoSync == 1 || ChkOfflineSync == 1)) {
                //if (networkAvailable() && autoSync.equals("1") ) {
                
                if (isBetween) {
                    
                    // startLocation();
                    new SendData(context, t, Integer.parseInt(SyncDur), "1").execute();
                    
                    //Toast.makeText(context,"DATA SENT",Toast.LENGTH_LONG).show();
                    
                } else {
                    
                   
                   // mLocationCallBack = null;
                    Log.e("TrackElse", String.valueOf(isBetween));
                    if (wakeLock != null) {
                        if (wakeLock.isHeld()) {
                            wakeLock.release();
                        }
                    }
                }
                
            } // end if
            else if (!networkAvailable() /*&&  ChkOfflineSync == 1*/) {
                
                if (isBetween) {
                    //startLocation();
                    Log.e("TrackIdStack", "runOffline");
                    new SendData(context, t, Integer.parseInt(SyncDur), "0").execute();
                    //Toast.makeText(context,"DATA SENT",Toast.LENGTH_LONG).show();
                } else {
                  
                    //mLocationCallBack = new LocationCallback();
                    Log.e("TrackElse", String.valueOf(isBetween));
                    if (wakeLock != null) {
                        if (wakeLock.isHeld()) {
                            wakeLock.release();
                        }
                    }
                }
                if (isNetworkAvailableWITHOUTPING()) {
                    //DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dt = df.format(Calendar.getInstance().getTime());
                    db.OpenDb();
                    db.createCHKNETEntry(dt, 1);
                    db.CloseDb();
                    /*Toast.makeText(context,"STATUS 1",Toast.LENGTH_LONG).show();*/
                } else {
                    //DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dt = df.format(Calendar.getInstance().getTime());
                    
                    db.OpenDb();
                    db.createCHKNETEntry(dt, 2);
                    db.CloseDb();
                    /*Toast.makeText(context,"STATUS 2",Toast.LENGTH_LONG).show();*/
                }
            }
            
            
        }
        
        
    }
    
    private boolean networkAvailable() {
        
        Runtime runtime = Runtime.getRuntime();
        try {
            
            //Process ipProcess = runtime.exec("/system/bin/ping -c 1 67.222.152.138");
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 35.190.168.249");
            //Process ipProcess = runtime.exec("/system/bin/ping -c 1 google.com");
            int exitValue = ipProcess.waitFor();
            
      /*      Log.d("IP ADDR", ipProcess.toString());
            Log.d("IP ADDR", String.valueOf(exitValue));*/
            return (exitValue == 0 || exitValue == 1);
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return false;
        
     /*   try {
            Socket socket = new Socket();
            InetSocketAddress socketAddress = new InetSocketAddress("www.google.com", 80);
            
            socket.connect(socketAddress, 5000);
            
 
            socket.close();
            return true;
        } catch (IOException e) {
            
            Log.e("CheckResponse",
                    e.getMessage());
            return false;
        }*/
        
        
    }
    
    private boolean isNetworkAvailableWITHOUTPING() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    private void updateDisplay_WithAlert(String Time, double lat, double longi, String Area, Context ctx, int syncDur, String NetworkORGPS) {
        //  private void updateDisplay(Intent intent) {
        
        
        Intent resultIntent = new Intent(ctx, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        // Sets an ID for the notification, so it can be updated
        int notifyID = NOTIFICATION_ID;
        
        mNotifyBuilder = new NotificationCompat.Builder(ctx, "default")
                .setContentTitle(ctx.getString(R.string.app_name))
                .setContentText("You've received new messages.")
                .setSmallIcon(R.drawable.ic_launcher);
        
        
        // Set pending intent
        mNotifyBuilder.setContentIntent(resultPendingIntent);
        // Set Vibrate, Sound and Light
        defaults = 0;
        
        
        if (vib.equals("1")) {
            defaults = defaults | Notification.DEFAULT_VIBRATE;
            
        }
        
        if (lights.equals("1")) {
            mNotifyBuilder.setLights(Color.YELLOW, 500, 500);
            
        }
        
        if (sound.equals("1")) {
            mNotifyBuilder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + ctx.getPackageName() + "/raw/beep"));
            
        }


//        mNotifyBuilder.setColor(R.color.list_background_pressed);
        mNotifyBuilder.setDefaults(defaults);

//        mNotifyBuilder.setPriority(Notification.PRIORITY_MAX);
        
        // Set the content for Notification
//        mNotifyBuilder.setContentText(intent.getStringExtra("intntdata"));
        
        
        //        mNotifyBuilder.setContentText("Location Sent\n"+Area);
        mNotifyBuilder.setContentText( /*Time +"\n"+*/Area);
        // Set autocancel
        mNotifyBuilder.setAutoCancel(true);
        // Post a notification
        mNotificationManager.notify(notifyID, mNotifyBuilder.build());
        
        // CheckINJSON(Area, lat, longi, EmpID);
        
        
    }
    
    private String updatePosition(double lat, double longi, Context ctx) {
        
        String AreaName = "";
        
        
        Geocoder gcd = new Geocoder(ctx, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(lat, longi, 1);
            
            if (addresses == null) {
            
            } else {
                
                if (addresses.size() <= 0) {
                
                } else {


//        if (addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0);
                    // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String adminArea = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    String SubLocality = addresses.get(0).getSubLocality();
                    String Phone = addresses.get(0).getPhone();
                    String Premises = addresses.get(0).getPremises();
                    String ThorughFare = addresses.get(0).getThoroughfare();
                    String SubThorughFare = addresses.get(0).getSubThoroughfare();
                    String SubAdminArea = addresses.get(0).getSubAdminArea();
                    String URL = addresses.get(0).getUrl();
                    
                    AreaName = address + ", " + SubLocality + ", "/* + knownName + ", "*/ + SubAdminArea + ", " + adminArea + ", " + city + ", " + country;
                    
                }
            }
            
            
        } catch (IOException e) {
            
            AreaName = "N/A";
            
            e.printStackTrace();
        }


        /*}

        else{

           // updatePosition(lat,longi,ctx);

        }
        */
        
        return AreaName;
        
    }
    
    private void SendPaid(double latitude, double longitude, Context ctx) {
        
        
        if (networkAvailable()) {
            //Toast.makeText(ctx, "Lati :" + latitude + " , Longitude : "+ longitude, Toast.LENGTH_SHORT).show();
            Log.e("Latitude For Service 1", latitude + " ");
            Log.e("Longitude For Service 1", longitude + " ");
            CheckIn(updatePosition(latitude, longitude, ctx), latitude, longitude, EmpID);
            
        }
        
        //stopLocation();
        //stopLocationUpdates();
        if (wakeLock != null) {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
    }
    
    private void SendCheckNetDATA(final String empID, final String time, final String status) {
        
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
                .build(); //Finally building the adapter
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        api.sync_chknet(
                
                empID,
                time,
                status,
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                    
                    }
                    
                    @Override
                    public void failure(RetrofitError error) {
                    
                    }
                }
        );
        
        
    }
    
    private void SendCheckNetDATA(final String status) {
        
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
                .build(); //Finally building the adapter
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        api.sync_chknet(
                
                status,
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                    
                    }
                    
                    @Override
                    public void failure(RetrofitError error) {
                    
                    }
                }
        );
        
        
    }
    
    private void SendPhoneSTART(final String phoneStrtVals) {
        
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
                .build(); //Finally building the adapter
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        api.sync_phoneStart(
                
                phoneStrtVals,
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                    
                    }
                    
                    @Override
                    public void failure(RetrofitError error) {
                    
                    }
                }
        );
        
        
    }
    
    private void SendEmail(String emailAdd, String emp_name, int time, double lat, double longi, String mapName) {
        
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
                .build(); //Finally building the adapter
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        api.send_email(
                
                emailAdd,
                emp_name,
                String.valueOf(time),
                String.valueOf(lat),
                String.valueOf(longi),
                mapName,
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                    
                    }
                    
                    @Override
                    public void failure(RetrofitError error) {
                    
                    }
                }
        );
        
        
    }
    
    private void CheckIn(String CurrentLoc, double lat, double longi, String EmpID) {
        
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
                .build(); //Finally building the adapter
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        Log.e("TrackLocationData", "runOnline : " + CurrentLoc + " : " + lat + " : " + longi + " : " + EmpID);
        
        api.checkin_random(
                
                CurrentLoc,
                String.valueOf(lat),
                String.valueOf(longi),
                EmpID,
                
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {

                        /*BufferedReader reader = null;

                        String output = "";

                        try {
                            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                            output = reader.readLine();

                            Log.d("Prof", output + "--- output");

                            JSONObject json = new JSONObject(output);

                            Log.d("Prof", json.toString() + "---");

                            *//*ldr.HideLoader();*//*

                            String codeResp = json.getString("code");

                            if (codeResp.equalsIgnoreCase("0")) {
                                //Toast.makeText(ctx, "Data not inserted", Toast.LENGTH_SHORT).show();

                            } else if (codeResp.equalsIgnoreCase("1")) {
                                //Toast.makeText(ctx, "Data inserted", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {

                            *//*ldr.HideLoader();*//*

                            Log.d("Prof", "Excep " + e.toString());
                            Toast.makeText(ctx, "I/O Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
                        } catch (JSONException e) {
                            *//*ldr.HideLoader();*//*

                            Log.d("Prof", "Excep JSOn " + e.toString());
                            Toast.makeText(ctx, "Json Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
                        }*/
                        
                    }
                    
                    @Override
                    public void failure(RetrofitError error) {
                        
                        // Toast.makeText(ctx, "Please Check Your Internet", Toast.LENGTH_LONG).show();
                    }
                }
        );
        
    }
    
    private void CheckInOffline(String offlineValues/*String CurrentLoc, String lat, String longi, String  EmpID, String Date*/) {
        
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
                .build(); //Finally building the adapter
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        api.checkin_offline(
                
                offlineValues,
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                    
                    }
                    
                    @Override
                    public void failure(RetrofitError error) {
                    
                    }
                }
        );
        
        
    }
    
    private String printLocation(final Location loc, final int state) {
        
        
        String result = "";
        switch (state) {
            case GPS_PROVIDER_DISABLED: {
                result = "GPS Disabled";
//                locStatusTxt.setTextColor( ContextCompat.getColor( context, R.color.dark_red) );
//                AskForGPSEnable((Activity) context);


                /*PosDB db = new PosDB(ctx);

                db.OpenDb();
                String empID = db.getMobEmpId();
                String TimeIn = db.getTimeIn();
                String TimeOut = db.getTimeOut();
                db.CloseDb();*/
                
                if (!TimeIn.equalsIgnoreCase("") && !TimeOut.equalsIgnoreCase("") && !EmpID.isEmpty()) {
                    
                    int TI = Integer.parseInt(TimeIn.replaceAll(":", ""));
                    int TO = Integer.parseInt(TimeOut.replaceAll(":", ""));
                    
                    Date date = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    final int t = cal.get(Calendar.HOUR_OF_DAY) * 100 + cal.get(Calendar.MINUTE);
                    //final boolean isBetween = (TO > TI && t >= TI) && (t <= TO || TO < TI && (t >= TI || t <= TO) );
                    final boolean isBetween = (t < TO && t > TI);
                    
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dt = df.format(Calendar.getInstance().getTime());
                    
                    if (networkAvailable()) {
                        if (isBetween) {
                            SendCheckNetDATA(EmpID, dt, "3");
                            Log.e("TrackIdStack", "");
                        }
                    }
                }
                
            }
            break;
            case GPS_GETTING_COORDINATES:
                
                result = "Fetching Coordinates";
                
                break;
            case GPS_PAUSE_SCANNING:
                result = "GPS Scanning Paused";
//                locStatusTxt.setTextColor( ContextCompat.getColor( context, R.color.dark_red) );
                
                break;
            case GPS_GOT_COORDINATES:
                if (loc != null) {
                    
                    // Accuracy
                    // Location events (we use GPS only)
                    String gAccuracy;
                    if (loc.getAccuracy() < 0.0001) {
                        gAccuracy = "?";
                    } else if (loc.getAccuracy() > 99) {
                        gAccuracy = "> 99";
                    } else {
                        gAccuracy = String.format(Locale.US, "%2.0f",
                                loc.getAccuracy());
                    }
                    
                    String separ = System.getProperty("line.separator");
                    
                    String la = String
                            .format(Locale.US, "%2.7f", loc.getLatitude());
                    String lo = String.format(Locale.US, "%3.7f",
                            loc.getLongitude());
                    
                    String coordsToSend = la + "," + lo;
                    Log.e("Lat_Long_Before", loc.getLatitude() + " : " + loc.getLongitude());
                    Latitude = loc.getLatitude();
                    Longitude = loc.getLongitude();
                    Log.e("Lat_Long_After", loc.getLatitude() + " : " + loc.getLongitude());
                    result = coordsToSend;
                    
                    
                } else {
                    result = "GPS_UNAVAILABLE";
//                    locStatusTxt.setTextColor( ContextCompat.getColor( context, R.color.dark_red) );
                
                }
                break;
        }

//        locStatusTxt.setText( result );
        return result;
        
    }
    
    private void stopLocation() {
        
        try {
            manager.removeUpdates(locListener);
        } catch (SecurityException ignored) {
        }
    }
    
    private void startLocation() {
        // Возобновляем работу с GPS-приемником
        
        try {
            if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                
                
                //    Toast.makeText(AppContextProvider.getContext(), "GPS Doesn't Exist", Toast.LENGTH_SHORT).show();
                
            } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location location;
                if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60 * 2, 10, this);
                    
                    if (manager != null) {
                        Log.e("TrackRunInMANAGER", "Run");
                        
                        if ((location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)) != null) {
                            Log.e("TrackRunInNetwork", "Run");
                            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            printLocation(location, GPS_GOT_COORDINATES);
                        } else if ((location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)) != null) {
                            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            Log.e("TrackRunInGPS", "Run");
                            
                            printLocation(location, GPS_GOT_COORDINATES);
                        }
                        //printLocation(null,GPS_GETTING_COORDINATES);
                        
                    } else {
                        Log.e("TrackRunInMANAGER-ELSE", "Run");
                        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60 * 2, 10, this);
                        if (manager != null) {
                            Log.e("TrackRunInMANAGER", "Run");
                            
                            if ((location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)) != null) {
                                Log.e("TrackRunInNetwork", "Run");
                                location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                printLocation(location, GPS_GOT_COORDINATES);
                            } else if ((location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)) != null) {
                                location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                Log.e("TrackRunInGPS", "Run");
                                
                                printLocation(location, GPS_GOT_COORDINATES);
                            }
                            
                        }
                        
                    }
                }/*if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
                    //printLocation(null,GPS_GETTING_COORDINATES);
                    Log.e("TrackRunInGPS","Run");
                }*/
                
            } else {
                //  Toast.makeText(AppContextProvider.getContext(), "GPS Doesn't Exist", Toast.LENGTH_SHORT).show();
            }
            
        } catch (SecurityException ignored) {
        }
    }
    
    @Override
    public void onLocationChanged(Location location) {


       /* Latitude = location.getLatitude();
        Longitude = location.getLongitude();
        Log.e("TrackRunInListener","Coordinates = "+Latitude +" : "+ Longitude);
        if (ONE_TIME)*/
        
        printLocation(location, GPS_GOT_COORDINATES);
    }
    
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    
    }
    
    @Override
    public void onProviderEnabled(String provider) {
    
    }
    
    @Override
    public void onProviderDisabled(String provider) {
        printLocation(null, GPS_PROVIDER_DISABLED);
    }
    
    // Check whether location settings are satisfied
    // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
    
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }
    
    @Override
    public void onConnectionSuspended(int i){
    }
    
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    
    }
    
    protected synchronized void buildGoogleApiClient() {
        if (ctx != null) {
            mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
            
            startLocationUpdates();
            //startLocationUpdates();
        }
    }
    
        // Trigger new location updates at interval
        @SuppressLint("RestrictedApi")
        protected void startLocationUpdates() {
            int mode = 0;
            // Create the location request to start receiving updates
            if (ctx != null) {
                mLocationRequest = new LocationRequest();
                try {
                    mode = Settings.Secure.getInt(ctx.getContentResolver(),
                            Settings.Secure.LOCATION_MODE);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }

                Log.e("getPro", String.valueOf(mode));
                if (mode == 3) {
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    Log.e("getProInside", String.valueOf(mode));
                    Log.e("mLocationRequest", String.valueOf(mLocationRequest.getPriority()));
                } else if (mode == 2) {

                    mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    Log.e("getProInside", String.valueOf(mode));
                    Log.e("mLocationRequest", String.valueOf(mLocationRequest.getPriority()));
                } else if (mode == 1) {
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
                    Log.e("getProInside", String.valueOf(mode));
                    Log.e("mLocationRequest", String.valueOf(mLocationRequest.getPriority()));
                } else if (mode == 0) {
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                    Log.e("getProInside", String.valueOf(mode));
                    Log.e("mLocationRequest", String.valueOf(mLocationRequest.getPriority()));
                }

                mLocationRequest.setInterval(UPDATE_INTERVAL);
                mLocationRequest.setFastestInterval(FASTEST_INTERVAL);


                // Create LocationSettingsRequest object using location request
                builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(mLocationRequest);

                locationSettingsRequest = builder.build();

                settingsClient = LocationServices.getSettingsClient(ctx);
                settingsClient.checkLocationSettings(locationSettingsRequest).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e("onFailure", e.getMessage());


                    }
                });


                settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        if (ActivityCompat.checkSelfPermission(ctx,
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            //Log.d(TAG, "Missing Permission Location");
                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, null);

                        mFusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //           Log.e("CheckLocStats","Last Known Location By Method " + latitudeLastByFused + " , " + longitudeLastByFused);
                            }
                        });

                        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                if (location != null) {

                                    Latitude = location.getLatitude();
                                    Longitude = location.getLongitude();
                                    printLocation(location, GPS_GOT_COORDINATES);
                                    // Log.e("CheckLocStats","Last Known Location By Method " + latitudeLastByFused + " , " + longitudeLastByFused);

                                }
                            }

                        });


                    }
                });

                // new Google API SDK v11 uses getFusedLocationProviderClient(this)


            }
        }
    
    
    private void stopLocationUpdates() {
        
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallBack);
        }
    }
    
    private class SendData extends AsyncTask<String, Void, String> {
        
        
        final Context ctx;
        final int t;
        final int dur;
        final String NetworkORGPS;
        JSONObject json = null;
        
        SendData(Context c, int time, int syncDur, String net) {
            this.ctx = c;
            this.t = time;
            this.dur = syncDur;
            this.NetworkORGPS = net;
            
            // manager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
            //startLocation();
            
        }
        
        @Override
        protected String doInBackground(String... params) {
            
            PosDB db = PosDB.getInstance(ctx);
            
            db.OpenDb();
            //db.OpenReadDb();
            String empID = db.getMobEmpId();
            //  db.CloseDb();
            
            ArrayList<HashMap<String, String>> dataPhoneStart = db.getUnsyncPHONESTART();
            ArrayList<HashMap<String, String>> dataOfflineTrack = db.getUnsentTracking();
            ArrayList<HashMap<String, String>> dataChkNet = db.getUnsyncCHKNET();
            
            
            db.CloseDb();
            
            String offlineValues = "", chckNetVals = "", phoneStrtVals = "";
            
            if (networkAvailable()) {
                if (dataChkNet.size() > 0) {
                    for (int i = 0; i < dataChkNet.size(); i++) {
                        HashMap<String, String> f = dataChkNet.get(i);
                        
                        String id = f.get("id");
                        String time = f.get("time");
                        String status = f.get("status");
                        
                        chckNetVals += Constant.GetValues(empID, time, status);

                        /*SendCheckNetDATA(empID, time, status);

                        db.OpenDb();
                        db.updateChkNetSync(id);

                        db.DeleteSyncCHKNET();

                        db.CloseDb();*/
                        
                    }
                    SendCheckNetDATA(chckNetVals);
                    Log.e("TrackIdStack", "SendCheckNetDATA : " + chckNetVals);
                    
                    db.OpenDb();
                    db.DeleteCHKNET();
                    db.CloseDb();
                }
                
                if (dataOfflineTrack.size() > 0) {
                    for (int i = 0; i < dataOfflineTrack.size(); i++) {
                        HashMap<String, String> fi = dataOfflineTrack.get(i);
                        
                        String Offline_latitude = fi.get("lati");
                        String Offline_longitude = fi.get("longi");
                        String Offline_date = fi.get("date");
                        String Offline_area = fi.get("area");
                        
                        offlineValues += Constant.GetValues(EmpID, Offline_latitude, Offline_longitude, Offline_area, Offline_date);
                        
                        //CheckInOffline( Offline_area, Offline_latitude , Offline_longitude , EmpID, Offline_date);
                        
                    }
                    CheckInOffline(offlineValues);
                    Log.e("TrackIdStack", "CheckInOffline : " + chckNetVals);
                    db.OpenDb();
                    db.DeleteSyncOfflineTracking();
                    //db.Truncate_OfflineTracking();
                    db.CloseDb();
                }
                
                if (dataPhoneStart.size() > 0) {
                    for (int i = 0; i < dataPhoneStart.size(); i++) {
                        HashMap<String, String> f = dataPhoneStart.get(i);
                        
                        String id = f.get("id");
                        String time = f.get("time");
                        
                        phoneStrtVals += Constant.GetValues(empID, time);

                        /*SendPhoneSTART(empID, time);

                        db.OpenDb();
                        db.updatePhoneSTARTSync(id);

                        db.DeleteSyncPHONESTART();

                        db.CloseDb();*/
                    }
                    
                    SendPhoneSTART(phoneStrtVals);
                    Log.e("TrackIdStack", "SendPhoneSTART : " + chckNetVals);
                    db.OpenDb();
                    db.DeletePHONESTART();
                    db.CloseDb();
                }
            }
            
            String Area;
            
            if (networkAvailable()) {
                Area = updatePosition(Latitude, Longitude, ctx);
            } else {
                Area = "N/A";
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                db.OpenDb();
                
                db.createOffline_Tracking(Latitude, Longitude, currentDateandTime, Area);
                db.CloseDb();
            }
            
            if (alert.equals("1")) {
                
                String time = String.valueOf(t);
                SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm a");
                String out = "";
                try {
                    Date date = dateFormat.parse(time);
                    
                    out = dateFormat2.format(date);
                    Log.e("Time", out);
                } catch (ParseException ignored) {
                }
                updateDisplay_WithAlert(out, Latitude, Longitude, Area, ctx, dur, NetworkORGPS);
                
            }
            
            if (alert.equals("0")) {
                // updateDisplay_WithOutAlert(t, lat, longi,ctx);
            }
            
            if (email.equals("1")) {
                //if (Results.getBoolean("email", false) == true) {
                SendEmail(EmailDB, Emp_Name, t, Latitude, Longitude, Area);
            }
            if (paidVal == 1) {
                
                
                Log.e("TrackLocationBefore", Latitude + " : " + Longitude);
                SendPaid(Latitude, Longitude, ctx);
                Log.e("TrackLocationAfter", Latitude + " : " + Longitude);
                //stopLocationUpdates();
                
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}