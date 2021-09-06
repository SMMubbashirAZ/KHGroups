package com.blazeminds.pos.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.BuildConfig;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.Login;
import com.blazeminds.pos.MyService;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.PrefManager;
import com.blazeminds.pos.R;
import com.blazeminds.pos.resources.GaugeView;
import com.blazeminds.pos.resources.Loader;
import com.blazeminds.pos.resources.ReminderBroadcastReciever;
import com.blazeminds.pos.resources.UserSettings;
import com.blazeminds.pos.webservice_url.RetrofitWebService;
import com.blazeminds.pos.webservice_url.SyncData;
import com.github.lzyzsd.circleprogress.ArcProgress;
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
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import pl.pawelkleczkowski.customgauge.CustomGauge;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.blazeminds.pos.Constant.GetValues2;
import static com.blazeminds.pos.Constant.MIN_CLICK_INTERVAL;
import static com.blazeminds.pos.Constant.NOTIFICATION_ID;
import static com.blazeminds.pos.Constant.checkLocationPermission;
import static com.blazeminds.pos.Constant.convertDate;
import static com.blazeminds.pos.Constant.customToast;
import static com.blazeminds.pos.Constant.getCityAreaCountryFromLatitudeLongitude;
import static com.blazeminds.pos.Constant.prettyCount;
import static com.blazeminds.pos.MainActivity.FRAGMENT_TAG;
import static com.blazeminds.pos.MainActivity.granted;
import static com.blazeminds.pos.MainActivity.requestPermission;
import static com.blazeminds.pos.MainActivity.trackCount;
import static com.blazeminds.pos.resources.UserSettings.FIRST_NOTIFY;
import static com.blazeminds.pos.resources.UserSettings.IS_FIRST_NOTIFY;

public class DashBoard extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    public static final String TAG = DashBoard.class.getSimpleName();
    // My GPS states
    public static final int GPS_PROVIDER_DISABLED = 1;
    public static final int GPS_GETTING_COORDINATES = 2;
    static final double[] latitude = {0};
    static final double[] longitude = {0};
    private static final int GPS_GOT_COORDINATES = 3;
    /*
        static String AutoSyncFileName = "NotificationValues";
        SharedPreferences SyncResult;
        SharedPreferences.Editor editAutoSync;
    */
    //public static final int GPS_PROVIDER_UNAVAILABLE = 4;
    //public static final int GPS_PROVIDER_OUT_OF_SERVICE = 5;
    private static final int GPS_PAUSE_SCANNING = 6;
    private static final int GPS_PROVIDER_DOESNOT_EXIST = 4;
    // JSON Response node names
    private static final String KEY_SUCCESS = "code";
    public static TextView location_status, network_status;
    static Context context;
    //    static LinearLayout CInSection, COutSection;
    static TextView DegVal, CheckInTimeTxt, Task;
    static Button CheckInBtn;
    static TextView CheckedInAreaTxt, CheckedOutAreaTxt, PreviousCustomer, NextCustomer;
    static Spinner KnownLocation;
    static TextView CurrentLoc;
    static CustomGauge gv;
    static String CurrentLocation = "";
    static String dbDate;
    ///Fused Location Work End
    static int pos = 0, enableAdvanceDashboard = 0;
    static int i = 1, enableClockIn, enableAttendanceMust;
    static int currentSelection = 0;
    static Loader loader;
    static String[] CityArea;
    static String empId, empTimeIn;
    static TextView locStatusTxt;
    static long lastClickTime = 0;
    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */

    static String SelectedCustID = "0";
    static PosDB db;
    private static String loc_holder = "";
    private static Button timeInBtn;
    private static boolean DIALOG_VISIBLE = false;
    private static double latis, longis, latitudeLastByFused, longitudeLastByFused;
    private static String coordsToSend;
    private static String gAccuracy;
    private static double Latitude;
    private static double Longitude;
    private static double LastKnownLatitudeGPS;
    private static double LastKnownLongitudeGPS;
    private static double LastKnownLatitudeNet;
    private static double LastKnownLongitudeNet;
    private static SharedPreferences saveLocStats;
    private static SharedPreferences.Editor editor;
    // Location manager
    private static LocationManager manager;
    private static Location lastKnownLocationGPS;
    private static Location lastKnownLocationNetwork;
    // Location events (we use GPS only)
    private static boolean isOne = true;
    private static NetworkChangeReceiver networkChangeReceiver;
    private static Resources resources;
    private static Runtime runtime;
    private static Handler handler;
    private static Timer timer;
    private static AnimationSet as;
    private static boolean loc_statusOn = true, loc_statusOff = true, net_statusOn = true,
            net_statusOff = true;
    private static LinearLayout location_layout, network_layout;
    private static FragmentActivity fragmentActivity;
    private final long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private final long FASTEST_INTERVAL = 2 * 1000; /* 2 sec */
    private final String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
    /// Fused Location Work Start
    FusedLocationProviderClient mFusedLocationClient;
    GoogleApiClient mGoogleApiClient;
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private LocationListener locationListener;
    private LocationRequest mLocationRequest;
    private AlertDialog.Builder builder;

    private final LocationCallback mLocationCallBack = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {

            int mode = 0;

            // Create the location request to start receiving updates

            if (manager.getProviders(true).size() != 3) {

                try {
                    if (getActivity() != null)
                        mode = Settings.Secure.getInt(getActivity().getContentResolver(),
                                Settings.Secure.LOCATION_MODE);
                } catch (Settings.SettingNotFoundException ee) {
                    ee.printStackTrace();
                }
                Log.e("getPro", "onLocationResult   " + mode);
                if (mode != 3) {
                    return;
                }
            }

            for (Location location : locationResult.getLocations()) {

                if (location != null) {

                    latis = location.getLatitude();

                    longis = location.getLongitude();

                    if (location_status != null) {

                        if (loc_statusOn) {

                            location_status.setAnimation(as);

                            location_layout.setBackgroundColor(resources.getColor(R.color.green_back));

                            location_status.setText(("Location Found"));

                            loc_statusOn = false;

                            loc_statusOff = true;
                            editor = saveLocStats.edit();
                            editor.putString("loc_stats", "Location Found");
                            editor.apply();


                        }

                    }

                    //Log.d(TAG, "Current Location " + latis + " , " + longis);
                } else {

                    if (location_status != null) {

                        if (loc_statusOff) {

                            location_status.setAnimation(as);

                            location_layout.setBackgroundColor(Color.YELLOW);

                            location_status.setText(("Finding Location..."));

                            loc_statusOn = true;

                            loc_statusOff = false;

                            editor = saveLocStats.edit();
                            editor.putString("loc_stats", "Finding Location...");
                            editor.apply();

                        }

                        Log.d(TAG, "Last known location is null");
                    }
                }
            }

            Location locationLast = locationResult.getLastLocation();
            if (locationLast != null) {

                latitudeLastByFused = locationLast.getLatitude();
                longitudeLastByFused = locationLast.getLongitude();

                if (location_status != null) {

                    if (loc_statusOn) {

                        location_status.setAnimation(as);

                        location_layout.setBackgroundColor(resources.getColor(R.color.green_back));

                        location_status.setText(("Location Found"));
                        editor = saveLocStats.edit();
                        editor.putString("loc_stats", "Location Found");
                        editor.apply();
                        loc_statusOn = false;

                        loc_statusOff = true;
                    }

                }
                /* Log.d(TAG, "Last Known Location " + latitudeLastByFused + " , " + longitudeLastByFused);*/

            } else {

                if (location_status != null) {

                    if (loc_statusOff) {

                        location_status.setAnimation(as);

                        location_layout.setBackgroundColor(Color.YELLOW);

                        location_status.setText(("Finding Location..."));
                        editor = saveLocStats.edit();
                        editor.putString("loc_stats", "Finding Location...");
                        editor.apply();
                        loc_statusOn = true;

                        loc_statusOff = false;

                    }
                    Log.d(TAG, "Last known location is null");
                }
            }

        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);

            if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


                printLocation(null, GPS_PAUSE_SCANNING);
                showGPSGDialog();
                isOne = false;


            } else if (getLocationMode(getActivity()) == 3) {
                printLocation(null, GPS_GETTING_COORDINATES);


            } else {
                printLocation(null, GPS_PROVIDER_DISABLED);
                showGPSGDialog();
                isOne = false;
            }

        }
    };
    private final LocationListener locListener = new LocationListener() {

        public void onLocationChanged(Location argLocation) {
            //printLocation(argLocation, GPS_GOT_COORDINATES);
        }

        @Override
        public void onProviderDisabled(String arg0) {
            //printLocation(null, GPS_PROVIDER_DISABLED);
            if (isOne) {


                showGPSGDialog();
                //printLocation(null, GPS_PROVIDER_DISABLED);

                isOne = false;
            }
            // printLocation(null, GPS_PROVIDER_DISABLED);
        }

        @Override
        public void onProviderEnabled(String arg0) {

        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        }

    };
    private Sync sync;
    private PrefManager prefManager;

    private static boolean isInternetAvailable2() {

        if (networkChangeReceiver != null && networkChangeReceiver.checkInternet(context)) {


            try {
                Socket socket = new Socket();
                InetSocketAddress socketAddress = new InetSocketAddress("35.190.168.249", 80);

                socket.connect(socketAddress, 15000);

                socket.close();
                return true;
            } catch (IOException e) {

                Log.e("CheckResponse",
                        e.getMessage());
                return false;
            }

            /*try {
                Process ipProcess = runtime.exec("/system/bin/ping -c 1 35.190.168.249");
                //Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
                //Process ipProcess = runtime.exec("/system/bin/ping -c 1 35.190.168.249");
                //Process ipProcess = runtime.exec("/system/bin/ping -c 1 google.com");
                int exitValue = ipProcess.waitFor();

                Log.d("IP ADDR", ipProcess.toString());
                Log.d("IP ADDR", String.valueOf(exitValue));
                // ipProcess.destroy();

                return exitValue == 0;

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.fillInStackTrace());
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e.fillInStackTrace());
            }*/
        }
        return false;
    }

    public static DashBoard newInstance() {
        return new DashBoard();
    }

    private static void SelectKnownLocDropDown(double latitude, double longitude) {
        // Spinner Drop down elements

        List<String> KnownOptions = new ArrayList<>();
        KnownOptions.add(0, "None"); // Place it on Top
        final List<String> KnownOptionsID = new ArrayList<>();
        KnownOptionsID.add("0"); // Place it on Top

        List<String> CustID;


        db.OpenDb();
        List<String> CustName = db.GetCustomerWithAreaNamesDROPDOWN(latitude, longitude);
        CustID = db.GetCustomerIDWithAreaNamesDROPDOWN(latitude, longitude);
        db.CloseDb();

        CustID.add(0, "0");
        KnownOptions.addAll(CustName);




/*

        for(int i=0; i<CustID.size(); i++){
            KnownOptionsID.add(CustID.get(i));
        }
*/

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterKnownLoc = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, KnownOptions);

        // Drop down layout style
        dataAdapterKnownLoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        KnownLocation.setAdapter(dataAdapterKnownLoc);


        // Spinner click listener
        final List<String> finalCustID = CustID;
        KnownLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                SelectedCustID = finalCustID.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private static void CustomDialogNoInternet() {
        /*
         * Custom Dialog box starts
         */

        AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setTitle("Sorry");
        alert.setMessage("Please Check Your Internet Connection");
        alert.setIcon(R.drawable.not_selected);
        alert.show();
        /*
         * Custom dialog box ends
         */

    }

    public static void RefreshData(PosDB dba) {

        String PrevID = dba.getPreviousCheckInCustomer();
        String NextID = dba.getNextCheckInCustomer();

        if (PrevID.equalsIgnoreCase("0")) {
            PreviousCustomer.setText("Previous Customer NOT DEFINED");
        } else {
            CheckedInAreaTxt.setText(PrevID);
            PreviousCustomer.setText(dba.getCustomerAREAFromPREV(CheckedInAreaTxt.getText().toString()));

        }
        if (NextID.equalsIgnoreCase("0")) {
            NextCustomer.setText("Next Customer NOT DEFINED");
        } else {
            CheckedOutAreaTxt.setText(NextID);
            NextCustomer.setText(dba.getCustomerAREAFromNEXT(CheckedOutAreaTxt.getText().toString()));

        }

    }

    public static boolean isInternetAvailable() {
        try {
            Socket socket = new Socket();
            InetSocketAddress socketAddress = new InetSocketAddress("35.190.168.249", 80);

            socket.connect(socketAddress, 15000);

            socket.close();
            return true;
        } catch (IOException e) {

            Log.e("CheckResponse",
                    e.getMessage());
            return false;
        }

    }

    public static void cancelNotification(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(NOTIFICATION_ID);
    }

    public int getLocationMode(Context context) {
        int mode = -1;
        try {
            if (context != null)
                mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return mode;
    }

    private void startLocation() {

        try {

            if (manager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);

                Location lastKnownLocationGPS = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                printLocation(null, GPS_GETTING_COORDINATES);
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } else {
                Toast.makeText(AppContextProvider.getContext(), "GPS Doesn't Exist", Toast.LENGTH_SHORT).show();
                printLocation(null, GPS_PROVIDER_DOESNOT_EXIST);
            }

        } catch (SecurityException ignore) {
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_item_one, container, false);


        context = getActivity();
        sync = new Sync();
        fragmentActivity = getActivity();
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager
        // .isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        //startLocation();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if (getActivity() != null) {

            saveLocStats = getActivity().getSharedPreferences("SAVE_LOC_STATS", MODE_PRIVATE);


        }

        db = PosDB.getInstance(getActivity());

        db.OpenDb();

        if (db.getMobUser().equals("")) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), Login.class));
        }

        int dbVersion = 0;
        enableAdvanceDashboard = db.getAppSettingsValueByKey("en_advance_dashboard");
        enableAdvanceDashboard = 1;

        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getChildFragmentManager());

        mViewPager = v.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        /*if (i == 1) {
            db.createOffline_Tracking(20.50, 58.90, "2018-2-22 03:22:00", "Office BMS");
            i++;
        }*/
        int versionName = BuildConfig.VERSION_CODE;
        String dbVersionFromServer = db.getMobEmpAppVersion();
        //ArrayList<HashMap<String, String>> totalDiscList = db.getTotalDiscountList();
        if (dbVersionFromServer != null && !dbVersionFromServer.equals("null")) {
            dbVersion = Integer.parseInt(dbVersionFromServer);
        }
        Log.d("DB App Version", dbVersion + "");

        if (versionName < dbVersion) {
            // Need to Uncomment it After working done
            AreYouSure();
            //Toast.makeText( getActivity(), "Please Upgrade Your App From PlayStore you are using older version", Toast.LENGTH_SHORT ).show();

        }
        final String empID = db.getMobEmpId();
        db.getSupportStatusFromDB();
        //db.DeleteSalesOrderAfter7Days();
        double Days = db.CheckDays();
        db.CloseDb();



        /* Check Inactive User */

        String msg = "";
        msg = sync.doInBackground("0", empID);

        if (!msg.equals(""))
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

        if (Days >= 7) {

            Log.e("Days", String.valueOf(Days));
            msg = sync.doInBackground("1", empID);
            if (!msg.equals(""))
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }


