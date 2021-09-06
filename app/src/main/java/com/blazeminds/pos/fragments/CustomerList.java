package com.blazeminds.pos.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.ListViewAdapter4Cust;
import com.blazeminds.pos.ListViewAdapter4CustHeader;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.adapter.FilterWithSpaceAdapter;
import com.blazeminds.pos.autocomplete_resource.MyObject;
import com.blazeminds.pos.resources.Loader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.blazeminds.pos.Constant.CODE_COLUMN;
import static com.blazeminds.pos.Constant.FIRST_COLUMN;
import static com.blazeminds.pos.Constant.SECOND_COLUMN;
import static com.blazeminds.pos.Constant.THIRD_COLUMN;
import static com.blazeminds.pos.Constant.checkLocationPermission;
import static com.blazeminds.pos.Constant.getCityAreaCountryFromLatitudeLongitude;
import static com.blazeminds.pos.MainActivity.granted;
import static com.blazeminds.pos.MainActivity.requestPermission;
import static com.blazeminds.pos.fragments.DashBoard.db;

public class CustomerList extends Fragment {
    
    public static final String TAG = CustomerList.class.getSimpleName();
    // My GPS states
    public static final int GPS_PROVIDER_DISABLED = 1;
    public static final int GPS_GETTING_COORDINATES = 2;
    public static final int GPS_GOT_COORDINATES = 3;
    //public static final int GPS_PROVIDER_UNAVIALABLE = 4;
    //public static final int GPS_PROVIDER_OUT_OF_SERVICE = 5;
    public static final int GPS_PAUSE_SCANNING = 6;
    public static final int GPS_PROVIDER_DOESNOT_EXIST = 4;
    static public FilterWithSpaceAdapter<String> myAdapterCustomer,CustomerCategoryAdapter,CustomerSubCategoryAdapter;
    static public String[] itemCustomer = new String[]{"Please search..."};
    static ArrayList<String> dataCustId;
    static ArrayList<HashMap<String, String>> data;
    static Context c;
    static ArrayList<HashMap<String, String>> list = new ArrayList<>();
    static ArrayList<HashMap<String, String>> listGPS = new ArrayList<>();
    static ArrayList<HashMap<String, String>> Hlist = new ArrayList<>();
    static ListView lview;
    static ListView lviewVerifiedShops;
    static ListView ListGPS;
    static long selectedRouteDropDown;
    static long selectedCustomerDropDown;
    static long selectedTaxTypeDropDown;
    static long selectedRouteDrop;
    static long selectedCustomerDrop;
    static String selectedCustomerCategory;
    static TextView GPSLocationTxt, noItemText, noItemTextVerifiedShops;
    static Spinner Radius;
    /*
       public static Context getAppContext(){

           return c = getContext();
        }
    */
    static int SelectedRadius, enableShopLocation;
    static AutoCompleteTextView CustomerTxt;
    static String SelectedCustomerId = "0";
    static boolean selectedCustomerItem;
    static int pos = 0;
    private static String selectedShopCatDropDown = "0";
    private static String selectedSubShopCatDropDown = "0";
    private static int anim = -1;
    private static TextView locStatusTxt;
    private static TextView locStatusTV;
    private static String coordsToSend;
    private static String gAccuracy;
    private static double Latitude;
    private static double Longitude;
    private static boolean isOne = true;
    // Location manager
    
