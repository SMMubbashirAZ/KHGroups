package com.blazeminds.pos.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.BuildConfig;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.MainActivity;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.ShopVisitListAdapter;
import com.blazeminds.pos.ShopVisitListHeaderAdapter;
import com.blazeminds.pos.adapter.FilterWithSpaceAdapter;
import com.blazeminds.pos.autocomplete_resource.MyObject;
import com.blazeminds.pos.resources.UserSettings;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.blazeminds.pos.Constant.checkLocationPermission;
import static com.blazeminds.pos.Constant.getCityAreaCountryFromLatitudeLongitude;
import static com.blazeminds.pos.MainActivity.granted;
import static com.blazeminds.pos.MainActivity.requestPermission;

/**
 * Created by Blazeminds on 3/8/2018.
 */

public class ShopsVisit extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public final static String TAG = ShopsVisit.class.getSimpleName();
    public static String ShopID = "0";

    static ImageView frontImagePic, takeFrontImagePic;
    static TextView locStatusTxt, noItemTxt, startTimeTxt;
    static EditText remarksEdtTxt;
    static Spinner noReasonDrop;
    static long selectedNoReasonDrop;
    static AutoCompleteTextView shopTxt;
    static FilterWithSpaceAdapter<String> myAdapterShop;
    static String[] itemShop = new String[]{"Please search..."};
    static String selectedShopId = "0";
    static boolean selectShopItem;
    static Button submitBtn;
    static PosDB db;
    static String[] CityArea;
    static ArrayList<HashMap<String, String>> list;
    static ArrayList<HashMap<String, String>> data;
    static ArrayList<String> dataCustId;
    static ArrayList<HashMap<String, String>> Hlist;
    static android.widget.ListView lview, hList, ListView;


    // New Work Start Here
    static Context c;
    static int pos = 0;

    // public static final String TAG = CustomerList.class.getSimpleName();
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    ShopsVisit.SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public static ShopsVisit newInstance() {
        ShopID = "0";
        return new ShopsVisit();
    }

    public static ShopsVisit newInstanceSwipe(String Shop_id) {
        ShopID = Shop_id;
        return new ShopsVisit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_item_one, container, false);
        mSectionsPagerAdapter = new ShopsVisit.SectionsPagerAdapter(
                getChildFragmentManager());
        //locStatusTV = (TextView) v.findViewById(R.id.locStatusTextView);
        //locStatusTV = (TextView) v.findViewById(R.id.locStatusTextView);
        mViewPager = v.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        c = getActivity();


        return v;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
        static StrictMode.VmPolicy.Builder builder;
        static Uri imageUri;
        static int CAMERA_FRONT_IMAGE = 1;
        private static boolean isOne = true;
        private static String coordsToSend;
        private static String gAccuracy;
        private static double Latitude, Longitude;
        // Location manager
        private static LocationManager manager;
        private final LocationListener locListener = new LocationListener() {

            public void onLocationChanged(Location argLocation) {
                printLocation(argLocation, GPS_GOT_COORDINATES);
            }

            @Override
            public void onProviderDisabled(String arg0) {
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
        private final LocationCallback mLocationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {

                    printLocation(location, GPS_GOT_COORDINATES);
                }


            }

        };
        StringBuilder ImagePathtoUploadFrontImage = new StringBuilder();
        byte[] ba;
        private long mLastClickTime = 0;
        private FusedLocationProviderClient mFusedLocationClient;
        private GoogleApiClient mGoogleApiClient;
        public DummySectionFragment() {
        }

        // Location events (we use GPS only)
        public static boolean checkCameraPermission(Context c) {

            String permission = "android.permission.CAMERA";
            int res = c.checkCallingOrSelfPermission(permission);
            return (res == PackageManager.PERMISSION_GRANTED);
        }

        private static ArrayList<HashMap<String, String>> populateListOfShopVisit(TextView noItemText) {

            data = new ArrayList<>();
            //dataCustId = new ArrayList<String>();
            //dataCustId.clear();
            data.clear();

            PosDB db = PosDB.getInstance(c);

            db.OpenDb();

            ///dataCustId = db.getSelectedCustomerID();
            data = db.getShopVisitList();

            db.CloseDb();

            if (data.size() > 0) {
                noItemText.setVisibility(View.GONE);
                ListView.setVisibility(View.VISIBLE);
                ShopVisitListAdapter adapter1 = new ShopVisitListAdapter((Activity) c, data);
                ListView.setAdapter(adapter1);

            } else {
                //Toast.makeText(c, "No Data", Toast.LENGTH_SHORT).show();
                ListView.setVisibility(View.GONE);
                noItemText.setVisibility(View.VISIBLE);
            }


            return data;

        }

        private static String getDateTime() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            DateFormat df = DateFormat.getDateTimeInstance();

            //SelectedDate = dateFormat.format(new Date());

            return dateFormat.format(new Date());
            //return df.format(new Date());
        }

        private void listeners() {


            shopTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                    shopTxt.setError(null);

                    // update the adapater
                    myAdapterShop.notifyDataSetChanged();
                    myAdapterShop = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, itemShop);
                    shopTxt.setAdapter(myAdapterShop);


                    if (s.toString().contains("'") || s.toString().contains("%") || s.toString().contains("&")) {
                        Toast.makeText(AppContextProvider.getContext(), " Symbols like \" ' \" \" % \" \" & \"\n not acceptable ", Toast.LENGTH_SHORT).show();

                    } else {

                        itemShop = getCustomerNameFromDb(Constant.testInput(s.toString()), db);


                        db.OpenDb();

                        selectedShopId = (db.getCustomerID(Constant.testInput(s.toString())));
                        //SelectedCustomerTypeId = db.getCustomerTypeID(s.toString());
                        Log.d("ShopID", selectedShopId);
                        db.CloseDb();
                    }


                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });

            shopTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    selectShopItem = true;
                    shopTxt.setEnabled(false);
                    shopTxt.setTextColor(Color.BLACK);

                }
            });
        }

        public void DropdownSetupForNoReason(Spinner dropDown) {

            List<String> ItemsData = new ArrayList<>();
            List<String> ItemsID = new ArrayList<>();

            db.OpenDb();
            ItemsData = db.getNoReasonForDropDown();
            ItemsID = db.getNoReasonIDForDropDown();
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
            ItemsData.add(0, "Select Reason ");
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


                    selectedNoReasonDrop = Long.parseLong(finalItemsID.get(i));
					/*db.OpenDb();
					db.updateSavedRoute(finalItemsID.get(i));
                    db.CloseDb();*/

                    Log.d("sql", "no reason id: " + finalItemsID.get(i));

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        }

        // this function is used in CustomAutoCompleteTextChangedListener.java
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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_shop_visit,
                    container, false);