//        if (db.getAppSettingsValueByKey("en_target_notification") != 0) {
//            int time= db.getAppSettingsValueByKey("en_target_notification_time");
//            if (time!=0){
//
////                                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//                Intent intent = new Intent(context, NoticationReceiver.class);
//                intent.putExtra("event", "SG Sales Progress");
//                intent.putExtra("time", MessageFormat.format(" Your Monthly Target {1} | Your Achievement {0}",
//                        db.getMobEmpProductSale(),
//                        db.getMobEmpTarget()));
//                intent.putExtra("date", Constant.getDateTimeSHORT());
////                                    Intent notificationIntent = new Intent(context, NoticationReceiver.class);
//
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
//                Calendar cal = Calendar.getInstance();
//                cal.add(Calendar.HOUR_OF_DAY,17);
//                cal.add(Calendar.MINUTE,7);
//                cal.add(Calendar.SECOND,5);
//                am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//            }
//        }
        prefManager = new PrefManager(getActivity());

        UserSettings userSettings = UserSettings.getInstance(getActivity());
//        userSettings.set(FIRST_NOTIFY,String.valueOf(time));
        if (userSettings.getBoolean(IS_FIRST_NOTIFY)) {
            if (db.getAppSettingsValueByKey("en_target_notification") != 0) {
                int time = db.getAppSettingsValueByKey("en_target_notification_time");
                String productVal = db.getMobEmpProductSale();
                String targetVal = db.getMobEmpTarget();

                userSettings.set(IS_FIRST_NOTIFY, false);
                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                int min = Calendar.getInstance().get(Calendar.MINUTE);
                int sec = Calendar.getInstance().get(Calendar.SECOND);
//                prefManager.setFirstTimeNotification(String.valueOf(time));
                userSettings.set(FIRST_NOTIFY, String.valueOf(time));
                int final_time;
                final_time = hour;
                Log.e("getProgress", productVal + " : " + targetVal);
                int productSaleFromDB = 0;
                int targetFromDB = 0;
                if (productVal != null && !productVal.equals("null")) {
                    productSaleFromDB = Integer.parseInt(productVal);
                }
                if (targetVal != null && !targetVal.equals("null")) {
                    targetFromDB = Integer.parseInt(targetVal);
                }
                int result = 0;
                if (targetFromDB == 0) {
                    result = 0;
                } else {

                    result = (100 * productSaleFromDB) / targetFromDB;
                }
                if (time != 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        CharSequence name = "SG Sales Progress";
                        String description = MessageFormat.format(" Target  Achievement {0}% | Total Target {2} | Unit Booked: {1}",
                                String.valueOf(result),
                                db.getMobEmpProductSale(),
                                db.getMobEmpTarget());
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel channel = new NotificationChannel("SaeedGhani", name, importance);
                        channel.setDescription(description);

                        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);
                    }


                    Intent intent = new Intent(context, ReminderBroadcastReciever.class);
                    intent.putExtra("event", "SG Sales Progress");
                    intent.putExtra("time", MessageFormat.format(" Target  Achievement {0}% | Total Target {2} | Unit Booked: {1}",
                            String.valueOf(result),
                            db.getMobEmpProductSale(),
                            db.getMobEmpTarget()));
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, final_time);
                    calendar.set(Calendar.MINUTE, min + 1);
                    calendar.set(Calendar.SECOND, sec + 1);
                    if (calendar.getTime().compareTo(new Date()) < 0)
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    long timeintest = System.currentTimeMillis();
                    Log.d(TAG, "onCreateView: " + calendar.getTimeInMillis());
                    long tenTest = 1000 * 1;
                    alarmManager.set(AlarmManager.RTC_WAKEUP,
                            timeintest + tenTest, pendingIntent);
//                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//                            timeintest+tenTest, AlarmManager.INTERVAL_DAY, pendingIntent);
                }

            }
        } else {
            if (db.getAppSettingsValueByKey("en_target_notification") != 0) {
                int time = db.getAppSettingsValueByKey("en_target_notification_time");
                String productVal = db.getMobEmpProductSale();
                String targetVal = db.getMobEmpTarget();
//                prefManager.setFirstTimeNotification(String.valueOf(time));

                userSettings.set(FIRST_NOTIFY, String.valueOf(time));
                Log.e("getProgress", productVal + " : " + targetVal);
                int productSaleFromDB = 0;
                int targetFromDB = 0;
                if (productVal != null && !productVal.equals("null")) {
                    productSaleFromDB = Integer.parseInt(productVal);
                }
                if (targetVal != null && !targetVal.equals("null")) {
                    targetFromDB = Integer.parseInt(targetVal);
                }
                int result = 0;
                if (targetFromDB == 0) {
                    result = 0;
                } else {

                    result = (100 * productSaleFromDB) / targetFromDB;
                }
                if (time != 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        CharSequence name = "SG Sales Progress";
                        String description = MessageFormat.format(" Target  Achievement {0}% | Total Target {2} | Unit Booked: {1}",
                                String.valueOf(result),
                                db.getMobEmpProductSale(),
                                db.getMobEmpTarget());
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel channel = new NotificationChannel("SaeedGhani", name, importance);
                        channel.setDescription(description);

                        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, time);
                    calendar.set(Calendar.MINUTE, 7);
                    calendar.set(Calendar.SECOND, 0);
                    Intent intent = new Intent(context, ReminderBroadcastReciever.class);
                    intent.putExtra("event", "SG Sales Progress");
//                intent.putExtra("timestamp",calendar.getTimeInMillis());
                    intent.putExtra("time", MessageFormat.format(" Target  Achievement {0}% | Total Target {2} | Unit Booked: {1}",
                            String.valueOf(result),
                            db.getMobEmpProductSale(),
                            db.getMobEmpTarget()));

//                if (calendar.getTime().compareTo(new Date()) < 0)
//                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    long timeintest = System.currentTimeMillis();
                    Log.d(TAG, "onCreateView: " + calendar.getTimeInMillis());
                    long tenTest = 1000 * 5;
//        alarmManager.set(AlarmManager.RTC_WAKEUP,
//                timeintest+tenTest,pendingIntent);
                    Calendar nowCalendar = Calendar.getInstance();

                    if (calendar.after(nowCalendar)) {
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY,
                                pendingIntent);

                    } else {
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY,
                                pendingIntent);
                    }
//                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//                        calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                }

            }
        }


        //        intent.putExtra("date", Constant.getDateTimeSHORT());
////                                    Intent notificationIntent = new Intent(context, NoticationReceiver.class);
//
////        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.HOUR_OF_DAY,17);
//        cal.add(Calendar.MINUTE,34);
//        cal.add(Calendar.SECOND,1);
//        startAlarm(cal,intent);
        return v;

    }
//    private void startAlarm(Calendar c,Intent intent) {
//        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);
//        if (c.before(Calendar.getInstance())) {
//            c.add(Calendar.DATE, 1);
//        }
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
//    }

    private boolean networkAvailable() {
            /*ConnectivityManager cm = (ConnectivityManager)
                    getSystemService(this.CONNECTIVITY_SERVICE);
					NetworkInfo ni = cm.getActiveNetworkInfo();
					if (ni == null)
					    return false;
					return ni.isConnectedOrConnecting();*/

        try {
            Socket socket = new Socket();
            InetSocketAddress socketAddress = new InetSocketAddress("35.190.168.249", 80);

            socket.connect(socketAddress, 15000);


            socket.close();
            return true;
        } catch (Exception e) {

//            Log.e("CheckResponse",
//                    e.getMessage());
            return false;
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        isOne = true;
        DIALOG_VISIBLE = false;
        if (requestCode == 2313) {

            SharedPreferences preferences = context.getSharedPreferences("APP_VERSION", MODE_PRIVATE);
            float appV = Float.parseFloat(BuildConfig.VERSION_NAME);
            float appV2 = Float.parseFloat(preferences.getString("app_version", String.valueOf(appV)));
            final String appPackageName = BuildConfig.APPLICATION_ID;
            if (appV < appV2) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setTitle("New update available at Google Play Store");
                builder.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        db.OpenDb();
                        if (db.getAppSettingsValueByKey("en_target_notification") != 0) {
                            int time = db.getAppSettingsValueByKey("en_target_notification_time");
                            String productVal = db.getMobEmpProductSale();
                            String targetVal = db.getMobEmpTarget();
//                            prefManager.setFirstTimeNotification("");

                            UserSettings userSettings = UserSettings.getInstance(getActivity());
                            userSettings.set(FIRST_NOTIFY, "");
                            userSettings.set(IS_FIRST_NOTIFY, false);
                            Log.e("getProgress", productVal + " : " + targetVal);
                            int productSaleFromDB = 0;
                            int targetFromDB = 0;
                            if (productVal != null && !productVal.equals("null")) {
                                productSaleFromDB = Integer.parseInt(productVal);
                            }
                            if (targetVal != null && !targetVal.equals("null")) {
                                targetFromDB = Integer.parseInt(targetVal);
                            }
                            int result = (100 * productSaleFromDB) / targetFromDB;
                            if (time != 0) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    CharSequence name = "SG Sales Progress";
                                    String description = MessageFormat.format(" Target  Achievement {0}% | Total Target {2} | Unit Booked: {1}",
                                            String.valueOf(0),
                                            String.valueOf(0),
                                            String.valueOf(0));
                                    int importance = NotificationManager.IMPORTANCE_HIGH;
                                    NotificationChannel channel = new NotificationChannel("SaeedGhani", name, importance);
                                    channel.setDescription(description);

                                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);
                                }


                                Intent intent = new Intent(context, ReminderBroadcastReciever.class);
                                intent.putExtra("event", "SG Sales Progress");
                                intent.putExtra("time", MessageFormat.format(" Target  Achievement {0}% | Total Target {2} | Unit Booked: {1}",
                                        String.valueOf(0),
                                        String.valueOf(0),
                                        String.valueOf(0)));
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, time);
                                calendar.set(Calendar.MINUTE, 02);
                                calendar.set(Calendar.SECOND, 0);
                                if (calendar.getTime().compareTo(new Date()) < 0)
                                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
                                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                long timeintest = System.currentTimeMillis();
                                Log.d(TAG, "onCreateView: " + calendar.getTimeInMillis());
                                long tenTest = 1000 * 30;
//        alarmManager.set(AlarmManager.RTC_WAKEUP,
//                timeintest+tenTest,pendingIntent);
                                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                        calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                            }

                        }
                        //db.mUserLogout();
                        db.DeleteAllRecords();
                        //String res = db.getSettingsData();
                        db.CloseDb();

                        /*Log.d("Setings DB Data LOGOUT", res);*/

