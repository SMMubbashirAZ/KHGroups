package com.blazeminds.pos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;
import com.likebamboo.widget.SwipeListView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Saad Kalim on 06-Apr-15.
 */

public class Constant {
    
    public static final String CODE_COLUMN = "Code";

    public static final String CART_LIST = "cart_list_dummy";
    public static final String CART_LIST_PRICE = "cart_list_dummy_price";
    public static final String CODE_PIC = "Picture";
    //    public static final String CODE_PICTXT = "Picture";
    public static final String FIRST_COLUMN = "Invent Name";
    public static final String SECOND_COLUMN = "Brand Name";
    public static final String THIRD_COLUMN = "Item Type";
    public static final String FOURTH_COLUMN = "Price";
    public static final String FIFTH_COLUMN = "Route";

    public static final String REPORT_FIRST_COLUMN = "REPORT_FIRST_COLUMN";
    public static final String REPORT_SECOND_COLUMN = "REPORT_SECOND_COLUMN";
    public static final String REPORT_THIRD_COLUMN = "REPORT_THIRD_COLUMN";
    public static final String REPORT_FOURTH_COLUMN = "REPORT_FOURTH_COLUMN";
    public static final String REPORT_FIFTH_COLUMN = "REPORT_FIFTH_COLUMN";
    public static final String REPORT_SIXTH_COLUMN = "REPORT_SIXTH_COLUMN";
    
    public static final String OID_COLUMN = "o_id";
    public static final String CID_COLUMN = "c_id";
    public static final String EID_COLUMN = "e_id";
    public static final String VAL_COLUMN = "val";
    public static final String NOTES_COLUMN = "notes";
    public static final String START_DATE_COLUMN = "start_date";
    public static final String DATE_COLUMN = "date";
    public static final String TOTAL_COLUMN = "total";
    public static final String LATITUDE_COLUMN = "latitude";
    public static final String LONGITUDE_COLUMN = "longitude";
    public static final String NET_OID_COLUMN = "net_oid";
    public static final String DELETE_ITEM_COLUMN = "delete_item";
    public static final String EXE_COMPLETE_COLUMN = "exe_complete";
    public static final String EXE_DATE_COLUMN = "exe_date";
    public static final String AMOUNT_RECIEVED_COLUMN = "amt_rec";
    public static final String DISCOUNT_COLUMN = "so_discount";
    public static final String PAYMENT_TYPE_COLUMN = "so_payment_type";
    public static final String SELECTED_DISTRIBUTOR_COLUMN = "so_selected_distributor";
    public static final String SRID_COLUMN = "sr_id";
    public static final String SRCID_COLUMN = "src_id";
    public static final String SREID_COLUMN = "sre_id";
    public static final String SRVAL_COLUMN = "srval";
    public static final String SRNOTES_COLUMN = "srnotes";
    public static final String SRSTART_DATE_COLUMN = "sr_start_date";
    public static final String SRDATE_COLUMN = "srdate";
    public static final String SRTOTAL_COLUMN = "srtotal";
    public static final String SRLATITUDE_COLUMN = "srlatitude";
    public static final String SRLONGITUDE_COLUMN = "srlongitude";
    public static final String SRNET_OID_COLUMN = "srnet_oid";
    public static final String SRDELETE_ITEM_COLUMN = "srdelete_item";
    public static final String SRRETURN_REASON_COLUMN = "srreturn_reason";
    public static final String SRDISCOUNT_COLUMN = "sr_discount";
    public static final String SRSALE_TYPE_COLUMN = "sr_sale_type";
    public static final String SRSELECTED_DISTRIBUTOR_COLUMN = "sr_selected_distributor";
    public static final String ORDER_COLUMN = "Sales Order";
    public static final String ORDER_ID_COLUMN = "Order ID";
    public static final String ORDER_DATE_COLUMN = "DATE";
    public static final String ORDER_CUST_COLUMN = "Customer Name";
    public static final String ORDER_AMOUNT_COLUMN = "AMOUNT";
    public static final String ORDER_NEW_AMOUNT_COLUMN = "NEW_AMOUNT";
    public static final String ORDER_EXE_COMPLETE = "EXE_COMPLETE";
    public static final String ORDER_CONFIRM_COLUMN = "confirm";
    public static final String EDIT_ORDER_ITEMID = "item_id";
    public static final String EDIT_ORDER_ITEM = "item_name";
    public static final String EDIT_ORDER_OLDQTY = "old_qty";
    public static final String EDIT_ORDER_NEWQTY = "new_qty";
    public static final String EDIT_ORDER_EXEQTY = "exe_qty";
    public static final String EDIT_ORDER_TOTAL2 = "total2";
    public static final String EDIT_ORDER_TOTALEXE = "total_exe";
    public static final String EDIT_ORDER_ITEM_DETAILS = "item_details";
    public static final String EDIT_ORDER_ITEM_DISCOUNT = "item_discount";
    public static final long MIN_CLICK_INTERVAL = 2 * 1000; // 2 mili seconds = 2 seconds
    public static final String SPINNER_COLUMN = "spinner";
    public static final int NOTIFICATION_ID = 9001;
    public static String TOTAL2_COLUMN = "total_2";
    public static String TOTALEXE_COLUMN = "total_3";
    public static String SRTOTAL2_COLUMN = "total_2";
    public static boolean DIALOG_VISIBLE = false;
    private static AlertDialog.Builder builder;
    private static String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
    