/*
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
                    ARG_SECTION_NUMBER)));
*/
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(c);
            //locStatusTV = (TextView) rootView.findViewById(R.id.locStatusTextView);
            int pos2 = Integer.parseInt(Integer.toString(getArguments().getInt(
                    pos1)));


            try {
                switch (pos2) {

                    case 1: {

                        LinearLayout paymentRecieving = rootView.findViewById(R.id.shopVisitLayout);
                        paymentRecieving.setVisibility(View.VISIBLE);

                        Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);

                        paymentRecieving.startAnimation(enter);

                        db = PosDB.getInstance(c);


                        initUI(rootView);


                        listeners();

                        paymentRecieving.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (selectShopItem) {
                                    shopTxt.setEnabled(true);
                                }

                            }
                        });

                        submitBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestPermission(getActivity());
                                if (!granted) {
                                    return;
                                }
                                UserSettings userSettings = UserSettings.getInstance(getActivity());

                                if (checkLocationPermission(getActivity())/*c.checkCallingOrSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && c.checkCallingOrSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED*/) {

                                    //new Handler().postDelayed(new Runnable() {

                                    // @Override
                                    // public void run() {


                                    if (Constant.networkAvailable()) {


                                        // If Check Commented By me because Lati and Logi I m getting are 0
                                        // if( Latitude !=0 && Longitude !=0 ){
                                        //CityArea = getCityAreaFromLatitudeLongitude(latitude, longitude, ShopRegActivity.this, ldr);

                                        if (Latitude != 0 && Longitude != 0) {

                                            if (getActivity() != null)
                                                CityArea = getCityAreaCountryFromLatitudeLongitude(Latitude, Longitude, getActivity());

                                            //callApi();
                                            // loader.HideLoader();
                                        } else {
                                            CityArea = new String[3];
                                            //Toast.makeText(getActivity(), "Please Move to Open Area", Toast.LENGTH_SHORT).show();

                                        }
                                        //if (CityArea != null) {

                                        //}

                                        //}
                                        //cityEdt.setText(CityArea[0]+"");
                                        //areaEdt.setText(CityArea[1]+"");
                                        //CountryName = CityArea[2]+"";
                                        //locStatusTV.setText(Latitude + " , " + Longitude );


                                    } else {
                                        //Constant.CustomDialogNoInternet(getActivity());

                                    }

                                    // }
                                    //  }, 1000);

                                } else {
                                    // loader.HideLoader();
                                    Toast.makeText(AppContextProvider.getContext(), "Location Permission required", Toast.LENGTH_SHORT).show();
                                }
