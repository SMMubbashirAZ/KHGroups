package com.blazeminds.pos;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.legacy.app.ActionBarDrawerToggle;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.adapter.NavDrawerListAdapter;
import com.blazeminds.pos.adapter.OrderBookingChildAdapter;
import com.blazeminds.pos.fragments.About;
import com.blazeminds.pos.fragments.AttendanceFinal;
import com.blazeminds.pos.fragments.CustomerList;
import com.blazeminds.pos.fragments.DashBoard;
import com.blazeminds.pos.fragments.Expenses;
import com.blazeminds.pos.fragments.ItemListSalesOrder;
import com.blazeminds.pos.fragments.MerchandizingFinal;
import com.blazeminds.pos.fragments.OfflineTracking;
import com.blazeminds.pos.fragments.PaymentRecieving;
import com.blazeminds.pos.fragments.PhoneStatus;
import com.blazeminds.pos.fragments.ProductCatalog;
import com.blazeminds.pos.fragments.Profile;
import com.blazeminds.pos.fragments.ProgressReport;
import com.blazeminds.pos.fragments.Reports;
import com.blazeminds.pos.fragments.RoutePlan;
import com.blazeminds.pos.fragments.SaleOrderFinal;
import com.blazeminds.pos.fragments.SaleReturnFinal;
import com.blazeminds.pos.fragments.SalesOrder;
import com.blazeminds.pos.fragments.SalesOrderList;
import com.blazeminds.pos.fragments.SalesReturn;
import com.blazeminds.pos.fragments.SalesReturnList;
import com.blazeminds.pos.fragments.ShopStock;
import com.blazeminds.pos.fragments.ShopsVisit;
import com.blazeminds.pos.fragments.Support;
import com.blazeminds.pos.fragments.TravelExpense;
import com.blazeminds.pos.model.NavDrawerItem;
import com.blazeminds.pos.resources.Loader;
import com.blazeminds.pos.resources.UserSettings;
import com.blazeminds.pos.webservice_url.SyncData;

import java.util.ArrayList;
import java.util.List;

import static com.blazeminds.pos.Constant.MIN_CLICK_INTERVAL;
import static com.blazeminds.pos.Constant.NOTIFICATION_ID;
import static com.blazeminds.pos.Constant.customToast;
import static com.blazeminds.pos.Constant.networkAvailable;
import static com.blazeminds.pos.resources.UserSettings.FIRST_NOTIFY;
import static com.blazeminds.pos.resources.UserSettings.IS_FIRST_NOTIFY;

public class MainActivity extends FragmentActivity {
    
    private static final String TAG = MainActivity.class.getSimpleName();
    public static String FRAGMENT_TAG = MainActivity.TAG;
    public static int trackCount = 4;
    public static boolean granted = true;
    private static SharedPreferences saveLocStats;
    
    private static MenuItem SYNC_BUTTON;
    //Settings
    
    private Activity activity;
    private int LogoutAccessChk;
    private int SOChk;
    private int ROChk;
    private int CustChk;
    private int PaymentRecChk;
    private int ShopVisitChk;
    private int ExpenseChk;
    private int TravelExpenseChk;
    private int count = 0;
    private boolean autoSync;
    private PosDB db;
    
    private Loader loader;
    private DrawerLayout mDrawerLayout;
    
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    
    
    private int enableAttendanceMust;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private long lastClickTime = 0;
    
    
    /*
     * If you do not have any menus, you still need this function
     * in order to open or close the NavigationDrawer when the user
     * clicking the ActionBar app icon.
     */
    
    
    /*
     * When using the ActionBarDrawerToggle, you must call it during onPostCreate()
     * and onConfigurationChanged()
     */
    private PrefManager prefManager;
    
    public static void requestPermission(final Activity context) {
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            granted = false;
            List<String> permissions = new ArrayList<>();
            
            int p1 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
            if (p1 < 0) permissions.add(Manifest.permission.READ_PHONE_STATE);
            int p2 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            if (p2 < 0) permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

            if(! BuildConfig.FLAVOR.equalsIgnoreCase("lorenzo_industries")) {
                int p3 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (p3 < 0) permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int p4 = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
                if (p4 < 0) permissions.add(Manifest.permission.CAMERA);
            }
            
            
            final String[] p = permissions.toArray(new String[permissions.size()]);
            if (p.length > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("First we need some permissions to proceed next");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        
                        ActivityCompat.requestPermissions(context,
                                p,
                                Integer.parseInt(p.length + "01"));
                        
                        dialog.dismiss();
                    }
                });
                
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                
                builder.show();
                
            } else granted = true;
            
        }
    }
    @Override
    public void onDestroy() {
       finish();
        super.onDestroy();


    }
    @Override
    @SuppressWarnings("ResourceType")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        requestPermission(MainActivity.this);
        db = PosDB.getInstance(MainActivity.this);
        
        createNotificationChannel();
        SharedPreferences preferences = getSharedPreferences("APP_VERSION", MODE_PRIVATE);
        float appV = Float.parseFloat(BuildConfig.VERSION_NAME);
        float appV2 = Float.parseFloat(preferences.getString("app_version", String.valueOf(appV)));
        final String appPackageName = BuildConfig.APPLICATION_ID;
        
        FRAGMENT_TAG = MainActivity.TAG;

