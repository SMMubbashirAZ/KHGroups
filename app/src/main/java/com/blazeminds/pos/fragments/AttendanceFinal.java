package com.blazeminds.pos.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.AttendanceListAdapter;
import com.blazeminds.pos.AttendanceListHeaderAdapter;
import com.blazeminds.pos.BuildConfig;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.MainActivity;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.resources.Loader;
import com.blazeminds.pos.webservice_url.RetrofitWebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.blazeminds.pos.Constant.MIN_CLICK_INTERVAL;
import static com.blazeminds.pos.Constant.checkLocationPermission;
import static com.blazeminds.pos.Constant.customToast;
import static com.blazeminds.pos.Constant.getCityAreaCountryFromLatitudeLongitude;
import static com.blazeminds.pos.MainActivity.granted;
import static com.blazeminds.pos.MainActivity.requestPermission;
import static com.blazeminds.pos.webservice_url.Url_Links.KEY_SUCCESS;

/**
 * Created by Blazeminds on 3/8/2018.
 */

public class AttendanceFinal extends Fragment {
    
    public final static String TAG = AttendanceFinal.class.getSimpleName();
    
    static Button timeInBtn;
    static TextView locStatusTxt, timeInTime;
    static TableRow timeInTimeRow;
    static Loader loader;
    static String[] CityArea;
    static PosDB db;
    static String empId;
    
    static ArrayList<HashMap<String, String>> list;
    static ArrayList<HashMap<String, String>> data;
    static ArrayList<String> dataCustId;
    static ArrayList<HashMap<String, String>> Hlist;
    static android.widget.ListView lview, hList, ListView;
    static TextView openingBalance, totalAmountTxt, noItemTxt, oldReceivable;
    
    static Context c;
    static long lastClickTime = 0;
    
    
    // New Work Start Here
    
    static int pos = 0;
    
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    AttendanceFinal.SectionsPagerAdapter mSectionsPagerAdapter;
    
    // public static final String TAG = CustomerList.class.getSimpleName();
    
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    
    
    public static AttendanceFinal newInstance() {
        return new AttendanceFinal();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_item_one, container, false);
        mSectionsPagerAdapter = new AttendanceFinal.SectionsPagerAdapter(
                getChildFragmentManager());
        //locStatusTV = (TextView) v.findViewById(R.id.locStatusTextView);
        //locStatusTV = (TextView) v.findViewById(R.id.locStatusTextView);
        mViewPager = v.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        
        c = getActivity();
        
        
        return v;
    }
    
    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    @SuppressLint("ValidFragment")
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";
        public static final String pos1 = "";
        // My GPS states
        public static final int GPS_PROVIDER_DISABLED = 1;
        public static final int GPS_GETTING_COORDINATES = 2;
        public static final int GPS_GOT_COORDINATES = 3;
        //public static final int GPS_PROVIDER_UNAVIALABLE = 4;
        //public static final int GPS_PROVIDER_OUT_OF_SERVICE = 5;
        public static final int GPS_PAUSE_SCANNING = 6;
        public static final int GPS_PROVIDER_DOESNOT_EXIST = 4;
        private static String coordsToSend;
        private static String gAccuracy;
        private static double Latitude, Longitude;
        private static double LastKnownLatitudeGPS;
        private static double LastKnownLongitudeGPS;
        private static double LastKnownLatitudeNet;
        private static double LastKnownLongitudeNet;
        
        // Location manager
        private static LocationManager manager;
        private static Location lastKnownLocationGPS;
        private static Location lastKnownLocationNetwork;
        // Location events (we use GPS only)
        
        private static boolean isOne = true;
        private LocationListener locListener = new LocationListener() {
            
            public void onLocationChanged(Location argLocation) {
                printLocation(argLocation, GPS_GOT_COORDINATES);
            }
            
            @Override
            public void onProviderDisabled(String arg0) {
                Log.e("CheckonProviderDisabled", "Run");
                if (isOne) {
                    final String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    
                    final String message = "Set your gps location to high accuracy";
                    
                    builder.setMessage(message)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int id) {
                                            startActivityForResult(new Intent(action), 1111);
                                            d.dismiss();
                                            
                                        }
                                    });
                    
                    builder.setCancelable(false);
                    
                    builder.create().show();
                    
                    printLocation(null, GPS_PROVIDER_DISABLED);
                    
                    isOne = false;
                }
                
            }
            
            @Override
            public void onProviderEnabled(String arg0) {
            
            }
            
            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            }
            
        };
        
        public DummySectionFragment() {
        }
        
        private static ArrayList<HashMap<String, String>> populateListOfAttendance(TextView noItemText) {
            
            data = new ArrayList<>();
            //dataCustId = new ArrayList<String>();
            //dataCustId.clear();
            data.clear();
            
            PosDB db = PosDB.getInstance(c);
            
            db.OpenDb();
            
            ///dataCustId = db.getSelectedCustomerID();
            data = db.getClockInTimeList();
            
            db.CloseDb();
            
            if (data.size() > 0) {
                noItemText.setVisibility(View.GONE);
                ListView.setVisibility(View.VISIBLE);
                AttendanceListAdapter adapter1 = new AttendanceListAdapter(c, data);
                ListView.setAdapter(adapter1);
                
            } else {
                //Toast.makeText(c, "No Data", Toast.LENGTH_SHORT).show();
                ListView.setVisibility(View.GONE);
                noItemText.setVisibility(View.VISIBLE);
            }
            
            
            return data;
            
        }
        
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.e("onActivityForResult", requestCode + " : " + requestCode);
            isOne = true;
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_attendance,
                    container, false);