//
//                                if ((db.getAppSettingsValueByKey("en_shop_visit_image_req") != 0)) {
//
//                                }
                                if (shopTxt.getText().toString().trim().isEmpty() || selectedShopId.equalsIgnoreCase("0")
                                        || selectedShopId.trim().isEmpty() || selectedNoReasonDrop == 0 ||
                                        locStatusTxt.getText().toString().equalsIgnoreCase("GPS Disabled")
                                        ||((db.getAppSettingsValueByKey("en_shop_visit_image_req") != 0)&&ImagePathtoUploadFrontImage.toString().equals(""))
                                    /*|| Latitude == 0.0 || Longitude == 0.0*/) {

                                    if (shopTxt.getText().toString().trim().isEmpty()) {
                                        shopTxt.setError("Shop Required");
                                    }

                                    if (selectedShopId.equalsIgnoreCase("0")) {
                                        shopTxt.setError("Shop Invalid");
                                    }

                                    if (selectedShopId.trim().isEmpty()) {
                                        shopTxt.setError("Shop Invalid");
                                    }

                                    if (selectedNoReasonDrop == 0) {
                                        Toast.makeText(AppContextProvider.getContext(), "Please Select Reason", Toast.LENGTH_SHORT).show();

                                    }

                    /*if (Latitude == 0.0){
						Toast.makeText(getActivity(), "Open GPS & Please Move to Open Area", Toast.LENGTH_SHORT).show();

                    }

                    if (Longitude == 0.0){
                        Toast.makeText(getActivity(), "Open GPS & Please Move to Open Area", Toast.LENGTH_SHORT).show();

                    }*/

                                    if (locStatusTxt.getText().toString().equalsIgnoreCase("GPS Disabled")) {
                                        Toast.makeText(AppContextProvider.getContext(), "Enable your GPS first", Toast.LENGTH_SHORT).show();

                                    }
                                    if (ImagePathtoUploadFrontImage.toString().equalsIgnoreCase("")) {
                                        Toast.makeText(AppContextProvider.getContext(), "Please Select Image to continue", Toast.LENGTH_SHORT).show();

                                    }

                                } else {

                                    submitBtn.setClickable(false);

                                    String cityArea = "";
                                    if (CityArea == null) {
                                        cityArea = "N/A";
                                    } else {
                                        cityArea = CityArea[1];
                                    }


                                    db.OpenDb();
                                    int shopId = db.getMaxIdFromShopVisit();
                                    String savedDistributor = db.getSavedDistributorList();
                                    long shopVisitInserted = db.createShopVisit(shopId + 1, selectedShopId, Integer.parseInt(String.valueOf(selectedNoReasonDrop)), startTimeTxt.getText().toString(), getDateTime(), Constant.testInput(remarksEdtTxt.getText().toString()), Latitude, Longitude, /*CityArea[1]*/cityArea, savedDistributor, 1);
                                    if (shopVisitInserted > 0) {

                                        //    db.updateCustomerLastUpdate(selectedShopId, Constant.getDateTimeSHORT());
                                        int dbMaxId = db.getMaxIdFromShopVisit();


                                        userSettings.set(String.valueOf(dbMaxId), ImagePathtoUploadFrontImage.toString());

                                        Toast.makeText(AppContextProvider.getContext(), "Shop Visit Created", Toast.LENGTH_SHORT).show();
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.content_frame, ShopsVisit.newInstance(), ShopsVisit.TAG).commit();
                                        new MainActivity().updateSync(getActivity());
                                    } else {
                                        //Toast.makeText(getActivity(), "Shop Visit Not Inserted in DB", Toast.LENGTH_SHORT).show();
                                    }
                                    db.CloseDb();
                                }
                            }
                        });
                        if (!ShopID.equals("0") && !ShopID.equals("")) {
                            shopTxt.setText(db.getSelectedCustomerSearchName(ShopID));

                            selectShopItem = true;
                            shopTxt.setEnabled(false);
                            shopTxt.setTextColor(Color.BLACK);
                        }
                    }
                    break;

                    case 2: {

                        LinearLayout paymentRecievingList = rootView.findViewById(R.id.showShopVisit);
                        paymentRecievingList.setVisibility(View.VISIBLE);
                        paymentRecievingList.setAnimation(AnimationUtils.loadAnimation(getActivity(),
                                R.anim.enter_from_right));
                        ListView = rootView.findViewById(R.id.listviewRList);
                        ListView hList = rootView.findViewById(R.id.Hlist);
                        noItemTxt = rootView.findViewById(R.id.NoItemTxt);
                        Hlist = new ArrayList<>();
                        Hlist.clear();

                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("sv_datetime", "DATETIME");
                        temp.put("sv_shopName", "SHOP NAME");
                        temp.put("sv_reasonName", "REASON");
                        //temp.put("sv_longitude", "REMARKS");
                        //temp.put(FOURTH_COLUMN, "Contact");
                        Hlist.add(temp);


                        ShopVisitListHeaderAdapter adapter = new ShopVisitListHeaderAdapter(c, Hlist);
                        hList.setAdapter(adapter);

                        populateListOfShopVisit(noItemTxt);


                    }
                    break;

                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.fillInStackTrace());

            }

            return rootView;
        }

        private void TakePicture(int option) {

        /*    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(getActivity().getExternalCacheDir(),
                    String.valueOf(System.currentTimeMillis()) + ".jpg");
            imageUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            switch (option) {

                case 1: {
                    startActivityForResult(intent, CAMERA_FRONT_IMAGE);
                }
                break;


            }*/

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
            //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));

            imageUri = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.detectFileUriExposure();
                imageUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", photo);
            } else {
                imageUri = Uri.fromFile(photo);
            }

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            switch (option) {

                case 1: {
                    startActivityForResult(cameraIntent, CAMERA_FRONT_IMAGE);
                }
                break;


            }


        }

        private void initUI(View rootView) {

            selectShopItem = false;
            startTimeTxt = rootView.findViewById(R.id.startTime);
            submitBtn = rootView.findViewById(R.id.SubmitBtn);
            remarksEdtTxt = rootView.findViewById(R.id.NotesTxt);
            noReasonDrop = rootView.findViewById(R.id.noReasonDrop);
            locStatusTxt = rootView.findViewById(R.id.locStatusTxt);
            shopTxt = rootView.findViewById(R.id.SelectShop);

            manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            startLocation();

            DropdownSetupForNoReason(noReasonDrop);

            startTimeTxt.setText(getDateTime());

            startTimeTxt.setVisibility(View.GONE);
            shopTxt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            shopTxt.setRawInputType(InputType.TYPE_CLASS_TEXT);

            remarksEdtTxt.setImeOptions(EditorInfo.IME_ACTION_DONE);
            remarksEdtTxt.setRawInputType(InputType.TYPE_CLASS_TEXT);


            // set our adapter
            myAdapterShop = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, itemShop);
            shopTxt.setAdapter(myAdapterShop);

            CityArea = new String[3];


            frontImagePic = rootView.findViewById(R.id.frontImagePic);
            takeFrontImagePic = rootView.findViewById(R.id.takeFrontImagePic);

            builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            takeFrontImagePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (checkCameraPermission(getActivity())) {
//                                    mCheckForUpdate = "create";
                        TakePicture(1);
                    } else {
                        Toast.makeText(getActivity(), "Camera Permission Required", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


        @Override
        public void onActivityResult(int requestCode, int resultCode,Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            isOne = true;
            Log.e("onProviderDisabled", "Code : " + requestCode);
            if (requestCode == CAMERA_FRONT_IMAGE && resultCode == Activity.RESULT_OK) {

                //Uri selectedURI = data.getData();

                //Uri selectedImage = imageUri;
                getActivity().getContentResolver().notifyChange(imageUri, null);
                //getContentResolver().notifyChange(selectedURI, null);
                ContentResolver cr = getActivity().getContentResolver();
                Bitmap bitmap;
                int temp;
                try {
                    bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
                    //bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedURI );
//                    Bitmap convertedImg = getResizedBitmap(bitmap, 100);
//                    Bitmap photo = (Bitmap) data.getExtras().get("data");
//                    frontImagePic.setImageBitmap(convertedImg);
//                    Uri tempUri = getImageUri(getActivity(), convertedImg);
//                    File finalFile = new File(getRealPathFromURI(tempUri));
//                    ImagePathtoUploadFrontImage=finalFile.getAbsolutePath();
//                    Log.d(TAG, "onActivityResult: "+ImagePathtoUploadFrontImage);


                    Bitmap convertedImg = getResizedBitmap(bitmap, 100);
//                    if (mCheckForUpdate.equals("create")) {

                        frontImagePic.setImageBitmap(convertedImg);

                        //log.e("Rotation1", String.valueOf(frontImagePic.getRotation()));
                        temp = (int) (frontImagePic.getRotation());
                        //log.e("Rotation2", String.valueOf(temp));
                        if (temp == 0) {

                            frontImagePic.setRotation(90);

                        } else if (temp == 90) {
                            frontImagePic.setRotation(0);
                        }
                        frontImagePic.buildDrawingCache();


                        ByteArrayOutputStream bao = new ByteArrayOutputStream();
                        convertedImg.compress(Bitmap.CompressFormat.PNG, 100, bao);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);

                        ba = bao.toByteArray();
//                    ImagePathtoUploadFrontImage = FileUtils.getPath(getActivity(), imageUri);
//                    ImagePathtoUploadFrontImage = getOriginalImagePath();
//
                        ImagePathtoUploadFrontImage.append(Base64.encodeToString(ba, Base64.NO_WRAP));
                        Log.d("BASE64_Before_Image", ImagePathtoUploadFrontImage.toString() + "--");
                    /*} else {
                        frontImagePicUpdate.setImageBitmap(convertedImg);

                        //log.e("Rotation1", String.valueOf(frontImagePic.getRotation()));
                        temp = (int) (frontImagePicUpdate.getRotation());
                        //log.e("Rotation2", String.valueOf(temp));
                        if (temp == 0) {

                            frontImagePicUpdate.setRotation(90);

                        } else if (temp == 90) {
                            frontImagePicUpdate.setRotation(0);
                        }
                        frontImagePicUpdate.buildDrawingCache();


                        ByteArrayOutputStream bao = new ByteArrayOutputStream();
                        convertedImg.compress(Bitmap.CompressFormat.PNG, 100, bao);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);

                        baupdate = bao.toByteArray();
//                    ImagePathtoUploadFrontImage = FileUtils.getPath(getActivity(), imageUri);
//                    ImagePathtoUploadFrontImage = getOriginalImagePath();
//
                        ImagePathtoUploadFrontImageUpdate.append(Base64.encodeToString(baupdate, Base64.NO_WRAP));
                        Log.d("BASE64_Before_Image", ImagePathtoUploadFrontImageUpdate.toString() + "--updated");
                    }*/
//                    bitmap1 = frontImagePic.getDrawingCache();
//                    bitmap1.setConfig(frontImagePic.getDrawingCache().getConfig());


                    //CameraDialog( getActivity(), imageUri, bitmap, ImagePathToUpload );


                } catch (Exception e) {
                    Log.d("Exception", e.getMessage());
                    Toast.makeText(getActivity(), "Failed to develop image", Toast.LENGTH_SHORT)
                            .show();

                }
            }

            else{
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
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
                if (manager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);


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
            stopLocationUpdates();
        }

        public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
            int width = image.getWidth();
            int height = image.getHeight();

            float bitmapRatio = (float) width / (float) height;
            if (bitmapRatio > 1) {
                width = maxSize;
                height = (int) (width / bitmapRatio);
            } else {
                height = maxSize;
                width = (int) (height * bitmapRatio);
            }
            return Bitmap.createScaledBitmap(image, width, height, true);
        }

        private synchronized void buildGoogleApiClient() {
            if (getActivity() != null) {
                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                startLocationUpdates();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {

                            }
                        })
                        .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            }
                        })
                        .addApi(LocationServices.API)
                        .build();
                mGoogleApiClient.connect();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