/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if ( ! checkLocationPermission( getApplicationContext() )) {
                new CountDownTimer(10000, 1000) {

                    public void onTick(long millisUntilFinished) {

                        //Status.setText( "application exiting in "+millisUntilFinished / 1000+" seconds" );

                        Toast.makeText( getApplicationContext(), "Location Permission Denied.\nApplication exiting in "+millisUntilFinished / 1000+" seconds", Toast.LENGTH_SHORT ).show();

                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        System.exit(0);
                    }

                }.start();

            }

        }
*/


/*
        Results = getApplicationContext().getSharedPreferences(FileName, 0);
        edit = Results.edit();

        autoSync = Results.getBoolean("autoSync", false);
*/
        
        
        ActionBar bar = getActionBar();
//for color
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0080bd"))); //#00C4CD
//for image
//        bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.settings_icon));
        
        
        mTitle = mDrawerTitle = getTitle();
        
        // load slide menu items
        // slide menu items
        String[] navMenuTitles = getResources().getStringArray(R.array.drawer_titles);
        
        // nav drawer icons from resources
        TypedArray navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
        
        
        String[] mDrawerItmes = getResources().getStringArray(R.array.drawer_titles);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        
        
        navDrawerItems = new ArrayList<>();
        
        db.OpenDb();
        
        int enableSaleOrder = db.getAppSettingsValueByKey("en_sale_order");
        int enableSaleOrderList = db.getAppSettingsValueByKey("en_sale_order_list");
        int enableSaleReturn = db.getAppSettingsValueByKey("en_sale_return");
        int enableSaleReturnList = db.getAppSettingsValueByKey("en_sale_return_list");
        int enablaPaymentCollection = db.getAppSettingsValueByKey("en_payment_collection");
        int enableShopList = db.getAppSettingsValueByKey("en_shop_list");
        int enableProductList = db.getAppSettingsValueByKey("en_product_list");
        int enableMerchandizing = db.getAppSettingsValueByKey("en_merchandizing");
        int enableExpense = db.getAppSettingsValueByKey("en_expense");
        int enableTravelExpense = db.getAppSettingsValueByKey("en_travel_expense");
        int enableShopVisit = db.getAppSettingsValueByKey("en_shop_visit");
        int enableProductCatalog = db.getAppSettingsValueByKey("en_product_catalog");
        int enableReports = db.getAppSettingsValueByKey("en_reports");
        int enablePhoneStatus = db.getAppSettingsValueByKey("en_phone_status");
        int enableAboutSoftware = db.getAppSettingsValueByKey("en_about_software");
        int enableOfflineTracking = db.getAppSettingsValueByKey("en_offline_tracking");
        int enableClockIn = db.getAppSettingsValueByKey("en_clock_in");
        int enableAdvanceDashboard = db.getAppSettingsValueByKey("en_advance_dashboard");
        int enableSupport = db.getAppSettingsValueByKey("en_support");
        int enableProgress_Report = db.getAppSettingsValueByKey("en_progress_reports");
        enableAttendanceMust = db.getAppSettingsValueByKey("attendance_must");
        int enableShopStock = db.getAppSettingsValueByKey("en_shop_stock");
        int enableRoutePlan = db.getAppSettingsValueByKey("en_route_plan");
        
        db.CloseDb();
        // adding nav drawer items to array
        // Dashboard
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(10, -1), 0));
        
        if (enableRoutePlan == 1) {
            // Route Plan
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(29, -1), 1));
        }
        if (enableClockIn == 1) {
            //Attendance
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[17], navMenuIcons.getResourceId(24, -1), 19));
        }

        if (enableSaleOrder == 1) {
            // Sales Order
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(11, -1), 2));
        }
        //Template Auto Item sales order
 //       navDrawerItems.add(new NavDrawerItem(navMenuTitles[21], navMenuIcons.getResourceId(11, -1), 22));

        if (enableSaleOrderList == 1) {
            // Sales Order List
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[22], navMenuIcons.getResourceId(12, -1), 3));
        }
        // Sales Execute List
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(13, -1)));
        if (enableSaleReturn == 1) {
            // Sales Return
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(14, -1), 4));
        }

        if (enableSaleReturnList == 1) {
            // Sales Return List
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[23], navMenuIcons.getResourceId(15, -1), 5));
        }
        
        if (enablaPaymentCollection == 1) {
            // Payment Recieving
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(16, -1), 6));
        }
        
        if (enableShopList == 1) {
            // Shop List
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(17, -1), 7));
        }
        
        if (enableProductList == 1) {
            // Item List (Inventory)
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(18, -1), 8));
        }
        
        if (enableMerchandizing == 1) {
            //Merchandizing
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(23, -1), 9));
        }
        
        
        if (enableExpense == 1) {
            //Expense
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(25, -1), 10));
        }
        
        if (enableTravelExpense == 1) {
            //Travel Expense
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(22, -1), 11));
        }
        
        if (enableShopVisit == 1) {
            //Shop Visit
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[10], navMenuIcons.getResourceId(26, -1), 12));
        }
        
        if (enableShopStock == 1) {
//             Shop Stock
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[11], navMenuIcons.getResourceId(28, -1), 13));
        }
        
        if (enableProductCatalog == 1) {
            //Product Catalog
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[12], navMenuIcons.getResourceId(22, -1), 14));
        }
        
        if (enableReports == 1) {
            //Reports
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[13], navMenuIcons.getResourceId(19, -1), 15));
        }
        
        if (enablePhoneStatus == 1) {
            //PHONE StATUS
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[14], navMenuIcons.getResourceId(20, -1), 16));
        }
        
        if (enableAboutSoftware == 1) {
            //ABOUT POS
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[15], navMenuIcons.getResourceId(21, -1), 17));
        }
        
        //ABOUT Pringting
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[11], navMenuIcons.getResourceId(7, -1)));
        if (enableOfflineTracking == 1) {
            //Offline Tracking
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[16], navMenuIcons.getResourceId(22, -1), 18));
        }
        

        if (enableSupport == 1) {
            //Support
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[18], navMenuIcons.getResourceId(27, -1), 20));
        }
        //progress report
        if(enableProgress_Report == 1) {
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[20], navMenuIcons.getResourceId(30, -1), 21));
        }
        navMenuIcons.recycle();
        
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mDrawerItmes));
        
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
        
        
        // Enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (BuildConfig.FLAVOR.equals("brands_unlimited"))
            getActionBar().setIcon(R.drawable.brandslogos);
        getActionBar().setHomeButtonEnabled(true);
        
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                
                
                getActionBar().setTitle(mTitle);
                
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
            }
            
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        // Set the default content area to item 0
        // when the app opens for the first time
        if (savedInstanceState == null) {
            navigateTo(0);
            
        }
        
        mDrawerList.setItemChecked(0, true);
        
        saveLocStats = getSharedPreferences("SAVE_LOC_STATS", MODE_PRIVATE);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        
        SYNC_BUTTON = menu.findItem(R.id.menu_sync);
        
        
        new SyncUpdateThread(MainActivity.this, SYNC_BUTTON, PosDB.getInstance(MainActivity.this));
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            
            return true;
        }
        
        
        switch (item.getItemId()) {
            
            case R.id.menu_sync: {
                
                mDrawerLayout.closeDrawer(mDrawerList);
                long currentClickTime = System.currentTimeMillis();
                long elapsedTime = currentClickTime - lastClickTime;
                
                lastClickTime = currentClickTime;
                Log.e("CLICK_TIME",
                        currentClickTime + " : " + elapsedTime + " : " + lastClickTime + " : " + MIN_CLICK_INTERVAL);
                if (elapsedTime <= MIN_CLICK_INTERVAL) {
                    Log.e("CLICK_TIME_1", "Returned");
                    Toast.makeText(activity, "Please wait while first sync complete", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("CLICK_TIME_2", "SYNC");
                    activity = MainActivity.this;
                    loader = new Loader();
                    
                    loader.showDialog(activity);
                    
                    new Sync().execute();
                    
                    
                }
                
                
                //startActivity( new Intent( MainActivity.this, TestingActivity.class ) );
            }
            break;
            
            
            case R.id.Menu_Profile: {
                
                
                //Profile
                if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
                    
                    
                    mDrawerLayout.closeDrawer(mDrawerList);
                    getSupportFragmentManager()
                            .beginTransaction()
//                        .setCustomAnimations(R.anim.enter_from_left, R.anim.enter_from_right)
                            .replace(R.id.content_frame,
                                    Profile.newInstance(),
                                    Profile.TAG).addToBackStack("Profile").commit();
                    // performNoBackStackTransaction(getSupportFragmentManager(), Profile.TAG,Profile.newInstance());
                }
                
            }
            return true;
            
            
            case R.id.Menu_Logout: {
                
                Log.e("LogoutDisable", String.valueOf(LogoutAccessChk));
                try {
                    mDrawerLayout.closeDrawer(mDrawerList);
                    db.OpenDb();
                    SOChk = db.CheckSalesOrderUpdate();
                    ROChk = db.CheckReturnOrderUpdate();
                    CustChk = db.CheckCustomerUpdate();
                    PaymentRecChk = db.CheckPaymentRecievedUpdate();
                    ShopVisitChk = db.CheckShopVisitUpdate();
                    ExpenseChk = db.CheckExpenseUpdate();
                    TravelExpenseChk = db.CheckTravelExpenseUpdate();
                    LogoutAccessChk = db.chkMobUserLogout();
                    db.CloseDb();
                    
                    if (SOChk >= 1 || ROChk >= 1 || CustChk >= 1 || PaymentRecChk >= 1 || ShopVisitChk >= 1 || ExpenseChk >= 1 || TravelExpenseChk >= 1) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(AppContextProvider.getContext(), "Please synchronize the data", Toast.LENGTH_LONG).show();
                            }
                        });
                        
                    } else {
                        
                        switch (LogoutAccessChk) {
                            
                            case 1: {
                                AreYouSure();
                            }
                            break;
                            
                            case 0: {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(AppContextProvider.getContext(), "Logout Disabled", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                
                            }
                            break;
                            
                            default: {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(AppContextProvider.getContext(), "DB issue\nVal: " + LogoutAccessChk, Toast.LENGTH_LONG).show();
                                    }
                                });
                                
                            }
                            break;
                            
                        }
                    }
                    
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.fillInStackTrace());
                }
                return true;
                
            }
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    private void navigateTo(int position) {
        Log.v(TAG, "List View Item: " + position);
        
        long GetPrevSelection = -1;
        
        
        String empTimeIn;
        switch (position) {
            
            
            case 0:
                //Dashboard
                Log.e("CheckFragment!@#", FRAGMENT_TAG);
                //requestPermission(MainActivity.this);
                
                if (!FRAGMENT_TAG.equals(DashBoard.TAG)) {
                    FRAGMENT_TAG = DashBoard.TAG;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, DashBoard.newInstance(), DashBoard.TAG).commit();
                    mDrawerLayout.closeDrawer(mDrawerList);
                    trackCount = 1;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                
                
                break;
            
            case 1:
                
                //Support
                
                requestPermission(MainActivity.this);
            {
                if (granted) {
                    
                    
                    
                    if (!FRAGMENT_TAG.equals(RoutePlan.TAG)) {
                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                        FRAGMENT_TAG = RoutePlan.TAG;
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, RoutePlan.newInstance(), RoutePlan.TAG).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        trackCount = 0;
                    } else mDrawerLayout.closeDrawer(mDrawerList);
                }
            }
            
            break;
            
            case 2:
                
                // Sale Order
                requestPermission(MainActivity.this);
                
                if (granted) {
                    
                    
                    if (!FRAGMENT_TAG.equals(SaleOrderFinal.TAG)) {
                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                        FRAGMENT_TAG = SaleOrderFinal.TAG;
                        if (!checkLocationPermission(getApplicationContext())) {
                            Toast.makeText(AppContextProvider.getContext(), "Location Permission Denied", Toast.LENGTH_LONG).show();
                        } else {
                            
                            db.OpenDb();
                            empTimeIn = db.getMobEmpTimeIn();
                            
                            db.CloseDb();
                            
                            if (enableAttendanceMust == 1) {
                                if (empTimeIn.equalsIgnoreCase("1")) {
                                    
                                    getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.content_frame, SaleOrderFinal.newInstance(), SaleOrderFinal.TAG).commit();
                                    
                                    mDrawerLayout.closeDrawer(mDrawerList);
                                } else {
                                    customToast(AppContextProvider.getContext(), "Can't Create Sale Order, Time In first to Create Order");
                                }
                            } else {
                                
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.content_frame, SaleOrderFinal.newInstance(), SaleOrderFinal.TAG).commit();
                                
                                mDrawerLayout.closeDrawer(mDrawerList);
                            }
                            
                        }
                        trackCount = 0;
                    } else mDrawerLayout.closeDrawer(mDrawerList);
                }
                break;
            
            case 3:
                
                //Sale Order List
                requestPermission(MainActivity.this);
                if (granted) {
                    
                    if (!FRAGMENT_TAG.equals(SalesOrderList.TAG)) {
                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                        FRAGMENT_TAG = SalesOrderList.TAG;
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame,
                                        SalesOrderList.newInstance(),
                                        SalesOrderList.TAG).commit();
                        
                        mDrawerLayout.closeDrawer(mDrawerList);
                        trackCount = 0;
                    } else mDrawerLayout.closeDrawer(mDrawerList);
                }
                break;
            
            
            case 4:
                //Sale Return
                
                requestPermission(MainActivity.this);
                if (!granted) {
                    return;
                }
               
                
                if (!FRAGMENT_TAG.equals(SaleReturnFinal.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    db.OpenDb();
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    FRAGMENT_TAG = SaleReturnFinal.TAG;
                    if (enableAttendanceMust == 1) {
                        if (empTimeIn.equalsIgnoreCase("1")) {
                            
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame, SaleReturnFinal.newInstance(), SaleReturnFinal.TAG).commit();
                            mDrawerLayout.closeDrawer(mDrawerList);
                            
                        } else {
                            customToast(AppContextProvider.getContext(), "Can't Create Sale Return, Time In first to Create Sale Return");
                        }
                    } else {
                        
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, SaleReturnFinal.newInstance(), SaleReturnFinal.TAG).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            
            case 5:
                
                //Sale Return List
                requestPermission(MainActivity.this);
                if (!granted) {
                    return;
                }
                
                
                
                
                if (!FRAGMENT_TAG.equals(SalesReturnList.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    FRAGMENT_TAG = SalesReturnList.TAG;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame,
                                    SalesReturnList.newInstance(),
                                    SalesReturnList.TAG).commit();
                    
                    mDrawerLayout.closeDrawer(mDrawerList);
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            
            case 6:
                
                //Payment Collection List
                requestPermission(MainActivity.this);
                if (!granted) {
                    return;
                }
               
                if (!FRAGMENT_TAG.equals(PaymentRecieving.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    db.OpenDb();
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    FRAGMENT_TAG = PaymentRecieving.TAG;
                    if (enableAttendanceMust == 1) {
                        if (empTimeIn.equalsIgnoreCase("1")) {
                            
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame,
                                            PaymentRecieving.newInstance(),
                                            PaymentRecieving.TAG).commit();
                            mDrawerLayout.closeDrawer(mDrawerList);
                            
                        } else {
                            customToast(AppContextProvider.getContext(), "Can't Create Payment, Time In first to Create Payment");
                        }
                    } else {
                        
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame,
                                        PaymentRecieving.newInstance(),
                                        PaymentRecieving.TAG).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                    
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            
            
            case 7:
                requestPermission(MainActivity.this);
                if (!granted) {
                    return;
                }
              
                // Shop List / Customer List
                if (!FRAGMENT_TAG.equals(CustomerList.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    
                    FRAGMENT_TAG = CustomerList.TAG;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame,
                                    CustomerList.newInstance(),
                                    CustomerList.TAG).commit();
                    
                    mDrawerLayout.closeDrawer(mDrawerList);
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            
            case 8:
                
               
                //Product List
                if (!FRAGMENT_TAG.equals(ItemListSalesOrder.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    
                    FRAGMENT_TAG = ItemListSalesOrder.TAG;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.content_frame,
                                    ItemListSalesOrder.newInstance(),
                                    ItemListSalesOrder.TAG).commit();
                    
                    mDrawerLayout.closeDrawer(mDrawerList);
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            
            case 9:
                requestPermission(MainActivity.this);
                if (!granted) {
                    return;
                }
               
                //Merchandizing
                if (!FRAGMENT_TAG.equals(MerchandizingFinal.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    db.OpenDb();
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    FRAGMENT_TAG = MerchandizingFinal.TAG;
                    if (enableAttendanceMust == 1) {
                        if (empTimeIn.equalsIgnoreCase("1")) {
                            
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame,
                                            MerchandizingFinal.newInstance(),
                                            MerchandizingFinal.TAG).commit();
                            
                            mDrawerLayout.closeDrawer(mDrawerList);
                        } else {
                            customToast(AppContextProvider.getContext(), "Can't Create Merchandizing, Time In first to Create Merchandizing");
                        }
                    } else {
                        
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame,
                                        MerchandizingFinal.newInstance(),
                                        MerchandizingFinal.TAG).commit();
                        
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                
                break;
            
            case 10:
                requestPermission(MainActivity.this);
                if (!granted) {
                    return;
                }
               
                //Expense
                if (!FRAGMENT_TAG.equals(Expenses.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    db.OpenDb();
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    FRAGMENT_TAG = Expenses.TAG;
                    if (enableAttendanceMust == 1) {
                        if (empTimeIn.equalsIgnoreCase("1")) {
                            
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame,
                                            Expenses.newInstance(),
                                            Expenses.TAG).commit();
                            mDrawerLayout.closeDrawer(mDrawerList);
                        } else {
                            customToast(AppContextProvider.getContext(), "Can't Create Expense, Time In first to Create Expense");
                            
                        }
                    } else {
                        
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame,
                                        Expenses.newInstance(),
                                        Expenses.TAG).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            
            
            case 11:
                requestPermission(MainActivity.this);
                if (!granted) {
                    return;
                }
               
                //Travel Expense
                if (!FRAGMENT_TAG.equals(TravelExpense.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    db.OpenDb();
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    
                    FRAGMENT_TAG = TravelExpense.TAG;
                    
                    if (enableAttendanceMust == 1) {
                        if (empTimeIn.equalsIgnoreCase("1")) {
                            
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame,
                                            TravelExpense.newInstance(),
                                            TravelExpense.TAG).commit();
                            mDrawerLayout.closeDrawer(mDrawerList);
                            
                        } else {
                            customToast(AppContextProvider.getContext(), "Can't Create Travel Expense, Time In first to Create Travel Expense");
                            
                        }
                    } else {
                        
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame,
                                        TravelExpense.newInstance(),
                                        TravelExpense.TAG).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                
                break;
            
            case 12:
                requestPermission(MainActivity.this);
                if (!granted) {
                    return;
                }
               
                //ShopVisit
                if (!FRAGMENT_TAG.equals(ShopsVisit.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    db.OpenDb();
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    FRAGMENT_TAG = ShopsVisit.TAG;
                    if (enableAttendanceMust == 1) {
                        if (empTimeIn.equalsIgnoreCase("1")) {
                            
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame,
                                            ShopsVisit.newInstance(),
                                            ShopsVisit.TAG).commit();
                            mDrawerLayout.closeDrawer(mDrawerList);
                            
                        } else {
                            customToast(AppContextProvider.getContext(), "Can't Create Shop Visit, Time In first to Create Shop Visit");
                            
                        }
                    } else {
                        
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame,
                                        ShopsVisit.newInstance(),
                                        ShopsVisit.TAG).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            
            case 13:
                requestPermission(MainActivity.this);
                if (!granted) {
                    return;
                }
                
                //Shop Stock
                if (!FRAGMENT_TAG.equals(ShopStock.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    db.OpenDb();
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    FRAGMENT_TAG = ShopStock.TAG;
                    if (enableAttendanceMust == 1) {
                        if (empTimeIn.equalsIgnoreCase("1")) {
                            
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame,
                                            ShopStock.newInstance(),
                                            ShopStock.TAG).commit();
                            mDrawerLayout.closeDrawer(mDrawerList);
                            
                        } else {
                            customToast(AppContextProvider.getContext(), "Can't Create Shop Stock, Time In first to Create Shop Stock");
                            
                        }
                    } else {
                        
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame,
                                        ShopStock.newInstance(),
                                        ShopStock.TAG).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            
            
            case 14:
                requestPermission(MainActivity.this);
                if (!granted) {
                    return;
                }
               
                //PDF Catalog
                if (!FRAGMENT_TAG.equals(ProductCatalog.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    db.OpenDb();
                    String enableCatalog = db.getMobEmpEnableCatalog();
                    db.CloseDb();
                    
                    if (enableCatalog.equalsIgnoreCase("1")) {
                        FRAGMENT_TAG = ProductCatalog.TAG;
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame,
                                        ProductCatalog.newInstance(),
                                        ProductCatalog.TAG).commit();
                        //performNoBackStackTransaction(getSupportFragmentManager(), ProductCatalog.TAG, ProductCatalog.newInstance());
                        mDrawerLayout.closeDrawer(mDrawerList);
                        
                    } else {
                        //Toast.makeText(getApplicationContext(), "Catalog Disabled ", Toast.LENGTH_LONG).show();
                        customToast(AppContextProvider.getContext(), "Catalog Disabled");
                    }
                    //startActivity( new Intent( MainActivity.this, AndroidDatabaseManager.class) );
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 15:
                
                if (!networkAvailable()) {
                    Toast.makeText(AppContextProvider.getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    //CustomDialogNoInternet();
                } else {
                    //Reports
                    if (!FRAGMENT_TAG.equals(Reports.TAG)) {
                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                        FRAGMENT_TAG = Reports.TAG;
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame,
                                        Reports.newInstance(),
                                        Reports.TAG).commit();
                        //performNoBackStackTransaction(getSupportFragmentManager(), Reports.TAG, Reports.newInstance());
                        
                        mDrawerLayout.closeDrawer(mDrawerList);
                    } else mDrawerLayout.closeDrawer(mDrawerList);
                    trackCount = 0;
                }
                
                
                break;
            
            
            case 16:
               
                //Phone Status
                if (!FRAGMENT_TAG.equals(PhoneStatus.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    FRAGMENT_TAG = PhoneStatus.TAG;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame,
                                    PhoneStatus.newInstance(),
                                    PhoneStatus.TAG).commit();
                    //performNoBackStackTransaction(getSupportFragmentManager(), PhoneStatus.TAG, PhoneStatus.newInstance());
                    mDrawerLayout.closeDrawer(mDrawerList);
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            
            case 17:
             
                //About
                if (!FRAGMENT_TAG.equals(About.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    FRAGMENT_TAG = About.TAG;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame,
                                    About.newInstance(),
                                    About.TAG).commit();
                    //performNoBackStackTransaction(getSupportFragmentManager(), About.TAG, About.newInstance());
                    mDrawerLayout.closeDrawer(mDrawerList);
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            
            case 18:
                requestPermission(MainActivity.this);
                if (!granted) {
                    return;
                }
                
                //About
                if (!FRAGMENT_TAG.equals(OfflineTracking.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    FRAGMENT_TAG = OfflineTracking.TAG;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame,
                                    OfflineTracking.newInstance(),
                                    OfflineTracking.TAG).commit();
                    mDrawerLayout.closeDrawer(mDrawerList);
                    //performNoBackStackTransaction(getSupportFragmentManager(),OfflineTracking.TAG,OfflineTracking.newInstance());
                    mDrawerLayout.closeDrawer(mDrawerList);
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            
            
            case 19:
                requestPermission(MainActivity.this);
                if (!granted) {
                    return;
                }
                
                //Attendance
                if (!FRAGMENT_TAG.equals(AttendanceFinal.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    FRAGMENT_TAG = AttendanceFinal.TAG;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame,
                                    AttendanceFinal.newInstance(),
                                    AttendanceFinal.TAG).commit();
                    mDrawerLayout.closeDrawer(mDrawerList);
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            
            case 20:
               
                //Support
                if (!FRAGMENT_TAG.equals(Support.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    FRAGMENT_TAG = Support.TAG;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame,
                                    Support.newInstance(),
                                    Support.TAG).commit();
                    mDrawerLayout.closeDrawer(mDrawerList);
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 21:

                //Support
                if (!FRAGMENT_TAG.equals(ProgressReport.TAG)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    FRAGMENT_TAG = Support.TAG;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame,
                                    ProgressReport.newInstance(),
                                    ProgressReport.TAG).commit();
                    mDrawerLayout.closeDrawer(mDrawerList);
                    trackCount = 0;
                } else mDrawerLayout.closeDrawer(mDrawerList);
                break;
            
        }
        
        
    }
    
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
        
        
    }
    
    @Override
    public void onBackPressed() {
        
        
        if (SalesOrder.popupWindow != null) {
            
            if (SalesOrder.popupWindow.isShowing()) {
                SalesOrder.popupWindow.dismiss();
                SalesOrder.popupWindow = null;
                return;
            }
            
        }


        if (SalesReturn.popupWindow != null) {
            
            if (SalesReturn.popupWindow.isShowing()) {
                SalesReturn.popupWindow.dismiss();
                SalesReturn.popupWindow = null;
                return;
            }
            
        }


        int counting = getSupportFragmentManager().getBackStackEntryCount();
        updateSync(MainActivity.this);

//        int counting = getSupportFragmentManager().getBackStackEntryCount();
//        Log.d(TAG, "onBackPressed: " + counting);
        if (trackCount == 0) {
            trackCount = 1;
            mDrawerLayout.closeDrawers();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, DashBoard.newInstance(), DashBoard.TAG).commit();

        } else if (trackCount == 1) {
            trackCount = 2;
            Toast.makeText(this, "Press back one more time to exit...", Toast.LENGTH_SHORT).show();
        } else {
            trackCount = 0;
            finish();
            finishAffinity();
        }
//        }

//       /* Log.e("CheckFragmentTag",
//                "Tag : " + FRAGMENT_TAG + " Count : " + counting + " : " + trackCount);*/
//
//        if (FRAGMENT_TAG.equals(SaleOrderFinal.TAG)) {
//
//            trackCount = 1;
//
//            mDrawerLayout.closeDrawers();
//
//
//            FRAGMENT_TAG = DashBoard.TAG;
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
//                    DashBoard.newInstance(), DashBoard.TAG).commit();
//        } else if ((FRAGMENT_TAG.equals("DashBoard") && counting == 0) || trackCount == 1) {
//            count++;
//            if (count == 2) {
//                super.onBackPressed();
//                finish();
//            } else {
//                Toast.makeText(MainActivity.this, "Press back again to close this app",
//                        Toast.LENGTH_SHORT).show();
//
//            }
//        } else if (counting ==1 ) {
//
//            trackCount = 0;
//            if (FRAGMENT_TAG.equals(ShopsVisit.TAG)) {
//
//
//                mDrawerLayout.closeDrawers();
//
//
//                FRAGMENT_TAG = DashBoard.TAG;
//                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
//                        DashBoard.newInstance(), DashBoard.TAG).commit();
//            }else{
//
////                trackCount = 0;
//                getSupportFragmentManager().popBackStackImmediate();
//            }
//
//            /*  getSupportFragmentManager().p;*/
//            /**/
//
//
//        } else {
//
//            trackCount = 1;
//
//            mDrawerLayout.closeDrawers();
//
//
//            FRAGMENT_TAG = DashBoard.TAG;
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
//                    DashBoard.newInstance(), DashBoard.TAG).commit();
//        }
        
    }
    
    private void AreYouSure() {
        
        final Dialog dialog = new Dialog(MainActivity.this);
        
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_are_you_sure);
        //dialog.getWindow().setBac(getResources().getColor(R.color.login_bg));
        // dialog.getWindow().
        
        
        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview
        
        //  dialog.setTitle(ItemName);
        Button Yes = dialog.findViewById(R.id.YesBtn);
        Button No = dialog.findViewById(R.id.NoBtn);
        
        
        dialog.show();
        
        Yes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                db.OpenDb();
                //db.mUserLogout();
                db.DeleteAllRecords();
                //String res = db.getSettingsData();
                db.CloseDb();
                
                cancelNotification(MainActivity.this);
                autoSync = false;
                
                stopService(new Intent(MainActivity.this, MyService.class));
                dialog.dismiss();
                
                prefManager = new PrefManager(MainActivity.this);
                prefManager.setFirstTimeLaunch(true);

                UserSettings userSettings=UserSettings.getInstance(MainActivity.this);
                userSettings.set(FIRST_NOTIFY,"");
                userSettings.set(IS_FIRST_NOTIFY,false);


                finish();
                Intent i;
                i = new Intent(getApplicationContext(), WelcomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(AppContextProvider.getContext(), "You have Signed Out Successfully", Toast.LENGTH_LONG).show();

//                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//                        notificationManager.cancelAll();
                    }
                });
                
            }
        });
        
        No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
    }
    
    private void cancelNotification(Context ctx) {
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//        nMgr.cancelAll();
        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = (NotificationManager) ctx.getSystemService(NotificationManager.class);
            notificationManager.cancelAll();
        }
        nMgr.cancel(NOTIFICATION_ID);
    }
    
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    
                    
                } else if (grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                
                    
                }
                
            }
            break;
            
            case 101: {
                
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) granted = false;
                
            }
            break;
            
            case 201: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    granted = false;
                }
            }
            break;
            
            case 301: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED || grantResults[2] == PackageManager.PERMISSION_DENIED) {
                    granted = false;
                }
            }
            break;
            
            case 401: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED || grantResults[2] == PackageManager.PERMISSION_DENIED || grantResults[3] == PackageManager.PERMISSION_DENIED) {
                    granted = false;
                }
            }
            break;
            
        }
    }
    
    private boolean checkLocationPermission(Context c) {
        
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = c.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification";
            String description = "Push Notification For Fatima Communications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    public void updateSync(Context context) {
        
        Activity activity = (Activity) context;
        
        if (SYNC_BUTTON != null) {
            new SyncUpdateThread(activity, SYNC_BUTTON, PosDB.getInstance(MainActivity.this));
        } else Log.e("MenuItem", "MenuItem is null");
        
    }
    
    
    private class DrawerItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.v(TAG, "ponies");
            
            //mDrawerItmes.get(position);
            NavDrawerItem pos = navDrawerItems.get(position);
            Log.v("Position - ThingToDo", pos.getThingToDo() + "");
            
            navigateTo(pos.getThingToDo());
        }
    }
    
    class SyncUpdateThread {
        
        final Activity act;
        final MenuItem menuItem2;
        final PosDB db1;
        
        SyncUpdateThread(Activity ac, MenuItem menu, PosDB db) {
            
            this.act = ac;
            this.menuItem2 = menu;
            this.db1 = db;
            
            
            DoFunction();
        }
        
        
        void DoFunction() {
            
            
            Drawable draw = menuItem2.getIcon();
            
            
            db1.OpenDb();
            SOChk = db1.CheckSalesOrderUpdate();
            ROChk = db1.CheckReturnOrderUpdate();
            CustChk = db1.CheckCustomerUpdate();
            PaymentRecChk = db1.CheckPaymentRecievedUpdate();
            ShopVisitChk = db1.CheckShopVisitUpdate();
            ExpenseChk = db1.CheckExpenseUpdate();
            TravelExpenseChk = db1.CheckTravelExpenseUpdate();
            LogoutAccessChk = db1.chkMobUserLogout();
            db1.CloseDb();
            
            Log.d("CHkUpload",
                    SOChk + " so " + ROChk + " sr " + PaymentRecChk + " p " + CustChk + " c " + ExpenseChk + " exp " + TravelExpenseChk + " traExp " + ShopVisitChk + " sv " + " \nLogout: " + LogoutAccessChk);
            
            if (SOChk >= 1 || ROChk >= 1 || CustChk >= 1 || PaymentRecChk >= 1 || ShopVisitChk >= 1 || ExpenseChk >= 1 || TravelExpenseChk >= 1) {
                menuItem2.setEnabled(true);
                
                int colorARGB = R.color.yellow;
                draw.setColorFilter(act.getResources().getColor(colorARGB), PorterDuff.Mode.SRC_ATOP);
                //draw.setColorFilter((Color)R.color.dark_green, PorterDuff.Mode.SRC_ATOP);
            } else/* if( SYNC_UPLOAD_TIME == 0 )*/ {
                /*SYNC_BUTTON.setEnabled(false);*/
                
                int colorARGB = R.color.notificaton_light;
                draw.setColorFilter(act.getResources().getColor(colorARGB), PorterDuff.Mode.SRC_ATOP);
                
                //draw.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
            }
        }
        
    }
    
    private class Sync extends AsyncTask<String, String, String> {
        
        
        @Override
        protected String doInBackground(String... strings) {
            
            
            new SyncData(loader, activity, db);


            /*SyncData syncData = new SyncData();
            GetSalesmanSettings(empID);*/
            
            return null;
        }
        
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            
            
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // don't restore state
        super.onSaveInstanceState(new Bundle());
    }
}