    public static void AreYouSure(final Context ctx, final PosDB db, final int cases, final String orderID, final SwipeListView lView, final TextView noItemTxt) {
        
        final Dialog dialog = new Dialog(ctx);
        
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_are_you_sure);
        
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview
        
        
        //  dialog.setTitle(ItemName);
        Button Yes = dialog.findViewById(R.id.YesBtn);
        Button No = dialog.findViewById(R.id.NoBtn);
        
        
        dialog.show();
        
        Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                switch (cases) {
                    
                    case 1: { //Delete order
                        db.OpenDb();
                        db.deleteSalesOrder(Integer.parseInt(orderID));
                        /*db.UpdateChangesSalesOrder( Integer.parseInt(orderID), 1 );*/
                        db.CloseDb();
                        
                        populateOrderList(lView, db, ctx, noItemTxt);
                        
                        Toast.makeText(AppContextProvider.getContext(), "Order Deleted", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    
                    case 2: { //Confirm Order
                        db.OpenDb();
                        db.confirmSalesOrder(Integer.parseInt(orderID));
                        db.CloseDb();
                        
                        Toast.makeText(AppContextProvider.getContext(), "Item Confirmed", Toast.LENGTH_SHORT).show();
                        
                    }
                    break;
                    
                    case 3: { //Delete Return order
                        db.OpenDb();
                        db.deleteSalesReturn(Integer.parseInt(orderID));
                        /*db.UpdateChangesSalesOrder( Integer.parseInt(orderID), 1 );*/
                        db.CloseDb();
                        
                        populateReturnOrderList(lView, db, ctx, noItemTxt);
                        
                        Toast.makeText(AppContextProvider.getContext(), "Order Deleted", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    
                    
                }
                
                dialog.dismiss();
                
            }
        });
        
        No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        
    }
    public static String prettyCount(Number number) {
        char[] suffix = {' ', 'K', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    public static int average2(int calls,int diff){
        if (diff != 0) {
            Float result = Float.valueOf(calls)/Float.valueOf(diff);
            Log.d("TAG", "average: "+result);
            return Math.round(result * 100);
        }else{
            return 0;
        }
    }
    public static void populateOrderList(SwipeListView itemList, PosDB db, Context ctx, TextView NoItemTxt) {
        
        ArrayList<HashMap<String, String>> data;
        db.OpenDb();
        data = db.getSalesOrderForList();
        
        db.CloseDb();
        
        if (data.size() > 0) {
            NoItemTxt.setVisibility(View.GONE);
            itemList.setVisibility(View.VISIBLE);
            
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) ctx).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displaymetrics);
            
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            
            itemList.setRightViewWidth(width / 2);
            
            SalesOrderListViewAdapter adapter1 = new SalesOrderListViewAdapter(ctx, itemList.getRightViewWidth(), data, db, itemList, NoItemTxt);
            
            itemList.setAdapter(adapter1);
            
        } else {
            NoItemTxt.setVisibility(View.VISIBLE);
            itemList.setVisibility(View.GONE);
        }
    }