//            startLocation();
//            if (mGoogleApiClient != null && mFusedLocationClient != null) {
//                startLocationUpdates();
//                Log.e("startLocationUpdates();", "startLocationUpdates()");
//            } else {
//                buildGoogleApiClient();
//                Log.e("buildGoogleApiClient();", "buildGoogleApiClient()");
//            }

        /*Toast.makeText(getActivity(), "Latitude = "+Latitude+"" , Toast.LENGTH_SHORT).show();
		Toast.makeText(getActivity(), "Longitude = "+Longitude+"" , Toast.LENGTH_SHORT).show();*/
        }

        @SuppressLint("RestrictedApi")
        private void startLocationUpdates() {
            if (getActivity() != null) {
                // Create the location request to start receiving updates
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                /* 10 secs */
                long UPDATE_INTERVAL = 1000;
                mLocationRequest.setInterval(UPDATE_INTERVAL);
                /* 2 sec */
                long FASTEST_INTERVAL = 2000;
                mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

                // Create LocationSettingsRequest object using location request
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(mLocationRequest);
                LocationSettingsRequest locationSettingsRequest = builder.build();

                // Check whether location settings are satisfied
                // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient

                SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
                settingsClient.checkLocationSettings(locationSettingsRequest);

                // new Google API SDK v11 uses getFusedLocationProviderClient(this)
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
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());

                mFusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        printLocation(null, GPS_PROVIDER_DISABLED);
                    }
                });


                mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {

                            printLocation(location, GPS_GOT_COORDINATES);

                        }
                    }

                });
            }
        }

        private void stopLocationUpdates() {

            if (mFusedLocationClient != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallBack);
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = new ShopsVisit.DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(ShopsVisit.DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
            args.putInt(ShopsVisit.DummySectionFragment.pos1, position + 1);
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
                    return "Shop Visit".toUpperCase();
                case 1:
                    return "Shop Visit List".toUpperCase();
/*
			case 2:
                return "Shop gps".toUpperCase();
*/

            }
            return null;
        }
    }
}