/*
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
                    ARG_SECTION_NUMBER)));
*/
            
            //locStatusTV = (TextView) rootView.findViewById(R.id.locStatusTextView);
            int pos2 = Integer.parseInt(Integer.toString(getArguments().getInt(
                    pos1)));
            
            
            try {
                switch (pos2) {
                    
                    case 1: {
                        
                        LinearLayout paymentRecieving = rootView.findViewById(R.id.attendanceLayout);
                        paymentRecieving.setVisibility(View.VISIBLE);
                        
                        Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
                        
                        paymentRecieving.startAnimation(enter);
                        
                        db = PosDB.getInstance(c);
                        
                        initUI(rootView);
                        
                        timeInBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                
                                requestPermission(getActivity());
                                if (!granted) {
                                    return;
                                }
                                
                                long currentClickTime = SystemClock.uptimeMillis();
                                long elapsedTime = currentClickTime - lastClickTime;
                                
                                lastClickTime = currentClickTime;
                                if (elapsedTime <= MIN_CLICK_INTERVAL) {
                                    Log.d("Time_In", "Returned");
                                    return;
                                }
                                if (checkLocationPermission(getActivity())/*c.checkCallingOrSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && c.checkCallingOrSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED*/) {
                                    
                                    new Handler().postDelayed(new Runnable() {
                                        
                                        @Override
                                        public void run() {
                                            
                                            
                                            if (Constant.networkAvailable()) {
                                                
                                                
                                                if (Latitude != 0 && Longitude != 0) {
                                                    
                                                    if (getActivity() != null) {
                                                        CityArea = getCityAreaCountryFromLatitudeLongitude(Latitude, Longitude, getActivity());
                                                        callApi(Latitude, Longitude);
                                                    }
                                                    
                                                } else {
                                                    
                                                    if (lastKnownLocationGPS != null) {
                                                        
                                                        LastKnownLatitudeGPS = lastKnownLocationGPS.getLatitude();
                                                        LastKnownLongitudeGPS = lastKnownLocationGPS.getLongitude();
                                                        if (LastKnownLatitudeGPS != 0 && LastKnownLongitudeGPS != 0) {
                                                            
                                                            if (getActivity() != null) {
                                                                CityArea = getCityAreaCountryFromLatitudeLongitude(LastKnownLatitudeGPS, LastKnownLongitudeGPS, getActivity());
                                                                
                                                                callApi(LastKnownLatitudeGPS, LastKnownLongitudeGPS);
                                                                
                                                            }
                                                        }
                                                        
                                                        
                                                    } else if (lastKnownLocationNetwork != null) {
                                                        
                                                        LastKnownLatitudeNet = lastKnownLocationNetwork.getLatitude();
                                                        LastKnownLongitudeNet = lastKnownLocationNetwork.getLongitude();
                                                        if (LastKnownLatitudeNet != 0 && LastKnownLongitudeNet != 0) {
                                                            
                                                            if (getActivity() != null) {
                                                                CityArea = getCityAreaCountryFromLatitudeLongitude(LastKnownLatitudeNet, LastKnownLongitudeNet, getActivity());
                                                                
                                                                callApi(LastKnownLatitudeNet, LastKnownLongitudeNet);
                                                                
                                                            }
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
                                    }, 100);
                                    
                                } else {
                                    // loader.HideLoader();
                                    Toast.makeText(AppContextProvider.getContext(), "Location Permission required", Toast.LENGTH_SHORT).show();
                                }
                                
                            }
                        });
                        
                    }
                    break;
                    
                    case 2: {
                        
                        LinearLayout paymentRecievingList = rootView.findViewById(R.id.ShowAttendanceList);
                        paymentRecievingList.setVisibility(View.VISIBLE);
                        paymentRecievingList.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_right));
                        ListView = rootView.findViewById(R.id.listviewRList);
                        ListView hList = rootView.findViewById(R.id.Hlist);
                        noItemTxt = rootView.findViewById(R.id.NoItemTxt);
                        Hlist = new ArrayList<>();
                        Hlist.clear();
                        
                        HashMap<String,String> temp = new HashMap<>();
                        //temp.put("exp_amount", "EX_AMT");
                        temp.put("id", "DATE");
                        temp.put("clock_in", "CLOCK IN");
                        temp.put("clock_out", "CLOCK OUT");
                        //temp.put(FOURTH_COLUMN, "Contact");
                        Hlist.add(temp);
                        
                        
                        AttendanceListHeaderAdapter adapter = new AttendanceListHeaderAdapter(c, Hlist);
                        hList.setAdapter(adapter);
                        
                        populateListOfAttendance(noItemTxt);
                        
                        
                    }
                    break;
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.fillInStackTrace());
                
            }
            
            return rootView;
        }
        
        private void initUI(View rootView) {
            
            timeInTimeRow = rootView.findViewById(R.id.timeInTimeRow);
            timeInTime = rootView.findViewById(R.id.timeInTime);
            timeInBtn = rootView.findViewById(R.id.SubmitBtn);
            locStatusTxt = rootView.findViewById(R.id.locStatusTxt);
            
            loader = new Loader();
            manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            startLocation();
            
            CityArea = new String[3];
            
            db.OpenDb();
            empId = db.getMobEmpId();
            String timeIn = db.getMobEmpTimeIn();
            
            
            if (db.getMobEmpTimeIn().equalsIgnoreCase("1")) {
                timeInTime.setText("TIME IN TIME : " + convertDate(db.getMobEmpTimeInTime()));
                timeInTimeRow.setVisibility(View.VISIBLE);
                timeInBtn.setText("TIME OUT");
            } else if (db.getMobEmpTimeIn().equalsIgnoreCase("0")) {
                timeInTimeRow.setVisibility(View.GONE);
                timeInBtn.setText("TIME IN");
            }
            db.CloseDb();
            Log.d("EmpId", empId);
            
            
        }
        
        public String convertDate(String dbDate) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            //DateFormat dfProper = new SimpleDateFormat("dd-MMM-yy\nK:mm a");
            DateFormat dfProper = new SimpleDateFormat("d MMM yyyy, hh:mm a");
            Date dt = null;
            try {
                dt = df.parse(dbDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
            return dfProper.format(dt);
        }
        
        private String GetValues2(String... vals) {
            
            String result = "";
    
            for (String val : vals) {
        
                result = result + val + "@@";

            /*if( i == vals.length - 1 ){
				result = result + vals[i] + "##";
            }*/
        
            }
            return result;
            
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
            
            //timeInVals += String.valueOf(timeInBtn.getTag()) +""+ String.valueOf(Latitude)+""+ String.valueOf(Longitude)+""+ CityArea[0].toString()+""+ CityArea[1].toString() ;
            timeInVals += GetValues2(timeIn, String.valueOf(latitude), String.valueOf(longitude), /*CityArea[0].toString()*/city, /*CityArea[1].toString()*/area);
            Log.d("TimeInVals ", timeInVals);
            
            SyncMerchandizing(loader, empId, timeInVals);
        }
        
        public void SyncMerchandizing(final Loader loader, final String empID, final String timeInVals) {
            
            if (getActivity() != null)
                loader.showDialog(getActivity());
            
            RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
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
                                            
                                            SyncTimeSheet(loader, empID);
                                            timeInBtn.setText("TIME OUT");
                                            if (getActivity() != null) {
                                                getActivity().finish();
                                                getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                                            }
											/*getActivity().runOnUiThread(new Runnable() {
												@Override
                                                public void run() {
                                                    new SyncData(getActivity(),  db );
                                                }
                                            });*/
                                            
                                        } else if (timeOut.equalsIgnoreCase("1")) {
                                            
                                            db.updateMobUserTimeIn(empID, "0");
                                            
                                            timeInBtn.setText("TIME IN");
                                            SyncTimeSheet(loader, empID);
                                            if (getActivity() != null) {
                                                getActivity().finish();
                                                getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                                            }
											/*getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new SyncData(getActivity(),  db );
                                                }
                                            });*/
                                        } else {
                                            Toast.makeText(AppContextProvider.getContext(), "You are Not at Attendance Location", Toast.LENGTH_SHORT).show();
                                            
                                        }
                                        
                                        
                                        loader.HideLoader();
                                        //Toast.makeText( getActivity() , "Data Synced", Toast.LENGTH_SHORT).show();
                                        
                                        // timeInBtn.setText("Clock Out");

                                        /*getActivity().finish();
                                        getActivity().startActivity(new Intent(getActivity(), MainActivity.class));*/
                                        
                                        
                                    } else if (Integer.parseInt(res) == 0) {
                                        loader.HideLoader();
                                        Toast.makeText(AppContextProvider.getContext(), "Res 0.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (IOException e) {
                                
                                loader.HideLoader();
                                Log.d("Prof", "Excep " + e.toString());
                                Toast.makeText(AppContextProvider.getContext(), "I/O Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                
                                e.printStackTrace();
                            } catch (JSONException e) {
                                loader.HideLoader();
                                
                                Log.d("Prof", "Excep JSOn " + e.toString());
                                Toast.makeText(AppContextProvider.getContext(), "Json Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                
                                e.printStackTrace();
                            }
                            
                            
                        }
                        
                        @Override
                        public void failure(RetrofitError error) {
                            
                            loader.HideLoader();
                            Toast.makeText(AppContextProvider.getContext(), "Kindly Check Your Internet Connection", Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }
        
        public void SyncTimeSheet(final Loader ldr, final String empID) {
            
            
            RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
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
        
        private void printLocation(Location loc, int state) {
            
            String result = "";
            switch (state) {
                case GPS_PROVIDER_DOESNOT_EXIST:
                    result = "GPS Doesn't Exist";
                    if (locStatusTxt != null)
                        locStatusTxt.setTextColor(Color.RED);
                    break;
                case GPS_PROVIDER_DISABLED:
                    result = "GPS Disabled";
                    if (locStatusTxt != null)
                        locStatusTxt.setTextColor(Color.RED);
                    break;
                case GPS_GETTING_COORDINATES:
                    
                    result = "Fetching Coordinates";
                    if (locStatusTxt != null)
                        locStatusTxt.setTextColor(Color.BLACK);
                    
                    break;
                case GPS_PAUSE_SCANNING:
                    result = "GPS Scanning Paused";
                    if (locStatusTxt != null)
                        locStatusTxt.setTextColor(Color.RED);
                    
                    break;
                case GPS_GOT_COORDINATES:
                    if (loc != null) {
                        if (locStatusTxt != null)
                            locStatusTxt.setTextColor(Color.BLACK);
                        
                        //locStatusTxt.setTextColor(getColor(fragmentActivity, R.color.black));
                        // Accuracy
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
                        
                        coordsToSend = la + "," + lo;
                        
                        Latitude = loc.getLatitude();
                        Longitude = loc.getLongitude();
                        result = coordsToSend;
                        
                        
                    } else {
                        result = "GPS_UNAVAILABLE";
                        if (locStatusTxt != null)
                            locStatusTxt.setTextColor(Color.RED);
                        
                    }
                    break;
            }
            if (locStatusTxt != null)
                locStatusTxt.setText(result);
            
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
                if (manager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
                    
                    lastKnownLocationNetwork = manager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    
                    if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        printLocation(null, GPS_GETTING_COORDINATES);
                    }
                } else {
                    Toast.makeText(AppContextProvider.getContext(), "Provider Doesn't Exist", Toast.LENGTH_SHORT).show();
                    //printLocation(null, GPS_PROVIDER_DOESNOT_EXIST);
                }
                if (manager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
                    
                    lastKnownLocationGPS = manager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    
                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        printLocation(null, GPS_GETTING_COORDINATES);
                    }
                } else {
                    Toast.makeText(AppContextProvider.getContext(), "GPS Doesn't Exist", Toast.LENGTH_SHORT).show();
                    printLocation(null, GPS_PROVIDER_DOESNOT_EXIST);
                    
                }
            } catch (SecurityException ignored) {
            }
        }
        
        @Override
        public void onPause() {
            super.onPause();
            stopLocation();
        }
        
        @Override
        public void onResume() {
            super.onResume();
            
            if (manager == null) {
                manager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
            }
            startLocation();

        /*Toast.makeText(getActivity(), "Latitude = "+Latitude+"" , Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "Longitude = "+Longitude+"" , Toast.LENGTH_SHORT).show();*/
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
            Fragment fragment = new AttendanceFinal.DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(AttendanceFinal.DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
            args.putInt(AttendanceFinal.DummySectionFragment.pos1, position + 1);
            pos = position;
            fragment.setArguments(args);
            return fragment;
        }
        
        @Override
        public int getCount() {
            // Show 2 total pages (tabs).
            return 2;
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Attendance".toUpperCase();
                case 1:
                    return "Attendance List".toUpperCase();
/*
			case 2:
                return "Shop gps".toUpperCase();
*/
            
            }
            return null;
        }
    }
}