    final double radius = 0;
    String MapName = "";
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
    // Location events (we use GPS only)
    
    
    private static ArrayList<HashMap<String, String>> populateList(TextView noItemText) {
        
        data = new ArrayList<>();
        dataCustId = new ArrayList<>();
        dataCustId.clear();
        data.clear();
        
        PosDB db = PosDB.getInstance(c);
        
        db.OpenDb();
        int savedRoute = db.getSavedRouteID();
        if (savedRoute == 0) {
            dataCustId = db.getSelectedCustomerId();
            data = db.getCustomerList();
        } else {
            dataCustId = db.getSelectedCustomerIdByRoute(savedRoute);
            data = db.getCustomerListByRoute(savedRoute);
            
        }
        db.CloseDb();
        
        if (data.size() > 0) {
            noItemText.setVisibility(View.GONE);
            ListViewAdapter4Cust adapter1 = new ListViewAdapter4Cust(c, data);
            lview.setAdapter(adapter1);
            
        } else {
            noItemText.setVisibility(View.VISIBLE);
            //Toast.makeText(c, "No Data", Toast.LENGTH_SHORT).show();
        }
        
        
        return data;
        
    }
    
    
    public static void CustomerDialog(final String CustId) {
        
        final Dialog dialog;
        dialog = new Dialog(c);
        
        
        dialog.setContentView(R.layout.popup_customer);
        
        dialog.setTitle("Shop Details");

//        final TextView Code =(TextView)dialog.findViewById(R.id.Code);
        final EditText Name = dialog.findViewById(R.id.Name);
        final EditText CompName = dialog.findViewById(R.id.Comp);
        final EditText Email = dialog.findViewById(R.id.Email);
        final EditText Add = dialog.findViewById(R.id.Add);
        final EditText Cell = dialog.findViewById(R.id.Cell);
        final EditText P1 = dialog.findViewById(R.id.phone1);
        final EditText P2 = dialog.findViewById(R.id.phone2);
        final EditText OpeningBal = dialog.findViewById(R.id.obalTV);
        final EditText cnic = dialog.findViewById(R.id.cnic);
        final EditText notes = dialog.findViewById(R.id.notes);
        final EditText routeDrop = dialog.findViewById(R.id.routeDrop);
        final EditText customerTypeDrop = dialog.findViewById(R.id.customerTypeDrop);
        final EditText customerTaxTypeDrop = dialog.findViewById(R.id.customerTaxTypeDrop);
        final EditText customerShopCategory = dialog.findViewById(R.id.customerShopCategory);
        
        //final EditText customerSubShopCategory = dialog.findViewById(R.id
        // .customerSubShopCategory);
       
        
        //DropdownSetupForCustType(customerTypeDrop);
        Name.setEnabled(false);
        CompName.setEnabled(false);
        Email.setEnabled(false);
        Add.setEnabled(false);
        Cell.setEnabled(false);
        P1.setEnabled(false);
        P2.setEnabled(false);
        notes.setEnabled(false);
        cnic.setEnabled(false);
        routeDrop.setEnabled(false);
        customerTypeDrop.setEnabled(false);
        customerTaxTypeDrop.setEnabled(false);
        customerShopCategory.setEnabled(false);
      //  customerSubShopCategory.setEnabled(false);
        
        //Button Update = (Button) dialog.findViewById(R.id.Update);
        Button CloseBtn = dialog.findViewById(R.id.CloseDialog);
        // Button SelectBtn = (Button)dialog.findViewById(R.id.SelectQty);

//        Code.setText(CustId);
        
        PosDB db = PosDB.getInstance(c);
        db.OpenDb();
        
        Name.setText(db.getSelectedCustomerName(CustId));
        CompName.setText(db.getSelectedCustomerCompName(CustId));
        Email.setText(db.getSelectedCustomerEmail(CustId));
        Add.setText(db.getSelectedCustomerAdd(CustId));
        Cell.setText(db.getSelectedCustomerCell(CustId));
        P1.setText(db.getSelectedCustomerP1(CustId));
        P2.setText(db.getSelectedCustomerP2(CustId));
        //OpeningBal.setText(db.getSelectedCustomerOpeningBalance(CustId));
        cnic.setText(db.getSelectedCustomerCnic(CustId));
        notes.setText(db.getSelectedCustomerNotes(CustId));
        routeDrop.setText(db.getCustomerRouteWithId(Integer.parseInt(db.getSelectedCustomerRoute(CustId))));
        customerTypeDrop.setText(db.getCustomerTypeWithId(Integer.parseInt(db.getSelectedCustomerType(CustId))));
        customerTaxTypeDrop.setText(db.CheckFilerNonFilerCust(CustId) == 1 ? "Filer" : "Non-Filer");
        customerShopCategory.setText(db.getShopCategoryNameByCustId(CustId));
        //customerSubShopCategory.setText(db.getShopSubCategoryNameByCustId(CustId));
        
        //routeDrop.setSelection(Integer.parseInt(db.getSelectedCustomerRoute(CustId)));
        //customerTypeDrop.setSelection(Integer.parseInt(db.getSelectedCustomerType(CustId)));
        db.CloseDb();
        
        
        CloseBtn.setOnTouchListener(new View.OnTouchListener() {
            
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                
                int action = event.getAction() & MotionEvent.ACTION_MASK;
                
                try {
                    if (action == MotionEvent.ACTION_DOWN) {
                        
                        dialog.dismiss();
                        
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.fillInStackTrace());
                    
                }
                
                return false;
            }
            
        });
        
        
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
        
        
    }
    
    public static void DropdownSetupRoute(Spinner dropDown, String customerId) {
        
        List<String> ItemsData;
        List<String> ItemsID;
        int id;
        
        db.OpenDb();
        id = Integer.parseInt(db.getSelectedCustomerRoute(customerId));
        ItemsData = db.getCustomerRoutesForDrop(id);
        ItemsID = db.getCustomerRoutesIDForDrop(id);
        
        
        ItemsData.add(0, db.getCustomerRouteWithId(Integer.parseInt(db.getSelectedCustomerRoute(customerId))));
        ItemsID.add(0, db.getSelectedCustomerRoute(customerId));
        
        db.CloseDb();
        
        try {
            // Creating adapter for spinner
            
            
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(c,
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
                
                
                selectedRouteDrop = Long.parseLong(finalItemsID.get(i));
				/*db.OpenDb();
				db.updateSavedRoute(finalItemsID.get(i));
                db.CloseDb();*/
                
                Log.d("sql", "route id: " + finalItemsID.get(i));
                
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            
            }
        });
        
        
    }
    
    public static void DropdownSetupCustType(Spinner dropDown, String customerId) {
        
        List<String> ItemsData;
        List<String> ItemsID;
        int id;
        db.OpenDb();
        id = Integer.parseInt(db.getSelectedCustomerType(customerId));
        ItemsData = db.getCustomerTypeForDrop(id);
        ItemsID = db.getCustomerTypeIDForDrop(id);
        
        
        ItemsData.add(0, db.getCustomerTypeWithId(Integer.parseInt(db.getSelectedCustomerType(customerId))));
        ItemsID.add(0, db.getSelectedCustomerType(customerId));
        
        db.CloseDb();
        
        try {
            // Creating adapter for spinner
            
            
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(c,
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
                
                
                selectedCustomerDrop = Long.parseLong(finalItemsID.get(i));
					/*db.OpenDb();
					db.updateSavedRoute(finalItemsID.get(i));
                    db.CloseDb();*/
                
                Log.d("sql", "customer type id: " + finalItemsID.get(i));
                
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            
            }
        });
        
        
    }
    
    public static CustomerList newInstance() {
        return new CustomerList();
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_item_one, container, false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getChildFragmentManager());
        //locStatusTV = (TextView) v.findViewById(R.id.locStatusTextView);
        //locStatusTV = (TextView) v.findViewById(R.id.locStatusTextView);
        mViewPager = v.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        
        
        c = getActivity();
        
        
        return v;
    }
    
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
    
    private void CustomDialogNoInternet() {
        /*
         * Custom Dialog box starts
         */
        
        AlertDialog alert = new AlertDialog.Builder(c).create();
        //alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        
        alert.setTitle("Sorry");
        alert.setMessage("Please Check Your Internet Connection");
        alert.setIcon(R.drawable.not_selected);
        alert.show();
        /*
         * Custom dialog box ends
         */
        
    }
    
    /**
     * Location Works Starts
     **/
    
    
    @Override
    public void onPause() {
        super.onPause();
        //stopLocation();
        
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        
        //startLocation();
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
         Spinner customerShopSubCatDrop;
        private LocationManager manager;
        private LocationListener locationListener = new LocationListener() {
            
            public void onLocationChanged(Location argLocation) {
                Log.e("GetLocationUpdateRun", argLocation.getProvider());
                printLocation(argLocation, GPS_GOT_COORDINATES);
            }
            
            @Override
            public void onProviderDisabled(String arg0) {
                printLocation(null, GPS_PROVIDER_DISABLED);
                if (isOne) {
                    if (getActivity() != null) {
                        final String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        
                        final String message = "Set your gps location to high accuracy";
                        
                        builder.setMessage(message)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface d, int id) {
                                                getActivity().startActivityForResult(new Intent(action), 1111);
                                                d.dismiss();
                                                
                                            }
                                        });
                        
                        builder.setCancelable(false);
                        
                        builder.create().show();
                        
                        
                        isOne = false;
                    }
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
        
        private static String printLocation(Location loc, int state) {
            
            String result = "";
            switch (state) {
                case GPS_PROVIDER_DOESNOT_EXIST:
                    result = "GPS Doesn't Exist";
                    if (locStatusTV != null)
                        locStatusTV.setTextColor(Color.RED);
                    if (locStatusTxt != null)
                        locStatusTxt.setTextColor(Color.RED);
                    break;
                case GPS_PROVIDER_DISABLED:
                    result = "GPS Disabled";
                    if (locStatusTV != null)
                        locStatusTV.setTextColor(Color.RED);
                    if (locStatusTxt != null)
                        locStatusTxt.setTextColor(Color.RED);
                    
                    break;
                case GPS_GETTING_COORDINATES:
                    if (locStatusTV != null)
                        locStatusTV.setTextColor(Color.BLACK);
                    if (locStatusTxt != null)
                        locStatusTxt.setTextColor(Color.BLACK);
                    result = "Fetching Coordinates";
                    
                    break;
                case GPS_PAUSE_SCANNING:
                    result = "GPS Scanning Paused";
                    if (locStatusTV != null)
                        locStatusTV.setTextColor(Color.BLACK);
                    if (locStatusTxt != null)
                        locStatusTxt.setTextColor(Color.BLACK);
                    
                    break;
                case GPS_GOT_COORDINATES:
                    if (loc != null) {
                        if (locStatusTV != null)
                            locStatusTV.setTextColor(Color.BLACK);
                        if (locStatusTxt != null)
                            locStatusTxt.setTextColor(Color.BLACK);
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
                        if (locStatusTV != null)
                            locStatusTV.setTextColor(Color.RED);
                        if (locStatusTxt != null)
                            locStatusTxt.setTextColor(Color.RED);
                        
                        
                    }
                    break;
            }
            if (locStatusTV != null)
                locStatusTV.setText(result);
            if (locStatusTxt != null)
                locStatusTxt.setText(result);
            return result;
            
        }
        
        private void startLocation() {
            // Возобновляем работу с GPS-приемником
            
            try {
                if (manager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, locationListener);
                    
                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //printLocation(null, GPS_GETTING_COORDINATES);
                    }
                } else {
                    Toast.makeText(AppContextProvider.getContext(), "GPS Doesn't Exist", Toast.LENGTH_SHORT).show();
                    printLocation(null, GPS_PROVIDER_DOESNOT_EXIST);
                    
                }
            } catch (SecurityException ignored) {
            }
        }
        
        private void stopLocation() {
            
            try {
                manager.removeUpdates(locationListener);
            } catch (SecurityException ignored) {
            }
        }
        
        @Override
        public void onPause() {
            super.onPause();
            stopLocation();
        }
        
        @Override
        public void onStart() {
            super.onStart();
            manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        }
        
        @Override
        public void onResume() {
            super.onResume();
            
            
        }
        
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            isOne = true;
            Log.e("onProviderDisabled", "Code : " + requestCode);
        }
        String custCatId="0", custSubCatId="0";
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_customer,
                    container, false);