/*    public void AskForGPSEnable(final Activity activity,String name) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Application wants to open GPS setting "+name;

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                 activity.startActivityForResult( new Intent(action),1111);
                                d.dismiss();

                            }
                        });
                *//*.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });*//*
        builder.setCancelable(false);
  *//*      if (!activity.isFinishing())*//*
            builder.create().show();



    }*/
    
    public static void populateSalesExecuteList(SwipeListView itemList, PosDB db, Context ctx, TextView NoItemTxt, String DateTime) {
        
        ArrayList<HashMap<String, String>> data;
        db.OpenDb();
        data = db.getSalesOrderForListFOREXECUTE(DateTime);
        db.CloseDb();
        
        if (data.size() > 0) {
            NoItemTxt.setVisibility(View.GONE);
            itemList.setVisibility(View.VISIBLE);
            
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) ctx).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displaymetrics);
            
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            
            itemList.setRightViewWidth(width / 2);
            
            SalesExecuteListViewAdapter adapter1 = new SalesExecuteListViewAdapter(ctx, itemList.getRightViewWidth(), data, db, itemList, NoItemTxt, DateTime);
            
            itemList.setAdapter(adapter1);
            
        } else {
            NoItemTxt.setVisibility(View.VISIBLE);
            itemList.setVisibility(View.GONE);
        }
    }
    
    public static void populateSalesExecuteList2(SwipeListView itemList, PosDB db, Context ctx, TextView NoItemTxt, String DateTime, ListView headerList) {
        
        ArrayList<HashMap<String, String>> data;
        db.OpenDb();
        data = db.getSalesOrderForListFOREXECUTE(DateTime);
        db.CloseDb();
        
        if (data.size() > 0) {
            NoItemTxt.setVisibility(View.GONE);
            itemList.setVisibility(View.VISIBLE);
            headerList.setVisibility(View.VISIBLE);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) ctx).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displaymetrics);
            
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            
            itemList.setRightViewWidth(width / 2);
            
            SalesExecuteListViewAdapter adapter1 = new SalesExecuteListViewAdapter(ctx, itemList.getRightViewWidth(), data, db, itemList, NoItemTxt, DateTime);
            
            itemList.setAdapter(adapter1);
            
        } else {
            NoItemTxt.setVisibility(View.VISIBLE);
            itemList.setVisibility(View.GONE);
            headerList.setVisibility(View.GONE);
        }
    }
    
    public static void populateReturnOrderList(SwipeListView itemList, PosDB db, Context ctx, TextView NoItemTxt) {
        
        ArrayList<HashMap<String, String>> data;
        db.OpenDb();
        data = db.getSalesReturnForList();
        
        db.CloseDb();
        
        if (data.size() > 0) {
            NoItemTxt.setVisibility(View.GONE);
            itemList.setVisibility(View.VISIBLE);
            
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) ctx).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displaymetrics);
            
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            
            itemList.setRightViewWidth(width / 2);
            
            // Previous it was SaleOrderListViewAdapter
            SalesReturnListViewAdapter adapter1 = new SalesReturnListViewAdapter(ctx, itemList.getRightViewWidth(), data, db, itemList, NoItemTxt);
            
            itemList.setAdapter(adapter1);
            
        } else {
            NoItemTxt.setVisibility(View.VISIBLE);
            itemList.setVisibility(View.GONE);
        }
    }
    
    public static void AskForGPSEnabled(final Context ctx) {
        
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        final String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Do you want open GPS setting?";
        
        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                ctx.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }
    
    public static String[] getCityAreaCountryFromLatitudeLongitude(double lat, double longi, Context ctx) {
        
        
        //String AreaName = "";
        String[] CityArea = new String[3];
        
        
        Geocoder gcd = new Geocoder(ctx, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, longi, 1);
        } catch (IOException e) {
            // Toast.makeText(ctx, "Please Enable Your Location", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        if (addresses != null) {
            if (addresses.size() <= 0) {
                //ldr.HideLoader();
                Toast.makeText(AppContextProvider.getContext(), "Unable to fetch location\nPlease move to open area", Toast.LENGTH_SHORT).show();
                
            } else {
                
                //ldr.HideLoader();
                //Toast.makeText(ctx, "Location found", Toast.LENGTH_SHORT).show();
                
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
                
                // AreaName = address + ", " + SubLocality + ", "/* + knownName + ", "*/ + SubAdminArea + ", " + adminArea + ", " + city + ", " + country;
                
                CityArea[0] = city + ", " + adminArea + ", " + country;
                CityArea[1] = knownName + ", " + SubLocality;
                CityArea[2] = country;
                
                
            }
        } else {
            //Toast.makeText(AppContextProvider.getContext(), "Please Enable Your Location ", Toast.LENGTH_SHORT).show();
        }
        
        
        //return AreaName;
        return CityArea;
        
    }
    
    public static boolean checkCameraPermission(Context c) {
        
        String permission = "android.permission.CAMERA";
        int res = c.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    
    public static boolean checkLocationPermission(Context c) {
        
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = c.checkCallingOrSelfPermission(permission);
        Log.e("CheckLocationStatus", String.valueOf(res == PackageManager.PERMISSION_GRANTED));
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    
    public static boolean networkAvailable() {
			/*ConnectivityManager cm = (ConnectivityManager)
					getSystemService(this.CONNECTIVITY_SERVICE);
					NetworkInfo ni = cm.getActiveNetworkInfo();
					if (ni == null)
					    return false;
					return ni.isConnectedOrConnecting();*/
     
 /*       Runtime runtime = Runtime.getRuntime();
        try {
            
            //Process ipProcess = runtime.exec("/system/bin/ping -c 1 67.222.152.138");
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            //Process ipProcess = runtime.exec("/system/bin/ping -c 1 google.com");
            int exitValue = ipProcess.waitFor();
            
            Log.d("IP ADDR", ipProcess.toString());
            Log.d("IP ADDR", String.valueOf(exitValue));
            return (exitValue == 0 || exitValue == 1);
            
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.fillInStackTrace());
            
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e.fillInStackTrace());
            
        }*/
        try {
            Socket socket = new Socket();
            InetSocketAddress socketAddress = new InetSocketAddress("35.190.168.249", 80);
            
            socket.connect(socketAddress, 15000);
            
            //Log.e("CheckResponse",String.valueOf(socket.isConnected()) + " ::: " + socketAddress.getHostName() + " ::: " + socketAddress.getAddress());
            socket.close();
            return true;
        } catch (Exception e) {
            
//            Log.e("CheckResponse",
//                    e.getMessage());
            return false;
        }
        
        
    }
    
    public static void CustomDialogNoInternet(Context c) {
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


    /*public static void ResponseTxtAnimation(final TextView ResponseTxt, String messageStr ){

        ResponseTxt.setVisibility( View.VISIBLE );
        ResponseTxt.setText( messageStr );

        AlphaAnimation alphaAnim = new AlphaAnimation(1.0f,0.0f);
        alphaAnim.setStartOffset( 120000 );                        // start in 2 minutes
        alphaAnim.setDuration(400);
        alphaAnim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation animation)
            {
                // make invisible when animation completes, you could also remove the view from the layout
                ResponseTxt.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ResponseTxt.setAnimation(alphaAnim);



    }*/
    
    public static String convertDate(String dbDate) {

        if(dbDate.contains("T")){
            dbDate=  dbDate.replace("T"," ")+":00";
        }
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
    
    public static String GetValues(String... vals) {
        
        String result = "";
        
        for (int i = 0; i < vals.length; i++) {
            
            result = result + vals[i] + "@@";
            
            if (i == vals.length - 1) {
                result = result + vals[i] + "##";
            }
            
        }
        return result;
        
    }
    
    public static String GetValues2(String... vals) {
        
        String result = "";
        
        for (String val : vals) {
            
            result = result + val + "@@";

            /*if( i == vals.length - 1 ){
                result = result + vals[i] + "##";
            }*/
            
        }
        return result;
        
    }
    
    public static void customToast(Context context, String msg) {
        
        Toast toast = Toast.makeText(AppContextProvider.getContext(), msg, Toast.LENGTH_SHORT);
        LinearLayout linearLayout = (LinearLayout) toast.getView();
        if (linearLayout.getChildCount() > 0) {
            TextView tv = (TextView) linearLayout.getChildAt(0);
            tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        }
        toast.show();
    }
    
    public static String getDateTimeSHORT() {
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        DateFormat df = DateFormat.getDateTimeInstance();
        
        //SelectedDate = dateFormat.format(new Date());
        
        return dateFormat.format(new Date());
        //return df.format(new Date());
    }
    
    public static String testInput(String text) {
        
        String result = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[^A-Za-z0-9-:#.@?=&,+()%$*/ ]+", "");
        return result;
        
    }
    public static int round(double d){
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if(result<0.5){
            return d<0 ? -i : i;
        }else{
            return d<0 ? -(i+1) : i+1;
        }
    }
    public static void showGPSGDialog(final Activity activity) {
        
        if (activity != null) {
            
            builder = new AlertDialog.Builder(activity);
            if (!DIALOG_VISIBLE) {
                
                String s =
                        activity.getSharedPreferences("SAVE_LOC_STATS", Context.MODE_PRIVATE).getString(
                                "loc_stats", "");
                
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
                                            if (activity != null)
                                                activity.startActivityForResult(new Intent(action), 1111);
                                            
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
                                            if (activity != null)
                                                activity.startActivityForResult(new Intent(action), 1111);
                                            
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
    