/*
                    edit.putBoolean("autoSync", false);
                    edit.commit();
*/

                        cancelNotification(context);
                        //autoSync = false;

                        if (getActivity() != null)
                            getActivity().stopService(new Intent(context, MyService.class));
                        dialog.dismiss();
			
			/*	try {
					new MyService().AlarmCancel();
				}catch (Exception e){
					e.getMessage();
				}*/
                        Intent i;
                        i = new Intent(context, Login.class);
                        startActivity(i);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().finish();

                    }
                });

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {


                            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)), 2313);
                        } catch (android.content.ActivityNotFoundException anfe) {

                            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)), 2313);
                            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play" +".google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });

                builder.show();
            }
        }
    }

    public void AreYouSure() {
        if (getActivity() != null) {
            final Dialog dialog = new Dialog(getActivity());

            //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_app_older_version);
            dialog.setTitle("Update Version Alert !!");
            //dialog.getWindow().setBac(getResources().getColor(R.color.login_bg));
            // dialog.getWindow().


            //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenWidth = (int) (metrics.widthPixels * 0.80);
            dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview


            //  dialog.setTitle(ItemName);
            Button Yes = dialog.findViewById(R.id.YesBtn);
            TextView dbVersion = dialog.findViewById(R.id.dbVersion);
            TextView latestVersion = dialog.findViewById(R.id.latestVersion);
            //Button No = (Button) dialog.findViewById(R.id.NoBtn);
            //db.OpenDb();
            String dbVersionFromServer = db.getMobEmpAppVersion();
            //int dbVersio = Integer.parseInt(dbVersionFromServer);
            // db.CloseDb();

            latestVersion.setText(BuildConfig.VERSION_CODE + "");
            dbVersion.setText(dbVersionFromServer);
            dialog.show();

            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            Yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    db.OpenDb();
                    //db.mUserLogout();
                    db.DeleteAllRecords();
                    //String res = db.getSettingsData();
                    db.CloseDb();
                    //dbVersionFromServer = "";
                    /*Log.d("Setings DB Data LOGOUT", res);*/

/*
                    edit.putBoolean("autoSync", false);
                    edit.commit();
*/
                    if (getActivity() != null)
                        cancelNotification(getActivity());
                    //autoSync = false;
                    if (getActivity() != null)
                        getActivity().stopService(new Intent(getActivity(), MyService.class));

                    dialog.dismiss();

                    Intent i;
                    i = new Intent(getActivity(), Login.class);
                    startActivity(i);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().finish();

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(AppContextProvider.getContext(), "You have Signed Out Successfully", Toast.LENGTH_LONG).show();
                        }
                    });


                }
            });

       /* No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        */
        }
    }

    /**
     * Location Works Starts
     **/

    @Override
    public void onPause() {
        super.onPause();
      /*  stopLocation();
        stopLocationUpdates();*/
        try {
            if (networkChangeReceiver != null) {
                //  Log.e("ONONON", "onPause");
                fragmentActivity.unregisterReceiver(networkChangeReceiver);
            }
            stopLocation();
            stopLocationUpdates();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

    private void stopLocation() {

//        stopLocationUpdates();
        try {
            manager.removeUpdates(locationListener);
        } catch (SecurityException ignored) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();


        try {
            resources = getResources();
            // Log.e("ONONON", "onResume");
            networkChangeReceiver = new NetworkChangeReceiver();

            fragmentActivity.registerReceiver(networkChangeReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            if (manager == null) {
                manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            }

            // startLocation();

            if (mGoogleApiClient != null && mFusedLocationClient != null) {
                startLocationUpdates();
                // Log.e("CheckLocStats", "startLocationUpdates");
            } else {
                buildGoogleApiClient();
                //  Log.e("CheckLocStats", "buildGoogleApiClient");
            }
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

    /**
     * Location Works Ends
     **/
    /*
    Fused Location Provider Client Work Starts here
    */
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
        // showGPSGDialog();
        // mGoogleApiClient.connect();
        // startLocationUpdates();
        // Log.e("CheckLocStats", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //showGPSGDialog();
        // startLocationUpdates();
        //Log.e("CheckLocStats", "onConnectionFailed");
    }

    protected synchronized void buildGoogleApiClient() {
        if (getActivity() != null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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

        // Create the location request to start receiving updates
        if (getActivity() != null) {
            mLocationRequest = new LocationRequest();

            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);


            // Create LocationSettingsRequest object using location request
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

            builder.addLocationRequest(mLocationRequest);


            final LocationSettingsRequest locationSettingsRequest = builder.build();

            // Check whether location settings are satisfied
            // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
            final SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
            settingsClient.checkLocationSettings(locationSettingsRequest).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    int mode = -1;
                    // Create the location request to start receiving updates
                    Log.e("onFailure", e.getMessage());
                    try {
                        if (getActivity() != null)
                            mode = Settings.Secure.getInt(getActivity().getContentResolver(),
                                    Settings.Secure.LOCATION_MODE);
                    } catch (Settings.SettingNotFoundException ee) {
                        ee.printStackTrace();
                    }

                    Log.e("getProInOnFail", String.valueOf(mode));
                    for (String s : manager.getProviders(true)) {
                        Log.e("getProviders", s);
                    }
                    if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        printLocation(null, GPS_PAUSE_SCANNING);
                        showGPSGDialog();
                        isOne = false;


                    } else if (mode != 3) {
                        printLocation(null, GPS_PROVIDER_DISABLED);
                        showGPSGDialog();

                    } else {
                        printLocation(null, GPS_PROVIDER_DISABLED);
                        showGPSGDialog();

                    }

                    //Log.e("CheckLocStats", e.getMessage());
                    //showGPSGDialog();
                }
            });


            settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                    if (getActivity() != null)
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            Log.d(TAG, "Missing Permission Location");
                            return;
                        }
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, null);

                    mFusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.e("CheckLocStats","Last Known Location By Method " + latitudeLastByFused + " , " + longitudeLastByFused);
                        }
                    });

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {

                                latitudeLastByFused = location.getLatitude();
                                longitudeLastByFused = location.getLongitude();
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
            Log.e("mFusedLocationClient", "mFusedLocationClient");
            mFusedLocationClient.removeLocationUpdates(mLocationCallBack);
        }


    }

    private void printLocation(Location loc, int state) {

        String result = "";


        switch (state) {
            case GPS_PROVIDER_DOESNOT_EXIST:

                result = "Location Doesn't Exist";

                if (location_status != null) {


                    location_layout.setBackgroundColor(Color.RED);
                    location_status.setText(result);
                }

                break;
            case GPS_PROVIDER_DISABLED:

                result = "Set your gps location to high accuracy";

                if (location_status != null) {
                    location_layout.setBackgroundColor(resources.getColor(R.color.loc_net_bg));
                    location_status.setText(result);
                    editor = saveLocStats.edit();
                    editor.putString("loc_stats", result);
                    editor.apply();

                }

                break;
            case GPS_GETTING_COORDINATES:
                result = "Finding Location...";
                if (location_status != null) {

                    if (loc_statusOff) {
                        location_status.setAnimation(as);
                        location_layout.setBackgroundColor(Color.YELLOW);
                        location_status.setText(("Finding Location..."));

                        loc_statusOn = true;
                        loc_statusOff = false;
                        editor = saveLocStats.edit();
                        editor.putString("loc_stats", result);
                        editor.apply();
                    }


                }


                break;
            case GPS_PAUSE_SCANNING:
                result = "Enable your GPS Location";
                if (location_status != null) {
                    location_layout.setBackgroundColor(Color.RED);
                    location_status.setText(result);
                    editor = saveLocStats.edit();
                    editor.putString("loc_stats", result);
                    editor.apply();
                }

                break;
            case GPS_GOT_COORDINATES:
                if (loc != null) {


                    // Accuracy
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

                    double latitude1 = loc.getLatitude();
                    double longitude1 = loc.getLongitude();
                    if (location_status != null) {


                        if (loc_statusOn) {
                            location_status.setAnimation(as);
                            location_layout.setBackgroundColor(resources.getColor(R.color.green_back));
                            location_status.setText(("Location Found"));
                            loc_statusOn = false;
                            loc_statusOff = true;
                            editor = saveLocStats.edit();
                            editor.putString("loc_stats", "Location Found");
                            editor.apply();

                        }

                    }


                } else {
                    result = "Location Unavailable";
                    if (location_status != null) {
                        location_status.setAnimation(as);
                        location_layout.setBackgroundColor(Color.RED);
                        location_status.setText(result);
                        editor = saveLocStats.edit();
                        editor.putString("loc_stats", result);
                        editor.apply();
                    }

                }
                break;
        }

    }

    private void showGPSGDialog() {

        if (getActivity() != null) {

            builder = new AlertDialog.Builder(getActivity());
            if (!DIALOG_VISIBLE) {
                if (location_status != null) {
                    String s = location_status.getText().toString();
                    if (s.equals("Set your gps location to high accuracy")) {
                        if (!DIALOG_VISIBLE) {
                            if (builder != null) {

                                DIALOG_VISIBLE = true;

                                //Uncomment the below code to Set the message and title from the strings.xml file
                                builder.setTitle("Alert");

                                //Setting message manually and performing action on button click
                                builder.setMessage("Set your gps location to high accuracy!")
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                DIALOG_VISIBLE = false;
                                                if (getActivity() != null)
                                                    getActivity().startActivityForResult(new Intent(action), 1111);

                                            }
                                        })
                                        .setNegativeButton("Hide", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //  Action for 'NO' Button
                                                dialog.cancel();
                                                DIALOG_VISIBLE = false;
                                                // isOne = true;
                                            }
                                        });
                                //Creating dialog box
                                AlertDialog alert = builder.create();
                                //Setting the title manually

                                alert.show();
                            }
                        }
                    } else if (s.equals("Enable your GPS Location")) {
                        if (!DIALOG_VISIBLE) {
                            if (builder != null) {

                                DIALOG_VISIBLE = true;

                                //Uncomment the below code to Set the message and title from the strings.xml file
                                builder.setTitle("Alert");

                                //Setting message manually and performing action on button click
                                builder.setMessage("Turn on your GPS Location!")
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                DIALOG_VISIBLE = false;
                                                if (getActivity() != null)
                                                    getActivity().startActivityForResult(new Intent(action), 1111);

                                            }
                                        });
                      /*  .setNegativeButton("Hide", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                                DIALOG_VISIBLE = false;
                                // isOne = true;
                            }
                        });*/
                                //Creating dialog box
                                AlertDialog alert = builder.create();
                                //Setting the title manually

                                alert.show();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            stopLocation();
        } catch (Exception e) {
            Log.e("ExceptionOnDestroy1", e.getMessage());
        }
        try {


            stopLocationUpdates();
        } catch (Exception e) {
            Log.e("ExceptionOnDestroy2", e.getMessage());
        }
    }

    @SuppressLint("ValidFragment")
    public static class DummySectionFragment extends Fragment implements View.OnClickListener {
        public static final String ARG_SECTION_NUMBER = "section_number";
        public static final String pos1 = "";
        private static FragmentActivity activity;
        private static Spinner distributorListDropDown;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        String routeName = "";
        String chkStatus = "";
        /*
        DISTANCE CALCULATION BETWEEN 2 GEO POINTS
         */

        List<String> ItemsData = new ArrayList<>();

        public DummySectionFragment() {


        }

        private static int getIndex(Spinner spinner, String myString) {
            int index = 0;

            for (int i = 0; i < spinner.getCount(); i++) {
                if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        @Override
        @NonNull
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

            activity = getActivity();
            int pos2 = 0;
            if (getArguments() != null)
                pos2 = Integer.parseInt(Integer.toString(getArguments().getInt(pos1)));


            if (enableAdvanceDashboard == 1) {

                try {

                    Log.wtf("Dashboard", String.valueOf(pos2));
                    switch (pos2) {


                  /*
                        SHOW Dashboard
                 */

                        case 1: {


                            /// case 2 code
                            FrameLayout RoutePlanFrame = rootView.findViewById(R.id.RoutePlanFrame);
                            RoutePlanFrame.setVisibility(View.GONE);
                            LinearLayout dashboardChartLayout = rootView.findViewById(R.id.dashboardChartLayout);
                            dashboardChartLayout.setVisibility(View.VISIBLE);

                            Animation in = new AlphaAnimation(0.0f, 1.0f);
                            in.setDuration(500);

                            Animation out = new AlphaAnimation(1.0f, 0.0f);
                            out.setDuration(500);

                            as = new AnimationSet(true);
                            as.addAnimation(out);
                            in.setStartOffset(50);
                            as.addAnimation(in);

                            location_layout = rootView.findViewById(R.id.location_layout);
                            network_layout = rootView.findViewById(R.id.network_layout);

                            location_status = rootView.findViewById(R.id.location_status_tv);
                            network_status = rootView.findViewById(R.id.network_status_tv);


                            handler = new Handler();
                            runtime = Runtime.getRuntime();
                            loader = new Loader();
                            CityArea = new String[3];

                            ArcProgress arcProgress = rootView.findViewById(R.id.arc_progress);
                            ArcProgress arcProgress2 = rootView.findViewById(R.id.arc_progress2);
                            TextView value_target = rootView.findViewById(R.id.MonthlyCallWorkPlan);
                            TextView prod_target = rootView.findViewById(R.id.DailyCallWorkPlan);
                            timeInBtn = rootView.findViewById(R.id.timeInBtn);
                            Button placeOrderBtn = rootView.findViewById(R.id.placeOrderBtn);
                            Button DailyExpenseBtn = rootView.findViewById(R.id.DailyExpenseBtn);
                            Button collectPaymentBtn = rootView.findViewById(R.id.collectPaymentBtn);
                            Button saleReturnBtn = rootView.findViewById(R.id.saleReturnBtn);
                            Button monthlyTargetBtn = rootView.findViewById(R.id.monthlyTargetBtn);
                            Button routePlanBtn = rootView.findViewById(R.id.routePlanBtn);
                            Button shopListButton = rootView.findViewById(R.id.shopListButton);
                            Button attendanceListBtn = rootView.findViewById(R.id.attendanceListBtn);
                            LinearLayout timeLayout = rootView.findViewById(R.id.timeLayout);
                            Button shopVisitBtn = rootView.findViewById(R.id.shopVisitBtn);
                            TextView userTV = rootView.findViewById(R.id.userTV);
                            GaugeView mgauge_y = rootView.findViewById(R.id.gauge_gol_y);
                            GaugeView mgauge_m = rootView.findViewById(R.id.gauge_gol_m);
                            mgauge_m.setTargetValue(35);
                            mgauge_m.setDefaultRanges();
                            mgauge_y.setTargetValue(40);
                            mgauge_y.setDefaultRanges();
                            Button progressreportBtn = rootView.findViewById(R.id.ProgressreportBtn);
                            ProgressBar target_progress = rootView.findViewById(R.id.pb_dash_targets);
                            TextView txt_targets = rootView.findViewById(R.id.tv_dash_targets);
                            TextView txt_targets_achieved = rootView.findViewById(R.id.tv_dash_target_achieved);
                            TextView txt_targets_achieved_lbl = rootView.findViewById(R.id.tv_dash_target_achieved_lbl);
                            TextView txt_sales_per_month = rootView.findViewById(R.id.tv_dash_sales_per_month);
                            txt_sales_per_month.setText(prettyCount(db.getSalesOrderTotal()));
                            txt_targets.setText(prettyCount(db.getTargetsTotal()));

                            // if (productSaleFromDB > 0&& targetFromDB >0) {
                            //
                            //                                int result = (100 * productSaleFromDB) / targetFromDB;
                            //                                Log.d(TAG, "productSaleFromDB: " + result);
                            //                                arcProgress.setProgress(result);
                            //                            } else arcProgress.setProgress(0);
                            if (db.getSalesOrderTotalPercent() > 0 && db.getTargetsTotal() > 0) {
                                int result = (100 * db.getSalesOrderTotalPercent()) / db.getTargetsTotal();


                                txt_targets_achieved.setText(result + " %  ");
                                txt_targets_achieved_lbl.setText(prettyCount(db.getSalesOrderTotalPercent()) + " Units Sold");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    target_progress.setProgress(result, true);
                                } else {
                                    target_progress.setProgress(result);
                                }
                            } else {
                                txt_targets_achieved.setText("0 %");
                                txt_targets_achieved_lbl.setText("0 Units Sold");
                                target_progress.setProgress(0);

                            }
                            Button QtyFormBtn = rootView.findViewById(R.id.QtyFormBtn);
                            Button HospitalSurveyBtn = rootView.findViewById(R.id.HospitalSurveyBtn);
                            Button SampleRequestForm = rootView.findViewById(R.id.SampleRequestForm);
//if(!BuildConfig.FLAVOR.equals("brands_unlimited"))
//{
                            //  QtyFormBtn.setVisibility(View.GONE);
//}
                            Button placeOrderTemplateBtn = rootView.findViewById(R.id.placeOrderTemplateBtn);
                            Button placeOrderReturnTemplateBtn = rootView.findViewById(R.id.placeOrderReturnTemplateBtn);
                            if (db.getAppSettingsValueByKey("en_bulk_sale_order") == 0) {
                                placeOrderTemplateBtn.setVisibility(View.GONE);
                            }
                            if (db.getAppSettingsValueByKey("en_qty_order") == 0) {
                                QtyFormBtn.setVisibility(View.GONE);
                            }
                            if (db.getAppSettingsValueByKey("en_bulk_sale_return") == 0) {
                                placeOrderReturnTemplateBtn.setVisibility(View.GONE);
                            }
                            Spinner RouteDropDown = rootView.findViewById(R.id.SelectRoute);
                            distributorListDropDown = rootView.findViewById(R.id.SelectDistributor);

                            db.OpenDb();

                            userTV.setText(db.getMobFName());
                            int SELECTED_ROUTE_NETID = db.getSavedRouteID();
                            routeName = db.getRouteNameFromNetID(SELECTED_ROUTE_NETID);

                            String savedDistributor = db.getSavedDistributorList();
                            String distributorName = db.getDistributorNameByID(Integer.parseInt(savedDistributor));

                            String productVal = db.getMobEmpProductSale();
                            String targetVal = db.getMobEmpTarget();

                            Log.e("getProgress", productVal + " : " + targetVal);
                            int productSaleFromDB = 0;
                            int targetFromDB = 0;
                            if (productVal != null && !productVal.equals("null")) {
                                productSaleFromDB = Integer.parseInt(productVal);
                            }
                            if (targetVal != null && !targetVal.equals("null")) {
                                targetFromDB = Integer.parseInt(targetVal);
                            }

                            empId = db.getMobEmpId();
                            enableClockIn = db.getAppSettingsValueByKey("en_clock_in");
                            enableAttendanceMust = db.getAppSettingsValueByKey("attendance_must");

                            if (db.getMobEmpTimeIn().equalsIgnoreCase("1")) {
                                //timeInTime.setText("TIME IN TIME : " + convertDate(db.getMobEmpTimeInTime()));
                                //timeInTimeRow.setVisibility(View.VISIBLE);
                                timeInBtn.setText((convertDate(db.getMobEmpTimeInTime()) + "\n " +
                                        "TIME OUT"));
                                if (getActivity() != null && isAdded()) {
                                    timeInBtn.setBackground(getResources().getDrawable(R.drawable.ripple_green));
                                }
                            } else if (db.getMobEmpTimeIn().equalsIgnoreCase("0")) {
                                //timeInTimeRow.setVisibility(View.GONE);
                                timeInBtn.setText("TIME IN");
                                if (getActivity() != null && isAdded()) {
                                    timeInBtn.setBackground(getResources().getDrawable(R.drawable.ripple));
                                }
                            }


                            if (BuildConfig.FLAVOR.equals("bms")) {
                                timeInBtn.setVisibility(View.VISIBLE);
                                attendanceListBtn.setVisibility(View.VISIBLE);
                            }else {
                                if (enableClockIn == 0) {
                                    timeInBtn.setVisibility(View.GONE);
                                }
                                if (db.getAppSettingsValueByKey("en_time_sheet") == 0) {
                                    attendanceListBtn.setVisibility(View.GONE);
                                }

                            }

                            if (db.getAppSettingsValueByKey("en_route_plan") == 0) {
                                routePlanBtn.setVisibility(View.GONE);
                            }
                            if (db.getAppSettingsValueByKey("en_monthly_target") == 0) {
                                monthlyTargetBtn.setVisibility(View.GONE);
                            }
                            if (db.getAppSettingsValueByKey("en_shop_list") == 0) {
                                shopListButton.setVisibility(View.GONE);
                            }
                            if (enableClockIn == 0) {
                                timeLayout.setVisibility(View.GONE);
                            }
                            if (db.getAppSettingsValueByKey("en_sale_order") == 0) {
                                placeOrderBtn.setVisibility(View.GONE);
                            }
                            if (db.getAppSettingsValueByKey("en_daily_expense") == 0) {
                                DailyExpenseBtn.setVisibility(View.GONE);
                            }
                            if (db.getAppSettingsValueByKey("en_sale_return") == 0) {
                                saleReturnBtn.setVisibility(View.GONE);
                                //        placeOrderReturnTemplateBtn.setVisibility(View.GONE);
                            }
                            if (db.getAppSettingsValueByKey("en_payment_collection") == 0) {
                                collectPaymentBtn.setVisibility(View.GONE);
                            }
                            if (db.getAppSettingsValueByKey("en_shop_visit") == 0) {
                                shopVisitBtn.setVisibility(View.GONE);
                            }

                            if (db.getAppSettingsValueByKey("en_progress_reports") == 0) {
                                progressreportBtn.setVisibility(View.GONE);
                            }

                            db.CloseDb();
                            //   int rkey = db.getAppSettingsValueByKey("day_route");
                            DropdownSetup(RouteDropDown);
//                            if (rkey == 1) {
//
//                                Date date = new Date();
//                                date.setTime(System.currentTimeMillis());
//                                Calendar c = Calendar.getInstance();
//                                c.setTime(date);
//                                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
//                                int check = 1;
//                                ArrayList<HashMap<String, String>> arrayList = db.getCustomerRoutesForDropDown();
//                                for (HashMap<String, String> map : arrayList) {
//
//                                    // Log.e("List-Data",map.get("name")+" : "+map.get("day"));
//                                    if (map.get("day").equals(String.valueOf(dayOfWeek - 1))) {
//                                        if (ItemsData.get(check).equals(map.get("name"))) {
//                                            RouteDropDown.setSelection(getIndex(RouteDropDown,
//                                                    map.get("name")));
//                                        }
//                                        break;
//                                    } else {
//                                        check++;
//                                    }
//                                }
//
//                                //   Log.e("List-Size", String.valueOf(arrayList.size() +" : "+check));
//                                //RouteDropDown.setSelection((check));
//
//
//                            } else if (SELECTED_ROUTE_NETID > 0) {
//


                            RouteDropDown.setSelection(getIndex(RouteDropDown, routeName));
                            //   }

                            DropdownSetupDistributor(distributorListDropDown);


                            Log.e("QUERY", db.getPricingCount() + "");


                            int key = db.getAppSettingsValueByKey("lock_route");

                            if (key == 1) {
                                RouteDropDown.setEnabled(false);
                            }


                            if (Integer.parseInt(savedDistributor) > 0) {

                                distributorListDropDown.setSelection(getIndex(distributorListDropDown, distributorName));
                            }
                            if (productSaleFromDB > 0 && targetFromDB > 0) {

                                int result = (100 * productSaleFromDB) / targetFromDB;
                                Log.d(TAG, "productSaleFromDB: " + result);
                                arcProgress.setProgress(result);
                            } else arcProgress.setProgress(0);
//                            arcProgress.setProgress(productSaleFromDB);
                            prod_target.setText(MessageFormat.format("{0}/{1}", productSaleFromDB,
                                    targetFromDB));
                            value_target.setText(MessageFormat.format("{0}/{1}", 0, 0));
                            if (targetFromDB > 0) {
                                int getVal = db.returnPercentage();
                                Log.e("TargetValue1", targetFromDB + " : " + getVal);
                                //                                if (getVal > 0) {
//                                    float t1 = ((float) getVal / (float) targetFromDB) * (float) 100;
//
//                                    Log.e("TargetValue2", targetFromDB + " : " + getVal + " : " + t1);
//                                    if (t1 <= 100) {
//                                        arcProgress2.setProgress((int) t1);
//                                    } else arcProgress2.setProgress(100);
//
//                                } else arcProgress2.setProgress(0);
                            }
                            arcProgress2.setProgress(0);

                            //Log.d("TestDebugTarget", String.valueOf(t1));
                            //Log.d("TestDebugProduct", String.valueOf(productSaleFromDB));
                            ////


                            timeInBtn.setOnClickListener(this);
                            placeOrderBtn.setOnClickListener(this);
                            DailyExpenseBtn.setOnClickListener(this);
                            shopVisitBtn.setOnClickListener(this);
                            collectPaymentBtn.setOnClickListener(this);
                            saleReturnBtn.setOnClickListener(this);
                            monthlyTargetBtn.setOnClickListener(this);
                            routePlanBtn.setOnClickListener(this);
                            shopListButton.setOnClickListener(this);
                            attendanceListBtn.setOnClickListener(this);
                            progressreportBtn.setOnClickListener(this);
                            placeOrderTemplateBtn.setOnClickListener(this);
                            /// case 2 code ends
                            QtyFormBtn.setOnClickListener(this);
                            SampleRequestForm.setOnClickListener(this);
                            HospitalSurveyBtn.setOnClickListener(this);
                            placeOrderReturnTemplateBtn.setOnClickListener(this);


                        }
                        break;


                     /*
                        SHOW Summary
                    */

                        case 2: {


//// case 1 code
                            LinearLayout ShowDashboard = rootView.findViewById(R.id.DashboardLayout);
                            ShowDashboard.setVisibility(View.VISIBLE);
                            FrameLayout RoutePlanFrame = rootView.findViewById(R.id.RoutePlanFrame);
                            RoutePlanFrame.setVisibility(View.GONE);
//                    TextView about = (TextView)rootView.findViewById(R.id.aboutTxtDash);

                            TextView TotalCustomer = rootView.findViewById(R.id.TotalCustomerTxt);
                            TextView TotalProducts = rootView.findViewById(R.id.TotalProductsTxt);
                            TextView TotalOrder = rootView.findViewById(R.id.TotalOrderTxt);
                            TextView TotalSales = rootView.findViewById(R.id.TotalSalesTxt);
                            TextView TotalSalesUnexecuted = rootView.findViewById(R.id.TotalSalesUnexecutedTxt);
                            TextView TotalExecuteSales = rootView.findViewById(R.id.TotalExecuteSalesTxt);
                            TextView TotalUnExecuteSales = rootView.findViewById(R.id.TotalUnExecuteSalesTxt);
                            TextView totalOrderTxt = rootView.findViewById(R.id.textViewD4);
                            TextView totalSalesTxt = rootView.findViewById(R.id.textViewD5);
                            TextView totalExecuteSalesTxt = rootView.findViewById(R.id.textViewD6);
                            TextView totalUnExecuteSalesTxt = rootView.findViewById(R.id.textViewD7);
                            TextView TotalShopVisit = rootView.findViewById(R.id.TotalShopVisitTxt);
                            TextView totalShopVisitTxt = rootView.findViewById(R.id.textViewD9);
                            TextView TotalOrdersToday = rootView.findViewById(R.id.TotalOrdersTodayTxt);
                            TextView totalOrdersTodayTxt = rootView.findViewById(R.id.textViewD10);
                            TextView TotalReturnsToday = rootView.findViewById(R.id.TotalReturnsTodayTxt);
                            TextView totalReturnsTodayTxt = rootView.findViewById(R.id.textViewD11);
                            // Abdul
                            TextView totalSumOrder = rootView.findViewById(R.id.TotalOrdersTodaySum);
                            TextView totalQtyOrder = rootView.findViewById(R.id.TotalOrdersTodayQty);
                            TextView totalOrderSumTextViewD12 = rootView.findViewById(R.id.textViewD12);
                            TextView totalOrderSumTextViewD13 = rootView.findViewById(R.id.textViewD13);
                            //  TableRow showRouteTableInLast =rootView.findViewById(R.id.showRouteInLast);
                            //showRouteTableInLast.setVisibility(View.GONE);

                            ImageView Img = rootView.findViewById(R.id.TotalCustomerImage);

//                            Spinner RouteDropDown = (Spinner) rootView.findViewById(R.id.SelectRoute);

                            int year = Calendar.getInstance().get(Calendar.YEAR);
                            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                            String month = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                            month += "    ";

                            totalOrderTxt.setText("Total Orders" + " ( " + month.substring(0, 3) + " " + year + " ) ");

                            totalSalesTxt.setText("Total Sales" + " ( " + month.substring(0, 3) + " " + year + " )");
                            totalExecuteSalesTxt.setText("Execute Orders" + " ( " + month.substring(0, 3) + " " + year + " )");
                            totalUnExecuteSalesTxt.setText("Un-Execute Orders (Total)");
                            totalShopVisitTxt.setText("Total Shops Visit" + " (" + day + " " + month.substring(0, 3) + " " + year + ")");
                            totalOrdersTodayTxt.setText("Total Orders" + " (" + day + " " + month.substring(0, 3) + " " + year + ")");
                            totalReturnsTodayTxt.setText("Total Returns" + " (" + day + " " + month.substring(0, 3) + " " + year + ")");

                            //Abdul
                            totalOrderSumTextViewD12.setText("Total Order Sum" + " (" + day + " " + month.substring(0, 3) + " " + year + ")");
                            totalOrderSumTextViewD13.setText("Total Order Quantity" + " (" + day + " " + month.substring(0, 3) + " " + year + ")");

                            try {


                                PosDB db = PosDB.getInstance(getActivity());


                                db.OpenDb();

                                int CustStr = db.getTotalCustomers();
                                int SalesRow = db.getSalesRow();
                                int SalesRowUnexecuted = db.getSalesRowUnexecuted();
                                int ProductsRow = db.getProductsRow();
                                int OrderRow = db.getOrderRow();
                                int ExecuteSalesOrderRow = db.getExecuteSaleOrdersRow();
                                int UnExecuteSalesOrderRow = db.getUnExecuteSaleOrdersRow();
//                                int SELECTED_ROUTE_NETID = db.getSavedRouteID();
//                                String routeName = db.getRouteNameFromNetID(SELECTED_ROUTE_NETID);
                                int ShopVisitRow = db.getShopVisitRow();
                                int OrdersRowToday = db.getOrdersRowToday();
                                int ReturnsRowToday = db.getReturnsRowToday();


                                int TotalPriceOrderSum = Integer.parseInt(db.totalOrderSum());
                                int TotalPriceOrderQty = Integer.parseInt(db.totalOrderQty());


                                db.CloseDb();

//                                DropdownSetup(RouteDropDown);
//
//
//                                if (SELECTED_ROUTE_NETID > 0) {
//
//                                    RouteDropDown.setSelection(getIndex(RouteDropDown, routeName));
//                                }

                                String Customer = "<b>" + prettyCount(CustStr) + "</b>";
                                TotalCustomer.setText(Html.fromHtml(Customer));

                                String Sales = "<b>" + prettyCount(SalesRow) + "</b>";
                                TotalSales.setText(Html.fromHtml(Sales));

                                String SalesUnexecuted = "<b>" + prettyCount(SalesRowUnexecuted) + "</b>";
                                TotalSalesUnexecuted.setText(Html.fromHtml(SalesUnexecuted));

                                String Products = "<b>" + prettyCount(ProductsRow) + "</b>";
                                TotalProducts.setText(Html.fromHtml(Products));

                                String Orders = "<b>" + prettyCount(OrderRow) + "</b>";
                                TotalOrder.setText(Html.fromHtml(Orders));

                                String ExecuteOrders = "<b>" + prettyCount(ExecuteSalesOrderRow) + "</b>";
                                TotalExecuteSales.setText(Html.fromHtml(ExecuteOrders));

                                String UnExecuteOrders = "<b>" + prettyCount(UnExecuteSalesOrderRow) + "</b>";
                                TotalUnExecuteSales.setText(Html.fromHtml(UnExecuteOrders));

                                String shopVisit = "<b>" + prettyCount(ShopVisitRow) + "</b>";
                                TotalShopVisit.setText(Html.fromHtml(shopVisit));

                                String ordersToday = "<b>" + prettyCount(OrdersRowToday) + "</b>";
                                TotalOrdersToday.setText(Html.fromHtml(ordersToday));

                                String ordersSumToday = "<b>" + prettyCount(TotalPriceOrderSum) + "</b>";
                                String ordersQtyToday = "<b>" + prettyCount(TotalPriceOrderQty) + "</b>";
                                totalSumOrder.setText(Html.fromHtml(ordersSumToday));
                                totalQtyOrder.setText(Html.fromHtml(ordersQtyToday));

                                String returnsToday = "<b>" + prettyCount(ReturnsRowToday) + "</b>";
                                TotalReturnsToday.setText(Html.fromHtml(returnsToday));


                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e.fillInStackTrace());
                            }

                            //// case 1 code end


                        }
                        break;
                        case 3: {


//// case 1 code
                            LinearLayout ShowDashboard = rootView.findViewById(R.id.DashboardLayout);
                            ShowDashboard.setVisibility(View.GONE);
                            LinearLayout dashboardChartLayout = rootView.findViewById(R.id.dashboardChartLayout);
                            dashboardChartLayout.setVisibility(View.GONE);
                            Log.wtf("WorkPlanDashboard", String.valueOf(pos2));
                            FrameLayout RoutePlanFrame = rootView.findViewById(R.id.RoutePlanFrame);
                            RoutePlanFrame.setVisibility(View.VISIBLE);
                            getChildFragmentManager()
                                    .beginTransaction()
//                        .setCustomAnimations(R.anim.enter_from_left, R.anim.enter_from_right)
                                    .replace(R.id.RoutePlanFrame,
                                            RoutePlan.newInstanceNoLabel(),
                                            RoutePlan.TAG).commit();


//
                            //// case 1 code end


                        }
                        break;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    //throw new RuntimeException( e.fillInStackTrace() );
                }

            } else {


                try {
                    
                    
                    /*
                        SHOW Summary
                    */
                    // mViewPager.canScrollHorizontally(0);
                    //// case 1 code
                    //                    TextView about = (TextView)rootView.findViewById(R.id.aboutTxtDash);
                    //Abdul Start here
                    //// case 1 code end
                    if (pos2 == 1) {
                        LinearLayout ShowDashboard = rootView.findViewById(R.id.DashboardLayout);
                        ShowDashboard.setVisibility(View.VISIBLE);
                        LinearLayout HideDashboard = rootView.findViewById(R.id.dashboardChartLayout);
                        HideDashboard.setVisibility(View.GONE);
                        TextView TotalCustomer = rootView.findViewById(R.id.TotalCustomerTxt);
                        TextView TotalProducts = rootView.findViewById(R.id.TotalProductsTxt);
                        TextView TotalOrder = rootView.findViewById(R.id.TotalOrderTxt);
                        TextView TotalSales = rootView.findViewById(R.id.TotalSalesTxt);
                        TextView TotalSalesUnexecuted = rootView.findViewById(R.id.TotalSalesUnexecutedTxt);
                        TextView TotalExecuteSales = rootView.findViewById(R.id.TotalExecuteSalesTxt);
                        TextView TotalUnExecuteSales = rootView.findViewById(R.id.TotalUnExecuteSalesTxt);
                        TextView totalOrderTxt = rootView.findViewById(R.id.textViewD4);
                        TextView totalSalesTxt = rootView.findViewById(R.id.textViewD5);
                        TextView totalExecuteSalesTxt = rootView.findViewById(R.id.textViewD6);
                        TextView totalUnExecuteSalesTxt = rootView.findViewById(R.id.textViewD7);
                        TextView TotalShopVisit = rootView.findViewById(R.id.TotalShopVisitTxt);
                        TextView totalShopVisitTxt = rootView.findViewById(R.id.textViewD9);
                        TextView TotalOrdersToday = rootView.findViewById(R.id.TotalOrdersTodayTxt);
                        TextView totalOrdersTodayTxt = rootView.findViewById(R.id.textViewD10);
                        TextView TotalReturnsToday = rootView.findViewById(R.id.TotalReturnsTodayTxt);
                        TextView totalReturnsTodayTxt = rootView.findViewById(R.id.textViewD11);
                        TextView totalSumOrder = rootView.findViewById(R.id.TotalOrdersTodaySum);
                        TextView totalQtyOrder = rootView.findViewById(R.id.TotalOrdersTodayQty);
                        TextView totalOrderSumTextViewD12 = rootView.findViewById(R.id.textViewD12);
                        TextView totalOrderSumTextViewD13 = rootView.findViewById(R.id.textViewD13);
                        //  TableRow showRouteTableInLast =rootView.findViewById(R.id.showRouteInLast);
                        //Spinner showRouteInLayout = rootView.findViewById(R.id.showRouteInLayout);
                        //showRouteTableInLast.setVisibility(View.GONE);
                       /* DropdownSetup(showRouteInLayout);
                        int SELECTED_ROUTE_NETID = db.getSavedRouteID();
                        String routeName = db.getRouteNameFromNetID(SELECTED_ROUTE_NETID);
                        if (SELECTED_ROUTE_NETID > 0) {
                            
                            showRouteInLayout.setSelection(getIndex(showRouteInLayout, routeName));
                        }*/
                        ImageView Img = rootView.findViewById(R.id.TotalCustomerImage);
                        int year = Calendar.getInstance().get(Calendar.YEAR);
                        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                        String month = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                        month += "    ";
                        totalOrderTxt.setText("Total Orders" + " ( " + month.substring(0, 3) + " " + year + " ) ");
                        totalSalesTxt.setText("Total Sales" + " ( " + month.substring(0, 3) + " " + year + " )");
                        totalExecuteSalesTxt.setText("Execute Orders" + " ( " + month.substring(0, 3) + " " + year + " )");
                        totalUnExecuteSalesTxt.setText("Un-Execute Orders (Total)");
                        totalShopVisitTxt.setText("Total Shops Visit" + " (" + day + " " + month.substring(0, 3) + " " + year + ")");
                        totalOrdersTodayTxt.setText("Total Orders" + " (" + day + " " + month.substring(0, 3) + " " + year + ")");
                        totalReturnsTodayTxt.setText("Total Returns" + " (" + day + " " + month.substring(0, 3) + " " + year + ")");
                        totalOrderSumTextViewD12.setText("Total Order Sum" + " (" + day + " " + month.substring(0, 3) + " " + year + ")");
                        totalOrderSumTextViewD13.setText("Total Order Quantity" + " (" + day + " " + month.substring(0, 3) + " " + year + ")");
                        try {


                            PosDB db = PosDB.getInstance(getActivity());


                            db.OpenDb();

                            int CustStr = db.getTotalCustomers();
                            int SalesRow = db.getSalesRow();
                            int SalesRowUnexecuted = db.getSalesRowUnexecuted();
                            int ProductsRow = db.getProductsRow();
                            int OrderRow = db.getOrderRow();
                            int ExecuteSalesOrderRow = db.getExecuteSaleOrdersRow();
                            int UnExecuteSalesOrderRow = db.getUnExecuteSaleOrdersRow();
//                                int SELECTED_ROUTE_NETID = db.getSavedRouteID();
//                                String routeName = db.getRouteNameFromNetID(SELECTED_ROUTE_NETID);
                            int ShopVisitRow = db.getShopVisitRow();
                            int OrdersRowToday = db.getOrdersRowToday();
                            int ReturnsRowToday = db.getReturnsRowToday();
                            int TotalPriceOrderSum = Integer.parseInt(db.totalOrderSum());
                            int TotalPriceOrderQty = Integer.parseInt(db.totalOrderSum());

                            db.CloseDb();

//                                DropdownSetup(RouteDropDown);
//
//
//                                if (SELECTED_ROUTE_NETID > 0) {
//
//                                    RouteDropDown.setSelection(getIndex(RouteDropDown, routeName));
//                                }

                            String Customer = "<b>" + CustStr + "</b>";
                            TotalCustomer.setText(Html.fromHtml(Customer));

                            String Sales = "<b>" + SalesRow + "</b>";
                            TotalSales.setText(Html.fromHtml(Sales));

                            String SalesUnexecuted = "<b>" + SalesRowUnexecuted + "</b>";
                            TotalSalesUnexecuted.setText(Html.fromHtml(SalesUnexecuted));

                            String Products = "<b>" + ProductsRow + "</b>";
                            TotalProducts.setText(Html.fromHtml(Products));


                            String Orders = "<b>" + OrderRow + "</b>";
                            TotalOrder.setText(Html.fromHtml(Orders));
                            //TotalProducts.setText(Html.fromHtml(Orders));

                            String ExecuteOrders = "<b>" + ExecuteSalesOrderRow + "</b>";
                            TotalExecuteSales.setText(Html.fromHtml(ExecuteOrders));

                            String UnExecuteOrders = "<b>" + UnExecuteSalesOrderRow + "</b>";
                            TotalUnExecuteSales.setText(Html.fromHtml(UnExecuteOrders));

                            String shopVisit = "<b>" + ShopVisitRow + "</b>";
                            TotalShopVisit.setText(Html.fromHtml(shopVisit));

                            String ordersToday = "<b>" + OrdersRowToday + "</b>";
                            TotalOrdersToday.setText(Html.fromHtml(ordersToday));

                            String ordersSumToday = "<b>" + TotalPriceOrderSum + "</b>";
                            String ordersQtyToday = "<b>" + TotalPriceOrderQty + "</b>";
                            totalSumOrder.setText(Html.fromHtml(ordersSumToday));
                            totalQtyOrder.setText(Html.fromHtml(ordersQtyToday));
                            String returnsToday = "<b>" + ReturnsRowToday + "</b>";
                            TotalReturnsToday.setText(Html.fromHtml(returnsToday));

                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e.fillInStackTrace());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //throw new RuntimeException( e.fillInStackTrace() );
                }
            }
            return rootView;
        }

        private void DropdownSetupDistributor(Spinner dropDown) {

            List<String> ItemsData;
            List<String> ItemsID;

            db.OpenDb();
            ItemsData = db.getDistributorNameForDropDown();
            ItemsID = db.getDistributorIdForDropDown();
            db.CloseDb();

//            ItemsData.add(0, "All");
//            ItemsID.add(0, "0");


            try {
                // Creating adapter for spinner


                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(activity,
                        android.R.layout.simple_spinner_item, ItemsData);

                // Drop down layout style - editOrderList view with radio button
                dataAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                dropDown.setAdapter(dataAdapter);
                db.OpenDb();
                //Log.e("selectedDistbutorId ", "1 : " + ItemsID.get(0));
                String savedDistributor = db.getSavedDistributorList();
                String distributorName = db.getDistributorNameByID(Integer.parseInt(savedDistributor));
                if (Integer.parseInt(savedDistributor) > 0) {

                    distributorListDropDown.setSelection(getIndex(distributorListDropDown, distributorName));
                } else {
                    //     distributorListDropDown.setSelection(0);
                    db.OpenDb();
                    db.updateSavedDistributorList(ItemsID.get(0));
                    db.CloseDb();
                }

                db.CloseDb();
            } catch (Exception e) {
                System.out.println(e.toString());
            }

            final List<String> finalItemsID = ItemsID;
            dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

//                    if (currentSelection == i) {
//                        return;
//                    } else {

                    long selectedDistbutorId = Long.parseLong(finalItemsID.get(i));
                    Log.e("selectedDistbutorId ", "2 : " + finalItemsID.get(i));
                    db.OpenDb();
                    db.updateSavedDistributorList(finalItemsID.get(i));
                    db.CloseDb();
                    //}
                    currentSelection = i;
                    Log.e("selectedDistbutorId ", "distributor id: " + finalItemsID.get(i));


                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        }

        private void DropdownSetup(Spinner dropDown) {


            List<String> ItemsID;
            if (ItemsData.size() > 0) {
                ItemsData.clear();
            }


            db.OpenDb();
            int rkey = db.getAppSettingsValueByKey("day_route");
            Calendar c = Calendar.getInstance();
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

            if (rkey == 1) {
//                ItemsID = db.getCustomerRoutesIDForDropDownByDay(String.valueOf(dayOfWeek - 1));
                ItemsID = db.getCustomerRoutesIDForDropDownByDay(String.valueOf(dayOfWeek));
            } else {
                ItemsID = db.getCustomerRoutesIDForDropDown();
            }
            db.CloseDb();

            if (db.checkRouteDayAvailable() <= 0) {
                ItemsData.add(0, "All");
                ItemsID.add(0, "0");
            }


            ArrayList<HashMap<String, String>> arrayList;
            if (rkey == 1) {
//                arrayList = db.getCustomerRoutesForDropDownbyDay(String.valueOf(dayOfWeek - 1));
                arrayList = db.getCustomerRoutesForDropDownbyDay(String.valueOf(dayOfWeek ));
            } else {

                arrayList = db.getCustomerRoutesForDropDown();
            }
            for (HashMap<String, String> map : arrayList) {

                ItemsData.add(map.get("name"));

            }
    
          /*  for (HashMap<String,String> s : arrayList) {
               // Log.e("List-Data",s.get("name")+" : "+s.get("day"));
            }*/

            try {
                // Creating adapter for spinner


                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(activity,
                        android.R.layout.simple_spinner_item, ItemsData);

                Log.e("List-Size", String.valueOf(ItemsData.size()));

                // Drop down layout style - editOrderList view with radio button
                dataAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                dropDown.setAdapter(dataAdapter);
            } catch (Exception e) {
                System.out.println(e.toString());
            }

            final List<String> finalItemsID = ItemsID;
            dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

//                    db.OpenDb();
//                    db.updateSavedRoute(finalItemsID.get(i));
//                    db.CloseDb();


                    if (currentSelection == i && ItemsData.contains(routeName)) {
                        return;
                    } else {

//                        long selectedRouteDropDown = Long.parseLong(finalItemsID.get(i));
                        db.OpenDb();
                        db.updateSavedRoute(finalItemsID.get(i));
                        db.CloseDb();
                    }
                    currentSelection = i;


                    Log.d("sql", "route id: " + finalItemsID.get(i));

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        }

        private void DropdownSetup2(Spinner dropDown, int day) {

            List<String> ItemsData;
            List<String> ItemsID;

            db.OpenDb();
            ItemsData = db.getCustomerRoutesForDropDown(day);
            ItemsID = db.getCustomerRoutesIDForDropDown();
            db.CloseDb();


            try {
                // Creating adapter for spinner


                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(activity,
                        android.R.layout.simple_spinner_item, ItemsData);

                // Drop down layout style - editOrderList view with radio button
                dataAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                dropDown.setAdapter(dataAdapter);
            } catch (Exception e) {
                System.out.println(e.toString());
            }

            final List<String> finalItemsID = ItemsID;
            dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

//                    db.OpenDb();
//                    db.updateSavedRoute(finalItemsID.get(i));
//                    db.CloseDb();
                    if (currentSelection == i) {
                        return;
                    } else {

                        long selectedRouteDropDown = Long.parseLong(finalItemsID.get(i));
                        db.OpenDb();
                        db.updateSavedRoute(finalItemsID.get(i));
                        db.CloseDb();
                    }
                    currentSelection = i;


                    Log.d("sql", "route id: " + finalItemsID.get(i));

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        }


        public double getDistanceFromLatLonInMeter(double lat1, double lon1, double lat2, double lon2) {
            double R = 6371; // Radius of the earth in km
            double dLat = deg2rad(lat2 - lat1);  // deg2rad below
            double dLon = deg2rad(lon2 - lon1);
            double a =
                    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                            Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                    Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double d = (R * c) / 1000; // Distance in meter
            return d;
        }


         /*
        DISTANCE CALCULATION BETWEEN 2 GEO POINTS
         */

        public double deg2rad(double deg) {
            return deg * (Math.PI / 180);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {


                case R.id.timeInBtn: {


                    requestPermission(getActivity());

                    if (granted) {
                        loc_holder = saveLocStats.getString("loc_stats", "");

                        if (loc_holder != null) {
                            if (loc_holder.equals("Set your gps location to high accuracy")
                                    || loc_holder.equals("Enable your GPS Location") || loc_holder.equals("Finding Location...")) {
                                if (loc_holder.equals("Set your gps location to high accuracy")) {
                                    Constant.showGPSGDialog(getActivity());
                                    return;
                                }
                                if (loc_holder.equals("Enable your GPS Location")) {
                                    Constant.showGPSGDialog(getActivity());
                                    return;
                                }

                            }
                        }
                        long currentClickTime = SystemClock.uptimeMillis();
                        long elapsedTime = currentClickTime - lastClickTime;

                        lastClickTime = currentClickTime;
                        if (elapsedTime <= MIN_CLICK_INTERVAL) {
                            Toast.makeText(activity, "Please wait a second while last operation " + "complete",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }


                        int key = db.getAppSettingsValueByKey("completeroute_timeout");


                        if (timeInBtn.getText().toString().contains("TIME OUT")) {
                            if (key == 1) {
                                ArrayList<HashMap<String, String>> data = new ArrayList<>();
                                ArrayList<String> dataCustId = new ArrayList<>();

                                db.OpenDb();
                                int savedRoute = db.getSavedRouteID();
                                if (savedRoute == 0) {
                                    dataCustId = db.getSelectedCustomerID();
                                    data = db.getCustomerListForRoutePlan(UserSettings.PENDING);
                                } else {
                                    dataCustId = db.getSelectedCustomerIDByRoute(savedRoute);
                                    data = db.getCustomerListForRoutePlanByRoute(savedRoute,UserSettings.PENDING);


                                }
                                int c = 0;

                                for (HashMap<String, String> map : data) {


                                    if (map.get("last_update").equals(getDateTimeSHORT())) {
                                        c++;
                                    }


                                }

                                if (data.size() > 0) {
                                    if ((c) != data.size()) {

                                        Toast.makeText(activity, "Complete Today's Routes First", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }
                        }


                        if (network_status != null)
                            if (network_status.getText().toString().equals("No Internet Access")) {
                                Constant.CustomDialogNoInternet(getActivity());
                                return;
                            }
                        if (getActivity() != null)
                            if (checkLocationPermission(getActivity())) {

                                new Handler().post(new Runnable() {

                                    @Override
                                    public void run() {

                                        if (Constant.networkAvailable()) {


                                            if (latis != 0 && longis != 0) {

                                                if (getActivity() != null) {
                                                    CityArea = getCityAreaCountryFromLatitudeLongitude(latis, longis, getActivity());

                                                    callApi(latis, longis);

                                                }
                                            } else {

                                                if (latitudeLastByFused != 0 && longitudeLastByFused != 0) {

                                                    if (getActivity() != null) {
                                                        CityArea = getCityAreaCountryFromLatitudeLongitude(latitudeLastByFused, longitudeLastByFused, getActivity());

                                                        callApi(latitudeLastByFused, longitudeLastByFused);

                                                    }
                                                } else {

                                                    customToast(AppContextProvider.getContext(), "Unable to fetch Location, Please Move to Open Area");
                                                }
                                            }

                                        } else {
                                            if (getActivity() != null) {
                                                Constant.CustomDialogNoInternet(getActivity());
                                            } else {
                                                customToast(AppContextProvider.getContext(), "Kindly Check Your Internet Connection");
                                            }
                                        }

                                    }
                                });

                            } else {
                                // loader.HideLoader();
                                Toast.makeText(AppContextProvider.getContext(), "Location Permission required", Toast.LENGTH_SHORT).show();
                            }


                    }
                    break;
                }

                case R.id.monthlyTargetBtn: {


                    // Toast.makeText(context , "Monthly Target Page", Toast.LENGTH_LONG).show();
                    trackCount = 0;
                    FRAGMENT_TAG = MonthlyTarget.TAG;
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, MonthlyTarget.newInstance(), MonthlyTarget.TAG).commit();


                    break;

                }
                case R.id.routePlanBtn: {


                    // Toast.makeText(context , "Monthly Target Page", Toast.LENGTH_LONG).show();
                    trackCount = 0;
                    FRAGMENT_TAG = RoutePlan.TAG;
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, RoutePlan.newInstance(), RoutePlan.TAG).commit();


                    break;

                }
                case R.id.DailyExpenseBtn: {


                    // Toast.makeText(context , "Monthly Target Page", Toast.LENGTH_LONG).show();
                    trackCount = 0;
                    FRAGMENT_TAG = DailyExpenseSheetFinal.TAG;
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, DailyExpenseSheetFinal.newInstance(), DailyExpenseSheetFinal.TAG).commit();


                    break;

                }

                case R.id.placeOrderBtn: {
                    requestPermission(getActivity());
                    if (!granted) {
                        return;
                    }


                    //Toast.makeText(context , "Place Order Page", Toast.LENGTH_LONG).show();
                    db.OpenDb();
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    trackCount = 0;
                    FRAGMENT_TAG = SaleOrderFinal.TAG;
                    if (enableAttendanceMust == 1) {
                        if (empTimeIn.equalsIgnoreCase("1")) {

                            activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame, SaleOrderFinal.newInstance(), SaleOrderFinal.TAG).commit();
                        } else {
                            //Toast.makeText(context, "Can't Create Sale Order, Time In first to Create Order", Toast.LENGTH_LONG).show();
                            customToast(AppContextProvider.getContext(), "Can't Create Sale Order, Time In first to Create Order");
                        }
                    } else {
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, SaleOrderFinal.newInstance(), SaleOrderFinal.TAG).commit();
                    }
                    break;

                }
                case R.id.placeOrderTemplateBtn: {
                    requestPermission(getActivity());
                    if (!granted) {
                        return;
                    }


                    //Toast.makeText(context , "Place Order Page", Toast.LENGTH_LONG).show();
                    db.OpenDb();
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    trackCount = 0;
                    FRAGMENT_TAG = SaleOrderFinal.TAG;
                    if (enableAttendanceMust == 1) {
                        if (empTimeIn.equalsIgnoreCase("1")) {

                            activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame, AutoItemSaleOrderFinal.newInstance(), AutoItemSaleOrderFinal.TAG).commit();
                        } else {
                            //Toast.makeText(context, "Can't Create Sale Order, Time In first to Create Order", Toast.LENGTH_LONG).show();
                            customToast(AppContextProvider.getContext(), "Can't Create Sale Order, Time In first to Create Order");
                        }
                    } else {
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, AutoItemSaleOrderFinal.newInstance(), AutoItemSaleOrderFinal.TAG).commit();
                    }
                    break;

                }

                case R.id.placeOrderReturnTemplateBtn: {
                    requestPermission(getActivity());
                    if (!granted) {
                        return;
                    }


                    //Toast.makeText(context , "Place Order Page", Toast.LENGTH_LONG).show();
                    db.OpenDb();
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    trackCount = 0;
                    FRAGMENT_TAG = AutoItemSaleReturnFinal.TAG;
                    if (enableAttendanceMust == 1) {
                        if (empTimeIn.equalsIgnoreCase("1")) {

                            activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame, AutoItemSaleReturnFinal.newInstance(), AutoItemSaleReturnFinal.TAG).commit();
                        } else {
                            //Toast.makeText(context, "Can't Create Sale Order, Time In first to Create Order", Toast.LENGTH_LONG).show();
                            customToast(AppContextProvider.getContext(), "Can't Create Sale Order, Time In first to Create Order");
                        }
                    } else {
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, AutoItemSaleReturnFinal.newInstance(), AutoItemSaleReturnFinal.TAG).commit();
                    }
                    break;

                }

                case R.id.shopListButton: {
                    requestPermission(getActivity());
                    if (!granted) {
                        return;
                    }
                    //Toast.makeText(context , "Place Order Page", Toast.LENGTH_LONG).show();

                    trackCount = 0;
                    FRAGMENT_TAG = CustomerList.TAG;
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, CustomerList.newInstance(),
                                    CustomerList.TAG).commit();
                    break;

                }

                case R.id.collectPaymentBtn: {
                    requestPermission(getActivity());
                    if (!granted) {
                        return;
                    }

                    FRAGMENT_TAG = PaymentRecieving.TAG;
                    // Toast.makeText(context , "Collect Payment Page", Toast.LENGTH_LONG).show();
                    db.OpenDb();
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    trackCount = 0;
                    if (enableAttendanceMust == 1) {
                        if (empTimeIn.equalsIgnoreCase("1")) {


                            activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame, PaymentRecieving.newInstance(), PaymentRecieving.TAG).commit();
                        } else {
                            //Toast.makeText(context, "Can't Create Payment, Time In first to Create Payment", Toast.LENGTH_LONG).show();
                            customToast(AppContextProvider.getContext(), "Can't Create Payment, Time In first to Create Payment");
                        }
                    } else {
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, PaymentRecieving.newInstance(), PaymentRecieving.TAG).commit();
                    }
                    break;

                }

                case R.id.shopVisitBtn: {

                    requestPermission(getActivity());
                    if (!granted) {
                        return;
                    }

                    FRAGMENT_TAG = ShopsVisit.TAG;
                    //Toast.makeText(context , "Place Order Page", Toast.LENGTH_LONG).show();
                    db.OpenDb();
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    trackCount = 0;
                    if (enableAttendanceMust == 1) {
                        if (empTimeIn.equalsIgnoreCase("1")) {


                            activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame, ShopsVisit.newInstance(), ShopsVisit.TAG).commit();
                        } else {
                            //Toast.makeText(context, "Can't Create Sale Return, Time In first to Create Sale Return", Toast.LENGTH_LONG).show();
                            customToast(AppContextProvider.getContext(), "Can't Create Shop Visit, Time In first to Create Shop Visit");
                        }
                    } else {
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, ShopsVisit.newInstance(), ShopsVisit.TAG).commit();
                    }
                    break;

                }

                case R.id.saleReturnBtn: {
                    requestPermission(getActivity());
                    if (!granted) {
                        return;
                    }

                    FRAGMENT_TAG = SaleReturnFinal.TAG;
                    //Toast.makeText(context , "Place Order Page", Toast.LENGTH_LONG).show();
                    db.OpenDb();
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    trackCount = 0;
                    if (enableAttendanceMust == 1) {
                        if (empTimeIn.equalsIgnoreCase("1")) {


                            activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame, SaleReturnFinal.newInstance(), SaleReturnFinal.TAG).commit();
                        } else {
                            //Toast.makeText(context, "Can't Create Sale Return, Time In first to Create Sale Return", Toast.LENGTH_LONG).show();
                            customToast(AppContextProvider.getContext(), "Can't Create Sale Return, Time In first to Create Sale Return");
                        }
                    } else {
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, SaleReturnFinal.newInstance(), SaleReturnFinal.TAG).commit();
                    }
                    break;

                }

                case R.id.attendanceListBtn: {

                    FRAGMENT_TAG = AttendanceList.TAG;
                    // Toast.makeText(context , "TODO Show Attendance List in Fragment Working ", Toast.LENGTH_LONG).show();
                    trackCount = 0;
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, AttendanceList.newInstance(), AttendanceList.TAG).commit();

                    break;
                }
                case R.id.ProgressreportBtn: {

                    FRAGMENT_TAG = ProgressReport.TAG;

                    trackCount = 0;
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, ProgressReport.newInstance(), ProgressReport.TAG).commit();

                    break;
                }
                case R.id.QtyFormBtn: {

                    FRAGMENT_TAG = QtyFormFinal.TAG;

                    trackCount = 0;
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, QtyFormFinal.newInstance(), QtyFormFinal.TAG).commit();

                    break;
                }
                case R.id.SampleRequestForm: {

                    FRAGMENT_TAG = QtyFormFinal2.TAG;

                    trackCount = 0;
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, QtyFormFinal2.newInstance(), QtyFormFinal2.TAG).commit();

                    break;
                }
                case R.id.HospitalSurveyBtn: {

                    FRAGMENT_TAG = HospitalSurveyFinal.TAG;

                    trackCount = 0;
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, HospitalSurveyFinal.newInstance(), HospitalSurveyFinal.TAG).commit();

                    break;
                }
            }

        }

        private void callApi(double latitude, double longitude) {

            String timeInVals = "", city, area;

            String timeIn = "0";

            if (timeInBtn.getText().toString().equalsIgnoreCase("TIME IN")) {
                timeIn = "1";
            } else if (timeInBtn.getText().toString().equalsIgnoreCase("TIME OUT")) {
                timeIn = "0";
            }
            if (CityArea[0] == null) {
                city = "N/A";
            } else {
                city = CityArea[0];
            }
            if (CityArea[1] == null) {
                area = "N/A";
            } else {
                area = CityArea[1];
            }

            timeInVals += GetValues2(timeIn, String.valueOf(latitude), String.valueOf(longitude), /*CityArea[0].toString()*/city, /*CityArea[1].toString()*/area);
            Log.d("TimeInVals ", timeInVals);

            SyncTime(loader, empId, timeInVals);
        }

        public void SyncTime(final Loader ldr, final String empID, final String timeInVals) {

            if (getActivity() != null)
                ldr.showDialog(getActivity());
            
        /*    RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
                    .build(); //Finally building the adapter
            
            RetrofitWebService api = adapter.create(RetrofitWebService.class);*/

            final OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);

            RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint(BuildConfig.BASE_URL).setClient(new OkClient(okHttpClient)) //Setting the Root URL
                    .build(); //Finally building the adapter

            RetrofitWebService api = adapter.create(RetrofitWebService.class);

            api.sync_everythingTimeIn(

                    empID,
                    timeInVals,

                    //Creating an anonymous callback
                    new Callback<Response>() {
                        @Override
                        public void success(Response result, Response response) {

                            //On success we will read the server's output using bufferedreader
                            //Creating a bufferedreader object

                            BufferedReader reader;

                            //An string to store output from the server
                            String output;
                            InputStream is = null;
                            try {
                                if (result != null && !result.equals(null) && result.getBody() != null && !result.getBody().equals(null) && result.getBody().in() != null && !result.getBody().in().equals(null)) {
                                    is = result.getBody().in();
                                } else {
                                    ldr.HideLoader();
                                    Toast.makeText(AppContextProvider.getContext(), "Please Sync again", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                ldr.HideLoader();

                                Toast.makeText(AppContextProvider.getContext(), "Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                            }

                            if (result != null && !result.equals(null) && result.getBody() != null && !result.getBody().equals(null) && is != null && !is.equals(null)) {
                                try {
                                    //Initializing buffered reader
                                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                                    //Reading the output in the string
                                    output = reader.readLine();

                                    JSONObject json = new JSONObject(output);

                                    Log.d("Prof", "OUT");

                                    if (json.getString(KEY_SUCCESS) != null) {

                                        String res = json.getString(KEY_SUCCESS);

                                        Log.d("Prof", "res Val " + res);

                                        if (Integer.parseInt(res) == 1) {

                                            String timeIn = json.getString("time_in");
                                            String timeOut = json.getString("time_out");
                                            String timeInTime = json.getString("timein_time");


                                            if (timeIn.equalsIgnoreCase("1")) {

                                                db.updateMobUserTimeIn(empID, timeIn);
                                                db.updateMobUserTimeInTIME(empID, timeInTime);
                                                timeInBtn.setText((convertDate(db.getMobEmpTimeInTime()) + "\n TIME OUT"));
                                                if (getActivity() != null && isAdded()) {
                                                    timeInBtn.setBackground(getResources().getDrawable(R.drawable.ripple_green));

                                                    SyncTimeSheet(ldr, empID);

                                                }


                                            } else if (timeOut.equalsIgnoreCase("1")) {

                                                db.updateMobUserTimeIn(empID, "0");

                                                timeInBtn.setText(("TIME IN"));
                                                if (getActivity() != null && isAdded()) {
                                                    timeInBtn.setBackground(getActivity().getResources().getDrawable(R.drawable.ripple));

                                                    SyncTimeSheet(ldr, empID);


                                                }

                                            } else {

                                                Toast.makeText(AppContextProvider.getContext(), "You are Not at Attendance Location", Toast.LENGTH_SHORT).show();
                                                ldr.HideLoader();
                                            }


                                            //Toast.makeText( getActivity() , "Data Synced", Toast.LENGTH_SHORT).show();

                                            // timeInBtn.setText("Clock Out");

                                        /*getActivity().finish();
                                        getActivity().startActivity(new Intent(getActivity(), MainActivity.class));*/


                                        } else if (Integer.parseInt(res) == 0) {
                                            ldr.HideLoader();
                                            Toast.makeText(AppContextProvider.getContext(), "Res 0.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (IOException e) {

                                    ldr.HideLoader();
                                    Log.d("Prof", "Excep " + e.toString());
                                    Toast.makeText(AppContextProvider.getContext(), "I/O Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();

                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    ldr.HideLoader();

                                    Log.d("Prof", "Excep JSOn " + e.toString());
                                    Toast.makeText(AppContextProvider.getContext(), "Json Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();

                                    e.printStackTrace();
                                }
                            } else {
                                ldr.HideLoader();
                                Toast.makeText(AppContextProvider.getContext(), "Please Sync again", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            ldr.HideLoader();

                            Toast.makeText(AppContextProvider.getContext(), "Kindly Check Your Internet Connection", Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }

        public void SyncTimeSheet(final Loader ldr, final String empID) {


            final OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);

            RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint(BuildConfig.BASE_URL).setClient(new OkClient(okHttpClient)) //Setting the Root URL
                    .build(); //Finally building the adapter

            RetrofitWebService api = adapter.create(RetrofitWebService.class);

            api.syncTimeSheet(

                    empID,


                    //Creating an anonymous callback
                    new Callback<Response>() {
                        @Override
                        public void success(Response result, Response response) {
                            //On success we will read the server's output using bufferedreader
                            //Creating a bufferedreader object

                            BufferedReader reader;

                            //An string to store output from the server
                            String output;

                            try {
                                //Initializing buffered reader
                                if (result == null || result.equals(null)) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            ldr.HideLoader();
                                            Toast.makeText(getActivity(), "Something went wrong...\n Sync again",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Log.e("Check", "OUT result");
                                    return;
                                }
                                if (result.getBody() == null || result.getBody().equals(null)) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            ldr.HideLoader();
                                            Toast.makeText(getActivity(), "Something went wrong...\n Sync again",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Log.e("Check", "OUT result.getBody()");
                                    return;
                                }
                                if (result.getBody().in() == null || result.getBody().in().equals(null)) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            ldr.HideLoader();
                                            Toast.makeText(getActivity(), "Something went wrong...\n Sync again",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Log.e("Check", "OUT result.getBody().in()");
                                    return;
                                }
                                reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                                //Reading the output in the string
                                output = reader.readLine();

                                JSONObject json = new JSONObject(output);

                                Log.d("Prof", "OUT");

                                if (json.getString(KEY_SUCCESS) != null) {

                                    String res = json.getString(KEY_SUCCESS);

                                    Log.d("Prof", "res Val " + res);

                                    if (Integer.parseInt(res) == 1) {

                                        JSONArray ValuesClockIn = json.getJSONArray("values_clockin");

                                        db.OpenDb();
                                        db.deleteClockInTime();

                                        JSONObject clockIn;
                                        for (int i = 0; i < ValuesClockIn.length(); i++) {

                                            clockIn = ValuesClockIn.getJSONObject(i);

                                            String clockin, clockout;

                                            clockin = clockIn.getString("clock_in");
                                            clockout = clockIn.getString("clock_out");

                                            if (clockin.equalsIgnoreCase("null")) {
                                                clockin = "0";
                                            }

                                            if (clockout.equalsIgnoreCase("null")) {
                                                clockout = "0";
                                            }
                                            long idClInTime = db.createClockInTime(clockIn.getString("iid"), clockin, clockout);


                                        }
                                        db.CloseDb();
                                        ldr.HideLoader();


                                       /* activity.finish();

                                        activity.startActivity(new Intent(getActivity(), MainActivity.class));*/


                                    } else if (Integer.parseInt(res) == 0) {
                                        ldr.HideLoader();
                                        Toast.makeText(AppContextProvider.getContext(), "Res 0.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (IOException e) {

                                ldr.HideLoader();
                                Log.d("Prof", "Excep " + e.toString());
                                Toast.makeText(AppContextProvider.getContext(), "I/O Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();

                                e.printStackTrace();
                            } catch (JSONException e) {
                                ldr.HideLoader();

                                Log.d("Prof", "Excep JSOn " + e.toString());
                                Toast.makeText(AppContextProvider.getContext(), "Json Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();

                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void failure(RetrofitError error) {

                            ldr.HideLoader();

                            Toast.makeText(AppContextProvider.getContext(), "Kindly Check Your Internet Connection", Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

        }


        @Override
        public void onResume() {
            super.onResume();
            loc_statusOn = true;
            loc_statusOff = true;
            net_statusOn = true;
            net_statusOff = true;


            // Log.e("ONONON", "onResume in Fragment");
            final CheckNetworkInBack checkNetworkInBack = new CheckNetworkInBack();
            timer = new Timer();
            timer.scheduleAtFixedRate(new java.util.TimerTask() {
                @Override
                public void run() {
                    if (getActivity() != null) {

                        checkNetworkInBack.doInBackground();

                    }
                }
            }, 0, 10000);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Log.e("ONONON", "onPause in Fragment");
            timer.cancel();
        }


        @Override
        public void onDestroy() {
            super.onDestroy();


        }

        private String getDateTimeSHORT() {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            DateFormat df = DateFormat.getDateTimeInstance();

            //SelectedDate = dateFormat.format(new Date());

            return dateFormat.format(new Date());
            //return df.format(new Date());
        }
    }

    private static class CheckNetworkInBack extends AsyncTask<Boolean, Boolean, Boolean> {
        private boolean abc;

        @Override
        protected Boolean doInBackground(Boolean... booleans) {

            abc = isInternetAvailable2();
            if (handler != null)
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Log.e("MyThreadRun", "RUN ");
                        if (abc) {
                            network_status.setText(("Internet Connected"));
                            if (network_status != null) {

                                if (net_statusOn) {
                                    network_layout.setBackgroundColor(resources.getColor(R.color.green_back));
                                    network_status.setText(("Internet Connected"));
                                    network_status.setAnimation(as);
                                    net_statusOn = false;
                                    net_statusOff = true;
                                }

                            }

                        } else {
                            if (network_status != null) {


                                if (net_statusOff) {
                                    network_layout.setBackgroundColor(resources.getColor(R.color.loc_net_bg));

                                    network_status.setText(("No Internet Access"));
                                    network_status.setAnimation(as);
                                    net_statusOn = true;
                                    net_statusOff = false;
                                }
                            }


                        }

                    }

                }, 50);
            return null;
        }
    }

    private class Sync extends AsyncTask<String, String, String> {

        private String message = "";

        @Override
        protected String doInBackground(String... strings) {


            if (strings[0].equals("0")) {
                Log.d("empID", strings[1]);

                message = new SyncData(getActivity()).SyncData2(getActivity(), db, strings[1]);


            } else if (strings[0].equals("1")) {
                message = new SyncData(getActivity()).SyncData3(getActivity(), db, strings[1],
                        "Get Settings" + " " + "Data");
            }


            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


        }
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {


        }


        boolean checkInternet(Context context) {
            ServiceManager serviceManager = new ServiceManager(context);
            return serviceManager.isNetworkAvailable();
        }

    }

    class ServiceManager extends ContextWrapper {

        ServiceManager(Context base) {
            super(base);
        }

        boolean isNetworkAvailable() {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo;
            if (cm != null) {
                networkInfo = cm.getActiveNetworkInfo();

                if (networkInfo != null) {
                    return networkInfo.isConnectedOrConnecting();
                }
            }
            return false;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = new DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
            args.putInt(DummySectionFragment.pos1, position + 1);
            pos = position;

            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {


            if (enableAdvanceDashboard == 1) {
                // Show 2 total pages.


                return 3;
            }
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            if (enableAdvanceDashboard == 1) {

                switch (position) {
                /*case 0:
                    return "Dashboard".toUpperCase();*/
                    case 0:

                        return "Dashboard".toUpperCase();
                /*case 1:
                    return "Summary".toUpperCase();*/
                    case 1:

                        return "Summary".toUpperCase();
                    case 2:

                        return "Route Plan".toUpperCase();

                }
            } else {
                /*case 1:
                    return "Summary".toUpperCase();*/
                if (position == 0) {
                    return "Summary".toUpperCase();
                }
            }
            return null;
        }
    }
}