/*
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
                    ARG_SECTION_NUMBER)));
*/
            
            
            //locStatusTV = rootView.findViewById(R.id.locStatusTextView);
            if (manager == null) {
                manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            }
            startLocation();
            final int pos2 = Integer.parseInt(Integer.toString(getArguments().getInt(
                    pos1)));
            Animation animation = AnimationUtils.loadAnimation(c, R.anim.enter_from_left);
            
            if (anim < 0) {
                animation = AnimationUtils.loadAnimation(c, R.anim.enter_from_left);
                anim++;
            } else if (anim > pos2) {
                animation = AnimationUtils.loadAnimation(c, R.anim.enter_from_left);
                anim--;
            } else if (anim < pos2) {
                animation = AnimationUtils.loadAnimation(c, R.anim.enter_from_right);
                anim++;
            }
            
            Log.e("CheckAnimations", pos2 + " : " + anim);
            
            try {
                switch (pos2) {

                  /*
                        SHOW Customers
                 */
                    
                    
                    case 1: {
                        //locStatusTV = (TextView) rootView.findViewById(R.id.locStatusTextView);
                        //locStatusTxt = (TextView) rootView.findViewById(R.id.locStatusTxt);
                        //manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        //startLocation();
                        
                        LinearLayout ShowCustomers = rootView.findViewById(R.id.ShowCustomers);
                        ShowCustomers.setVisibility(View.VISIBLE);
                        
                        
                        // load the animation
                        
                        
                        ShowCustomers.startAnimation(animation);
                        
                        lview = rootView.findViewById(R.id.listview);
                        ListView hList = rootView.findViewById(R.id.Hlist);
                        noItemText = rootView.findViewById(R.id.NoItemTxt);
                        
                        Hlist.clear();
                        
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put(CODE_COLUMN, "Shop Name");
                        temp.put(FIRST_COLUMN, "Name");
                        temp.put(SECOND_COLUMN, "Route");
                        temp.put(THIRD_COLUMN, "Balance");
                        Hlist.add(temp);
                        
                        
                        ListViewAdapter4CustHeader adapter = new ListViewAdapter4CustHeader(c, Hlist);
                        hList.setAdapter(adapter);
                        
                        populateList(noItemText);
                        
                        
                        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                
                                
                                String SelectedCustId = dataCustId.get(position);
                                
                                /*Toast.makeText( c, "ID: "+SelectedCustId, Toast.LENGTH_SHORT ).show();*/
                                
                                CustomerDialog(SelectedCustId);
                                
                                
                            }
                        });


                  /*
                        SHOW Customers Ends
                 */
                    
                    
                    }
                    break;

                  /*
                        ADD Customer Starts
                 */
                    
                    case 2: {
                        
                        //startLocation();
                        
                        RelativeLayout ShowAddCustomers = rootView.findViewById(R.id.ShowAddCustomer);
                        ShowAddCustomers.setVisibility(View.VISIBLE);
                        
                        
                        ShowAddCustomers.setAnimation(animation);
                        locStatusTV = rootView.findViewById(R.id.locStatusTextView);
                        
                        //locStatusTxt = (TextView) rootView.findViewById(R.id.locStatusTxt);
                        
                        // startLocation();
                        final EditText FName = rootView.findViewById(R.id.FName);
                        final EditText LName = rootView.findViewById(R.id.LName);
                        final EditText CompName = rootView.findViewById(R.id.CompName);
                        final EditText CellNo = rootView.findViewById(R.id.CellNo);
                        final EditText P1 = rootView.findViewById(R.id.P1);
                        final EditText P2 = rootView.findViewById(R.id.P2);
                        final EditText Add = rootView.findViewById(R.id.Add);
                        final EditText City = rootView.findViewById(R.id.City);
                        final EditText State = rootView.findViewById(R.id.State);
                        final EditText Country = rootView.findViewById(R.id.Country);
                        final EditText Email = rootView.findViewById(R.id.Email);
                        final EditText Notes = rootView.findViewById(R.id.Notes);
                        final EditText OpeningBalance = rootView.findViewById(R.id.openingBalance);
                        //final EditText cityEdt = (EditText) rootView.findViewById(R.id.CityEdt);
                        //final EditText areaEdt = (EditText) rootView.findViewById(R.id.AreaEdt);
                        final EditText cnicEdt = rootView.findViewById(R.id.cnic);
                        final Spinner RouteDropDown = rootView.findViewById(R.id.SelectTownAutoComplete);
                        final Spinner customerTypeDrop = rootView.findViewById(R.id.cutomerTypeDrop);
                        final Spinner customerTaxTypeDrop = rootView.findViewById(R.id.cutomerTaxTypeDrop);
                        final Spinner customerShopCatDrop = rootView.findViewById(R.id.cutomerShopCatDrop);
                        final TextView ShopCategoryLabel = rootView.findViewById(R.id.ShopCategoryLabel);
                         final TextView SubShopCategoryLabel = rootView.findViewById(R.id.SubShopCategoryLabel);
                        SharedPreferences Category_Labels_sharedPreferences = getActivity().getSharedPreferences(
                                "Category_Labels", MODE_PRIVATE);

                        ShopCategoryLabel.setText(Category_Labels_sharedPreferences.getString("category_label","Select Shop Category"));

                        SubShopCategoryLabel.setText(Category_Labels_sharedPreferences.getString("subcategory_label","Select Shop Sub Category"));

                        customerShopSubCatDrop =rootView.findViewById(R.id.cutomerShopSubCatDrop);
                        final Spinner customerCategoryDrop = rootView.findViewById(R.id.customerCategoryDrop);
                        
                        //locStatusTV.setText(Latitude + " , " + Longitude);
                        Notes.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        Notes.setRawInputType(InputType.TYPE_CLASS_TEXT);
                        showEditTextsAsMandatory(FName, CompName, CellNo, OpeningBalance);
                        DropdownSetupForCustType(customerTypeDrop);
                        DropdownSetupForCustTaxType(customerTaxTypeDrop);
                         DropdownSetupForShopCategory(customerShopCatDrop );
                        
                        DropdownSetupForRoute(RouteDropDown);
                        
                        DropdownSetupForCustCategory(customerCategoryDrop);
                        
                        final Button AddBtn = rootView.findViewById(R.id.AddCustomerBtn);
                        //final Button getLocationBtn = (Button) rootView.findViewById(R.id.getLocationBtn);
                        
                        
                        // Umais new start here
                        
                        AddBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                
                                requestPermission(getActivity());
                                if (!granted) {
                                    return;
                                }
                                
                                enableShopLocation = db.getAppSettingsValueByKey("en_shop_location");
                                
                                if (FName.getText().toString().isEmpty() || CompName.getText().toString().isEmpty() || CellNo.getText().toString().trim().isEmpty() || CellNo.getText().toString().trim().length() != 11 || selectedRouteDropDown == 0 || selectedCustomerDropDown == 0 || selectedTaxTypeDropDown == -1 || selectedShopCatDropDown.equals("0") || selectedSubShopCatDropDown.equals("0") ||OpeningBalance.getText().toString().trim().isEmpty() || locStatusTV.getText().toString().equalsIgnoreCase("GPS Disabled") || (enableShopLocation == 1 && Latitude == 0 && Longitude == 0 && !locStatusTV.getText().toString().equalsIgnoreCase("GPS Disabled"))) {
                                    
                                    if (FName.getText().toString().trim().isEmpty())
                                        FName.setError("First Name Required");
                                    
                                    
                                    if (CompName.getText().toString().trim().isEmpty())
                                        CompName.setError("Shop Name Required");
                                    
                                    if (CellNo.getText().toString().trim().isEmpty() || CellNo.getText().toString().trim().length() != 11) {
                                        CellNo.setError("Valid Mobile Number Required");
                                    }
                                    
                                    if (OpeningBalance.getText().toString().trim().isEmpty()) {
                                        OpeningBalance.setError("Opening Balance Required");
                                    }
                                    
                                    if (selectedRouteDropDown == 0) {
                                        Toast.makeText(AppContextProvider.getContext(), "Town Required", Toast.LENGTH_SHORT).show();
                                    }
                                    
                                    if (selectedCustomerDropDown == 0) {
                                        Toast.makeText(AppContextProvider.getContext(), "Customer Type Required", Toast.LENGTH_SHORT).show();
                                    }
                                    if (selectedTaxTypeDropDown == -1) {
                                        Toast.makeText(AppContextProvider.getContext(), "Tax Type Required", Toast.LENGTH_SHORT).show();
                                    }
                                    
                                    if (selectedShopCatDropDown.equals("0")) {
                                        Toast.makeText(AppContextProvider.getContext(), "Shop " + "Category Required", Toast.LENGTH_SHORT).show();
                                    }
                                    if (selectedSubShopCatDropDown.equals("0")) {
                                        Toast.makeText(AppContextProvider.getContext(),
                                                "Sub Shop " + "Category Required",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    if (locStatusTV.getText().toString().equalsIgnoreCase("GPS Disabled")) {
                                        Toast.makeText(AppContextProvider.getContext(), "Enable your GPS first", Toast.LENGTH_SHORT).show();
                                        
                                    }
                                    
                                    if (enableShopLocation == 1 && Latitude == 0 && Longitude == 0 && !locStatusTV.getText().toString().equalsIgnoreCase("GPS Disabled")) {
                                        Toast.makeText(AppContextProvider.getContext(), "Unable to fetch location\nPlease move to open area", Toast.LENGTH_SHORT).show();
                                    }
                                    
                                    
                                } else {
                                    
                                    AddBtn.setClickable(false);
                                    
                                    boolean valid = checkForDuplicateShops(FName.getText().toString(), CompName.getText().toString());
                                    
                                    if (!valid) {
                                        PosDB db = PosDB.getInstance(c);
                                        db.OpenDb();
                                        
                                        
                                        long custId =
                                                db.createCustomer(FName.getText().toString(), "",
                                                        CompName.getText().toString(),
                                                        CellNo.getText().toString(),
                                                        P1.getText().toString(),
                                                        P2.getText().toString(),
                                                        cnicEdt.getText().toString(),
                                                        Constant.testInput(Add.getText().toString()), "", "", "", Constant.testInput(Email.getText().toString()), Constant.testInput(Notes.getText().toString()), String.valueOf(Latitude), String.valueOf(Longitude), "", OpeningBalance.getText().toString().trim(), OpeningBalance.getText().toString().trim(), 0, Integer.parseInt(String.valueOf(selectedRouteDropDown)), Integer.parseInt(String.valueOf(selectedCustomerDropDown)), selectedCustomerCategory, selectedTaxTypeDropDown, selectedShopCatDropDown, selectedSubShopCatDropDown);
                                        
                                        db.CloseDb();
                                        
                                        if (custId > 0) {
                                            
                                            AddBtn.setClickable(true);
                                            Toast.makeText(AppContextProvider.getContext(), "Shop Created ", Toast.LENGTH_SHORT).show();
                                            FName.setText("");
                                            LName.setText("");
                                            CompName.setText("");
                                            CellNo.setText("");
                                            P1.setText("");
                                            P2.setText("");
                                            Add.setText("");
                                            City.setText("");
                                            State.setText("");
                                            Country.setText("");
                                            Email.setText("");
                                            Notes.setText("");
                                            //cityEdt.setText("");
                                            //areaEdt.setText("");
                                            OpeningBalance.setText("");
                                            cnicEdt.setText("");
                                            customerTypeDrop.setSelection(0);
                                            RouteDropDown.setSelection(0);
                                            customerTaxTypeDrop.setSelection(0);
                                            customerShopCatDrop.setSelection(0);
                                            customerShopSubCatDrop.setSelection(0);
                                            //locStatusTV.setText("Fetching Coordinates");
                                            
                                            populateList(noItemText);
                                        } else {
                                            //Toast.makeText(getActivity(), "Shop Something went wrong ", Toast.LENGTH_SHORT).show();
                                            
                                        }
                                        
                                    } else {
                                        Toast.makeText(AppContextProvider.getContext(), "Shop Already Exists ", Toast.LENGTH_SHORT).show();
                                        AddBtn.setClickable(true);
                                        
                                    }
                                }
                            }
                        });
                        // end here
                        
                        
                    }
                    break;

                /*
                        ADD Customer Ends
                 */
                    
                    case 3: {

                    /*locStatusTV = (TextView) rootView.findViewById(R.id.locStatusTextViewForUpdateLoc);
                    manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    startLocation();*/
                        
                        
                        LinearLayout ShowUpdateLocationCustomer = rootView.findViewById(R.id.ShowUpdateLocationCustomer);
                        ShowUpdateLocationCustomer.setVisibility(View.VISIBLE);
                        
                        ShowUpdateLocationCustomer.setAnimation(animation);
                        
                        locStatusTxt = rootView.findViewById(R.id.locStatusTextViewForUpdateLoc);
                        //manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        //startLocation();
                        CustomerTxt = rootView.findViewById(R.id.SelectCustomerForUpdateLoc);
                        final EditText cityEdt = rootView.findViewById(R.id.CityEdtForUpdateLoc);
                        final EditText areaEdt = rootView.findViewById(R.id.AreaEdtForUpdateLoc);
                        final AutoCompleteTextView SelectCategoryForUpdateLoc = rootView.findViewById(R.id.SelectCategoryForUpdateLoc);
                        final AutoCompleteTextView SelectSubCategoryForUpdateLoc = rootView.findViewById(R.id.SelectSubCategoryForUpdateLoc);
                        SharedPreferences Category_Labels_sharedPreferences = getActivity().getSharedPreferences(
                                "Category_Labels", MODE_PRIVATE);

                        SelectCategoryForUpdateLoc.setHint(Category_Labels_sharedPreferences.getString("category_label","Select Shop Category"));

                        SelectSubCategoryForUpdateLoc.setHint(Category_Labels_sharedPreferences.getString("subcategory_label","Select Shop Sub Category"));
                        final EditText AddressEdTxtUpdate = rootView.findViewById(R.id.AddressEdTxtUpdate);
                        CustomerTxt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                        CustomerTxt.setRawInputType(InputType.TYPE_CLASS_TEXT);
                        
                        selectedCustomerItem = false;
                        
                        showEditTextsAsMandatory(cityEdt, areaEdt);
                        
                        myAdapterCustomer = new FilterWithSpaceAdapter<>(c, R.layout.layout_custom_spinner, R.id.item, itemCustomer);
                        CustomerTxt.setAdapter(myAdapterCustomer);
                        CustomerCategoryAdapter = new FilterWithSpaceAdapter<>(c, R.layout.layout_custom_spinner, R.id.item, db.getShopCategory());
                        SelectCategoryForUpdateLoc.setAdapter(CustomerCategoryAdapter);

SelectCategoryForUpdateLoc.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        custCatId = db.getShopCategoryId(SelectCategoryForUpdateLoc.getText().toString());
        CustomerSubCategoryAdapter = new FilterWithSpaceAdapter<>(c, R.layout.layout_custom_spinner, R.id.item, db.getShopSubCategoryByCategory(custCatId));
        SelectSubCategoryForUpdateLoc.setAdapter(CustomerSubCategoryAdapter);
        CustomerSubCategoryAdapter.notifyDataSetChanged();
    }
        @Override
        public void afterTextChanged(Editable s) {


        }
    });
                        CustomerSubCategoryAdapter = new FilterWithSpaceAdapter<>(c, R.layout.layout_custom_spinner, R.id.item, db.getShopSubCategoryByCategory(custCatId));
                        SelectSubCategoryForUpdateLoc.setAdapter(CustomerSubCategoryAdapter);
						CustomerSubCategoryAdapter.notifyDataSetChanged();
                        SelectSubCategoryForUpdateLoc.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                custSubCatId=db.getShopSubCategoryId(SelectSubCategoryForUpdateLoc.getText().toString());
                            }
                            @Override
                            public void afterTextChanged(Editable s) {


                            }
                        });

                        CustomerTxt.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            
                            
                            }
                            
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                
                                
                                CustomerTxt.setError(null);
                                
                                // update the adapater
                                myAdapterCustomer.notifyDataSetChanged();
                                myAdapterCustomer = new FilterWithSpaceAdapter<>(c, R.layout.layout_custom_spinner, R.id.item, itemCustomer);
                                CustomerTxt.setAdapter(myAdapterCustomer);
                                
                                
                                if (s.toString().contains("'") || s.toString().contains("%") || s.toString().contains("&")) {
                                    Toast.makeText(AppContextProvider.getContext(), " Symbols like \" ' \" \" % \" \" & \"\n not acceptable ", Toast.LENGTH_SHORT).show();
                                    
                                } else {
                                    
                                    itemCustomer = getCustomerNameFromDb(Constant.testInput(s.toString()), db);
                                    
                                    
                                    db.OpenDb();
                                    
                                    SelectedCustomerId = (db.getCustomerID(Constant.testInput(s.toString())));
                                    AddressEdTxtUpdate.setText(    db.getSelectedCustomerAdd(SelectedCustomerId));
									if(!SelectedCustomerId.equals("") && !SelectedCustomerId.equals("0")){
										custCatId=db.getShopCategoryIDByCustId(SelectedCustomerId);
										SelectCategoryForUpdateLoc.setText(db.getShopCategoryNameByCustId(SelectedCustomerId));
										
										CustomerSubCategoryAdapter = new FilterWithSpaceAdapter<>(c, R.layout.layout_custom_spinner, R.id.item, db.getShopSubCategoryByCategory(custCatId));
										SelectSubCategoryForUpdateLoc.setAdapter(CustomerSubCategoryAdapter);
										CustomerSubCategoryAdapter.notifyDataSetChanged();
											
												custSubCatId=		db.getShopSubCategoryIDByCustId(SelectedCustomerId);
										SelectSubCategoryForUpdateLoc.setText(db.getShopSubCategoryNameByCustId(SelectedCustomerId));
									
									
									}
                                    //SelectedCustomerTypeId = db.getCustomerTypeID(s.toString());
                                    db.CloseDb();
                                }
                                
                                
                            }
                            
                            @Override
                            public void afterTextChanged(Editable s) {
                            
                            
                            }
                        });
                        
                        CustomerTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                
                                selectedCustomerItem = true;
                                CustomerTxt.setEnabled(false);
                                CustomerTxt.setTextColor(Color.BLACK);
                            }
                        });
                        
                        ShowUpdateLocationCustomer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (selectedCustomerItem) {
                                    CustomerTxt.setEnabled(true);
                                }
                            }
                        });
                        
                        
                        final Button updateBtn = rootView.findViewById(R.id.UpdateLocBtn);
                        final Button getLocationBtn = rootView.findViewById(R.id.getLocationBtnForUpdateLoc);
                        
                        final Loader loader = new Loader();
                        
                        getLocationBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestPermission(getActivity());
                                if (!granted) {
                                    return;
                                }
                                loader.showDialog(c);
                                if (checkLocationPermission(c)) {
                                    
                                    new Handler().postDelayed(new Runnable() {
                                        
                                        @Override
                                        public void run() {
                                            
                                            
                                            if (Constant.networkAvailable()) {
                                                
                                                
                                                String[] CityArea = new String[3];
                                                
                                                // If Check Commented By me because Lati and Logi I m getting are 0
                                                // if( Latitude !=0 && Longitude !=0 ){
                                                //CityArea = getCityAreaFromLatitudeLongitude(latitude, longitude, ShopRegActivity.this, ldr);
                                                if (getActivity() != null) {
                                                    CityArea = getCityAreaCountryFromLatitudeLongitude(Latitude, Longitude, getActivity());
                                                    //}
                                                }
                                                if (CityArea == null) {
                                                    cityEdt.setText("N/A");
                                                    areaEdt.setText("N/A");
                                                } else {
                                                    cityEdt.setText(CityArea[0] + "");
                                                    areaEdt.setText(CityArea[1] + "");
                                                    
                                                }//CountryName = CityArea[2]+"";
                                                locStatusTxt.setText(Latitude + " , " + Longitude);
                                                
                                                loader.HideLoader();
                                            } else {
                                                loader.HideLoader();
                                                Constant.CustomDialogNoInternet(c);
                                                
                                            }
                                            
                                        }
                                    }, 1000);
                                    
                                } else {
                                    loader.HideLoader();
                                    Toast.makeText(AppContextProvider.getContext(), "Location Permission required", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        
                        updateBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestPermission(getActivity());
                                if (!granted) {
                                    return;
                                }
                                if (SelectCategoryForUpdateLoc.getText().toString().trim().isEmpty() ||SelectSubCategoryForUpdateLoc.getText().toString().trim().isEmpty() || custCatId.equals("0")||custSubCatId.equals("0")||CustomerTxt.getText().toString().trim().isEmpty() || SelectedCustomerId.equalsIgnoreCase("0") || SelectedCustomerId.trim().isEmpty() || Latitude == 0 || Longitude == 0 || cityEdt.getText().toString().trim().isEmpty() || areaEdt.getText().toString().trim().isEmpty() ||AddressEdTxtUpdate.getText().toString().trim().isEmpty()|| cityEdt.getText().toString().equals("null") || areaEdt.getText().toString().equals("null") || locStatusTxt.getText().toString().equals("Fetching Coordinates")) {
                                	
                                    
                                    if (CustomerTxt.getText().toString().trim().isEmpty()) {
                                        CustomerTxt.setError("Customer Required");
                                    }  if (SelectCategoryForUpdateLoc.getText().toString().trim().isEmpty()) {
										SelectCategoryForUpdateLoc.setError("Category Required");
                                    }
                                    if (SelectSubCategoryForUpdateLoc.getText().toString().trim().isEmpty()) {
										SelectSubCategoryForUpdateLoc.setError("Sub Category Required");
                                    }
                                    if (custCatId.equals("0")) {
                                        SelectCategoryForUpdateLoc.setError("Customer Category Required");
                                    }  if (custSubCatId.equals("0")) {
                                        SelectSubCategoryForUpdateLoc.setError("Customer Sub Category Required");
                                    }
                                    
                                    if (SelectedCustomerId.equalsIgnoreCase("0")) {
                                        CustomerTxt.setError("Customer Invalid");
                                    }
                                    
                                    if (SelectedCustomerId.trim().isEmpty()) {
                                        CustomerTxt.setError("Customer Invalid");
                                        
                                    }
                                    
                                    if (cityEdt.getText().toString().trim().isEmpty()) {
                                        cityEdt.setError("City Required");
                                    }
                                    
                                    if (areaEdt.getText().toString().trim().isEmpty()) {
                                        areaEdt.setError("Area Required");
                                    }
                                    if (AddressEdTxtUpdate.getText().toString().trim().isEmpty()) {
                                        AddressEdTxtUpdate.setError("Address Required");
                                    }
                                    
                                    if (cityEdt.getText().toString().trim().equals("null")) {
                                        cityEdt.setError("City Required");
                                    }
                                    if (areaEdt.getText().toString().trim().equals("null")) {
                                        areaEdt.setError("Area Required");
                                    }
                                    
                                    if (Latitude == 0 || Longitude == 0) {
                                        Toast.makeText(AppContextProvider.getContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
                                        
                                    }
                                    
                                    if (Latitude == 0.0 || Longitude == 0.0) {
                                        Toast.makeText(AppContextProvider.getContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
                                    }
                                    
                                    if (locStatusTxt.getText().toString().equals("Fetching Coordinates")) {
                                        Toast.makeText(AppContextProvider.getContext(), "Location Not Found ", Toast.LENGTH_SHORT).show();
                                    }
                                    
                                } else {
                                    
                                    db.OpenDb();
                                    
                                    db.updateCustomerLocation(SelectedCustomerId, cityEdt.getText().toString(), areaEdt.getText().toString(), String.valueOf(Latitude), String.valueOf(Longitude),AddressEdTxtUpdate.getText().toString(),custCatId,custSubCatId);
                                    db.CloseDb();
                                    
                                    CustomerTxt.setText("");
                                    CustomerTxt.setEnabled(true);
                                    cityEdt.setText("");
                                    areaEdt.setText("");
                                    AddressEdTxtUpdate.setText("");
                                    custCatId="0";
                                    SelectCategoryForUpdateLoc.setText("");
                                    custSubCatId="0";
                                    SelectSubCategoryForUpdateLoc.setText("");
                                    locStatusTxt.setText("Fetching Coordinates");
                                    Toast.makeText(AppContextProvider.getContext(), "Shop Location Updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        
                    }
                    break;
                    
                    case 4: {
                        
                        LinearLayout addRouteLayout = rootView.findViewById(R.id.addRouteLayout);
                        addRouteLayout.setVisibility(View.VISIBLE);
                        
                        addRouteLayout.setAnimation(animation);
                        final EditText addRouteEdtTxt = rootView.findViewById(R.id.addRoute);
                        final Button submitBtn = rootView.findViewById(R.id.SubmitBtn);
                        final Spinner daySpinner = rootView.findViewById(R.id.select_day);
                     
                        List<String> days = new ArrayList<>();
                        
                        days.add("None");
                        days.add("Monday");
                        days.add("Tuesday");
                        days.add("Wednesday");
                        days.add("Thursday");
                        days.add("Friday");
                        days.add("Saturday");
                        days.add("Sunday");
                        
                        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(getActivity(),
                                android.R.layout.simple_spinner_dropdown_item,days);
                        
                        
                        daySpinner.setAdapter(dayAdapter);
                        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        
                                dayId = position;
                                
                            }
    
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
        
                            }
                        });
                        
                        showEditTextsAsMandatory(addRouteEdtTxt);
                        
                        submitBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                
                                if (addRouteEdtTxt.getText().toString().trim().isEmpty()) {
                                    
                                    addRouteEdtTxt.setError("Route Required");
                                } else {
                                    
                                    submitBtn.setClickable(false);
                                    boolean valid = checkForDuplicateRoutes(Constant.testInput(addRouteEdtTxt.getText().toString()));
                                    if (!valid) {
                                        db.OpenDb();
                                        int routeMaxNetId = db.getMaxIdFromRoute();
                                        long idRouteIns =
                                                db.InsertCustomerRoute(String.valueOf(routeMaxNetId + 1), Constant.testInput(addRouteEdtTxt.getText().toString()), 0, 1,dayId);
                                        db.CloseDb();
                                        if (idRouteIns > 0) {
                                            Toast.makeText(AppContextProvider.getContext(), "Route Created", Toast.LENGTH_SHORT).show();
                                            addRouteEdtTxt.setText("");
                                            submitBtn.setClickable(true);
                                            //getActivity().finish();
                                            //getActivity().startActivity( new Intent( getActivity(), MainActivity.class ) );
                                        } else {
                                            //Toast.makeText(getActivity(), "Route Not Inserted in DB, Something went wrong !", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(AppContextProvider.getContext(), "Route Already Exists", Toast.LENGTH_SHORT).show();
                                        submitBtn.setClickable(true);
                                        
                                    }
                                }
                            }
                        });
                        
                        
                    }
                    break;
                    
                    
                   /* case 5: {
                        
                        
                        LinearLayout ShowVerifiedCustomers = rootView.findViewById(R.id.ShowVerifiedShops);
                        ShowVerifiedCustomers.setVisibility(View.VISIBLE);
                        
                        ShowVerifiedCustomers.setAnimation(animation);
                        
                        // load the animation
                        Animation enter = AnimationUtils.loadAnimation(c, R.anim.enter_from_right);
                        
                        ShowVerifiedCustomers.startAnimation(enter);
                        
                        lviewVerifiedShops = rootView.findViewById(R.id.listviewVerifiedShops);
                        ListView hList = rootView.findViewById(R.id.HlistVerifiedShops);
                        noItemTextVerifiedShops = rootView.findViewById(R.id.NoItemTxtVerifiedShops);
                        
                        Hlist.clear();
                        
                        HashMap<String, String> temp = new HashMap<>();
                        //temp.put(CODE_COLUMN,"Code");
                        temp.put(FIRST_COLUMN, "Shop Name");
                        temp.put(SECOND_COLUMN, "Enter Code");
                        temp.put(THIRD_COLUMN, "Save");
                        temp.put(FOURTH_COLUMN, "Resend");
                        Hlist.add(temp);
                        
                        
                        ListviewHeaderAdapterVerifiedShops adapter = new ListviewHeaderAdapterVerifiedShops(c, Hlist);
                        hList.setAdapter(adapter);
                        
                        populateListVerifiedShops(noItemTextVerifiedShops);
                        
                        
                    }
                    break;*/
                    
                    default: {
                        //dummyTextView.setText(" WD");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.fillInStackTrace());
                
            }
            
            
            return rootView;
        }
    
        int dayId = 0;
        public boolean checkForDuplicateRoutes(String routeEdtTxt) {
            
            boolean isInList = false;
            List<String> routeNameList;
            db.OpenDb();
            routeNameList = db.getCustomerRoutesNames();
            db.CloseDb();
            for (String string : routeNameList) {
                
                if (string.equalsIgnoreCase(routeEdtTxt)) {
                    isInList = true;
                    break;
                }
            }
            
            return isInList;
            
            
        }
        
        public boolean checkForDuplicateShops(String customerNameEdtTxt, String compNameEdtTxt) {
            
            boolean isInList = false;
            ArrayList<HashMap<String, String>> compNameList;
            HashMap<String, String> map;
            String compName, customerName;
            db.OpenDb();
            compNameList = db.getCustomerShopNamesAndCellNo();
            db.CloseDb();
            for (int i = 0; i < compNameList.size(); i++) {
                
                map = compNameList.get(i);
                compName = map.get("company_name");
                customerName = map.get("customer_name");
                
                if (customerName.equalsIgnoreCase(customerNameEdtTxt) && compName.equalsIgnoreCase(compNameEdtTxt)) {
                    isInList = true;
                    break;
                }
                
            }
            
            return isInList;
            
            
        }
        
        public String[] getCustomerNameFromDb(CharSequence searchTerm, PosDB db) {
            
            // add items on the array dynamically
            db.OpenDb();
            List<MyObject> products = db.GetCustomerAutoCompleteData(searchTerm);
            
            db.CloseDb();
            int rowCount = products.size();
            
            String[] item = new String[rowCount];
            int x = 0;
            
            for (MyObject record : products) {
                
                item[x] = record.objectName;
                x++;
                
            }
            
            return item;
        }

        public void showEditTextsAsMandatory(EditText... ets) {
            for (EditText et : ets) {
                String hint = et.getHint().toString();
                
                et.setHint(Html.fromHtml(hint + "<font color=\"#ff0000\">" + " *" + "</font>"));
            }
        }
        
        public void DropdownSetupForRoute(Spinner dropDown) {
            
            List<String> ItemsData = new ArrayList<>();
            List<String> ItemsID;
            ArrayList<HashMap<String,String>> arrayList =db.getCustomerRoutesForDropDown();
            db.OpenDb();
           
            ItemsID = db.getCustomerRoutesIDForDropDown();
            db.CloseDb();
            
            ItemsData.add(0, "Select Route ");
            ItemsID.add(0, "0");
            for (HashMap<String,String> map: arrayList) {
                
                ItemsData.add(map.get("name"));
                
            }
            
            try {
                // Creating adapter for spinner
                
                
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
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
                    
                    
                    selectedRouteDropDown = Long.parseLong(finalItemsID.get(i));
//                    db.OpenDb();
//                    db.updateSavedRoute(finalItemsID.get(i));
//                    db.CloseDb();
                    
                    // Yahan kuch krna parygaaaaa
                    
                    Log.d("sql", "route id: " + finalItemsID.get(i));
                    
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                
                }
            });
            
            
        }
        
        public void DropdownSetupForCustType(Spinner dropDown) {
            
            List<String> ItemsData;
            List<String> ItemsID;
            
            db.OpenDb();
            ItemsData = db.getCustomerTypeForDropDown();
            ItemsID = db.getCustomerTypeIDForDropDown();
            db.CloseDb();

            /*String simple = "Select Customer Type ";
            String colored = "*";
            SpannableStringBuilder builder = new SpannableStringBuilder();

            builder.append(simple);
            int start = builder.length();
            builder.append(colored);
            int end = builder.length();

            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")), start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
            //Toast.makeText(getContext(),builder+"",Toast.LENGTH_SHORT).show();
            // String test = "Select Customer Type " + "<font color=\"#ff0000\"> *</font>";
            ItemsData.add(0, "Select Customer Type ");
            ItemsID.add(0, "0");
            
            
            try {
                // Creating adapter for spinner
                
                
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
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
                    
                    
                    selectedCustomerDropDown = Long.parseLong(finalItemsID.get(i));
                    /*db.OpenDb();
                    db.updateSavedRoute(finalItemsID.get(i));
                    db.CloseDb();*/
                    
                    Log.d("sql", "customer type id: " + finalItemsID.get(i));
                    
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                
                }
            });
            
            
        }
        
        public void DropdownSetupForShopCategory(Spinner dropDown ) {
            
            final ArrayList<HashMap<String, String>> ItemsData = PosDB.getInstance(getActivity()).getShopCategoryForDropDown();
            
            List<String> list = new ArrayList<>();
            SharedPreferences Category_Labels_sharedPreferences = getActivity().getSharedPreferences(
                    "Category_Labels", MODE_PRIVATE);


            list.add(Category_Labels_sharedPreferences.getString("category_label","Select Shop Category"));
            
            for (HashMap<String, String> map : ItemsData) {
                
                list.add(map.get("name"));
                
            }
            try {
                
                
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, list);
                
                
                dataAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                
                // attaching data adapter to spinner
                dropDown.setAdapter(dataAdapter);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            
            
            dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    
                    if (i != 0) {
                        String s = adapterView.getItemAtPosition(i).toString();
                        
                        for (HashMap<String, String> ss : ItemsData) {
                            
                            if (ss.get("name").equals(s)) {
                                selectedShopCatDropDown = ss.get("id");

//                                ArrayAdapter<String> SubCatAdapter = new ArrayAdapter<>(getActivity(),
//                                        android.R.layout.simple_spinner_item, new ArrayList<String>());
//                                SubCatAdapter.add("Select Sub Category");
//                                SubCatAdapter.addAll( db.getShopSubCategoryByCategory(selectedShopCatDropDown));
//                                SubCatAdapter
//                                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                customerShopSubCatDrop.setAdapter(SubCatAdapter);
//                                SubCatAdapter.notifyDataSetChanged();
                                Log.e("selectedShopCatDropDown", selectedShopCatDropDown);
                                DropdownSetupForShopSubCategory(selectedShopCatDropDown,customerShopSubCatDrop);
                            }
                            
                        }
                    } else {
                        Log.e("selectedShopCatDropDown", selectedShopCatDropDown);
                        selectedShopCatDropDown = "0";
                        DropdownSetupForShopSubCategory(selectedShopCatDropDown, customerShopSubCatDrop);
                    }
                    
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                
                }
            });
            
            
        }
        
       public void DropdownSetupForShopSubCategory(String id, Spinner dropDown) {
            
            final ArrayList<HashMap<String, String>> ItemsData =
                     PosDB.getInstance(getActivity()).getShopSubCategoryForDropDown(id);
            
            List<String> list = new ArrayList<>();
           SharedPreferences Category_Labels_sharedPreferences = getActivity().getSharedPreferences(
                   "Category_Labels", MODE_PRIVATE);


           list.add(Category_Labels_sharedPreferences.getString("subcategory_label","Select Shop Sub Category"));

            
            for (HashMap<String, String> map : ItemsData) {
                
                list.add(map.get("name"));
                
            }
            try {
                
                
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, list);
                
                
                dataAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                
                // attaching data adapter to spinner
                dropDown.setAdapter(dataAdapter);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            
            
            dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    
                    if (i != 0) {
                        String s = adapterView.getItemAtPosition(i).toString();
                        
                        for (HashMap<String, String> ss : ItemsData) {
                            
                            if (ss.get("name").equals(s)) {
                                selectedSubShopCatDropDown = ss.get("id");
                                Log.e("", "selectedSubShopCatDropDown " + selectedSubShopCatDropDown);
                            }
                            
                        }
                    } else {
                        Log.e("", "selectedSubShopCatDropDown " + selectedSubShopCatDropDown);
                        selectedSubShopCatDropDown = "0";
                    }
                    
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                
                }
            });
            
            
        }
        
        public void DropdownSetupForCustTaxType(Spinner dropDown) {
            
            List<String> ItemsData = new ArrayList<>();
            List<String> ItemsID = new ArrayList<>();
            
            db.OpenDb();
            
            db.CloseDb();
            
            
            ItemsData.add("Select Tax Type");
            ItemsData.add("Filer");
            ItemsData.add("Non-Filer");
            ItemsID.add("-1");
            ItemsID.add("1");
            ItemsID.add("0");
            
            
            try {
                
                
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, ItemsData);
                
                
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
                    
                    
                    selectedTaxTypeDropDown = Long.parseLong(finalItemsID.get(i));
                    
                    
                    Log.d("sql", "customer type id: " + finalItemsID.get(i));
                    
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                
                }
            });
            
            
        }
        
        public void DropdownSetupForCustCategory(Spinner dropDown) {
            
            List<String> ItemsData = new ArrayList<>();
            List<String> ItemsID = new ArrayList<>();
            
            ItemsData.add(0, "Cash");
            ItemsData.add(1, "Credit");
            
            ItemsID.add(0, "1");
            ItemsID.add(1, "2");
            
            
            try {
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
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
                    
                    
                    selectedCustomerCategory = finalItemsID.get(i);
                    Log.d("sql", "customer category id: " + selectedCustomerCategory);
                    
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                
                }
            });
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
            // Show 2 total pages (tabs).
            //return 2;
            return 4;
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Shop List".toUpperCase();
                case 1:
                    return "Add New Shop".toUpperCase();
/*
			case 2:
                return "Shop gps".toUpperCase();
*/
                case 2:
                    return "Update Shop Basic".toUpperCase();
                
                case 3:
                    return "Add New Route".toUpperCase();
                
                
            }
            return null;
        }
    }
    
    
    /*
     Location Works Ends
     */
    
    
}
