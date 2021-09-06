package com.blazeminds.pos.webservice_url;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.BuildConfig;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.FileDownloader;
import com.blazeminds.pos.Login;
import com.blazeminds.pos.MainActivity;
import com.blazeminds.pos.MyService;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.PrefManager;
import com.blazeminds.pos.R;
import com.blazeminds.pos.resources.Loader;
import com.blazeminds.pos.resources.ReminderBroadcastReciever;
import com.blazeminds.pos.resources.UserSettings;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.blazeminds.pos.Constant.AMOUNT_RECIEVED_COLUMN;
import static com.blazeminds.pos.Constant.CID_COLUMN;
import static com.blazeminds.pos.Constant.DATE_COLUMN;
import static com.blazeminds.pos.Constant.DELETE_ITEM_COLUMN;
import static com.blazeminds.pos.Constant.DISCOUNT_COLUMN;
import static com.blazeminds.pos.Constant.EID_COLUMN;
import static com.blazeminds.pos.Constant.EXE_COMPLETE_COLUMN;
import static com.blazeminds.pos.Constant.EXE_DATE_COLUMN;
import static com.blazeminds.pos.Constant.LATITUDE_COLUMN;
import static com.blazeminds.pos.Constant.LONGITUDE_COLUMN;
import static com.blazeminds.pos.Constant.NET_OID_COLUMN;
import static com.blazeminds.pos.Constant.NOTES_COLUMN;
import static com.blazeminds.pos.Constant.OID_COLUMN;
import static com.blazeminds.pos.Constant.PAYMENT_TYPE_COLUMN;
import static com.blazeminds.pos.Constant.SELECTED_DISTRIBUTOR_COLUMN;
import static com.blazeminds.pos.Constant.SRCID_COLUMN;
import static com.blazeminds.pos.Constant.SRDATE_COLUMN;
import static com.blazeminds.pos.Constant.SRDELETE_ITEM_COLUMN;
import static com.blazeminds.pos.Constant.SRDISCOUNT_COLUMN;
import static com.blazeminds.pos.Constant.SREID_COLUMN;
import static com.blazeminds.pos.Constant.SRID_COLUMN;
import static com.blazeminds.pos.Constant.SRLATITUDE_COLUMN;
import static com.blazeminds.pos.Constant.SRLONGITUDE_COLUMN;
import static com.blazeminds.pos.Constant.SRNET_OID_COLUMN;
import static com.blazeminds.pos.Constant.SRNOTES_COLUMN;
import static com.blazeminds.pos.Constant.SRRETURN_REASON_COLUMN;
import static com.blazeminds.pos.Constant.SRSELECTED_DISTRIBUTOR_COLUMN;
import static com.blazeminds.pos.Constant.SRSTART_DATE_COLUMN;
import static com.blazeminds.pos.Constant.SRTOTAL2_COLUMN;
import static com.blazeminds.pos.Constant.SRTOTAL_COLUMN;
import static com.blazeminds.pos.Constant.START_DATE_COLUMN;
import static com.blazeminds.pos.Constant.TOTAL2_COLUMN;
import static com.blazeminds.pos.Constant.TOTALEXE_COLUMN;
import static com.blazeminds.pos.Constant.TOTAL_COLUMN;
import static com.blazeminds.pos.PosDB.sqlDB;
import static com.blazeminds.pos.resources.UserSettings.FIRST_NOTIFY;
import static com.blazeminds.pos.resources.UserSettings.IS_FIRST_NOTIFY;
import static com.blazeminds.pos.webservice_url.Url_Links.KEY_SUCCESS;

/**
 * Created by Saad Kalim on 03-May-16.
 */
public class SyncData {
    
    private String sync_message = "";
    private String gss_msg = "";
    private BufferedReader reader = null;
    //An string to store output from the server
    private String output = "";
    private File backupPath = Environment.getExternalStorageDirectory();
    private Activity ctx;
    private PosDB posDB;
    private UserSettings userSettings;
    private FileWriter fr;
    private BufferedWriter br;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    /* Login End */
    private PrefManager prefManager;
    
    
    public SyncData(Activity activity) {
        
        this.ctx = activity;
        
        posDB = PosDB.getInstance(this.ctx);

        userSettings = UserSettings.getInstance(this.ctx);




    }
    
    public SyncData(final Loader ldr, Activity a, PosDB dba) {
        this.ctx = a;
        this.posDB = dba;

        userSettings = UserSettings.getInstance(a);
        
        /*backupPath = new File(backupPath.getPath() + "/Android/data/" + BuildConfig.APPLICATION_ID +
                "/log_file");*/
//        if(!BuildConfig.FLAVOR.equalsIgnoreCase("lorenzo_industries")) {
//            backupPath = new File(backupPath.getPath());
//        if (!backupPath.exists()) {
//            backupPath.mkdirs();
//        }}
        
        
      
            
            
            if (networkAvailable()) {
                
                sharedPreferences = a.getSharedPreferences("LOG", MODE_PRIVATE);
                editor = sharedPreferences.edit();
//                if(!BuildConfig.FLAVOR.equalsIgnoreCase("lorenzo_industries")) {
//                    if (sharedPreferences.getString("DATE", "").equals(Constant.getDateTimeSHORT())) {
//                        fr = new FileWriter(backupPath.getPath() + "/BMS_BACKUP.log", true);
//                    } else {
//                        fr = new FileWriter(backupPath.getPath() + "/BMS_BACKUP.log");
//                        editor.putString("DATE", Constant.getDateTimeSHORT());
//                        editor.apply();
//                    }
//                    br = new BufferedWriter(fr);
//                }
                if (posDB == null) {
                    posDB = PosDB.getInstance(this.ctx);
                    posDB.OpenDb();
                }
                
                
                ArrayList<HashMap<String, String>> dataOrder = posDB.getSalesOrder();
                
                ArrayList<HashMap<String, String>> orderDetails;
                
                Log.e("MyLog", String.valueOf(dataOrder.size()));
                
                ArrayList<HashMap<String, String>> dataSalesReturn = posDB.getSalesReturn();
                
                ArrayList<HashMap<String, String>> returnDetails;
                
                ArrayList<HashMap<String, String>> dataCustGPS = posDB.getUnsyncGPSCustomers();
                
                ArrayList<HashMap<String, String>> dataCust = posDB.getUnsyncCustomers();
                
                ArrayList<HashMap<String, String>> dataBrand = posDB.getUnsyncBrands();
                
                ArrayList<HashMap<String, String>> dataItem = posDB.getUnsyncItemType();
                
                ArrayList<HashMap<String, String>> dataInvent = posDB.getUnsyncInventory();
                
                ArrayList<HashMap<String, String>> dataPaymentRecieved = posDB.getPaymentRecieved();
                
                ArrayList<HashMap<String, String>> dataExpense = posDB.getExpense();
                
                ArrayList<HashMap<String, String>> dataShopVisit = posDB.getShopVisit();
                
                ArrayList<HashMap<String, String>> dataTravelExpense = posDB.getTravelExpense();
                
                ArrayList<HashMap<String, String>> dataSupport = posDB.getSupport();
                
                ArrayList<HashMap<String, String>> dataSupportDetail;
                
                ArrayList<HashMap<String, String>> dataSupportDetailConvo = posDB.getSupportDetail();
                
                ArrayList<HashMap<String, String>> dataCustomerRoutes = posDB.getCustomerRoute();
                
                ArrayList<HashMap<String, String>> dataCommitment = posDB.getCommitment();
                
                ArrayList<HashMap<String, String>> dataShopStock = posDB.getShopStock();
                
                String empID = posDB.getMobEmpId();
                
                int savedRouteID = posDB.getSavedRouteID();
                
                String savedDistributor = posDB.getSavedDistributorList();
                
                
                Log.d("empID", empID + "s");
                Log.d("selectedRoute", savedRouteID + "s");
                Log.d("SavedDistributor", savedDistributor + "s");
                
                
                StringBuilder CustomerGPSVals = new StringBuilder();
                StringBuilder CustomerVals = new StringBuilder();
                StringBuilder BrandVals = new StringBuilder();
                StringBuilder ItemTypeVals = new StringBuilder();
                StringBuilder ItemVals = new StringBuilder();
                StringBuilder OrderVals = new StringBuilder();
                StringBuilder SalesReturnVals = new StringBuilder();
                StringBuilder PaymentRecievedVals = new StringBuilder();
                StringBuilder ExpenseVals = new StringBuilder();
                StringBuilder ShopVisitVals = new StringBuilder();
                StringBuilder TravelExpenseVals = new StringBuilder();
                StringBuilder SupportVals = new StringBuilder();
                StringBuilder SupportDetailsVals = new StringBuilder();
                StringBuilder CommitmentVals = new StringBuilder();
                StringBuilder RouteVals = new StringBuilder();
                StringBuilder ShopStockVals = new StringBuilder();

                for (int i = 0; i < dataCustGPS.size(); i++) {
                    HashMap<String, String> f = dataCustGPS.get(i);
                    
                    CustomerGPSVals.append(GetValues(!f.get("id").equals("") ? f.get("id") : "0",
                            !f.get("lat").equals("") ? f.get("lat") : "0",
                            f.get("long").equals("") ? f.get("long") : "0", f.get("map").equals("") ? f.get("map") : "0", f.get("rad").equals("") ? f.get("rad") : "0"));
                }
                
                for (int i = 0; i < dataCust.size(); i++) {
                    
                    HashMap<String, String> f = dataCust.get(i);
                    
                    CustomerVals.append(GetValues(
                            f.get("id"),
                            f.get("fname"),
                            f.get("lname"),
                            f.get("comp_name"),
                            f.get("cell"),
                            f.get("ph1"),
                            f.get("ph2"),
                            f.get("add"),
                            f.get("city"),
                            f.get("state"),
                            f.get("country"),
                            f.get("notes"),
                            f.get("email"),
                            f.get("lat"),
                            f.get("long"),
                            f.get("map"),
                            f.get("rad"),
                            f.get("cust_route"),
                            f.get("cnic"),
                            f.get("opening_bal_new"),
                            f.get("opening_bal_old"),
                            f.get("cust_type"),
                            f.get("net_id"),
                            f.get("sms_code"),
                            f.get("number_verified"),
                            f.get("advance_payment"),
                            f.get("last_update"),
                            f.get("customer_celeb"),
                            f.get("customer_tax_type"),
                            f.get("shop_id"),
							f.get("sub_shop_id")));//category
                    
                }
                
                for (int i = 0; i < dataBrand.size(); i++) {
                    HashMap<String, String> f = dataBrand.get(i);
                    
                    BrandVals.append(GetValues(
                            f.get("id"),
                            f.get("brname")));
                }
                
                for (int i = 0; i < dataItem.size(); i++) {
                    HashMap<String, String> f = dataItem.get(i);
                    
                    ItemTypeVals.append(GetValues(
                            f.get("id"),
                            f.get("itemtype")));
                    
                }
                
                if (dataInvent.size() > 0) {
                    for (int i = 0; i < dataInvent.size(); i++) {
                        HashMap<String, String> f = dataInvent.get(i);
                        
                        ItemVals.append(GetValues(
                                f.get("id"),
                                f.get("brandid"),
                                f.get("typeid"),
                                f.get("vendorid"),
                                f.get("inventoryname"),
                                f.get("unitcost"),
                                f.get("saleprice"),
                                f.get("sku"),
                                f.get("tax"),
                                f.get("packing"),
                                f.get("qty"),
                                f.get("details")));
                        
                    }
                }
                
                for (int i = 0; i < dataOrder.size(); i++) {
                    HashMap<String, String> map = dataOrder.get(i);
                    
                    
                    OrderVals.append(GetValues2(
                            map.get(OID_COLUMN),
                            map.get(CID_COLUMN),
                            map.get(EID_COLUMN),
                            map.get(DATE_COLUMN),
                            map.get(NOTES_COLUMN),
                            map.get(TOTAL_COLUMN),
                            map.get(TOTAL2_COLUMN),
                            map.get(DELETE_ITEM_COLUMN),
                            map.get(EXE_COMPLETE_COLUMN),
                            map.get(EXE_DATE_COLUMN),
                            map.get(AMOUNT_RECIEVED_COLUMN),
                            map.get(LATITUDE_COLUMN),
                            map.get(LONGITUDE_COLUMN),
                            map.get(START_DATE_COLUMN) /*map.get(START_DATE_COLUMN).toString()*//*"2018-03-16 16:23:00"*/,
                            map.get(NET_OID_COLUMN),
                            map.get(DISCOUNT_COLUMN),
                            map.get(PAYMENT_TYPE_COLUMN),
                            map.get(SELECTED_DISTRIBUTOR_COLUMN)));
                    // posDB.OpenDb();
                    
                    
                    orderDetails = posDB.getOrderDetail(map.get(OID_COLUMN));
                    // posDB.CloseDb();
                    for (int j = 0; j < orderDetails.size(); j++) {
                        HashMap<String, String> map2 = orderDetails.get(j);
                        
                        OrderVals.append(map2.get("productId")).append("!!").append(map2.get("qty")).append("!!").append(map2.get("qtyExe")).append( /*"!!" + map2.get("tradePrice") +*/ "!!").append(map2.get("discount1")).append("!!").append(map2.get("discount2")).append("!!").append(map2.get("tradeOffer")).append("!!").append(map2.get("scheme")).append("!!").append(map2.get("tax1")).append("!!").append(map2.get("tax2")).append("!!").append(map2.get("tax3")).append("!!").append(map2.get("subTotal")).append("!!").append(map2.get("schemeQty")).append("!!").append(map2.get("tradePrice")).append("!!").append(map2.get("schemeFormula")).append("!!").append(map2.get("schemeProduct")).append("!!").append(map2.get("multi_scheme")).append("!!").append(map2.get("t_o_v")).append("!!").append(map2.get("d_v_1")).append("!!").append(map2.get("d_v_2")).append("!!").append(map2.get("t_type")).append("!!").append(map2.get("t_mrp_type")).append("!!").append(map2.get("t_val")).append("!!").append(map2.get("mrp_price")).append("!!!");
                        
                    }
                    
                    OrderVals.append("--@@@@").append(GetValues2(map.get(TOTALEXE_COLUMN))).append("##");
                    
                }
                
                for (int i = 0; i < dataSalesReturn.size(); i++) {
                    
                    HashMap<String, String> map = dataSalesReturn.get(i);
                    
                    SalesReturnVals.append(GetValues2(
                            map.get(SRID_COLUMN),
                            map.get(SRCID_COLUMN),
                            map.get(SREID_COLUMN)/*, map.get(SRVAL_COLUMN).toString()*/,
                            map.get(SRDATE_COLUMN),
                            map.get(SRNOTES_COLUMN),
                            map.get(SRTOTAL_COLUMN),
                            map.get(SRTOTAL2_COLUMN),
                            map.get(SRDELETE_ITEM_COLUMN),
                            map.get(SRRETURN_REASON_COLUMN),
                            map.get(SRLATITUDE_COLUMN),
                            map.get(SRLONGITUDE_COLUMN),
                            map.get(SRSTART_DATE_COLUMN),
                            map.get(SRNET_OID_COLUMN),
                            map.get(SRDISCOUNT_COLUMN),
                            map.get(SRSELECTED_DISTRIBUTOR_COLUMN)));
                    
                    returnDetails = posDB.getReturnDetail(map.get(SRID_COLUMN));
                    
                    for (int j = 0; j < returnDetails.size(); j++) {
                        
                        HashMap<String, String> map2 = returnDetails.get(j);
                        
                        SalesReturnVals.append(map2.get(
                                "productId")).append("!!").append(map2.get("qty")).append("!!").append(map2.get("qtyExe")).append( /*"!!" + map2.get("tradePrice") +*/ "!!").append(map2.get("discount1")).append("!!").append(map2.get("discount2")).append("!!").append(map2.get("tradeOffer")).append("!!").append(map2.get("scheme")).append("!!").append(map2.get("tax1")).append("!!").append(map2.get("tax2")).append("!!").append(map2.get("tax3")).append("!!").append(map2.get("subTotal")).append("!!").append(map2.get("schemeQty")).append("!!").append(map2.get("tradePrice")).append("!!").append(map2.get("schemeFormula")).append("!!").append(map2.get("schemeProduct")).append("!!").append(map2.get("multi_scheme")).append("!!").append(map2.get("t_o_v")).append("!!").append(map2.get("d_v_1")).append("!!").append(map2.get("d_v_2")).append("!!").append(map2.get("t_type")).append("!!").append(map2.get("t_mrp_type")).append("!!").append(map2.get("t_val")).append("!!").append(map2.get("mrp_price")).append("!!!");
                    }
                    
                    SalesReturnVals.append("--@@@@").append(GetValues2("0")).append("##");
                }
                
                for (int i = 0; i < dataPaymentRecieved.size(); i++) {
                    HashMap<String, String> f = dataPaymentRecieved.get(i);
                    
                    PaymentRecievedVals.append(GetValues(
                            f.get("payment_id"),
                            f.get("cust_id"),
                            f.get("emp_id"),
                            f.get("datetime"),
                            f.get("amount"),
                            f.get("details"),
                            f.get("start_datetime"),
                            f.get("latitude"),
                            f.get("longitude"),
                            f.get("mapName"),
                            f.get("cheque_no"),
                            f.get("cheque_date"),
                            f.get("bank_name"),
                            f.get("payment_type"),
                            f.get("selected_distributor_id")));
                }
                
                for (int i = 0; i < dataExpense.size(); i++) {
                    
                    HashMap<String, String> f = dataExpense.get(i);
                    
                    ExpenseVals.append(GetValues(
                            f.get("exp_type"),
                            f.get("exp_datetime"),
                            f.get("exp_amount"),
                            f.get("exp_remarks"),
                            f.get("exp_latitude"),
                            f.get("exp_longitude"),
                            f.get("exp_mapName"),
                            f.get("exp_status"),
                            f.get("exp_start_datetime"),
                            f.get("exp_shop_id"),
                            f.get("exp_commitment_id")));
                }
                
                for (int i = 0; i < dataShopVisit.size(); i++) {
                    
                    HashMap<String, String> f = dataShopVisit.get(i);
                    
                    ShopVisitVals.append(GetValues(
                            f.get("sv_cust_id"),
                            f.get("sv_remarks"),
                            f.get("sv_reason_id"),
                            f.get("sv_latitude"),
                            f.get("sv_longitude"),
                            f.get("sv_mapName"),
                            f.get("sv_datetime"),
                            f.get("sv_start_datetime"),
                            f.get("sv_selected_distributor_id"),
                            userSettings.getString(f.get("sv_id"))));
                    
                }
                
                for (int i = 0; i < dataTravelExpense.size(); i++) {
                    
                    HashMap<String, String> f = dataTravelExpense.get(i);
                    
                    TravelExpenseVals.append(GetValues(
                            f.get("t_exp_id"),
                            f.get("t_exp_from_town"),
                            f.get("t_exp_to_town"),
                            f.get("t_exp_from_date"),
                            f.get("t_exp_to_date"),
                            f.get("t_exp_days"),
                            f.get("t_exp_travelling_amount"),
                            f.get("t_exp_stay_compensation_amount"),
                            f.get("t_exp_amount"),
                            f.get("t_exp_start_datetime"),
                            f.get("t_exp_datetime"),
                            f.get("t_exp_remarks"),
                            f.get("t_exp_latitude"),
                            f.get("t_exp_longitude"),
                            f.get("t_exp_mapName"),
                            f.get("t_exp_status")));
                }
                
                for (int i = 0; i < dataSupport.size(); i++) {
                    
                    HashMap<String, String> f = dataSupport.get(i);
                    
                    SupportVals.append(GetValues2( /*f.get("support_id")*/
                            empID,
                            f.get("support_title"),
                            f.get("support_status"),
                            f.get("support_datetime"),
                            f.get("support_net_oid")));
                    
                    
                    dataSupportDetail = posDB.getSupportDetailList(f.get("support_id"));
                    
                    
                    for (int j = 0; j < dataSupportDetail.size(); j++) {
                        
                        HashMap<String, String> f2 = dataSupportDetail.get(j);
                        
                        SupportVals.append(f2.get("support_detail_id")).append("!!").append(f2.get("support_detail_support_id")).append("!!").append(f2.get("support_detail_message")).append("!!").append(f2.get("support_detail_person")).append("!!").append(f2.get("support_detail_datetime")).append("!!!");
                    }
                    
                    SupportVals.append("##");
                }
                
                for (int i = 0; i < dataSupportDetailConvo.size(); i++) {
                    
                    HashMap<String, String> f = dataSupportDetailConvo.get(i);
                    
                    SupportDetailsVals.append(GetValues(
                            f.get("support_detail_id"),
                            f.get("support_detail_support_id"),
                            f.get("support_detail_message"),
                            f.get("support_detail_person"),
                            f.get("support_detail_datetime")));
                }
                
                for (int i = 0; i < dataCommitment.size(); i++) {
                    
                    HashMap<String, String> f = dataCommitment.get(i);
                    
                    CommitmentVals.append(GetValues(
                            f.get("comm_id"),
                            f.get("comm_customer_id"),
                            f.get("comm_from_date"),
                            f.get("comm_to_date"),
                            f.get("comm_sale_amount"),
                            f.get("comm_gift_amount"),
                            f.get("comm_remarks"),
                            f.get("comm_status"),
                            f.get("comm_done")));
                }
                
                for (int i = 0; i < dataCustomerRoutes.size(); i++) {
                    
                    HashMap<String, String> f = dataCustomerRoutes.get(i);
                    
                    RouteVals.append(GetValues(
                            f.get("route_id"),
                            empID,
                            f.get("route_name"), f.get("day")));
                }
                
                for (int i = 0; i < dataShopStock.size(); i++) {
                    
                    HashMap<String, String> ss = dataShopStock.get(i);
                    
                    ShopStockVals.append(GetValues(
                            ss.get("id"),
                            ss.get("customer_id"),
                            ss.get("product_id"),
                            ss.get("quantity"),
                            ss.get("emp_id"),
                            ss.get("datetime")));
                }
                
                dataOrder.clear();
                dataSalesReturn.clear();
                dataCustGPS.clear();
                dataCust.clear();
                dataBrand.clear();
                dataItem.clear();
                dataInvent.clear();
                dataPaymentRecieved.clear();
                dataExpense.clear();
                dataShopVisit.clear();
                dataTravelExpense.clear();
                dataSupport.clear();
                dataSupportDetailConvo.clear();
                dataCommitment.clear();
                dataCustomerRoutes.clear();
                dataShopStock.clear();
//                if(!BuildConfig.FLAVOR.equalsIgnoreCase("lorenzo_industries")) {
//                    br.write("=====================SYNC_READ_BEGIN" + "=====================\n");
//
//                    br.write(("Date " + new Date(System.currentTimeMillis())));
//                    br.write(("\nSyncData1 :" + empID));
//                    br.write(("\nSyncData2 :" + OrderVals));
//                    br.write(("\nSyncData3 :" + SalesReturnVals));
//                    br.write(("\nSyncData4 :" + CustomerGPSVals));
//                    br.write(("\nSyncData5 :" + CustomerVals));
//                    br.write(("\nSyncData6 :" + BrandVals));
//                    br.write(("\nSyncData7 :" + ItemTypeVals));
//                    br.write(("\nSyncData8 :" + ItemVals));
//                    br.write(("\nSyncData9 :" + PaymentRecievedVals));
//                    br.write(("\nSyncData10 :" + ExpenseVals));
//                    br.write(("\nSyncData11 :" + ShopVisitVals));
//                    br.write(("\nSyncData12 :" + RouteVals));
//                    br.write(("\nSyncData13 :" + SupportVals));
//                    br.write(("\nSyncData14 :" + SupportDetailsVals));
//                    br.write(("\nSyncData15 :" + ShopStockVals));
//                    br.write(("\nSyncData16 :" + savedRouteID));
//                    br.write(("\nSyncData17 :" + savedDistributor));
//
//                    br.write("\n=====================SYNC_READ_END" + "=====================\n");
//                    br.write("\n");
//
//                    br.close();
//                    fr.close();
//                }
                Log.e("SyncData1", empID);
                Log.e("SyncData2", OrderVals.toString());
                Log.e("SyncData3", SalesReturnVals.toString());
                Log.e("SyncData4", CustomerGPSVals.toString());
                Log.e("SyncData5", CustomerVals.toString());
                Log.e("SyncData6", BrandVals.toString());
                Log.e("SyncData7", ItemTypeVals.toString());
                Log.e("SyncData8", ItemVals.toString());
                Log.e("SyncData9", PaymentRecievedVals.toString());
                Log.e("SyncData10", ExpenseVals.toString());
                Log.e("SyncData11", ShopVisitVals.toString());
                Log.e("SyncData12", RouteVals.toString());
                Log.e("SyncData13", SupportVals.toString());
                Log.e("SyncData14", SupportDetailsVals.toString());
                Log.e("SyncData15", ShopStockVals.toString());
                Log.e("SyncData16", String.valueOf(savedRouteID));
                Log.e("SyncData17", savedDistributor);
                
                Log.e("-0-0-0-0-0-", "");
                
                SharedPreferences sp = ctx.getSharedPreferences(
                        "SYNC_VALUE", MODE_PRIVATE);
                String store_qty_val="";
                String store_qty_val2="";
                ArrayList<HashMap<String, String>> dataQty = posDB.getQtyOrder();
                ArrayList<HashMap<String, String>> qtyorderDetails;
                for (int i = 0; i < dataQty.size(); i++) {
                    HashMap map = dataQty.get(i);

                    store_qty_val += GetValues2(
                            map.get("id").toString(),
                            map.get("customer_id").toString(),
                            map.get("emp_id").toString(),
                            // map.get("brand_id").toString(),
                            map.get("location_id").toString(),
                            map.get("datetime").toString());
                    Log.wtf("store_qty_val_order_only"+i,store_qty_val);
                    posDB.OpenDb();
                    qtyorderDetails = posDB.getqtyOrderDetail((String) map.get("id"));
                    posDB.CloseDb();
                    for (int j = 0; j < qtyorderDetails.size(); j++) {
                        HashMap map2 = qtyorderDetails.get(j);
                        store_qty_val += map2.get("prd_id") + "!!" + map2.get("qty") + "!!" + map2.get("order_id") + "!!!";

                    }

                    store_qty_val += "--@@@@"  + "##";
                    Log.wtf("store_qty_val"+i,store_qty_val);
                }
                ArrayList<HashMap<String, String>> dataQty2 = posDB.getQtyOrder2();
                ArrayList<HashMap<String, String>> qtyorderDetails2;
                for (int i = 0; i < dataQty2.size(); i++) {
                    HashMap map = dataQty2.get(i);

                    store_qty_val2 += GetValues2(
                            map.get("id").toString(),
                            map.get("customer_id").toString(),
                            map.get("emp_id").toString(),
                            // map.get("brand_id").toString(),
                            map.get("location_id").toString(),
                            map.get("datetime").toString());
//                    Log.wtf("store_qty_val_order_only"+i,store_qty_val);
                    posDB.OpenDb();
                    qtyorderDetails2 = posDB.getqtyOrderDetail2((String) map.get("id"));
                    posDB.CloseDb();
                    for (int j = 0; j < qtyorderDetails2.size(); j++) {
                        HashMap map2 = qtyorderDetails2.get(j);
                        store_qty_val2 += map2.get("prd_id") + "!!" + map2.get("qty") + "!!" + map2.get("order_id") + "!!!";

                    }

                    store_qty_val2 += "--@@@@"  + "##";
//                    Log.wtf("store_qty_val"+i,store_qty_val);
                }
                if (sp.getInt("sync_failed", 0) == 0) {
                    SyncEverything(
                            ldr,
                            empID,
                            OrderVals.toString(),
                            SalesReturnVals.toString(),
                            CustomerGPSVals.toString(),
                            CustomerVals.toString(),
                            BrandVals.toString(),
                            ItemTypeVals.toString(),
                            ItemVals.toString(),
                            PaymentRecievedVals.toString(),
                            ExpenseVals.toString(),
                            ShopVisitVals.toString(),
                            RouteVals.toString(),
                            SupportVals.toString(),
                            SupportDetailsVals.toString(),
                            ShopStockVals.toString(),
                            String.valueOf(savedRouteID),
                            savedDistributor,
                             store_qty_val,
                            store_qty_val2);
                } else if (sp.getInt("sync_failed", 0) == 1) {
                    
                    SyncData1(ldr, empID, CustomerVals.toString(), BrandVals.toString(),
                            ItemTypeVals.toString(), ItemVals.toString(), String.valueOf(savedRouteID),
                            savedDistributor);
                }
                
                
            } else {
                
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ldr.HideLoader();
                        
                        CustomDialogNoInternet();
                        
                    }
                });
                
            }
            
            
        
       
        
    }
    
    /* Login Start */
    
    public String SyncData2(Activity a, PosDB dba, final String empID) {
        this.ctx = a;
        this.posDB = dba;
        String msg;
        Log.d("EMPCHK", "OUT");
        
        if (networkAvailable()) {
            
            msg = checkUserInactive(empID);
            Log.d("EMPCHK", "NET IN");
            
        } else msg = "Check Your Internet Connection";
        return msg;
    }
    
    public String SyncData3(Activity a, PosDB dba, final String empID, String SYNC_SETTINGS) {
        this.ctx = a;
        this.posDB = dba;
        String msg;
        Log.d("EMPSettings", "OUT");
        
        if (networkAvailable()) {
            
            msg = GetSalesmanSettings(empID);
            Log.d("EMPSettings", "NET IN");
            
        } else msg = "Check Your Internet Connection";
        return msg;
    }
    
    private void SyncEverything(final Loader ldr, final String empID, final String OrderVals, final String SalesReturnVals, final String CustomerGPSVals, final String CustomerVals, final String BrandVals, final String ItemTypeVals, final String ItemVals,
                                final String PaymentRecievedVals, final String ExpenseVals, final String ShopVisitVals, final String RouteVals, final String SupportVals, final String SupportDetailsVals, final String ShopStockVals, final String selectedRoute, final String selectedDistributor,String store_qty_val,String store_qty_val2) {
        
        Log.e("CURRENT_MILLI_1_BEFORE", new Date(System.currentTimeMillis()) + "");
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
                .build(); //Finally building the adapter
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        api.sync_everyinsert(
                
                empID,
                OrderVals,
                SalesReturnVals,
                CustomerGPSVals,
                CustomerVals,
                BrandVals,
                ItemTypeVals,
                ItemVals,
                PaymentRecievedVals,
                ExpenseVals,
                ShopVisitVals,
                RouteVals,
                SupportVals,
                SupportDetailsVals,
                ShopStockVals,
                selectedRoute,
                selectedDistributor,
                store_qty_val,
                store_qty_val2,
                
                new Callback<Response>() {
                    @Override
                    public void success(final Response result, final Response response) {
                        
                        //On success we will read the server's output using bufferedreader
                        //Creating a bufferedreader object
                        
                        try {
                            Log.e("CURRENT_MILLI_1_AFTER", new Date(System.currentTimeMillis()) + "");
                            //Initializing buffered reader
                            if (result == null || result.equals(null)) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                
                                        ldr.HideLoader();
                                        Toast.makeText(ctx, "Something went wrong...\n Sync again",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.e("Check", "OUT result");
                                return;
                            }
                            if (result.getBody() == null || result.getBody().equals(null)) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                
                                        ldr.HideLoader();
                                        Toast.makeText(ctx, "Something went wrong...\n Sync again",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.e("Check", "OUT result.getBody()");
                                return;
                            }
                            if (result.getBody().in() == null || result.getBody().in().equals(null) ) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                
                                        ldr.HideLoader();
                                        Toast.makeText(ctx, "Something went wrong...\n Sync again",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.e("Check", "OUT result.getBody().in()");
                                return;
                            }
                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            
                            //Reading the output in the string
                            output = reader.readLine();
                            
                            Log.e("SYNC JSON READ", output);
                            
                            JSONObject json = new JSONObject(output);
                            
                            Log.e("MyLogJson", output);
                            
                            Log.d("Prof", "OUT");
                            
                            if (json.getString(KEY_SUCCESS) != null) {
                                
                                String res = json.getString(KEY_SUCCESS);
                                
                                Log.d("Prof", "res Val " + res);
                                
                                if (Integer.parseInt(res) == 1) {
                                    // SyncEverything(ldr, empID, CustomerVals, BrandVals,ItemTypeVals, ItemVals);
                                    ctx.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            
                                            Toast.makeText(ctx, "Data Sent", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    SyncData1(ldr, empID, CustomerVals, BrandVals, ItemTypeVals,
                                            ItemVals, selectedRoute,
                                            selectedDistributor);
                                } else if (Integer.parseInt(res) == 0) {
                                    
                                    ctx.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ldr.HideLoader();
                                            Toast.makeText(ctx, "User not found.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                
                            }
                            
                            
                        } catch (final IOException e) {
                            
                            Log.d("Prof", "Excep " + e.toString());
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ldr.HideLoader();
                                    Toast.makeText(AppContextProvider.getContext(), "I/O Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            
                            e.printStackTrace();
                        } catch (final JSONException e) {
                            
                            
                            if (sqlDB.inTransaction()) {
                                sqlDB.setTransactionSuccessful();
                                sqlDB.endTransaction();
                            }
                            Log.e("JSONException", e.toString());
                            try {
                                
                                if (sharedPreferences.getString("DATE", "").equals(Constant.getDateTimeSHORT())) {
                                    
                                    fr = new FileWriter(backupPath.getPath() + "/BMS_BACKUP.log", true);
                                    
                                } else {
                                    
                                    fr = new FileWriter(backupPath.getPath() + "/BMS_BACKUP.log");
                                    editor.putString("DATE", Constant.getDateTimeSHORT());
                                    editor.apply();
                                    
                                }
                                
                                br = new BufferedWriter(fr);
                                
                                br.write("=====================JSON_EXCEPTION_BEGIN=====================\n\n");
                                
                                br.write("OUTPUT\n" + output);
                                
                                br.write("\n\n:::EXCEPTION:::\n" + e.getMessage());
                                
                                br.write("\n\n=====================JSON_EXCEPTION_END=====================\n\n");
                                
                                br.close();
                                
                                fr.close();
                                
                            } catch (IOException e1) {
                                
                                e1.printStackTrace();
                                
                            }
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    
                                    ldr.HideLoader();
                                    Toast.makeText(AppContextProvider.getContext(), "Json Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                    
                                }
                            });
                            
                            e.printStackTrace();
                        } catch (final Exception e) {
                            
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ldr.HideLoader();
                                    Log.d("Prof", "Excep " + e.toString());
                                    
                                    e.printStackTrace();
                                    Toast.makeText(AppContextProvider.getContext(), "Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        
                        
                    }
                    
                    
                    @Override
                    public void failure(RetrofitError error) {
                        if (sqlDB.inTransaction()) {
                            sqlDB.setTransactionSuccessful();
                            sqlDB.endTransaction();
                        }
                        Log.e("RetrofitError", "Error " + error.toString());
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ldr.HideLoader();
                                Toast.makeText(AppContextProvider.getContext(), "Kindly Check Your Internet Connection", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
        );
        
    }
    
    private void SyncData1(final Loader ldr, final String empID, final String CustomerVals, final String BrandVals, final String ItemTypeVals, final String ItemVals, final String selectedRoute, final String selectedDistributor) {
        
        
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        
        Log.e("CURRENT_MILLI_2_BEFORE", new Date(System.currentTimeMillis()) + "");
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL).setClient((new OkClient(okHttpClient)))
                //Setting the Root URL
                .build(); //Finally building the adapter
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        
        api.sync_alldata(
                
                empID,
                selectedRoute,
                selectedDistributor,
                
                //Creating an anonymous callback
                new Callback<Response>() {
                    
                    @Override
                    public void success(Response result, Response response) {
                        //On success we will read the server's output using bufferedreader
                        //Creating a bufferedreader object
                        
                        SharedPreferences sharedPreferences1 = ctx.getSharedPreferences(
                                "SYNC_VALUE", MODE_PRIVATE);
                        
                        SharedPreferences.Editor editor2 = sharedPreferences1.edit();
                        editor2.putInt("sync_failed", 0);
                        editor2.apply();
                        
                        
                        BufferedReader reader;
                        
                        //An string to store output from the server
                        String output;
                        Log.e("CURRENT_MILLI_2_AFTER", new Date(System.currentTimeMillis()) + "");
                        
                        try {
                            
                            //Initializing buffered reader
    
                            if (result == null || result.equals(null)) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        
                                        ldr.HideLoader();
                                        Toast.makeText(ctx, "Something went wrong...\n Sync again",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.e("Check", "OUT result");
                                return;
                            }
                            if (result.getBody() == null || result.getBody().equals(null)) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        
                                        ldr.HideLoader();
                                        Toast.makeText(ctx, "Something went wrong...\n Sync again",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.e("Check", "OUT result.getBody()");
                                return;
                            }
                            if (result.getBody().in() == null || result.getBody().in().equals(null) ) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        
                                        ldr.HideLoader();
                                        Toast.makeText(ctx, "Something went wrong...\n Sync again",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.e("Check", "OUT result.getBody().in()");
                                return;
                            }
                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            
                            //Reading the output in the string
                            output = reader.readLine();
                            
                            Log.e("SYNC JSON READ", output);
                            
                            JSONObject json = new JSONObject(output);
                            
                            Log.e("MyLogJson", output);
                            
                            Log.d("Prof", "OUT");
                            
                            if (json.getString(KEY_SUCCESS) != null) {
                                
                                String res = json.getString(KEY_SUCCESS);

                                userSettings.clear();
                                userSettings.clearAllPreferences();


                                Log.d("Prof", "res Val " + res);
                                
                                if (Integer.parseInt(res) == 1) {


                                    JSONArray values_expense_type = json.getJSONArray("values_expense_type");
                                    SharedPreferences FormsElementDatasharedPreferences = ctx.getSharedPreferences(
                                            "FormsElementData", MODE_PRIVATE);

                                    SharedPreferences.Editor FormsElementDataeditor =
                                            FormsElementDatasharedPreferences.edit();
                                    FormsElementDataeditor.putString("values_expense_type",values_expense_type.toString());



                                    FormsElementDataeditor.apply();


                                    SharedPreferences FormsDatasharedPreferences =ctx.getSharedPreferences(
                                            "FormsData", MODE_PRIVATE);
                                    SharedPreferences.Editor FormsDatasharedPreferencesEditor =
                                            FormsDatasharedPreferences.edit();
                                    if(json.has("DailyExpenseDataValues")) {
                                        FormsDatasharedPreferencesEditor.putString("DailyExpenseDataValues",  json.getJSONArray("DailyExpenseDataValues").toString());
                                    }


                                    FormsDatasharedPreferencesEditor.apply();
                                    SharedPreferences Category_Labels_sharedPreferences = ctx.getSharedPreferences(
                                            "Category_Labels", MODE_PRIVATE);

                                        SharedPreferences.Editor Category_Labels_sharedPreferences_editor =
                                                Category_Labels_sharedPreferences.edit();

                                   if(json.has("category_label"))
                                   {
                                       Category_Labels_sharedPreferences_editor.putString("category_label", json.getString("category_label"));
                                   }
                                    if(json.has("subcategory_label"))
                                    {
                                        Category_Labels_sharedPreferences_editor.putString("subcategory_label", json.getString("subcategory_label"));
                                    }
                                    Category_Labels_sharedPreferences_editor.apply();
                                    JSONArray ValuesExpenseType = json.getJSONArray("values_expense_type");
                                    JSONArray ValuesExpenseStatus = json.getJSONArray("values_expense_status");
                                    JSONArray ValuesNoReason = json.getJSONArray("values_no_reason");
                                    JSONArray ValuesClockIn = json.getJSONArray("values_clockin");
                                    JSONArray ValuesMerchandizing = json.getJSONArray("values_merchandizing");
                                    JSONArray ValuesMarkExpense = json.getJSONArray("values_marketing_expense");
                                    JSONArray ValuesShopVisit = json.getJSONArray("values_shop_checkin");
                                    JSONArray ValuesMerchandizingPlan = json.getJSONArray("values_merchandizing_plan");
                                    JSONArray ValuesTown = json.getJSONArray("values_town");
                                    JSONArray ValuesTownTravel = json.getJSONArray("values_town_travel");
                                    JSONArray ValuesTarget = json.getJSONArray("values_targets");
                                    JSONArray ValuesSupport = json.getJSONArray("values_support");
                                    
                                    JSONArray ValuesSupportStatus = json.getJSONArray("values_support_status");
                                    JSONArray ValuesSupportDetailConvo = json.getJSONArray("values_support_convo");
                                    JSONArray ValuesTotalDiscount = json.getJSONArray("values_amount_discount");
                                    JSONArray ValuesReturnType = json.getJSONArray("values_returntype");
                                    JSONArray ValuesAppSettings = json.getJSONArray("values_app_settings");
                                    JSONArray ValuesShopCategory, ValuesSubShopCategory,
                                            ValuesProductSubType;
                                    
                                    try {
                                        ValuesShopCategory = json.getJSONArray(
                                                "values_customercategory");
                                        
                                        JSONObject vSC;
                                        sqlDB.beginTransaction();
                                        posDB.deleteShopCategory();
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        if (ValuesShopCategory != null) {
                                            for (int i = 0; i < ValuesShopCategory.length(); i++) {
                                                
                                                vSC = ValuesShopCategory.getJSONObject(i);
                                                
                                                posDB.createShopCategory(vSC.getString("iid"),
                                                        vSC.getString("iname"));
                                                
                                            }
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                    } catch (JSONException e) {
                                        
                                        if (sqlDB.inTransaction()) {
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                        }
                                        e.getMessage();
                                    }
                                    
                                    try {
                                        
                                        ValuesSubShopCategory = json.getJSONArray("values_customersubcategory");
                                        
                                        JSONObject vSC;
                                        
                                        sqlDB.beginTransaction();
                                        posDB.deleteShopSubCategory();
                                        
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        if (ValuesSubShopCategory != null) {
                                            for (int i = 0; i < ValuesSubShopCategory.length(); i++) {
                                                
                                                vSC = ValuesSubShopCategory.getJSONObject(i);
                                                
                                                posDB.createShopSubCategory(vSC.getString("iid"),
                                                        vSC.getString("iname"), vSC.getString(
                                                                "parent_id"));
                                                
                                            }
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                    } catch (JSONException e) {
                                        e.getMessage();
                                        if (sqlDB.inTransaction()) {
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                        }
                                    }
                                    
                                    try {
                                        ValuesProductSubType = json.getJSONArray(
                                                "values_subtype");
                                        
                                        JSONObject vSC;
                                        sqlDB.beginTransaction();
                                        posDB.deleteProductSubType();
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        if (ValuesProductSubType != null) {
                                            String parent_id;
                                            for (int i = 0; i < ValuesProductSubType.length(); i++) {
                                                
                                                vSC = ValuesProductSubType.getJSONObject(i);
                                                
                                                
                                                try {
                                                    parent_id = vSC.getString("parent_id");
                                                } catch (Exception e) {
                                                    
                                                    parent_id = "0";
                                                    
                                                }
                                                posDB.createProductSubType(vSC.getString("iid"),
                                                        vSC.getString("iname"), parent_id);
                                                
                                            }
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                    } catch (JSONException e) {
                                        e.getMessage();
                                        if (sqlDB.inTransaction()) {
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                        }
                                    }
                                    
                                    //String NetOrderID = json.getString("net_oid");
                                    // String AndroidID = json.getString("idChkAnd");
                                    // String PhpID = json.getString("idChk");
                                    String lastSyncedDate = json.getString("update_date");
                                    String appVersion = json.getString("appversion");
                                    String company = json.getString("company_name");
                                    String productValue = json.getString("product_val");


                                    String targetValue = json.getString("target_val");
                                    String timeIn = json.getString("time_in");
                                    String enable_catalog = json.getString("en_catalogue");
                                    String reportUrl = json.getString("report_url");
                                    
                                    Log.e("time_in", timeIn);
                                    
                                    String savedRoute, savedDistributor;
                                    
                                    try {
                                        savedRoute = json.getString("selected_route");
                                        
                                        if (savedRoute.equalsIgnoreCase("null")) {
                                            savedRoute = "0";
                                        }
                                    } catch (JSONException e) {
                                        savedRoute = selectedRoute;
                                        System.out.println("No selected_route in resp = " + e.toString());
                                    }
                                    
                                    Log.d("Saved Route in Resp", savedRoute);
                                    
                                    try {
                                        savedDistributor = json.getString("selected_distributor");
                                        
                                        if (savedDistributor.equalsIgnoreCase("null") || savedDistributor.equalsIgnoreCase("")) {
                                            savedDistributor = "0";
                                        }
                                    } catch (JSONException e) {
                                        savedDistributor = selectedDistributor;
                                        System.out.println("No selected_distributor in resp = " + e.toString());
                                    }
                                    
                                    Log.d("Saved Route in Resp", savedRoute);
                                    Log.d("Saved Dist in Resp", savedDistributor);
                                    
                                    
                                    //Log.d("Net Ids", NetOrderID);
                                    // Log.d("ids", AndroidID);
                                    // Log.d("ids", PhpID);
                                    
                                    //        posDB.OpenDb();
                                    SharedPreferences sharedPreferences = ctx.getSharedPreferences(
                                            "APP_VERSION", MODE_PRIVATE);
                                    try {
                                        SharedPreferences.Editor editor =
                                                sharedPreferences.edit();
                                        
                                        editor.putString("app_version", appVersion);
                                        
                                        editor.apply();
                                    } catch (NullPointerException e) {
                                        e.getMessage();
                                    }
                                    
                                    sqlDB.beginTransaction();
                                    posDB.updateMobUserEnableCatalog(empID, enable_catalog);
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.updateMobUserLastSynced(empID, lastSyncedDate);
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.updateMobUserAppVersion(empID, appVersion);
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.updateMobUserCompany(empID, company);
                                    
                                    posDB.updateMobUserProductSale(empID, productValue);
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.updateMobUserTarget(empID, targetValue);
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    //posDB.updateMobUserTimeIn(empID, timeIn);
                                    posDB.updateMobUserReportUrl(empID, reportUrl);
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    String[] parts = CustomerVals.split("##");
                                    
                                    if (parts.length > 0) {
                                        
                                        for (String s : parts) {
                                            String[] parts2 = s.split("@@");
                                            
                                            posDB.updateCustomerSyncStatus(parts2[0]);
                                            
                                        }
                                    }
                                    
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.DeleteSyncCustomer();
                                    
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteSaleOrder();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteSaleReturn();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteOrderDetail();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteReturnDetail();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deletePaymentRecieved();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    // Delete Table here
                                    posDB.deleteCustomerRoute();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteExpenseType();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteExpenseStatus();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteExpense();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteNoReason();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteShopVisit();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteClockInTime();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteMerchandizing();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteMerchandizingPlan();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteTown();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteTownTravel();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteTarget();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteTotalDiscount();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteReturnReason();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteAppSettings();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteItemTarget();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteDistributorList();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    // Need to Delete Support and SupportStatus and SupportDetails after Submit On Server
                                    posDB.deleteSupport();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteSupportDetail();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteSupportStatus();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.deleteShopStock();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    
                                    
                                    JSONArray ValuesSO = json.getJSONArray("values_so");
                                    
                                    JSONObject cSO;
                                    sqlDB.beginTransaction();
                                    if (ValuesSO != null) {
                                        for (int i = 0; i < ValuesSO.length(); i++) {
                                            
                                            cSO = ValuesSO.getJSONObject(i);
                                            
                                            String saleType = "1", selectedDistributorId = "0";
                                            try {
                                                saleType = cSO.getString("payment_type");
                                                
                                                if (saleType.equalsIgnoreCase("null")) {
                                                    saleType = "1";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No saleType in SaleOrder = " + e.toString());
                                            }
                                            
                                            try {
                                                selectedDistributorId = cSO.getString("selected_distributor_id");
                                                if (selectedDistributorId.equalsIgnoreCase("null") || selectedDistributorId.equalsIgnoreCase("")) {
                                                    selectedDistributorId = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No selected_distributor_id in SaleOrder = " + e.toString());
                                            }
                                            
                                            long idSO = posDB.createSalesOrderEntry(cSO.getString("order_id"), cSO.getString("customer_id"), cSO.getString("emp_id"), cSO.getString("values"), cSO.getString("notes"), "0", cSO.getString("datetime"), cSO.getString("date_short"), cSO.getString("total"), cSO.getString("total2"), cSO.getString("discount"), cSO.getString("amount_received"), cSO.getString("total_execute"), Double.parseDouble(cSO.getString("lati")), Double.parseDouble(cSO.getString("longi")), cSO.getString("execute_complete"), cSO.getString("exe_date"), cSO.getString("order_delete"), cSO.getString("order_id"), true, saleType, selectedDistributorId, 0,"");
                                            
                                            
                                        }
                                    }
                                    
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    JSONArray ValuesSR = json.getJSONArray("values_sret");
                                    
                                    
                                    JSONObject cSR;
                                    if (ValuesSR != null) {
                                        for (int i = 0; i < ValuesSR.length(); i++) {
                                            
                                            cSR = ValuesSR.getJSONObject(i);
                                            
                                            String exeComplete, selectedDistributorId = "0";
                                            
                                            exeComplete = cSR.getString("execute_complete");
                                            
                                            if (exeComplete.equalsIgnoreCase("null")) {
                                                exeComplete = "0";
                                            }
                                            
                                            try {
                                                selectedDistributorId = cSR.getString("selected_distributor_id");
                                                if (selectedDistributorId.equalsIgnoreCase("null") || selectedDistributorId.equalsIgnoreCase("")) {
                                                    selectedDistributorId = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No selected_distributor_id in SaleReturn = " + e.toString());
                                            }
                                            
                                            long idSR = posDB.createSalesReturnEntry(cSR.getString("sret_id"), cSR.getString("customer_id"), cSR.getString("emp_id"), cSR.getString("values"), cSR.getString("notes"), "0", cSR.getString("datetime"), 0, 0, cSR.getString("order_delete"), cSR.getString("sret_id"), true, cSR.getString("total"), cSR.getString("total2"), cSR.getString("discount"), 0, Integer.valueOf(cSR.getString("return_reason")), selectedDistributorId, Integer.parseInt(exeComplete));
                                            
                                            
                                        }
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    
                                    
                                    try {
                                        
                                        sqlDB.beginTransaction();
                                        JSONArray ValuesSaleOrderDetails = json.getJSONArray("values_so_details");
                                        
                                        JSONObject cOD;
                                        if (ValuesSaleOrderDetails != null) {
                                            for (int i = 0; i < ValuesSaleOrderDetails.length(); i++) {
                                                
                                                cOD = ValuesSaleOrderDetails.getJSONObject(i);
                                                
                                                String schemeFormula = "0", schemeProduct = "0",
                                                        multi_scheme;
                                                double t_o_v, d_v_1, d_v_2,
                                                        t_type, t_mrp_type, t_val;
                                                
                                                try {
                                                    schemeFormula = cOD.getString("scheme_formula");
                                                    
                                                    if (schemeFormula.equalsIgnoreCase("null")) {
                                                        schemeFormula = "0";
                                                    }
                                                } catch (JSONException e) {
                                                    System.out.println("No scheme_formula in OrderDet = " + e.toString());
                                                }
                                                
                                                try {
                                                    schemeProduct = cOD.getString("scheme_prd");
                                                    
                                                    if (schemeProduct.equalsIgnoreCase("null")) {
                                                        schemeProduct = "0";
                                                    }
                                                } catch (JSONException e) {
                                                    System.out.println("No scheme_prd in OrderDet = " + e.toString());
                                                }
                                                try {
                                                    multi_scheme = cOD.getString("multi_scheme");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    multi_scheme = "0";
                                                }
                                                
                                                try {
                                                    t_o_v = cOD.getDouble("t_o_v");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    t_o_v = 0;
                                                }
                                                try {
                                                    d_v_1 = cOD.getDouble("d_v_1");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    d_v_1 = 0;
                                                }
                                                try {
                                                    d_v_2 = cOD.getDouble("d_v_2");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    d_v_2 = 0;
                                                }
                                                try {
                                                    t_type = cOD.getDouble("t_type");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    t_type = 0;
                                                }
                                                try {
                                                    t_mrp_type = cOD.getDouble("t_mrp_type");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    t_mrp_type = 0;
                                                }
                                                try {
                                                    t_val = cOD.getDouble("t_val");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    t_val = 0;
                                                }
                                                String mrp_price;
                                                try {
                                                    mrp_price = cOD.getString("mrp_price");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    mrp_price = "0";
                                                }
                                                
                                                long idOD = posDB.createOrderDetails(cOD.getString(
                                                        "order_id"),
                                                        cOD.getString("prdid"),
                                                        cOD.getString("qty"),
                                                        cOD.getString("qty_exe"),
                                                        cOD.getString("trade_price"),
                                                        cOD.getString("discount"),
                                                        cOD.getString("discount2"),
                                                        cOD.getString("trade_offer"),
                                                        cOD.getString("scheme"),
                                                        cOD.getString("scheme_qty"),
                                                        schemeFormula,
                                                        schemeProduct,
                                                        cOD.getString("tax1"),
                                                        cOD.getString("tax2"),
                                                        cOD.getString("tax3"),
                                                        cOD.getString("sub_total"),
                                                        multi_scheme,
                                                        t_o_v,
                                                        d_v_1,
                                                        d_v_2,
                                                        t_type,
                                                        t_mrp_type,
                                                        t_val, mrp_price);
                                                
                                            }
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                    } catch (JSONException e) {
                                        
                                        if (sqlDB.inTransaction()) {
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                            
                                        }
                                        Log.e("JSONException", e.getMessage());
                                    }
                                    
                                    JSONArray ValuesReturnOrderDetails = json.getJSONArray("values_sret_details");
                                    
                                    JSONObject cRD;
                                    if (ValuesReturnOrderDetails != null) {
                                        for (int i = 0; i < ValuesReturnOrderDetails.length(); i++) {
                                            
                                            cRD = ValuesReturnOrderDetails.getJSONObject(i);
                                            
                                            String oId, qtyExe, schemeFormula = "0",
                                                    schemeProduct = "0", multi_scheme;
                                            oId = cRD.getString("order_id");
                                            qtyExe = cRD.getString("qty_exe");
                                            
                                            if (oId.equalsIgnoreCase("null")) {
                                                oId = "0";
                                            }
                                            if (qtyExe.equalsIgnoreCase("null")) {
                                                qtyExe = "0";
                                            }
                                            
                                            try {
                                                schemeFormula = cRD.getString("scheme_formula");
                                                
                                                if (schemeFormula.equalsIgnoreCase("null")) {
                                                    schemeFormula = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No scheme_formula in ReturnDet = " + e.toString());
                                            }
                                            
                                            try {
                                                schemeProduct = cRD.getString("scheme_prd");
                                                
                                                if (schemeProduct.equalsIgnoreCase("null")) {
                                                    schemeProduct = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No scheme_prd in ReturnDet = " + e.toString());
                                            }
                                            
                                            
                                            try {
                                                multi_scheme = cRD.getString("multi_scheme");
                                                
                                                
                                            } catch (JSONException e) {
                                                multi_scheme = "0";
                                            }
                                            
                                            double t_o_v, d_v_1, d_v_2,
                                                    t_type, t_mrp_type, t_val;
                                            try {
                                                t_o_v = cRD.getDouble("t_o_v");
                                                
                                                
                                            } catch (JSONException e) {
                                                t_o_v = 0;
                                            }
                                            try {
                                                d_v_1 = cRD.getDouble("d_v_1");
                                                
                                                
                                            } catch (JSONException e) {
                                                d_v_1 = 0;
                                            }
                                            try {
                                                d_v_2 = cRD.getDouble("d_v_2");
                                                
                                                
                                            } catch (JSONException e) {
                                                d_v_2 = 0;
                                            }
                                            try {
                                                t_type = cRD.getDouble("t_type");
                                                
                                                
                                            } catch (JSONException e) {
                                                t_type = 0;
                                            }
                                            try {
                                                t_mrp_type = cRD.getDouble("t_mrp_type");
                                                
                                                
                                            } catch (JSONException e) {
                                                t_mrp_type = 0;
                                            }
                                            try {
                                                t_val = cRD.getDouble("t_val");
                                                
                                                
                                            } catch (JSONException e) {
                                                t_val = 0;
                                            }
                                            String mrp_price;
                                            try {
                                                mrp_price = cRD.getString("mrp_price");
                                                
                                                
                                            } catch (JSONException e) {
                                                mrp_price = "0";
                                            }
                                            
                                            long idOD = posDB.createReturnDetails(oId,
                                                    cRD.getString("prdid"), cRD.getString("qty"), qtyExe, cRD.getString("trade_price"), cRD.getString("discount"), cRD.getString("discount2"), cRD.getString("trade_offer"), cRD.getString("scheme"), cRD.getString("scheme_qty"), schemeFormula, schemeProduct, cRD.getString("tax1"), cRD.getString("tax2"), cRD.getString("tax3"), cRD.getString("sub_total"),
                                                    multi_scheme, t_o_v, d_v_1, d_v_2, t_type
                                                    , t_mrp_type, t_val, mrp_price);
                                            
                                        }
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    
                                    JSONArray ValuesPayment = json.getJSONArray("values_payment");
                                    
                                    JSONObject cPR;
                                    for (int i = 0; i < ValuesPayment.length(); i++) {
                                        
                                        cPR = ValuesPayment.getJSONObject(i);
                                        String empId, executeComplete = "0", chequeNo = "0", chequeDate = "0", bankName = "0", paymentType = "0", selectedDistributorId = "0";
                                        empId = cPR.getString("emp_id");
                                        
                                        if (empId.equalsIgnoreCase("null")) {
                                            empId = "0";
                                        }
                                        
                                        try {
                                            executeComplete = cPR.getString("execute_complete");
                                            if (executeComplete.equalsIgnoreCase("null")) {
                                                executeComplete = "0";
                                            }
                                        } catch (JSONException e) {
                                            System.out.println("No execute_complete in PaymentReceived = " + e.toString());
                                        }
                                        
                                        try {
                                            chequeNo = cPR.getString("cheque_no");
                                            if (chequeNo.equalsIgnoreCase("null")) {
                                                chequeNo = "0";
                                            }
                                        } catch (JSONException e) {
                                            System.out.println("No cheque_no in PaymentReceived = " + e.toString());
                                        }
                                        
                                        try {
                                            chequeDate = cPR.getString("cheque_date");
                                            if (chequeDate.equalsIgnoreCase("null")) {
                                                chequeDate = "0";
                                            }
                                        } catch (JSONException e) {
                                            System.out.println("No cheque_date in PaymentReceived = " + e.toString());
                                        }
                                        
                                        try {
                                            bankName = cPR.getString("bank_name");
                                            if (bankName.equalsIgnoreCase("null")) {
                                                bankName = "0";
                                            }
                                        } catch (JSONException e) {
                                            System.out.println("No bank_name in PaymentReceived = " + e.toString());
                                        }
                                        
                                        try {
                                            paymentType = cPR.getString("mode");
                                            if (paymentType.equalsIgnoreCase("null")) {
                                                paymentType = "0";
                                            }
                                        } catch (JSONException e) {
                                            System.out.println("No payment_type/mode in PaymentReceived = " + e.toString());
                                        }
                                        
                                        try {
                                            selectedDistributorId = cPR.getString("selected_distributor_id");
                                            if (selectedDistributorId.equalsIgnoreCase("null") || selectedDistributorId.equalsIgnoreCase("")) {
                                                selectedDistributorId = "0";
                                            }
                                        } catch (JSONException e) {
                                            System.out.println("No selected_distributor_id in PaymentReceived = " + e.toString());
                                        }
                                        
                                        long idPR = posDB.createPaymentRecieved(cPR.getString("payment_id"), cPR.getString("cid"), empId, cPR.getString("amount"), cPR.getString("datetime"), cPR.getString("details"), chequeNo, chequeDate, bankName, Integer.parseInt(executeComplete), paymentType, selectedDistributorId, 0);
                                        
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    String[] BrandParts = BrandVals.split("##");
                                    
                                    if (BrandParts.length > 0) {
                                        for (String s : BrandParts) {
                                            
                                            String[] parts2 = s.split("@@");
                                            
                                            posDB.updateBrandsSyncStatus(parts2[0]);
                                        }
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    
                                    posDB.DeleteSyncBrands();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    String[] TypeParts = ItemTypeVals.split("##");
                                    
                                    if (TypeParts.length > 0) {
                                        for (String s : TypeParts) {
                                            
                                            String[] parts2 = s.split("@@");
                                            
                                            posDB.updateItemTypeSyncStatus(parts2[0]);
                                        }
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.DeleteSyncItemtype();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    
                                    String[] ItemParts = ItemVals.split("##");
                                    
                                    if (ItemParts.length > 0) {
                                        
                                        for (String s : ItemParts) {
                                            
                                            String[] parts2 = s.split("@@");
                                            posDB.updateInventorySyncStatus(parts2[0]);
                                        }
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    posDB.DeleteSyncInventory();
                                    
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.TruncateCustomerRoutes();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.truncateCustomerPricing();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.TruncateCustomerType();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    
                                    
                                    try {
                                        sqlDB.beginTransaction();
                                        JSONArray ValuesPricing = json.getJSONArray("values_multi_pricing");
                                        JSONObject iDetails;
                                        for (int i = 0; i < ValuesPricing.length(); i++) {
                                            
                                            iDetails = ValuesPricing.getJSONObject(i);
                                            
                                            String schemeProduct = "0",
                                                    filer1,
                                                    filer2,
                                                    filer3,
                                                    customer_id,
                                                    customer_category_id,
                                                    customer_subcategory_id,
                                                    min_amount,
                                                    max_amount,
                                                    brand_id,
                                                    product_type_id,
                                                    product_sub_type_id,
                                                    min_qty,
                                                    max_qty, multi,
                                                    use_defaultprice,
                                                    mrp_price,
                                                    use_defaultmrp,
                                                    dist_id,
                                                    emp_id,
                                                    prd_id,
                                            customer_type;
                                            
                                            try {
                                                schemeProduct = iDetails.getString("scheme_prd");
                                                
                                                if (schemeProduct.equalsIgnoreCase("null") || schemeProduct.equalsIgnoreCase("")) {
                                                    schemeProduct = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No scheme_prd in CustPricing = " + e.toString());
                                            }
                                            
                                            try {
                                                
                                                filer1 = iDetails.getString("tax_filer_1").equals("")? "0": iDetails.getString("tax_filer_1");
                                                
                                            } catch (JSONException e) {
                                                filer1 = "0";
                                            }
                                            
                                            try {
                                                
                                                filer2 = iDetails.getString("tax_filer_2").equals("")? "0": iDetails.getString("tax_filer_2");
                                                
                                            } catch (JSONException e) {
                                                filer2 = "0";
                                            }
                                            
                                            try {
                                                
                                                filer3 = iDetails.getString("tax_filer_3").equals("")? "0": iDetails.getString("tax_filer_3");
                                                
                                            } catch (JSONException e) {
                                                filer3 = "0";
                                            }
                                            
                                            
                                            try {
                                                
                                                customer_id = iDetails.getString("customer_id").equals("")? "0": iDetails.getString("customer_id");
                                                
                                            } catch (JSONException e) {
                                                customer_id = "0";
                                            }
                                            try {
                                                
                                                brand_id = iDetails.getString("brand_id").equals("")? "0": iDetails.getString("brand_id");
                                                
                                            } catch (JSONException e) {
                                                brand_id = "0";
                                            }
                                            try {
                                                
                                                product_type_id = iDetails.getString("category_id").equals("")? "0": iDetails.getString("category_id");
                                                
                                            } catch (JSONException e) {
                                                product_type_id = "0";
                                            }
                                            try {
                                                
                                                product_sub_type_id = iDetails.getString("subcategory_id").equals("")? "0": iDetails.getString("subcategory_id");
                                                
                                            } catch (JSONException e) {
                                                product_sub_type_id = "0";
                                            }
                                            try {
                                                
                                                min_qty = iDetails.getString("min_qty").equals("")? "0": iDetails.getString("min_qty");
                                                
                                            } catch (JSONException e) {
                                                min_qty = "NULL";
                                            }
                                            try {
                                                
                                                max_qty = iDetails.getString("max_qty").equals("")? "0": iDetails.getString("max_qty");
                                                
                                            } catch (JSONException e) {
                                                max_qty = "NULL";
                                            }
                                            try {
                                                
                                                multi = iDetails.getString("multi").equals("")? "0": iDetails.getString("multi");
                                                
                                            } catch (JSONException e) {
                                                multi = "0";
                                            }
                                            try {
                                                
                                                customer_category_id = iDetails.getString("customer_category_id").equals("")? "0": iDetails.getString("customer_category_id");
                                                
                                            } catch (JSONException e) {
                                                customer_category_id = "0";
                                            }
                                            
                                            try {
                                                
                                                customer_subcategory_id = iDetails.getString("customer_subcategory_id").equals("")? "0": iDetails.getString("customer_subcategory_id");
                                                
                                            } catch (JSONException e) {
                                                customer_subcategory_id = "0";
                                            }
                                            
                                            try {
                                                
                                                min_amount = iDetails.getString("min_amount").equals("")? "0": iDetails.getString("min_amount");
                                                
                                            } catch (JSONException e) {
                                                min_amount = "0";
                                            }
                                            try {
                                                
                                                max_amount = iDetails.getString(
                                                        "max_amount").equals("")? "0": iDetails.getString("max_amount");
                                                
                                            } catch (JSONException e) {
                                                max_amount = "0";
                                            }
                                            
                                            
                                            try {
                                                
                                                use_defaultmrp = iDetails.getString("use_defaultmrp").equals("")? "0": iDetails.getString("use_defaultmrp");
                                                
                                            } catch (JSONException e) {
                                                use_defaultmrp = "0";
                                            }
                                            try {
                                                
                                                mrp_price = iDetails.getString("mrp_price").equals("")? "0": iDetails.getString("mrp_price");
                                                
                                            } catch (JSONException e) {
                                                mrp_price = "0";
                                            }
                                            try {
                                                
                                                use_defaultprice = iDetails.getString("use_defaultprice").equals("")? "0": iDetails.getString("use_defaultprice");
                                                
                                            } catch (JSONException e) {
                                                use_defaultprice = "0";
                                            }
                                            
                                            try {
                                                
                                                dist_id = iDetails.getString("distributor_id").equals("")? "0": iDetails.getString("distributor_id");
                                                
                                            } catch (JSONException e) {
                                                dist_id = "0";
                                            }
                                            try {
                                                
                                                emp_id = iDetails.getString("booker_id").equals("")? "0": iDetails.getString("booker_id");
                                                
                                            } catch (JSONException e) {
                                                emp_id = "0";
                                            }
    
                                            try {
    
                                                customer_type = iDetails.getString("customer_type").equals("")? "0": iDetails.getString("customer_type");
        
                                            } catch (JSONException e) {
                                                customer_type = "0";
                                            }
                                            try {
    
                                                prd_id = iDetails.getString("prd_id").equals("")? "0": iDetails.getString("prd_id");
        
                                            } catch (JSONException e) {
                                                prd_id = "0";
                                            }
                                            
                                            posDB.createCustomerPricing(
                                                    
                                                    customer_type,
                                                   prd_id,
                                                    iDetails.getString("sale_price"),
                                                    iDetails.getString("disc1_value"),
                                                    iDetails.getString("disc2_value"),
                                                    iDetails.getString("trade_offer"),
                                                    iDetails.getString("scheme"),
                                                    iDetails.getString("scheme_value"),
                                                    schemeProduct,
                                                    iDetails.getString("tax1"),
                                                    iDetails.getString("tax2"),
                                                    iDetails.getString("tax3"),
                                                    filer1,
                                                    filer2,
                                                    filer3, customer_id, brand_id, product_type_id,
                                                    product_sub_type_id, min_qty, max_qty, multi,
                                                    customer_category_id,
                                                    customer_subcategory_id,
                                                    min_amount,
                                                    max_amount,
                                                    use_defaultprice,
                                                    mrp_price,
                                                    use_defaultmrp,
                                                    emp_id,
                                                    dist_id);
                                            
                                        }
                                        
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        
                                    } catch (JSONException e) {
                                        if (sqlDB.inTransaction()) {
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                        }
                                        Log.e("CUST_PRICING",
                                                e.getMessage());
                                    }
                                    // Umais End here
                                    
                                    
                                    sqlDB.beginTransaction();
                                    JSONArray ValuesBrand = json.getJSONArray("values_brand");
                                    JSONObject cBrand;
                                    for (int i = 0; i < ValuesBrand.length(); i++) {
                                        cBrand = ValuesBrand.getJSONObject(i);
                                        posDB.InsertSyncedBrands(cBrand.getString("bid"), cBrand.getString("bname"));
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
if(BuildConfig.FLAVOR.equalsIgnoreCase("brands_unlimited")) {
    JSONArray ValuesCustomerBrand = json.getJSONArray("values_customerbrand");
    JSONObject v_CustomerBrand;
    posDB.DeleteFromCustomerBrand();
    sqlDB.beginTransaction();
    for (int i = 0; i < ValuesCustomerBrand.length(); i++) {
        v_CustomerBrand = ValuesCustomerBrand.getJSONObject(i);
        
        posDB.insertInCustomerBrand(v_CustomerBrand.getString("id"),
                v_CustomerBrand.getString("brand_id"), v_CustomerBrand.getString(
                        "customer_id"));
    }
    sqlDB.setTransactionSuccessful();
    sqlDB.endTransaction();
}
                                    
                                    sqlDB.beginTransaction();
                                    posDB.deleteVerifyStock();
                                    posDB.   deleteVerifyStockDetail();
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();

                                    if(json.has("values_verifystock")) {
                                    sqlDB.beginTransaction();

                                        JSONArray VerifyStock = json.getJSONArray("values_verifystock");

                                        JSONObject cVerifyStock = null;

                                        if (VerifyStock != null) {
                                            for (int i = 0; i < VerifyStock.length(); i++) {

                                                cVerifyStock = VerifyStock.getJSONObject(i);


                                                Log.wtf("VerifyStock", VerifyStock.toString());
                                                long idSO = posDB.createPatientOrderEntry(Integer.parseInt(cVerifyStock.getString(
                                                        "order_id")), cVerifyStock.getString("emp_id"), Integer.parseInt(cVerifyStock.getString("customer_id")), Integer.parseInt(cVerifyStock.getString("selected_distributor_id")), "", 2, cVerifyStock.getString("datetime"));


                                            }
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        JSONArray ValuesVerifyStockDetails = json.getJSONArray("values_verifystock2");


                                        JSONObject cVerifyStockDetail = null;
                                        if (ValuesVerifyStockDetails != null) {
                                            for (int i = 0; i < ValuesVerifyStockDetails.length(); i++) {

                                                cVerifyStockDetail = ValuesVerifyStockDetails.getJSONObject(i);


                                                posDB.createPatientOrderDetailsync(Integer.parseInt(cVerifyStockDetail.getString("order_id")), cVerifyStockDetail.getString("prd_id"), cVerifyStockDetail.getString("qty"));

                                            }
                                        }

                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                    }
                                    if(json.has("sample_request_values")) {
                                        sqlDB.beginTransaction();

                                        JSONArray VerifyStock = json.getJSONArray("sample_request_values");

                                        JSONObject cVerifyStock = null;

                                        if (VerifyStock != null) {
                                            for (int i = 0; i < VerifyStock.length(); i++) {

                                                cVerifyStock = VerifyStock.getJSONObject(i);


                                                Log.wtf("VerifyStock", VerifyStock.toString());
                                                long idSO = posDB.createPatientOrderEntry2(Integer.parseInt(cVerifyStock.getString(
                                                        "order_id")), cVerifyStock.getString("emp_id"), Integer.parseInt(cVerifyStock.getString("customer_id")), Integer.parseInt(cVerifyStock.getString("selected_distributor_id")), "", 2, cVerifyStock.getString("datetime"));


                                            }
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        JSONArray ValuesVerifyStockDetails = json.getJSONArray("sample_request_details");


                                        JSONObject cVerifyStockDetail = null;
                                        if (ValuesVerifyStockDetails != null) {
                                            for (int i = 0; i < ValuesVerifyStockDetails.length(); i++) {

                                                cVerifyStockDetail = ValuesVerifyStockDetails.getJSONObject(i);


                                                posDB.createPatientOrderDetailsync2(Integer.parseInt(cVerifyStockDetail.getString("order_id")), cVerifyStockDetail.getString("prd_id"), cVerifyStockDetail.getString("qty"));

                                            }
                                        }

                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                    }
                                    sqlDB.beginTransaction();
                                    
                                    JSONArray ValuesType = json.getJSONArray("values_type");
                                    JSONObject cType;
                                    for (int i = 0; i < ValuesType.length(); i++) {
                                        cType = ValuesType.getJSONObject(i);
                                        
                                        posDB.InsertSyncedItemType(cType.getString("iid"), cType.getString("iname"));
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    
                                    
                                    sqlDB.beginTransaction();
                                    JSONArray ValuesItem = json.getJSONArray("values_inventory");
                                    JSONObject cItem;
                                    
                                    try {
                                        for (int i = 0; i < ValuesItem.length(); i++) {
                                            
                                            cItem = ValuesItem.getJSONObject(i);
                                            
                                            posDB.InsertSyncedInventory(cItem.getString("inventid"),
                                                    cItem.getString("brid"),
                                                    cItem.getString("typeid"),
                                                    cItem.getString("vid"),
                                                    cItem.getString("name"),
                                                    cItem.getString("uc"),
                                                    cItem.getString("sp"),
                                                    cItem.getString("sku"),
                                                    cItem.getString("tax"),
                                                    cItem.getString("packing"),
                                                    cItem.getString("qty"),
                                                    cItem.getString("details")/*, dataImage.get(i)*/,
                                                    cItem.getString("subtypeid"),
                                                    cItem.getString("mrp_price"),
                                                    cItem.getString("tax_mrp"),
                                                    cItem.getString("is_taxable"));
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        
                                    } catch (JSONException e) {
                                        if (sqlDB.inTransaction()) {
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                            
                                        }
                                    }
                                    sqlDB.beginTransaction();
                                    try {
                                        JSONArray ValuesDistributorList = json.getJSONArray("values_distributor");
                                        
                                        JSONObject distList;
                                        for (int i = 0; i < ValuesDistributorList.length(); i++) {
                                            
                                            distList = ValuesDistributorList.getJSONObject(i);
                                            long insDistributorList = posDB.createDistributorList(Integer.parseInt(distList.getString("id")), distList.getString("key"), "0", 0);
                                            
                                        }
                                    } catch (JSONException e) {
                                        Log.d("NoDistListArray", "in syncEverything");
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    JSONArray ValuesCustRoute = json.getJSONArray("values_route");
                                    JSONObject cCustRoute;
                                    
                                    for (int i = 0; i < ValuesCustRoute.length(); i++) {
                                        cCustRoute = ValuesCustRoute.getJSONObject(i);
                                        
                                        posDB.InsertCustomerRoute(cCustRoute.getString("rid"),
                                                cCustRoute.getString("rname"), 0, 0,
                                                cCustRoute.getInt("day"));
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    posDB.updateSavedRoute(savedRoute);
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    posDB.updateSavedDistributorList(savedDistributor);
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    JSONArray ValuesCustType = json.getJSONArray("values_customertype");
                                    JSONObject custType;
                                    for (int i = 0; i < ValuesCustType.length(); i++) {
                                        
                                        custType = ValuesCustType.getJSONObject(i);
                                        posDB.insertCustomerType(custType.getString("iid"), custType.getString("iname"));
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    
                                    JSONArray ValuesCust = json.getJSONArray("values_cust");
                                    JSONObject cCust;
                                    for (int i = 0; i < ValuesCust.length(); i++) {
                                        cCust = ValuesCust.getJSONObject(i);
                                        
                                        String lat, longi, map, rad, smsCode = "0", numberVerified = "0", advancePayment = "0", lastUpdate = "0", customerCeleb = "0",
                                                filer_non_filer, customer_category,
                                                cust_sub_category;
                                        
                                        lat = cCust.getString("lat");
                                        longi = cCust.getString("long");
                                        map = cCust.getString("map");
                                        rad = cCust.getString("rad");
                                        
                                        if (lat.equalsIgnoreCase("null")) {
                                            lat = "0";
                                        }
                                        
                                        if (longi.equalsIgnoreCase("null")) {
                                            longi = "0";
                                        }
                                        
                                        if (map.equalsIgnoreCase("null")) {
                                            map = "0";
                                        }
                                        
                                        if (rad.equalsIgnoreCase("null")) {
                                            rad = "0";
                                        }
                                        try {
                                            smsCode = cCust.getString("sms_code");
                                            numberVerified = cCust.getString("number_verified");
                                            advancePayment = cCust.getString("advance_payment");
                                            
                                            if (smsCode.equalsIgnoreCase("null")) {
                                                smsCode = "0";
                                            }
                                            
                                            if (numberVerified.equalsIgnoreCase("null")) {
                                                numberVerified = "0";
                                            }
                                            
                                            if (advancePayment.equalsIgnoreCase("null")) {
                                                advancePayment = "0";
                                            }
                                            
                                            
                                        } catch (JSONException e) {
                                            System.out.println("No sms_code, num_verified, advance_payment in cust = " + e.toString());
                                        }
                                        
                                        try {
                                            lastUpdate = cCust.getString("last_update");
                                            
                                            if (lastUpdate.equalsIgnoreCase("null")) {
                                                lastUpdate = "0";
                                            }
                                        } catch (JSONException e) {
                                            System.out.println("No last_update in cust = " + e.toString());
                                        }
                                        
                                        try {
                                            customerCeleb = cCust.getString("payment_mode");
                                            
                                            if (customerCeleb.equalsIgnoreCase("null")) {
                                                customerCeleb = "0";
                                            }
                                        } catch (JSONException e) {
                                            System.out.println("No customer_category in cust = " + e.toString());
                                        }
                                        
                                        try {
                                            filer_non_filer = cCust.getString(
                                                    "filer_non_filer");
                                        } catch (JSONException e) {
                                            filer_non_filer = "0";
                                            /*TODO if (i%2 == 0) filer_non_filer = "0"; else  filer_non_filer = "1";*/
                                        }
                                        
                                        try {
                                            customer_category = cCust.getString(
                                                    "customer_category");
                                        } catch (JSONException e) {
                                            customer_category = "0";
                                            
                                        }
                                        
                                        try {
                                            cust_sub_category = cCust.getString(
                                                    "customer_sub_category");
                                        } catch (JSONException e) {
                                            cust_sub_category = "0";
                                            
                                        }
                                        
                                        
                                        // Yahan bhi kuch krna paryga jani
                                        posDB.InsertSyncedCustomer(cCust.getString("cid"),
                                                cCust.getString("cid"), cCust.getString("fname"),
                                                cCust.getString("lname"), cCust.getString(
                                                        "compname"), cCust.getString("cell"),
                                                cCust.getString("p1"), cCust.getString("p2"),
                                                cCust.getString("ob"), cCust.getString("oba"),
                                                cCust.getString("credit_amount"),
                                                cCust.getString("cnic"), cCust.getString("add"), cCust.getString("city"), cCust.getString("state"), cCust.getString("country"), cCust.getString("email"), cCust.getString("notes"), lat, longi, map, rad, 1, cCust.getInt("cust_route_id"), cCust.getInt("customer_type"), cCust.getString("app_payable"), smsCode, numberVerified, advancePayment, lastUpdate, customerCeleb, filer_non_filer, customer_category, cust_sub_category);
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    
                                    //////// New START
                                    
                                    JSONObject expType;
                                    for (int i = 0; i < ValuesExpenseType.length(); i++) {
                                        
                                        expType = ValuesExpenseType.getJSONObject(i);
                                        
                                        long idExpTyp = posDB.insertExpenseType(expType.getString("iid"), expType.getString("iname"));
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    
                                    JSONObject expStatus;
                                    for (int i = 0; i < ValuesExpenseStatus.length(); i++) {
                                        
                                        expStatus = ValuesExpenseStatus.getJSONObject(i);
                                        
                                        long idExpStatus = posDB.insertExpenseStatus(expStatus.getString("iid"), expStatus.getString("iname"));
                                        
                                        
                                    }
                                    
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    
                                    JSONObject noReason;
                                    for (int i = 0; i < ValuesNoReason.length(); i++) {
                                        
                                        noReason = ValuesNoReason.getJSONObject(i);
                                        
                                        long idNoREa = posDB.insertNoReason(noReason.getString("iid"), noReason.getString("iname"));
                                        
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    
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
                                        long idClInTime = posDB.createClockInTime(clockIn.getString("iid"), clockin, clockout);
                                        
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    JSONObject merchandizing;
                                    for (int i = 0; i < ValuesMerchandizing.length(); i++) {
                                        
                                        merchandizing = ValuesMerchandizing.getJSONObject(i);
                                        
                                        long idMerc = posDB.createMerchandizing(merchandizing.getString("iid"), merchandizing.getString("shop_id"), merchandizing.getString("campaign_id"), merchandizing.getString("product_id"), merchandizing.getString("datetime"), merchandizing.getString("remarks"));
                                        
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    JSONObject mercPlan;
                                    for (int i = 0; i < ValuesMerchandizingPlan.length(); i++) {
                                        
                                        mercPlan = ValuesMerchandizingPlan.getJSONObject(i);
                                        
                                        long idMercPlan = posDB.createMerchandizingPlan(Integer.parseInt(mercPlan.getString("iid")),
                                                mercPlan.getString("name"), Integer.parseInt(mercPlan.getString("prd_id1")),
                                                Integer.parseInt(mercPlan.getString("prd_id2")), Integer.parseInt(mercPlan.getString("prd_id3")));
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    JSONObject mExp;
                                    for (int i = 0; i < ValuesMarkExpense.length(); i++) {
                                        
                                        mExp = ValuesMarkExpense.getJSONObject(i);
                                        
                                        String shopId, commitmentId;
                                        
                                        shopId = mExp.getString("shop_id");
                                        commitmentId = mExp.getString("commitment_id");
                                        
                                        if (shopId.equalsIgnoreCase("null")) {
                                            shopId = "0";
                                        }
                                        if (commitmentId.equalsIgnoreCase("null")) {
                                            commitmentId = "0";
                                        }
                                        long idMarkExp = posDB.createMarketingExpense(mExp.getString("iid"), shopId, commitmentId, mExp.getString("amount"), mExp.getString("mexpense_type"), mExp.getString("datetime"), mExp.getString("remarks"), mExp.getString("status"), 0);
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    
                                    JSONObject shopChkIn;
                                    for (int i = 0; i < ValuesShopVisit.length(); i++) {
                                        
                                        shopChkIn = ValuesShopVisit.getJSONObject(i);
                                        
                                        String selectedDistributorId = "0";
                                        try {
                                            selectedDistributorId = shopChkIn.getString("selected_distributor_id");
                                            if (selectedDistributorId.equalsIgnoreCase("null") || selectedDistributorId.equalsIgnoreCase("")) {
                                                selectedDistributorId = "0";
                                            }
                                        } catch (JSONException e) {
                                            System.out.println("No selected_distributor_id in ShopCheckIn = " + e.toString());
                                        }
                                        
                                        
                                        posDB.createShopCheckIn(shopChkIn.getString("iid"), shopChkIn.getString("shop_id"), shopChkIn.getString("remarks"), shopChkIn.getString("reason_id"), shopChkIn.getString("datetime"), selectedDistributorId, 0);
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    JSONObject town;
                                    for (int i = 0; i < ValuesTown.length(); i++) {
                                        
                                        town = ValuesTown.getJSONObject(i);
                                        
                                        long idTown = posDB.createTown(Integer.parseInt(town.getString("iid")), town.getString("name"));
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    
                                    JSONObject townTravel;
                                    
                                    sqlDB.beginTransaction();
                                    for (int i = 0; i < ValuesTownTravel.length(); i++) {
                                        
                                        townTravel = ValuesTownTravel.getJSONObject(i);
                                        
                                        long idTownTravel = posDB.createTownTravel(Integer.parseInt(townTravel.getString("iid")), Integer.parseInt(townTravel.getString("town_id1")), Integer.parseInt(townTravel.getString("town_id2")), townTravel.getString("one_way"), townTravel.getString("two_way"), townTravel.getString("stay_accom"));
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    JSONObject target;
                                    for (int i = 0; i < ValuesTarget.length(); i++) {
                                        
                                        target = ValuesTarget.getJSONObject(i);
                                        
                                        long idTarget = posDB.createTarget(Integer.parseInt(target.getString("prd_id")), target.getString("qty"));
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    JSONObject support;
                                    for (int i = 0; i < ValuesSupport.length(); i++) {
                                        
                                        support = ValuesSupport.getJSONObject(i);
                                        long supportId = posDB.createSupport(Integer.parseInt(support.getString("iid")), support.getString("title"), Integer.parseInt(support.getString("status")), support.getString("datetime"), 0, support.getString("iid"));
                                        
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    JSONObject supportConvo;
                                    for (int i = 0; i < ValuesSupportDetailConvo.length(); i++) {
                                        
                                        supportConvo = ValuesSupportDetailConvo.getJSONObject(i);
                                        long supportDetailId = posDB.createSupportDetail(Integer.parseInt(supportConvo.getString("iid")), Integer.parseInt(supportConvo.getString("sid")), supportConvo.getString("message"), supportConvo.getString("datetime"), Integer.parseInt(supportConvo.getString("uid")), 0);
                                        
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    JSONObject supportStatus;
                                    for (int i = 0; i < ValuesSupportStatus.length(); i++) {
                                        
                                        supportStatus = ValuesSupportStatus.getJSONObject(i);
                                        long supportStatusId = posDB.createSupportStatus(Integer.parseInt(supportStatus.getString("iid")), supportStatus.getString("iname"));
                                        
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    JSONObject totalDisc;
                                    for (int i = 0; i < ValuesTotalDiscount.length(); i++) {
                                        
                                        totalDisc = ValuesTotalDiscount.getJSONObject(i);
                                        long totalDiscId = posDB.createTotalDiscount(Integer.parseInt(totalDisc.getString("iid")), Double.parseDouble(totalDisc.getString("min_amount")), Double.parseDouble(totalDisc.getString("discount_percentage")));
                                        
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    JSONObject rReason;
                                    for (int i = 0; i < ValuesReturnType.length(); i++) {
                                        
                                        rReason = ValuesReturnType.getJSONObject(i);
                                        
                                        long idReturnReason = posDB.InsertReturnReason(rReason.getString("iid"), rReason.getString("iname"));
                                        
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    JSONObject appSettings;
                                    for (int i = 0; i < ValuesAppSettings.length(); i++) {
                                        
                                        appSettings = ValuesAppSettings.getJSONObject(i);
                                        long appSettingsID = posDB.createAppSetting(Integer.parseInt(appSettings.getString("id")), appSettings.getString("key"), Integer.parseInt(appSettings.getString("value")));
                                        
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    
                                    sqlDB.beginTransaction();
                                    try {
                                        
                                        
                                        JSONArray ValuesItemTarget = json.getJSONArray("values_item_target");
                                        
                                        JSONObject itemTarget;
                                        for (int i = 0; i < ValuesItemTarget.length(); i++) {
                                            
                                            itemTarget = ValuesItemTarget.getJSONObject(i);
                                            long insItemTarget = posDB.createItemTarget(Integer.parseInt(itemTarget.getString("id")),
                                                    itemTarget.getString("item"), itemTarget.getString("target"),
                                                    itemTarget.getString("sold"), itemTarget.getString("achieved"));
                                            
                                            
                                        }
                                        
                                        
                                    } catch (JSONException e) {
                                        
                                        Log.d("No ItemTarget array", "in syncEverything");
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    
                                    //////// New END
                                    
                                    
                                    String timeInLogic = posDB.getTimeInLogic();
                                    
                                    if (!posDB.getTimeInLogic().equalsIgnoreCase("")) {
                                        
                                        
                                        posDB.updateMobUserTimeIn(empID, "1");
                                        
                                        
                                        posDB.updateMobUserTimeInTIME(empID, timeInLogic);
                                        
                                        
                                    } else {
                                        posDB.updateMobUserTimeIn(empID, "0");
                                    }

                             /*       if (posDB.getAppSettingsValueByKey("en_target_notification") != 0) {
                                        int time = posDB.getAppSettingsValueByKey("en_target_notification_time");
                                        String productVal = posDB.getMobEmpProductSale();
                                        String targetVal = posDB.getMobEmpTarget();

                                        Log.e("getProgress", productVal + " : " + targetVal);
                                        int productSaleFromDB = 0;
                                        int targetFromDB = 0;
                                        if (productVal != null && !productVal.equals("null")) {
                                            productSaleFromDB = Integer.parseInt(productVal);
                                        }
                                        if (targetVal != null && !targetVal.equals("null")) {
                                            targetFromDB = Integer.parseInt(targetVal);
                                        }
                                        int result2=0;
                                        if(targetFromDB==0){
                                            result2=0;
                                        }else{

                                            result2=(100*productSaleFromDB)/targetFromDB;
                                        }
                                        if (time != 0) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                CharSequence name = "SG Sales Progress";
                                                String description = MessageFormat.format(" Target  Achievement {0}% | Total Target {2} | Unit Booked: {1}",
                                                        String.valueOf(result2),
                                                        posDB.getMobEmpProductSale(),
                                                        posDB.getMobEmpTarget());
                                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                                NotificationChannel channel = new NotificationChannel("SaeedGhani", name, importance);
                                                channel.setDescription(description);

                                                NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
                                                notificationManager.createNotificationChannel(channel);
                                            }


                                            Intent intent = new Intent(ctx, ReminderBroadcastReciever.class);
                                            intent.putExtra("event", "SG Sales Progress");
                                            intent.putExtra("time",  MessageFormat.format(" Target  Achievement {0}% | Total Target {2} | Unit Booked: {1}",
                                                    String.valueOf(result2),
                                                    posDB.getMobEmpProductSale(),
                                                    posDB.getMobEmpTarget()));
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.set(Calendar.HOUR_OF_DAY, time);
                                            calendar.set(Calendar.MINUTE, 00);
                                            calendar.set(Calendar.SECOND, 0);
                                            if (calendar.getTime().compareTo(new Date()) < 0)
                                                calendar.add(Calendar.DAY_OF_MONTH, 1);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
                                            AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                                            long timeintest = System.currentTimeMillis();
                                            Log.d("TAG", "onCreateView: " + calendar.getTimeInMillis());
                                            long tenTest = 1000 * 30;
//        alarmManager.set(AlarmManager.RTC_WAKEUP,
//                timeintest+tenTest,pendingIntent);
                                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                                    calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                        }

                                    }*/
                                    GetSalesmanSettings(ldr, empID);
                                    
                                    
                                } else if (Integer.parseInt(res) == 0) {
                                    
                                    if (sqlDB.inTransaction()) {
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                    }
                                    ctx.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ldr.HideLoader();
                                            Toast.makeText(ctx, "User not found.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                
                            }
                            
                            
                        } catch (IOException e) {
                            if (sqlDB.inTransaction()) {
                                sqlDB.setTransactionSuccessful();
                                sqlDB.endTransaction();
                            }
                            ldr.HideLoader();
                            
                            Log.d("Prof", "Excep " + e.toString());
                            Toast.makeText(AppContextProvider.getContext(), "I/O Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                            
                            e.printStackTrace();
                        } catch (JSONException e) {
                            ldr.HideLoader();
                            if (sqlDB.inTransaction()) {
                                sqlDB.setTransactionSuccessful();
                                sqlDB.endTransaction();
                            }
                            Log.d("Prof", "Excep JSOn " + e.toString());
                            Toast.makeText(AppContextProvider.getContext(), "Json Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                            
                            e.printStackTrace();
                        } catch (final Exception e) {
                            if (sqlDB.inTransaction()) {
                                sqlDB.setTransactionSuccessful();
                                sqlDB.endTransaction();
                            }
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ldr.HideLoader();
                                    Log.d("Prof", "Excep " + e.toString());
                                    
                                    e.printStackTrace();
                                    Toast.makeText(AppContextProvider.getContext(), "Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        
                        
                    }
                    
                    @Override
                    public void failure(RetrofitError error) {
                        //If any error occured displaying the error as toast
                        SharedPreferences sharedPreferences = ctx.getSharedPreferences("SYNC_VALUE", MODE_PRIVATE);
                        
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("sync_failed", 1);
                        editor.apply();
                        Log.e("RetrofitError", "Sync Error " + error.getMessage());
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                
                                ldr.HideLoader();
                                
                                Toast.makeText(AppContextProvider.getContext(), "Kindly Check Your Internet Connection", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
        );
        
        
    }
    
    public void SignIn(final Loader ldr, final String user_name, final String password, final String imei) {
        
        
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
                .build(); //Finally building the adapter
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        api.sign_in(
                
                user_name,
                password,
                imei,
                
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
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        
                                        ldr.HideLoader();
                                        Toast.makeText(ctx, "Something went wrong...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.e("Check", "OUT result");
                                return;
                            }
                            if (result.getBody() == null || result.getBody().equals(null)) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        
                                        ldr.HideLoader();
                                        Toast.makeText(ctx, "Please try again...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.e("Check", "OUT result.getBody()");
                                return;
                            }
                            if (result.getBody().in() == null || result.getBody().in().equals(null) ) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        
                                        ldr.HideLoader();
                                        Toast.makeText(ctx, "Please try again...",
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
                                    
                                    
                                    try {
                                        
                                        JSONArray Values = json.getJSONArray("values");
                                        JSONObject c;
                                        int id = 0;
                                        
                                        for (int i = 0; i < Values.length(); i++) {
                                            c = Values.getJSONObject(i);
                                            
                                            String fname, lname, comp, cell, p1, p2, add, city, state, country, zip, email, secondrySaleType = "1", secondryDiscountType = "1";
                                            fname = c.getString("fname");
                                            lname = c.getString("lname");
                                            comp = c.getString("comp_name");
                                            cell = c.getString("cell");
                                            p1 = c.getString("phone");
                                            p2 = c.getString("phone2");
                                            add = c.getString("address");
                                            city = c.getString("city");
                                            state = c.getString("state");
                                            country = c.getString("country");
                                            zip = c.getString("zip");
                                            id = c.getInt("empid");
                                            email = c.getString("email_add");
                                            try {
                                                secondrySaleType = c.getString("secondry_salestype");
                                                secondryDiscountType = c.getString("secondry_discount_type");
                                                
                                                if (secondrySaleType.equalsIgnoreCase("null") || secondrySaleType.equalsIgnoreCase("")) {
                                                    secondrySaleType = "1";
                                                }
                                                
                                                if (secondryDiscountType.equalsIgnoreCase("null") || secondryDiscountType.equalsIgnoreCase("")) {
                                                    secondryDiscountType = "1";
                                                }
                                                
                                            } catch (JSONException e) {
                                                Log.d("In login API", "No Secondey_SalesType/ secondry_discount_type");
                                            }
                                            
                                            posDB.OpenDb();
                                            posDB.createMobUser(id, fname, lname, comp, cell, p1, p2,
                                                    add, city, state, country, zip, email, secondrySaleType, secondryDiscountType);

                                      /*      if (posDB.getAppSettingsValueByKey("en_target_notification") != 0) {
                                                int time = posDB.getAppSettingsValueByKey("en_target_notification_time");
                                                String productVal = posDB.getMobEmpProductSale();
                                                String targetVal = posDB.getMobEmpTarget();

                                                Log.e("getProgress", productVal + " : " + targetVal);
                                                int productSaleFromDB = 0;
                                                int targetFromDB = 0;
                                                if (productVal != null && !productVal.equals("null")) {
                                                    productSaleFromDB = Integer.parseInt(productVal);
                                                }
                                                if (targetVal != null && !targetVal.equals("null")) {
                                                    targetFromDB = Integer.parseInt(targetVal);
                                                }
                                                int result2=0;
                                                if(targetFromDB==0){
                                                     result2=0;
                                                }else{

                                                    result2=(100*productSaleFromDB)/targetFromDB;
                                                }
                                                if (time != 0) {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        CharSequence name = "SG Sales Progress";
                                                        String description = MessageFormat.format(" Target  Achievement {0}% | Total Target {2} | Unit Booked: {1}",
                                                                String.valueOf(result2),
                                                                posDB.getMobEmpProductSale(),
                                                                posDB.getMobEmpTarget());
                                                        int importance = NotificationManager.IMPORTANCE_HIGH;
                                                        NotificationChannel channel = new NotificationChannel("SaeedGhani", name, importance);
                                                        channel.setDescription(description);

                                                        NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
                                                        notificationManager.createNotificationChannel(channel);
                                                    }


                                                    Intent intent = new Intent(ctx, ReminderBroadcastReciever.class);
                                                    intent.putExtra("event", "SG Sales Progress");
                                                    intent.putExtra("time",  MessageFormat.format(" Target  Achievement {0}% | Total Target {2} | Unit Booked: {1}",
                                                            String.valueOf(result2),
                                                            posDB.getMobEmpProductSale(),
                                                            posDB.getMobEmpTarget()));
                                                    Calendar calendar = Calendar.getInstance();
                                                    calendar.set(Calendar.HOUR_OF_DAY, time);
                                                    calendar.set(Calendar.MINUTE, 00);
                                                    calendar.set(Calendar.SECOND, 0);
                                                    if (calendar.getTime().compareTo(new Date()) < 0)
                                                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
                                                    AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                                                    long timeintest = System.currentTimeMillis();
                                                    Log.d("TAG", "onCreateView: " + calendar.getTimeInMillis());
                                                    long tenTest = 1000 * 30;
//        alarmManager.set(AlarmManager.RTC_WAKEUP,
//                timeintest+tenTest,pendingIntent);
                                                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                                            calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                                }

                                            }*/
                                            int CountRow = posDB.CheckEMPIdClockIn(id);
                                            
                                            if (CountRow == 0) {
                                                final long ClockInID = posDB.CreateClockIn(id);
                                            }
                                            
                                            posDB.CloseDb();
                                            
                                        }
                                        
                                        
                                        final int finalId = id;
                                        


                                        //new SyncData(Login.this,  db );
                                        SyncData4(ldr, String.valueOf(finalId));
                                        
                                        
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    
                                } else if (Integer.parseInt(res) == 0) {
                                    
                                    ctx.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ldr.HideLoader();
                                            Toast.makeText(AppContextProvider.getContext(), "Invalid user", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    
                                    
                                }
                                
                            }
                            
                            
                        } catch (final IOException e) {
                            
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    e.printStackTrace();
                                    ldr.HideLoader();
                                    Toast.makeText(AppContextProvider.getContext(), "I/O Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            
                            
                        } catch (final JSONException e) {
                            
                            
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ldr.HideLoader();
                                    Log.d("Prof", "Excep JSOn " + e.toString());
                                    
                                    e.printStackTrace();
                                    Toast.makeText(AppContextProvider.getContext(), "Json Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            
                            
                        } catch (final Exception e) {
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ldr.HideLoader();
                                    Log.d("Prof", "Excep " + e.toString());
                                    
                                    e.printStackTrace();
                                    Toast.makeText(AppContextProvider.getContext(), "Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        
                        
                    }
                    
                    @Override
                    public void failure(final RetrofitError error) {
                        //If any error occured displaying the error as toast
                        
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ldr.HideLoader();
                                Log.e("MyLog", "Error " + error.getMessage());
                                Toast.makeText(AppContextProvider.getContext(), "Kindly Check Your Internet Connection", Toast.LENGTH_LONG).show();
                            }
                        });
                        
                        
                    }
                }
        );
        
        
    }
    
    private void SyncData4(final Loader ldr, final String empID) {
        
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL).setClient(new OkClient(okHttpClient)) //Setting the Root URL
                .build(); //Finally building the adapter
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        api.sync_alldata(
                
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
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        
                                        ldr.HideLoader();
                                        Toast.makeText(ctx, "Please try again...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.e("Check", "OUT result");
                                return;
                            }
                            if (result.getBody() == null || result.getBody().equals(null)) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        
                                        ldr.HideLoader();
                                        Toast.makeText(ctx, "Please try again...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.e("Check", "OUT result.getBody()");
                                return;
                            }
                            if (result.getBody().in() == null || result.getBody().in().equals(null) ) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        
                                        ldr.HideLoader();
                                        Toast.makeText(ctx, "Please try again...",
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
                            
                            //Log.d("Output Response", json.toString());
                            Log.d("Prof", "OUT");


                            if (json.getString(KEY_SUCCESS) != null) {
                                
                                String res = json.getString(KEY_SUCCESS);
                                
                                Log.d("Prof", "res Val " + res);
                                
                                if (Integer.parseInt(res) == 1) {
                                    
                                    
                                    try {


                                        JSONArray values_expense_type = json.getJSONArray("values_expense_type");
                                        SharedPreferences FormsElementDatasharedPreferences = ctx.getSharedPreferences(
                                                "FormsElementData", MODE_PRIVATE);

                                        SharedPreferences.Editor FormsElementDataeditor =
                                                FormsElementDatasharedPreferences.edit();
                                        FormsElementDataeditor.putString("values_expense_type",values_expense_type.toString());



                                        FormsElementDataeditor.apply();


                                        SharedPreferences FormsDatasharedPreferences =ctx.getSharedPreferences(
                                                "FormsData", MODE_PRIVATE);
                                        SharedPreferences.Editor FormsDatasharedPreferencesEditor =
                                                FormsDatasharedPreferences.edit();
                                        if(json.has("DailyExpenseDataValues")) {
                                            FormsDatasharedPreferencesEditor.putString("DailyExpenseDataValues",  json.getJSONArray("DailyExpenseDataValues").toString());
                                        }


                                        FormsDatasharedPreferencesEditor.apply();
                                        SharedPreferences Category_Labels_sharedPreferences = ctx.getSharedPreferences(
                                                "Category_Labels", MODE_PRIVATE);

                                        SharedPreferences.Editor Category_Labels_sharedPreferences_editor =
                                                Category_Labels_sharedPreferences.edit();
    
                                        if(json.has("category_label"))
                                        {
                                            Category_Labels_sharedPreferences_editor.putString("category_label", json.getString("category_label"));
                                        }
                                        if(json.has("subcategory_label"))
                                        {
                                            Category_Labels_sharedPreferences_editor.putString("subcategory_label", json.getString("subcategory_label"));
                                        }
                                        Category_Labels_sharedPreferences_editor.apply();
                                        JSONArray ValuesBrand = json.getJSONArray("values_brand");
                                        JSONArray ValuesType = json.getJSONArray("values_type");
                                        JSONArray ValuesInventory = json.getJSONArray("values_inventory");
                                        JSONArray ValuesCust = json.getJSONArray("values_cust");
                                        JSONArray ValuesCustRoute = json.getJSONArray("values_route");
                                        JSONArray ValuesSO = json.getJSONArray("values_so");
                                        JSONArray ValuesSR = json.getJSONArray("values_sret");
                                        JSONArray ValuesReturnType = json.getJSONArray("values_returntype");
                                        JSONArray ValuesPricing = json.getJSONArray("values_multi_pricing");
                                        JSONArray ValuesCustType = json.getJSONArray("values_customertype");
                                        JSONArray ValuesPayment = json.getJSONArray("values_payment");
                                        JSONArray ValuesExpenseType = json.getJSONArray("values_expense_type");
                                        
                                        JSONArray ValuesExpenseStatus = json.getJSONArray("values_expense_status");
                                        
                                        JSONArray ValuesNoReason = json.getJSONArray("values_no_reason");
                                        
                                        JSONArray ValuesClockIn = json.getJSONArray("values_clockin");
                                        JSONArray ValuesMerchandizing = json.getJSONArray("values_merchandizing");
                                        JSONArray ValuesMarkExpense = json.getJSONArray("values_marketing_expense");
                                        JSONArray ValuesShopVisit = json.getJSONArray("values_shop_checkin");
                                        JSONArray ValuesMerchandizingPlan = json.getJSONArray("values_merchandizing_plan");
                                        JSONArray ValuesTown = json.getJSONArray("values_town");
                                        JSONArray ValuesTownTravel = json.getJSONArray("values_town_travel");
                                        JSONArray ValuesTarget = json.getJSONArray("values_targets");
                                        JSONArray ValuesSupport = json.getJSONArray("values_support");
                                        JSONArray ValuesSupportStatus = json.getJSONArray("values_support_status");
                                        JSONArray ValuesSupportDetailConvo = json.getJSONArray("values_support_convo");
                                        JSONArray ValuesTotalDiscount = json.getJSONArray("values_amount_discount");
                                        JSONArray ValuesAppSettings = json.getJSONArray("values_app_settings");
                                        JSONArray ValuesSubShopCategory,
                                                ValuesProductSubType;
                                        
                                        String lastSynced = json.getString("update_date");
                                        String appVersion = json.getString("appversion");
                                        String company = json.getString("company_name");
                                        String productValue = json.getString("product_val");
                                        String targetValue = json.getString("target_val");
                                        String timeIn = json.getString("time_in");
                                        String enable_catalog = json.getString("en_catalogue");
                                        String catalogURL = json.getString("catalogue_url");
                                        String reportUrl = json.getString("report_url");
                                        
                                        SharedPreferences sharedPreferences =
                                                ctx.getSharedPreferences("APP_VERSION",
                                                        MODE_PRIVATE);
                                        try {
                                            SharedPreferences.Editor editor =
                                                    sharedPreferences.edit();
                                            
                                            editor.putString("app_version", appVersion);
                                            
                                            editor.apply();
                                        } catch (NullPointerException e) {
                                            e.getMessage();
                                        }
                                        
                                        if (enable_catalog.equalsIgnoreCase("1")) {
                                            new DownloadFile().execute(catalogURL, "catalog.pdf");
                                            Log.d("Download complete", "----------");
                                            
                                        }
                                        
                                        posDB.OpenDb();
                                        
                                        //db.createOffline_Tracking(20.50, 58.90, "2018-2-22 03:22:00", "Office BMS");
                                        posDB.updateMobUserEnableCatalog(empID, enable_catalog);
                                        posDB.updateMobUserLastSynced(empID, lastSynced);
                                        posDB.updateMobUserAppVersion(empID, appVersion);
                                        posDB.updateMobUserCompany(empID, company);
                                        posDB.updateMobUserProductSale(empID, productValue);
                                        posDB.updateMobUserTarget(empID, targetValue);
                                        //db.updateMobUserTimeIn(empID, timeIn);
                                        posDB.updateMobUserReportUrl(empID, reportUrl);
                                        
                                        //////// New START
                                        
                                        
                                        try {
                                            JSONArray ValuesShopCategory = json.getJSONArray(
                                                    "values_customercategory");
                                            
                                            JSONObject vSC;
                                            posDB.deleteShopCategory();
                                            sqlDB.beginTransaction();
                                            if (ValuesShopCategory != null) {
                                                for (int i = 0; i < ValuesShopCategory.length(); i++) {
                                                    
                                                    vSC = ValuesShopCategory.getJSONObject(i);
                                                    
                                                    posDB.createShopCategory(vSC.getString("iid"),
                                                            vSC.getString("iname"));
                                                    
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.getMessage();
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        
                                        try {
                                            ValuesSubShopCategory = json.getJSONArray(
                                                    "values_customersubcategory");
                                            
                                            JSONObject vSC;
                                            posDB.deleteShopSubCategory();
                                            sqlDB.beginTransaction();
                                            if (ValuesSubShopCategory != null) {
                                                for (int i = 0; i < ValuesSubShopCategory.length(); i++) {
                                                    
                                                    vSC = ValuesSubShopCategory.getJSONObject(i);
                                                    
                                                    posDB.createShopSubCategory(vSC.getString("iid"),
                                                            vSC.getString("iname"), vSC.getString(
                                                                    "parent_id"));
                                                    
                                                }
                                            }
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                        } catch (JSONException e) {
                                            e.getMessage();
                                            if (sqlDB.inTransaction()) {
                                                sqlDB.setTransactionSuccessful();
                                                sqlDB.endTransaction();
                                            }
                                        }
                                        
                                        try {
                                            ValuesProductSubType = json.getJSONArray(
                                                    "values_subtype");
                                            
                                            JSONObject vSC;
                                            posDB.deleteProductSubType();
                                            sqlDB.beginTransaction();
                                            if (ValuesProductSubType != null) {
                                                String parent_id;
                                                for (int i = 0; i < ValuesProductSubType.length(); i++) {
                                                    
                                                    vSC = ValuesProductSubType.getJSONObject(i);
                                                    
                                                    
                                                    try {
                                                        parent_id = vSC.getString("parent_id");
                                                    } catch (Exception e) {
                                                        
                                                        parent_id = "0";
                                                        
                                                    }
                                                    posDB.createProductSubType(vSC.getString("iid"),
                                                            vSC.getString("iname"), parent_id);
                                                    
                                                }
                                            }
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                        } catch (JSONException e) {
                                            if (sqlDB.inTransaction()) {
                                                sqlDB.setTransactionSuccessful();
                                                sqlDB.endTransaction();
                                            }
                                            e.getMessage();
                                        }
                                        
                                        
                                        sqlDB.beginTransaction();
                                        JSONObject expType;
                                        for (int i = 0; i < ValuesExpenseType.length(); i++) {
                                            
                                            expType = ValuesExpenseType.getJSONObject(i);
                                            
                                            posDB.insertExpenseType(expType.getString("iid"), expType.getString("iname"));
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        JSONObject expStatus;
                                        for (int i = 0; i < ValuesExpenseStatus.length(); i++) {
                                            
                                            expStatus = ValuesExpenseStatus.getJSONObject(i);
                                            
                                            posDB.insertExpenseStatus(expStatus.getString("iid"), expStatus.getString("iname"));
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        JSONObject noReason;
                                        for (int i = 0; i < ValuesNoReason.length(); i++) {
                                            
                                            noReason = ValuesNoReason.getJSONObject(i);
                                            
                                            posDB.insertNoReason(noReason.getString("iid"), noReason.getString("iname"));
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
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
                                            posDB.createClockInTime(clockIn.getString("iid"), clockin, clockout);
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        JSONObject merchandizing;
                                        for (int i = 0; i < ValuesMerchandizing.length(); i++) {
                                            
                                            merchandizing = ValuesMerchandizing.getJSONObject(i);
                                            
                                            long idMerch = posDB.createMerchandizing(merchandizing.getString("iid"), merchandizing.getString("shop_id"), merchandizing.getString("campaign_id"), merchandizing.getString("product_id"), merchandizing.getString("datetime"), merchandizing.getString("remarks"));
                                            
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        JSONObject mercPlan;
                                        for (int i = 0; i < ValuesMerchandizingPlan.length(); i++) {
                                            
                                            mercPlan = ValuesMerchandizingPlan.getJSONObject(i);
                                            
                                            
                                            long idMercPlan = posDB.createMerchandizingPlan(Integer.parseInt(mercPlan.getString("iid")), mercPlan.getString("name"), Integer.parseInt(mercPlan.getString("prd_id1")), Integer.parseInt(mercPlan.getString("prd_id2")), Integer.parseInt(mercPlan.getString("prd_id3")));
                                            
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        JSONObject mExp;
                                        for (int i = 0; i < ValuesMarkExpense.length(); i++) {
                                            
                                            mExp = ValuesMarkExpense.getJSONObject(i);
                                            String shopId, commitmentId;
                                            
                                            shopId = mExp.getString("shop_id");
                                            commitmentId = mExp.getString("commitment_id");
                                            
                                            if (shopId.equalsIgnoreCase("null")) {
                                                shopId = "0";
                                            }
                                            if (commitmentId.equalsIgnoreCase("null")) {
                                                commitmentId = "0";
                                            }
                                            long idMarkExp = posDB.createMarketingExpense(mExp.getString("iid"), shopId, commitmentId, mExp.getString("amount"), mExp.getString("mexpense_type"), mExp.getString("datetime"), mExp.getString("remarks"), mExp.getString("status"), 0);
                                            
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        
                                        JSONObject shopChkIn;
                                        for (int i = 0; i < ValuesShopVisit.length(); i++) {
                                            
                                            shopChkIn = ValuesShopVisit.getJSONObject(i);
                                            
                                            String selectedDistributorId = "0";
                                            try {
                                                selectedDistributorId = shopChkIn.getString("selected_distributor_id");
                                                if (selectedDistributorId.equalsIgnoreCase("null") || selectedDistributorId.equalsIgnoreCase("")) {
                                                    selectedDistributorId = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No selected_distributor_id in ShopCheckIn = " + e.toString());
                                            }
                                            
                                            posDB.createShopCheckIn(shopChkIn.getString("iid"), shopChkIn.getString("shop_id"), shopChkIn.getString("remarks"), shopChkIn.getString("reason_id"), shopChkIn.getString("datetime"), selectedDistributorId, 0);
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        JSONObject town;
                                        for (int i = 0; i < ValuesTown.length(); i++) {
                                            
                                            town = ValuesTown.getJSONObject(i);
                                            
                                            long idTown = posDB.createTown(Integer.parseInt(town.getString("iid")), town.getString("name"));
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        JSONObject townTravel;
                                        for (int i = 0; i < ValuesTownTravel.length(); i++) {
                                            
                                            townTravel = ValuesTownTravel.getJSONObject(i);
                                            
                                            long idTownTravel = posDB.createTownTravel(Integer.parseInt(townTravel.getString("iid")), Integer.parseInt(townTravel.getString("town_id1")), Integer.parseInt(townTravel.getString("town_id2")), townTravel.getString("one_way"), townTravel.getString("two_way"), townTravel.getString("stay_accom"));
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        JSONObject target;
                                        for (int i = 0; i < ValuesTarget.length(); i++) {
                                            
                                            target = ValuesTarget.getJSONObject(i);
                                            
                                            long idTarget = posDB.createTarget(Integer.parseInt(target.getString("prd_id")), target.getString("qty"));
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        JSONObject support;
                                        for (int i = 0; i < ValuesSupport.length(); i++) {
                                            
                                            support = ValuesSupport.getJSONObject(i);
                                            long supportId = posDB.createSupport(Integer.parseInt(support.getString("iid")), support.getString("title"), Integer.parseInt(support.getString("status")), support.getString("datetime"), 0, support.getString("iid"));
                                            
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        JSONObject supportConvo;
                                        for (int i = 0; i < ValuesSupportDetailConvo.length(); i++) {
                                            
                                            supportConvo = ValuesSupportDetailConvo.getJSONObject(i);
                                            long supportDetailId = posDB.createSupportDetail(Integer.parseInt(supportConvo.getString("iid")), Integer.parseInt(supportConvo.getString("sid")), supportConvo.getString("message"), supportConvo.getString("datetime"), Integer.parseInt(supportConvo.getString("uid")), 0);
                                            
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        JSONObject supportStatus;
                                        for (int i = 0; i < ValuesSupportStatus.length(); i++) {
                                            
                                            supportStatus = ValuesSupportStatus.getJSONObject(i);
                                            long supportStatusId = posDB.createSupportStatus(Integer.parseInt(supportStatus.getString("iid")), supportStatus.getString("iname"));
                                            
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        JSONObject totalDisc;
                                        for (int i = 0; i < ValuesTotalDiscount.length(); i++) {
                                            
                                            totalDisc = ValuesTotalDiscount.getJSONObject(i);
                                            long totalDiscId = posDB.createTotalDiscount(Integer.parseInt(totalDisc.getString("iid")), Double.parseDouble(totalDisc.getString("min_amount")), Double.parseDouble(totalDisc.getString("discount_percentage")));
                                            
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        JSONObject appSettings;
                                        for (int i = 0; i < ValuesAppSettings.length(); i++) {
                                            
                                            appSettings = ValuesAppSettings.getJSONObject(i);
                                            long appSettingsID = posDB.createAppSetting(Integer.parseInt(appSettings.getString("id")), appSettings.getString("key"), Integer.parseInt(appSettings.getString("value")));
                                            
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        
                                        try {
                                            
                                            sqlDB.beginTransaction();
                                            JSONArray ValuesItemTarget = json.getJSONArray("values_item_target");
                                            
                                            JSONObject itemTarget;
                                            for (int i = 0; i < ValuesItemTarget.length(); i++) {
                                                
                                                itemTarget = ValuesItemTarget.getJSONObject(i);
                                                long insItemTarget = posDB.createItemTarget(Integer.parseInt(itemTarget.getString("id")), itemTarget.getString("item"), itemTarget.getString("target"), itemTarget.getString("sold"), itemTarget.getString("achieved"));
                                                
                                                
                                            }
                                        } catch (JSONException e) {
                                            Log.d("No ItemTarget array", "in syncAllDataChck");
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        try {
                                            JSONArray ValuesDistributorList = json.getJSONArray("values_distributor");
                                            
                                            JSONObject distList;
                                            for (int i = 0; i < ValuesDistributorList.length(); i++) {
                                                
                                                distList = ValuesDistributorList.getJSONObject(i);
                                                long insDistributorList = posDB.createDistributorList(Integer.parseInt(distList.getString("id")), distList.getString("key"), "0", 0);
                                                
                                                
                                            }
                                        } catch (JSONException e) {
                                            Log.d("NoDistListArray", "in syncAllDataChck");
                                        }
                                        
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        //////// New END
                                        
                                        
                                        JSONObject cBr;
                                        for (int i = 0; i < ValuesBrand.length(); i++) {
                                            cBr = ValuesBrand.getJSONObject(i);
                                            
                                            posDB.InsertSyncedBrands(cBr.getString("bid"), cBr.getString("bname"));
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        if(BuildConfig.FLAVOR.equalsIgnoreCase("brands_unlimited")) {
                                            JSONArray ValuesCustomerBrand = json.getJSONArray("values_customerbrand");
                                            JSONObject v_CustomerBrand;
                                            posDB.DeleteFromCustomerBrand();
                                            sqlDB.beginTransaction();
                                            for (int i = 0; i < ValuesCustomerBrand.length(); i++) {
                                                v_CustomerBrand = ValuesCustomerBrand.getJSONObject(i);
        
                                                posDB.insertInCustomerBrand(v_CustomerBrand.getString("id"),
                                                        v_CustomerBrand.getString("brand_id"), v_CustomerBrand.getString(
                                                                "customer_id"));
                                            }
    
    
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                        }
                                        sqlDB.beginTransaction();
                                        posDB.deleteVerifyStock();
                                        posDB.   deleteVerifyStockDetail();
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        if(json.has("values_verifystock")) {
                                            sqlDB.beginTransaction();

                                            JSONArray VerifyStock = json.getJSONArray("values_verifystock");

                                            JSONObject cVerifyStock = null;

                                            if (VerifyStock != null) {
                                                for (int i = 0; i < VerifyStock.length(); i++) {

                                                    cVerifyStock = VerifyStock.getJSONObject(i);


                                                    Log.wtf("VerifyStock", VerifyStock.toString());
                                                    long idSO = posDB.createPatientOrderEntry(Integer.parseInt(cVerifyStock.getString(
                                                            "order_id")), cVerifyStock.getString("emp_id"), Integer.parseInt(cVerifyStock.getString("customer_id")), Integer.parseInt(cVerifyStock.getString("selected_distributor_id")), "", 2, cVerifyStock.getString("datetime"));


                                                }
                                            }
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                            sqlDB.beginTransaction();
                                            JSONArray ValuesVerifyStockDetails = json.getJSONArray("values_verifystock2");


                                            JSONObject cVerifyStockDetail = null;
                                            if (ValuesVerifyStockDetails != null) {
                                                for (int i = 0; i < ValuesVerifyStockDetails.length(); i++) {

                                                    cVerifyStockDetail = ValuesVerifyStockDetails.getJSONObject(i);


                                                    posDB.createPatientOrderDetailsync(Integer.parseInt(cVerifyStockDetail.getString("order_id")), cVerifyStockDetail.getString("prd_id"), cVerifyStockDetail.getString("qty"));

                                                }
                                            }

                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                        }
                                        if(json.has("sample_request_values")) {
                                            sqlDB.beginTransaction();

                                            JSONArray VerifyStock = json.getJSONArray("sample_request_values");

                                            JSONObject cVerifyStock = null;

                                            if (VerifyStock != null) {
                                                for (int i = 0; i < VerifyStock.length(); i++) {

                                                    cVerifyStock = VerifyStock.getJSONObject(i);


                                                    Log.wtf("VerifyStock", VerifyStock.toString());
                                                    long idSO = posDB.createPatientOrderEntry2(Integer.parseInt(cVerifyStock.getString(
                                                            "order_id")), cVerifyStock.getString("emp_id"), Integer.parseInt(cVerifyStock.getString("customer_id")), Integer.parseInt(cVerifyStock.getString("selected_distributor_id")), "", 2, cVerifyStock.getString("datetime"));


                                                }
                                            }
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                            sqlDB.beginTransaction();
                                            JSONArray ValuesVerifyStockDetails = json.getJSONArray("sample_request_details");


                                            JSONObject cVerifyStockDetail = null;
                                            if (ValuesVerifyStockDetails != null) {
                                                for (int i = 0; i < ValuesVerifyStockDetails.length(); i++) {

                                                    cVerifyStockDetail = ValuesVerifyStockDetails.getJSONObject(i);


                                                    posDB.createPatientOrderDetailsync2(Integer.parseInt(cVerifyStockDetail.getString("order_id")), cVerifyStockDetail.getString("prd_id"), cVerifyStockDetail.getString("qty"));

                                                }
                                            }

                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                        }
                                        sqlDB.beginTransaction();
                                        posDB.truncateCustomerPricing();
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        JSONObject iDetails;
                                        try {
                                            
                                            for (int i = 0; i < ValuesPricing.length(); i++) {
                                                
                                                iDetails = ValuesPricing.getJSONObject(i);
                                                
                                                String schemeProduct = "0",
                                                        filer1, filer2, filer3,
                                                        customer_id, customer_category_id,
                                                        customer_subcategory_id, min_amount, max_amount,
                                                        brand_id,
                                                        product_type_id,
                                                        product_sub_type_id,
                                                        min_qty,
                                                        max_qty, multi,
                                                        use_defaultprice,
                                                        mrp_price,
                                                        use_defaultmrp,
                                                        dist_id,
                                                        emp_id,
                                                        customer_type,
                                                prd_id;
                                                
                                                try {
                                                    schemeProduct = iDetails.getString("scheme_prd");
                                                    
                                                    if (schemeProduct.equalsIgnoreCase("null") || schemeProduct.equalsIgnoreCase("")) {
                                                        schemeProduct = "0";
                                                    }
                                                } catch (JSONException e) {
                                                    System.out.println("No scheme_prd in CustPricing = " + e.toString());
                                                }
                                                
                                                try {
                                                    
                                                    filer1 = iDetails.getString("tax_filer_1").equals("")? "0": iDetails.getString("tax_filer_1");
                                                    
                                                } catch (JSONException e) {
                                                    filer1 = "0";
                                                }
                                                
                                                try {
                                                    
                                                    filer2 = iDetails.getString("tax_filer_2").equals("")? "0": iDetails.getString("tax_filer_2");
                                                    
                                                } catch (JSONException e) {
                                                    filer2 = "0";
                                                }
                                                
                                                try {
                                                    
                                                    filer3 = iDetails.getString("tax_filer_3").equals("")? "0": iDetails.getString("tax_filer_3");
                                                    
                                                } catch (JSONException e) {
                                                    filer3 = "0";
                                                }
                                                
                                                
                                                try {
                                                    
                                                    customer_id = iDetails.getString("customer_id").equals("")? "0": iDetails.getString("customer_id");
                                                    
                                                } catch (JSONException e) {
                                                    customer_id = "0";
                                                }
                                                try {
                                                    
                                                    brand_id = iDetails.getString("brand_id").equals("")? "0": iDetails.getString("brand_id");
                                                    
                                                } catch (JSONException e) {
                                                    brand_id = "0";
                                                }
                                                try {
                                                    
                                                    product_type_id = iDetails.getString("category_id").equals("")? "0": iDetails.getString("category_id");
                                                    
                                                } catch (JSONException e) {
                                                    product_type_id = "0";
                                                }
                                                try {
                                                    
                                                    product_sub_type_id = iDetails.getString("subcategory_id").equals("")? "0": iDetails.getString("subcategory_id");
                                                    
                                                } catch (JSONException e) {
                                                    product_sub_type_id = "0";
                                                }
                                                try {
                                                    
                                                    min_qty = iDetails.getString("min_qty").equals("")? "0": iDetails.getString("min_qty");
                                                    
                                                } catch (JSONException e) {
                                                    min_qty = "NULL";
                                                }
                                                try {
                                                    
                                                    max_qty = iDetails.getString("max_qty").equals("")? "0": iDetails.getString("max_qty");
                                                    
                                                } catch (JSONException e) {
                                                    max_qty = "NULL";
                                                }
                                                try {
                                                    
                                                    multi = iDetails.getString("multi").equals("")? "0": iDetails.getString("multi");
                                                    
                                                } catch (JSONException e) {
                                                    multi = "0";
                                                }
                                                try {
                                                    
                                                    customer_category_id = iDetails.getString("customer_category_id").equals("")? "0": iDetails.getString("customer_category_id");
                                                    
                                                } catch (JSONException e) {
                                                    customer_category_id = "0";
                                                }
                                                
                                                try {
                                                    
                                                    customer_subcategory_id = iDetails.getString("customer_subcategory_id").equals("")? "0": iDetails.getString("customer_subcategory_id");
                                                    
                                                } catch (JSONException e) {
                                                    customer_subcategory_id = "0";
                                                }
                                                
                                                try {
                                                    
                                                    min_amount = iDetails.getString("min_amount").equals("")? "0": iDetails.getString("min_amount");
                                                    
                                                } catch (JSONException e) {
                                                    min_amount = "0";
                                                }
                                                try {
                                                    
                                                    max_amount = iDetails.getString(
                                                            "max_amount").equals("")? "0": iDetails.getString("max_amount");
                                                    
                                                } catch (JSONException e) {
                                                    max_amount = "0";
                                                }
                                                try {
                                                    
                                                    use_defaultmrp = iDetails.getString("use_defaultmrp").equals("")? "0": iDetails.getString("use_defaultmrp");
                                                    
                                                } catch (JSONException e) {
                                                    use_defaultmrp = "0";
                                                }
                                                try {
                                                    
                                                    mrp_price = iDetails.getString("mrp_price").equals("")? "0": iDetails.getString("mrp_price");
                                                    
                                                } catch (JSONException e) {
                                                    mrp_price = "0";
                                                }
                                                try {
                                                    
                                                    use_defaultprice = iDetails.getString("use_defaultprice").equals("")? "0": iDetails.getString("use_defaultprice");
                                                    
                                                } catch (JSONException e) {
                                                    use_defaultprice = "0";
                                                }
                                                
                                                
                                                try {
                                                    
                                                    dist_id = iDetails.getString("distributor_id").equals("")? "0": iDetails.getString("distributor_id");
                                                    
                                                } catch (JSONException e) {
                                                    dist_id = "0";
                                                }
                                                try {
                                                    
                                                    emp_id = iDetails.getString("booker_id").equals("")? "0": iDetails.getString("booker_id");
                                                    
                                                } catch (JSONException e) {
                                                    emp_id = "0";
                                                }
                                                try {
        
                                                    customer_type = iDetails.getString("customer_type").equals("")? "0": iDetails.getString("customer_type");
        
                                                } catch (JSONException e) {
                                                    customer_type = "0";
                                                }
                                                try {
        
                                                    prd_id = iDetails.getString("prd_id").equals("")? "0": iDetails.getString("prd_id");
        
                                                } catch (JSONException e) {
                                                    prd_id = "0";
                                                }
                                                posDB.createCustomerPricing(
        
                                                        customer_type,
                                                        prd_id,
                                                        iDetails.getString("sale_price"),
                                                        iDetails.getString("disc1_value"),
                                                        iDetails.getString("disc2_value"),
                                                        iDetails.getString("trade_offer"),
                                                        iDetails.getString("scheme"),
                                                        iDetails.getString("scheme_value"),
                                                        schemeProduct,
                                                        iDetails.getString("tax1"),
                                                        iDetails.getString("tax2"),
                                                        iDetails.getString("tax3"),
                                                        filer1,
                                                        filer2,
                                                        filer3,
                                                        customer_id,
                                                        brand_id,
                                                        product_type_id,
                                                        product_sub_type_id,
                                                        min_qty,
                                                        max_qty,
                                                        multi,
                                                        customer_category_id,
                                                        customer_subcategory_id,
                                                        min_amount,
                                                        max_amount,
                                                        use_defaultprice,
                                                        mrp_price,
                                                        use_defaultmrp,
                                                        emp_id,
                                                        dist_id);
                                                
                                            }
                                            
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                            
                                        } catch (JSONException e) {
                                            if (sqlDB.inTransaction()) {
                                                sqlDB.setTransactionSuccessful();
                                                sqlDB.endTransaction();
                                            }
                                            Log.e("CUST_PRICING",
                                                    e.getMessage());
                                        }
                                        
                                        
                                        sqlDB.beginTransaction();
                                        
                                        
                                        JSONObject cType;
                                        
                                        for (int i = 0; i < ValuesType.length(); i++) {
                                            cType = ValuesType.getJSONObject(i);
                                            
                                            posDB.InsertSyncedItemType(cType.getString("iid"), cType.getString("iname"));
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        JSONObject cItem;
                                        posDB.DeleteSyncInventory();
                                        try {
                                            for (int i = 0; i < ValuesInventory.length(); i++) {
                                                
                                                cItem = ValuesInventory.getJSONObject(i);
                                                
                                                posDB.InsertSyncedInventory(cItem.getString("inventid"),
                                                        cItem.getString("brid"),
                                                        cItem.getString("typeid"), cItem.getString("vid")
                                                        , cItem.getString("name"), cItem.getString("uc"),
                                                        cItem.getString("sp"), cItem.getString("sku"), cItem.getString("tax"), cItem.getString("packing"), cItem.getString("qty"), cItem.getString("details")/*, dataImage.get(i)*/, cItem.getString("subtypeid"),
                                                        cItem.getString("mrp_price"),
                                                        cItem.getString("tax_mrp"),
                                                        cItem.getString("is_taxable"));
                                                
                                            }
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                            
                                        } catch (JSONException e) {
                                            if (sqlDB.inTransaction()) {
                                                sqlDB.setTransactionSuccessful();
                                                sqlDB.endTransaction();
                                                
                                            }
                                        }
                                        sqlDB.beginTransaction();
                                        
                                        JSONObject custType;
                                        for (int i = 0; i < ValuesCustType.length(); i++) {
                                            
                                            custType = ValuesCustType.getJSONObject(i);
                                            posDB.insertCustomerType(custType.getString("iid"), custType.getString("iname"));
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        JSONObject rReason;
                                        for (int i = 0; i < ValuesReturnType.length(); i++) {
                                            
                                            rReason = ValuesReturnType.getJSONObject(i);
                                            
                                            posDB.InsertReturnReason(rReason.getString("iid"), rReason.getString("iname"));
                                            
                                        }
                                        
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        posDB.DeleteSyncCustomer();
                                        //db.TruncateCustomerRoutes();
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        JSONObject cCustRoute;
                                        for (int i = 0; i < ValuesCustRoute.length(); i++) {
                                            cCustRoute = ValuesCustRoute.getJSONObject(i);
                                            
                                            posDB.InsertCustomerRoute(cCustRoute.getString("rid")
                                                    , cCustRoute.getString("rname"), 0, 0, cCustRoute.getInt("day"));
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        JSONObject cCust;
                                        for (int i = 0; i < ValuesCust.length(); i++) {
                                            cCust = ValuesCust.getJSONObject(i);
                                            
                                            String lat, longi, map, rad, smsCode = "0",
                                                    numberVerified = "0", advancePayment = "0",
                                                    lastUpdate = "0", customerCeleb = "0",
                                                    filer_non_filer, customer_category, cust_sub_category;
                                            
                                            lat = cCust.getString("lat");
                                            longi = cCust.getString("long");
                                            map = cCust.getString("map");
                                            rad = cCust.getString("rad");
                                            
                                            
                                            if (lat.equalsIgnoreCase("null")) {
                                                lat = "0";
                                            }
                                            
                                            if (longi.equalsIgnoreCase("null")) {
                                                longi = "0";
                                            }
                                            
                                            if (map.equalsIgnoreCase("null")) {
                                                map = "0";
                                            }
                                            
                                            if (rad.equalsIgnoreCase("null")) {
                                                rad = "0";
                                            }
                                            
                                            
                                            try {
                                                smsCode = cCust.getString("sms_code");
                                                numberVerified = cCust.getString("number_verified");
                                                advancePayment = cCust.getString("advance_payment");
                                                
                                                if (smsCode.equalsIgnoreCase("null")) {
                                                    smsCode = "0";
                                                }
                                                
                                                if (numberVerified.equalsIgnoreCase("null")) {
                                                    numberVerified = "0";
                                                }
                                                
                                                if (advancePayment.equalsIgnoreCase("null")) {
                                                    advancePayment = "0";
                                                }
                                                
                                                
                                            } catch (JSONException e) {
                                                System.out.println("No sms_code, num_verified, advance_payment in cust = " + e.toString());
                                            }
                                            
                                            try {
                                                lastUpdate = cCust.getString("last_update");
                                                
                                                if (lastUpdate.equalsIgnoreCase("null")) {
                                                    lastUpdate = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No last_update in cust = " + e.toString());
                                            }
                                            
                                            try {
                                                customerCeleb = cCust.getString("payment_mode");
                                                
                                                if (customerCeleb.equalsIgnoreCase("null")) {
                                                    customerCeleb = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No payment_mode in cust = " + e.toString());
                                            }
                                            
                                            try {
                                                filer_non_filer = cCust.getString(
                                                        "filer_non_filer");
                                            } catch (JSONException e) {
                                                filer_non_filer = "0";
                                            }
                                            
                                            try {
                                                customer_category = cCust.getString(
                                                        "customer_category");
                                            } catch (JSONException e) {
                                                customer_category = "0";
                                            }
                                            
                                            try {
                                                cust_sub_category = cCust.getString(
                                                        "customer_sub_category");
                                            } catch (JSONException e) {
                                                cust_sub_category = "0";
                                                
                                            }
                                            
                                            posDB.InsertSyncedCustomer(cCust.getString("cid"),
                                                    cCust.getString("cid"), cCust.getString(
                                                            "fname"), cCust.getString("lname"),
                                                    cCust.getString("compname"), cCust.getString(
                                                            "cell"), cCust.getString("p1"),
                                                    cCust.getString("p2"), cCust.getString("ob"),
                                                    cCust.getString("oba"), cCust.getString(
                                                            "credit_amount"), cCust.getString(
                                                            "cnic"), cCust.getString("add"), cCust.getString("city"), cCust.getString("state"), cCust.getString("country"), cCust.getString("email"), cCust.getString("notes"), lat, longi, map, rad, 1, cCust.getInt("cust_route_id"), cCust.getInt("customer_type"), cCust.getString("app_payable"), smsCode, numberVerified, advancePayment, lastUpdate, customerCeleb, filer_non_filer, customer_category, cust_sub_category);
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        JSONObject cSO;
                                        Log.e("ValuesSO", String.valueOf(ValuesSO.length()));
                                        for (int i = 0; i < ValuesSO.length(); i++) {
                                            
                                            cSO = ValuesSO.getJSONObject(i);
                                            
                                            String saleType = "1", selectedDistributorId = "0";
                                            try {
                                                saleType = cSO.getString("payment_type");
                                                
                                                if (saleType.equalsIgnoreCase("null")) {
                                                    saleType = "1";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No payment_type in SaleOrder = " + e.toString());
                                            }
                                            
                                            
                                            try {
                                                selectedDistributorId = cSO.getString("selected_distributor_id");
                                                if (selectedDistributorId.equalsIgnoreCase("null") || selectedDistributorId.equalsIgnoreCase("")) {
                                                    selectedDistributorId = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No selected_distributor_id in SaleOrder = " + e.toString());
                                            }
                                            
                                            long idSO = posDB.createSalesOrderEntry(cSO.getString("order_id"), cSO.getString("customer_id"), cSO.getString("emp_id"), cSO.getString("values"), cSO.getString("notes"), "0", cSO.getString("datetime"), cSO.getString("date_short"), cSO.getString("total"), cSO.getString("total2"), cSO.getString("discount"), cSO.getString("amount_received"), cSO.getString("total_execute"), Double.parseDouble(cSO.getString("lati")), Double.parseDouble(cSO.getString("longi")), cSO.getString("execute_complete"), cSO.getString("exe_date"), cSO.getString("order_delete"), cSO.getString("order_id"), true, saleType, selectedDistributorId, 0,"");
                                            
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        JSONObject cSR;
                                        for (int i = 0; i < ValuesSR.length(); i++) {
                                            
                                            cSR = ValuesSR.getJSONObject(i);
                                            
                                            String exeComplete, selectedDistributorId = "0";
                                            
                                            exeComplete = cSR.getString("execute_complete");
                                            
                                            if (exeComplete.equalsIgnoreCase("null")) {
                                                exeComplete = "0";
                                            }
                                            
                                            try {
                                                selectedDistributorId = cSR.getString("selected_distributor_id");
                                                if (selectedDistributorId.equalsIgnoreCase("null") || selectedDistributorId.equalsIgnoreCase("")) {
                                                    selectedDistributorId = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No selected_distributor_id in SaleReturn = " + e.toString());
                                            }
                                            
                                            
                                            long idSR = posDB.createSalesReturnEntry(cSR.getString("sret_id"), cSR.getString("customer_id"), cSR.getString("emp_id"), cSR.getString("values"), cSR.getString("notes"), "0", cSR.getString("datetime"), 0, 0, cSR.getString("order_delete"), cSR.getString("sret_id"), true, cSR.getString("total"), cSR.getString("total2"), cSR.getString("discount"), 0, Integer.valueOf(cSR.getString("return_reason")), selectedDistributorId, Integer.parseInt(exeComplete));
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        
                                        
                                        try {
                                            
                                            sqlDB.beginTransaction();
                                            
                                            JSONArray ValuesSaleOrderDetails = json.getJSONArray("values_so_details");
                                            
                                            JSONObject cOD;
                                            if (ValuesSaleOrderDetails != null) {
                                                for (int i = 0; i < ValuesSaleOrderDetails.length(); i++) {
                                                    
                                                    cOD = ValuesSaleOrderDetails.getJSONObject(i);
                                                    
                                                    String schemeFormula = "0", schemeProduct = "0",
                                                            multi_scheme;
                                                    try {
                                                        schemeFormula = cOD.getString("scheme_formula");
                                                        
                                                        if (schemeFormula.equalsIgnoreCase("null")) {
                                                            schemeFormula = "0";
                                                        }
                                                    } catch (JSONException e) {
                                                        System.out.println("No scheme_formula in OrderDet = " + e.toString());
                                                    }
                                                    
                                                    try {
                                                        schemeProduct = cOD.getString("scheme_prd");
                                                        
                                                        if (schemeProduct.equalsIgnoreCase("null")) {
                                                            schemeProduct = "0";
                                                        }
                                                    } catch (JSONException e) {
                                                        System.out.println("No scheme_prd in OrderDet = " + e.toString());
                                                    }
                                                    try {
                                                        multi_scheme = cOD.getString("multi_scheme");
                                                        
                                                        
                                                    } catch (JSONException e) {
                                                        multi_scheme = "0";
                                                    }
                                                    
                                                    double t_o_v, d_v_1, d_v_2,
                                                            t_type, t_mrp_type, t_val;
                                                    try {
                                                        t_o_v = cOD.getDouble("t_o_v");
                                                        
                                                        
                                                    } catch (JSONException e) {
                                                        t_o_v = 0;
                                                    }
                                                    try {
                                                        d_v_1 = cOD.getDouble("d_v_1");
                                                        
                                                        
                                                    } catch (JSONException e) {
                                                        d_v_1 = 0;
                                                    }
                                                    try {
                                                        d_v_2 = cOD.getDouble("d_v_2");
                                                        
                                                        
                                                    } catch (JSONException e) {
                                                        d_v_2 = 0;
                                                    }
                                                    try {
                                                        t_type = cOD.getDouble("t_type");
                                                        
                                                        
                                                    } catch (JSONException e) {
                                                        t_type = 0;
                                                    }
                                                    try {
                                                        t_mrp_type = cOD.getDouble("t_mrp_type");
                                                        
                                                        
                                                    } catch (JSONException e) {
                                                        t_mrp_type = 0;
                                                    }
                                                    try {
                                                        t_val = cOD.getDouble("t_val");
                                                        
                                                        
                                                    } catch (JSONException e) {
                                                        t_val = 0;
                                                    }
                                                    String mrp_price;
                                                    try {
                                                        mrp_price = cOD.getString("mrp_price");
                                                        
                                                        
                                                    } catch (JSONException e) {
                                                        mrp_price = "0";
                                                    }
                                                    long idOD = posDB.createOrderDetails(cOD.getString(
                                                            "order_id"), cOD.getString("prdid"),
                                                            cOD.getString("qty"), cOD.getString(
                                                                    "qty_exe"), cOD.getString(
                                                                    "trade_price"),
                                                            cOD.getString("discount"), cOD.getString(
                                                                    "discount2"), cOD.getString(
                                                                    "trade_offer"),
                                                            cOD.getString("scheme"), cOD.getString(
                                                                    "scheme_qty"), schemeFormula,
                                                            schemeProduct, cOD.getString("tax1"),
                                                            cOD.getString("tax2"),
                                                            cOD.getString("tax3"),
                                                            
                                                            cOD.getString("sub_total"),
                                                            multi_scheme, t_o_v, d_v_1, d_v_2, t_type
                                                            , t_mrp_type, t_val, mrp_price);
                                                    
                                                    
                                                }
                                            }
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                            
                                        } catch (JSONException e) {
                                            
                                            if (sqlDB.inTransaction()) {
                                                sqlDB.setTransactionSuccessful();
                                                sqlDB.endTransaction();
                                            }
                                            e.getMessage();
                                            
                                        }
                                        
                                        
                                        sqlDB.beginTransaction();
                                        
                                        JSONArray ValuesReturnOrderDetails = json.getJSONArray("values_sret_details");
                                        
                                        JSONObject cRD;
                                        if (ValuesReturnOrderDetails != null) {
                                            for (int i = 0; i < ValuesReturnOrderDetails.length(); i++) {
                                                
                                                cRD = ValuesReturnOrderDetails.getJSONObject(i);
                                                
                                                String oId, qtyExe, schemeFormula = "0",
                                                        schemeProduct = "0",
                                                        multi_scheme;
                                                oId = cRD.getString("order_id");
                                                qtyExe = cRD.getString("qty_exe");
                                                try {
                                                    multi_scheme = cRD.getString("multi_scheme");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    multi_scheme = "0";
                                                }
                                                
                                                double t_o_v, d_v_1, d_v_2,
                                                        t_type, t_mrp_type, t_val;
                                                try {
                                                    t_o_v = cRD.getDouble("t_o_v");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    t_o_v = 0;
                                                }
                                                try {
                                                    d_v_1 = cRD.getDouble("d_v_1");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    d_v_1 = 0;
                                                }
                                                try {
                                                    d_v_2 = cRD.getDouble("d_v_2");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    d_v_2 = 0;
                                                }
                                                try {
                                                    t_type = cRD.getDouble("t_type");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    t_type = 0;
                                                }
                                                try {
                                                    t_mrp_type = cRD.getDouble("t_mrp_type");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    t_mrp_type = 0;
                                                }
                                                try {
                                                    t_val = cRD.getDouble("t_val");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    t_val = 0;
                                                }
                                                String mrp_price;
                                                try {
                                                    mrp_price = cRD.getString("mrp_price");
                                                    
                                                    
                                                } catch (JSONException e) {
                                                    mrp_price = "0";
                                                }
                                                
                                                if (oId.equalsIgnoreCase("null")) {
                                                    oId = "0";
                                                }
                                                if (qtyExe.equalsIgnoreCase("null")) {
                                                    qtyExe = "0";
                                                }
                                                
                                                try {
                                                    schemeFormula = cRD.getString("scheme_formula");
                                                    
                                                    if (schemeFormula.equalsIgnoreCase("null")) {
                                                        schemeFormula = "0";
                                                    }
                                                } catch (JSONException e) {
                                                    System.out.println("No scheme_formula in ReturnDet = " + e.toString());
                                                }
                                                
                                                try {
                                                    schemeProduct = cRD.getString("scheme_prd");
                                                    
                                                    if (schemeProduct.equalsIgnoreCase("null")) {
                                                        schemeProduct = "0";
                                                    }
                                                } catch (JSONException e) {
                                                    System.out.println("No scheme_prd in ReturnDet = " + e.toString());
                                                }
                                                
                                                long idOD = posDB.createReturnDetails(oId, cRD.getString("prdid"), cRD.getString("qty"), qtyExe, cRD.getString("trade_price"), cRD.getString("discount"), cRD.getString("discount2"), cRD.getString("trade_offer"), cRD.getString("scheme"), cRD.getString("scheme_qty"), schemeFormula, schemeProduct, cRD.getString("tax1"), cRD.getString("tax2"), cRD.getString("tax3"), cRD.getString("sub_total"),
                                                        multi_scheme, t_o_v, d_v_1, d_v_2, t_type
                                                        , t_mrp_type, t_val, mrp_price);
                                                
                                                
                                            }
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        JSONObject cPR;
                                        for (int i = 0; i < ValuesPayment.length(); i++) {
                                            
                                            cPR = ValuesPayment.getJSONObject(i);
                                            String empId, executeComplete = "0", chequeNo = "0", chequeDate = "0", bankName = "0", paymentType = "0", selectedDistributorId = "0";
                                            empId = cPR.getString("emp_id");
                                            
                                            if (empId.equalsIgnoreCase("null")) {
                                                empId = "0";
                                            }
                                            
                                            try {
                                                executeComplete = cPR.getString("execute_complete");
                                                if (executeComplete.equalsIgnoreCase("null")) {
                                                    executeComplete = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No execute_complete in PaymentReceived = " + e.toString());
                                            }
                                            
                                            try {
                                                chequeNo = cPR.getString("cheque_no");
                                                if (chequeNo.equalsIgnoreCase("null")) {
                                                    chequeNo = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No cheque_no in PaymentReceived = " + e.toString());
                                            }
                                            
                                            try {
                                                chequeDate = cPR.getString("cheque_date");
                                                if (chequeDate.equalsIgnoreCase("null")) {
                                                    chequeDate = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No cheque_date in PaymentReceived = " + e.toString());
                                            }
                                            
                                            try {
                                                bankName = cPR.getString("bank_name");
                                                if (bankName.equalsIgnoreCase("null")) {
                                                    bankName = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No bank_name in PaymentReceived = " + e.toString());
                                            }
                                            
                                            try {
                                                paymentType = cPR.getString("mode");
                                                if (paymentType.equalsIgnoreCase("null")) {
                                                    paymentType = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No payment_type/mode in PaymentReceived = " + e.toString());
                                            }
                                            
                                            
                                            try {
                                                selectedDistributorId = cPR.getString("selected_distributor_id");
                                                if (selectedDistributorId.equalsIgnoreCase("null") || selectedDistributorId.equalsIgnoreCase("")) {
                                                    selectedDistributorId = "0";
                                                }
                                            } catch (JSONException e) {
                                                System.out.println("No selected_distributor_id in PaymentReceived = " + e.toString());
                                            }
                                            
                                            long idPR = posDB.createPaymentRecieved(cPR.getString("payment_id"), cPR.getString("cid"), empId, cPR.getString("amount"), cPR.getString("datetime"), cPR.getString("details"), chequeNo, chequeDate, bankName, Integer.parseInt(executeComplete), paymentType, selectedDistributorId, 0);
                                            
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        sqlDB.beginTransaction();
                                        
                                        
                                        JSONArray ValuesSettings = json.getJSONArray("values_settings");
                                        JSONObject cSet;
                                        
                                        for (int i = 0; i < ValuesSettings.length(); i++) {
                                            cSet = ValuesSettings.getJSONObject(i);
                                            
                                            posDB.createSalesman_Settings(cSet.getString("eid"), cSet.getString("admin_email"), cSet.getString("admin_phone"), cSet.getString("tracking_en"), cSet.getString("tracking_timein"), cSet.getString("tracking_timeout"), cSet.getString("tracking_offline"), cSet.getString("tracking_duration"), cSet.getString("notify_alert"), cSet.getString("notify_vibrate"), cSet.getString("notify_sound"), cSet.getString("notify_light"), cSet.getString("notify_email"), cSet.getString("notify_phone"), cSet.getString("access_route"), cSet.getString("access_report"), cSet.getString("access_customer"), cSet.getString("access_sync"), cSet.getString("access_order"), cSet.getString("access_inventory"), cSet.getString("access_logout"), cSet.getString("min_discount"), cSet.getString("max_discount"));
                                            
                                        }
                                        sqlDB.setTransactionSuccessful();
                                        sqlDB.endTransaction();
                                        
                                        String autoSync = posDB.getSettingsAutoSync();
                                        
                                        /// Below is the Nasli Kaaam JugaaarOooO YahOOoOOOooOO :D :D :D :P
                                        
                                        
                                        String timeInLogic = posDB.getTimeInLogic();
                                        
                                        if (!posDB.getTimeInLogic().equalsIgnoreCase("")) {
                                            
                                            posDB.updateMobUserTimeIn(empID, "1");
                                            posDB.updateMobUserTimeInTIME(empID, timeInLogic);
                                            
                                            
                                        }
                                        
                                        // END OF Nasli Kaaam JugaaarOooO YahOOoOOOooOO :D :D :D :P
                                        
                                        posDB.CloseDb();
                                        
                                        if (autoSync.equals("1")) {
                                            //stopService(new Intent(Login.this, MyService.class));
                                            ctx.startService(new Intent(ctx,
                                                    MyService.class));
                                        }
                                        
                                        ctx.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ldr.HideLoader();
                                            }
                                        });
                                        
                                        
                                    } catch (JSONException e) {
                                        
                                        if (sqlDB.inTransaction()) {
                                            sqlDB.setTransactionSuccessful();
                                            sqlDB.endTransaction();
                                        }
                                        ctx.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ldr.HideLoader();
                                            }
                                        });
                                        e.printStackTrace();
                                    }
                                    
                                } else if (Integer.parseInt(res) == 0) {
                                    
                                    ctx.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ldr.HideLoader();
                                            
                                            Toast.makeText(AppContextProvider.getContext(), "Unable to create Profile. Sync again", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    
                                    
                                }
                                
                            }
                            prefManager = new PrefManager(ctx);
                            prefManager.setFirstTimeLaunch(false);

                            UserSettings userSettings=UserSettings.getInstance(ctx);
                            userSettings.set(FIRST_NOTIFY,"set");
                            userSettings.set(IS_FIRST_NOTIFY,true);

                            Intent loggedIn = new Intent(ctx, MainActivity.class);
                            ctx.finish();
                            ctx.startActivity(loggedIn);
                            Toast.makeText(AppContextProvider.getContext(), "Synchronizing Done ", Toast.LENGTH_SHORT).show();
                            
                            
                        } catch (IOException e) {
                            if (sqlDB.inTransaction()) {
                                sqlDB.setTransactionSuccessful();
                                sqlDB.endTransaction();
                            }
                            ldr.HideLoader();
                            
                            Log.d("Prof", "Excep " + e.toString());
                            Toast.makeText(AppContextProvider.getContext(), "I/O Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                            
                            e.printStackTrace();
                        } catch (JSONException e) {
                            ldr.HideLoader();
                            if (sqlDB.inTransaction()) {
                                sqlDB.setTransactionSuccessful();
                                sqlDB.endTransaction();
                            }
                            Log.d("Prof", "Excep JSOn " + e.toString());
                            Toast.makeText(AppContextProvider.getContext(), "Json Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                            
                            e.printStackTrace();
                        } catch (final Exception e) {
                            if (sqlDB.inTransaction()) {
                                sqlDB.setTransactionSuccessful();
                                sqlDB.endTransaction();
                            }
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ldr.HideLoader();
                                    Log.d("Prof", "Excep " + e.toString());
                                    
                                    e.printStackTrace();
                                    Toast.makeText(AppContextProvider.getContext(), "Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        
                        
                    }
                    
                    @Override
                    public void failure(final RetrofitError error) {
                        //If any error occured displaying the error as toast
                        
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("Login Error:", "run: "+error.getMessage()+"\nrun: "+error.getResponse()
                                        +"\nrun: "+error.getKind()+"\nrun: "+error.getUrl()+"\nrun: "+error.getBody());
                                ldr.HideLoader();
                                
                                Toast.makeText(AppContextProvider.getContext(), "Kindly Check Your Internet Connection", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
        );
        
        
    }
    
    private String checkUserInactive(final String empID) {
        
        
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
                .build(); //Finally building the adapter
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        try {
            api.chk_inactive(
                    
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
                                    
                                    sync_message = "Please try again...";
                                    Log.e("Check", "OUT result");
                                    
                                    return;
                                }
                                if (result.getBody() == null || result.getBody().equals(null)) {
                                    
                                    sync_message = "Please try again...";
                                    Log.e("Check", "OUT result.getBody()");
                                    
                                    return;
                                }
                                if (result.getBody().in() == null || result.getBody().in().equals(null) ) {
                                    
                                    sync_message = "Please try again...";
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
                                        
                                        JSONArray Values = json.getJSONArray("values");
                                        JSONObject c;
                                        
                                        String status = "";
                                        for (int i = 0; i < Values.length(); i++) {
                                            c = Values.getJSONObject(i);
                                            
                                            status = c.getString("status");
                                            
                                        }
                                        
                                        if (status.equalsIgnoreCase("1")) {
                                            
                                            LogoutFunction();
                                            
                                        }
                                        
                                    } else if (Integer.parseInt(res) == 0) {
                                        sync_message = "User not found.";
                                    }
                                }
                                
                            } catch (IOException e) {
                                
                                Log.d("Prof", "Excep " + e.toString());
                                /*  Toast.makeText(AppContextProvider.getContext(), "I/O Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();*/
                                sync_message = "I/O Exception\n" + e.toString();
                                
                                e.printStackTrace();
                            } catch (JSONException e) {
                                
                                
                                Log.d("Prof", "Excep JSOn " + e.toString());
                                /*Toast.makeText(AppContextProvider.getContext(), "Json Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();*/
                                sync_message = "Json Exception\n" + e.toString();
                                e.printStackTrace();
                            } catch (Exception e) {
                                Log.d("Prof", "Excep " + e.toString());
                                /*Toast.makeText(AppContextProvider.getContext(), "Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();*/
                                sync_message = " Exception\n" + e.toString();
                                e.printStackTrace();
                            }
                            
                        }
                        
                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("Exception", "Error " + error.getMessage());
                            sync_message = "Error " + error.getMessage();
                            
                            /*Toast.makeText(AppContextProvider.getContext(), "Kindly Check Your Internet Connection", Toast.LENGTH_LONG).show();*/
                        }
                    }
            );
        } catch (Exception e) {
            
            Log.e("Exception", "Error " + e.getMessage());
            sync_message = "Error " + e.getMessage();
            
            
        }
        
        
        return sync_message;
        
    }
    
    private String GetSalesmanSettings(final String empID) {
        
        
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
                .build(); //Finally building the adapter
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        api.salesman_settings(
                
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
                                
                                gss_msg = "Please try again...";
                                
                                Log.e("Check", "OUT result");
                                
                                return;
                            }
                            if (result.getBody() == null || result.getBody().equals(null)) {
                                
                                gss_msg = "Please try again...";
                                Log.e("Check", "OUT result.getBody()");
                                
                                return;
                            }
                            if (result.getBody().in() == null || result.getBody().in().equals(null) ) {
                                
                                gss_msg = "Please try again...";
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
                                    
                                    JSONArray Values = json.getJSONArray("values");
                                    JSONObject c;
                                    
                                    //posDB.OpenDb();
                                    
                                    
                                    sqlDB.beginTransaction();
                                    for (int i = 0; i < Values.length(); i++) {
                                        c = Values.getJSONObject(i);
                                        
                                        posDB.createSalesman_Settings(c.getString("eid"), c.getString("admin_email"), c.getString("admin_phone"), c.getString("tracking_en"), c.getString("tracking_timein"), c.getString("tracking_timeout"), c.getString("tracking_offline"), c.getString("tracking_duration"), c.getString("notify_alert"), c.getString("notify_vibrate"), c.getString("notify_sound"), c.getString("notify_light"), c.getString("notify_email"), c.getString("notify_phone"), c.getString("access_route"), c.getString("access_report"), c.getString("access_customer"), c.getString("access_sync"), c.getString("access_order"), c.getString("access_inventory"), c.getString("access_logout"), c.getString("min_discount"), c.getString("max_discount"));
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    JSONArray ValuesPro = json.getJSONArray("values_profile");
                                    JSONObject cPro;
                                    
                                    for (int i = 0; i < ValuesPro.length(); i++) {
                                        cPro = ValuesPro.getJSONObject(i);
                                        String secondrySaleType = "1", secondryDiscountType = "1";
                                        
                                        try {
                                            secondrySaleType = cPro.getString("secondry_salestype");
                                            secondryDiscountType = cPro.getString("secondry_discount_type");
                                            
                                            if (secondrySaleType.equalsIgnoreCase("null") || secondrySaleType.equalsIgnoreCase("")) {
                                                secondrySaleType = "1";
                                            }
                                            
                                            if (secondryDiscountType.equalsIgnoreCase("null") || secondryDiscountType.equalsIgnoreCase("")) {
                                                secondryDiscountType = "1";
                                            }
                                            
                                        } catch (JSONException e) {
                                            Log.d("In UpdateSyncEvery API", "No Secondey_SalesType/ secondry_discount_type");
                                        }
                                        
                                        
                                        posDB.updateMobUserDetail(cPro.getString("id"), cPro.getString("fname"), cPro.getString("lname"),/* cPro.getString("company"),*/ cPro.getString("email"), cPro.getString("add"), cPro.getString("city"), cPro.getString("state"), cPro.getString("zip"), cPro.getString("country"), cPro.getString("cell"), cPro.getString("p1"), cPro.getString("p2"), secondrySaleType, secondryDiscountType);
                                    }
                                    
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    
                                    
                                    posDB.CloseDb();
                                   /*
                                    ctx.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            
                                            Toast.makeText(ctx, "Data Synced", Toast.LENGTH_SHORT).show();
                                        }
                                    });*/
                                    gss_msg = "Data Synced";
                                    ctx.stopService(new Intent(ctx, MyService.class));
                                    ctx.startService(new Intent(ctx, MyService.class));
                                    
                                    
                                    //
                                  /*  ctx.finish();
                                    ctx.startActivity(new Intent(ctx, MainActivity.class).putExtra("Sync", "1"));*/
                                    
                                    
                                } else if (Integer.parseInt(res) == 0) {
                                /*    ctx.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            
                                            Toast.makeText(AppContextProvider.getContext(), "Settings not saved.\nUser not found.", Toast.LENGTH_SHORT).show();
                                        }
                                    });*/
                                    gss_msg = "Settings not saved.\nUser not found.";
                                }
                                
                            }
                            
                            
                        } catch (final IOException e) {
                            
                            if (sqlDB.inTransaction()) {
                                sqlDB.setTransactionSuccessful();
                                sqlDB.endTransaction();
                            }
                            
                            
                            gss_msg = "I/O Exception\n" + e.toString();
                        } catch (final JSONException e) {
                            if (sqlDB.inTransaction()) {
                                sqlDB.setTransactionSuccessful();
                                sqlDB.endTransaction();
                            }
                            
                            
                            gss_msg = "Json Exception\n" + e.toString();
                        } catch (final Exception e) {
                            if (sqlDB.inTransaction()) {
                                sqlDB.setTransactionSuccessful();
                                sqlDB.endTransaction();
                            }
                            
                            
                            Log.d("Prof", "Excep " + e.toString());
                            
                            e.printStackTrace();
                            gss_msg = "Exception " + e.getMessage();
                            
                        }
                        
                        
                    }
                    
                    @Override
                    public void failure(RetrofitError error) {
                        
                        
                        gss_msg = "Kindly Check Your Internet Connection";
                        
                    }
                }
        );
        return gss_msg;
        
        
    }
    
    private void GetSalesmanSettings(final Loader ldr1, final String empID) {
        
        
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
                .build(); //Finally building the adapter
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        api.salesman_settings(
                
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
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        
                                        ldr1.HideLoader();
                                        Toast.makeText(ctx, "Please try again...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.e("Check", "OUT result");
                                return;
                            }
                            if (result.getBody() == null || result.getBody().equals(null)) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        
                                        ldr1.HideLoader();
                                        Toast.makeText(ctx, "Please try again...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.e("Check", "OUT result.getBody()");
                                return;
                            }
                            if (result.getBody().in() == null || result.getBody().in().equals(null) ) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        
                                        ldr1.HideLoader();
                                        Toast.makeText(ctx, "Please try again...",
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
                                    
                                    JSONArray Values = json.getJSONArray("values");
                                    JSONObject c;
                                    
                                    posDB.OpenDb();
                                    
                                    
                                    sqlDB.beginTransaction();
                                    for (int i = 0; i < Values.length(); i++) {
                                        c = Values.getJSONObject(i);
                                        
                                        posDB.createSalesman_Settings(c.getString("eid"), c.getString("admin_email"), c.getString("admin_phone"), c.getString("tracking_en"), c.getString("tracking_timein"), c.getString("tracking_timeout"), c.getString("tracking_offline"), c.getString("tracking_duration"), c.getString("notify_alert"), c.getString("notify_vibrate"), c.getString("notify_sound"), c.getString("notify_light"), c.getString("notify_email"), c.getString("notify_phone"), c.getString("access_route"), c.getString("access_report"), c.getString("access_customer"), c.getString("access_sync"), c.getString("access_order"), c.getString("access_inventory"), c.getString("access_logout"), c.getString("min_discount"), c.getString("max_discount"));
                                        
                                    }
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    sqlDB.beginTransaction();
                                    
                                    JSONArray ValuesPro = json.getJSONArray("values_profile");
                                    JSONObject cPro;
                                    
                                    for (int i = 0; i < ValuesPro.length(); i++) {
                                        cPro = ValuesPro.getJSONObject(i);
                                        String secondrySaleType = "1", secondryDiscountType = "1";
                                        
                                        try {
                                            secondrySaleType = cPro.getString("secondry_salestype");
                                            secondryDiscountType = cPro.getString("secondry_discount_type");
                                            
                                            if (secondrySaleType.equalsIgnoreCase("null") || secondrySaleType.equalsIgnoreCase("")) {
                                                secondrySaleType = "1";
                                            }
                                            
                                            if (secondryDiscountType.equalsIgnoreCase("null") || secondryDiscountType.equalsIgnoreCase("")) {
                                                secondryDiscountType = "1";
                                            }
                                            
                                        } catch (JSONException e) {
                                            Log.d("In UpdateSyncEvery API", "No Secondey_SalesType/ secondry_discount_type");
                                        }
                                        
                                        
                                        posDB.updateMobUserDetail(cPro.getString("id"), cPro.getString("fname"), cPro.getString("lname"),/* cPro.getString("company"),*/ cPro.getString("email"), cPro.getString("add"), cPro.getString("city"), cPro.getString("state"), cPro.getString("zip"), cPro.getString("country"), cPro.getString("cell"), cPro.getString("p1"), cPro.getString("p2"), secondrySaleType, secondryDiscountType);
                                    }
                                    
                                    sqlDB.setTransactionSuccessful();
                                    sqlDB.endTransaction();
                                    
                                    
                                    posDB.CloseDb();
                                    
                                    ctx.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ldr1.HideLoader();
                                            Toast.makeText(ctx, "Data Synced", Toast.LENGTH_SHORT).show();
                                            ctx.finish();
                                            ctx.startActivity(new Intent(ctx, MainActivity.class));
                                        }
                                    });
                                    ctx.stopService(new Intent(ctx, MyService.class));
                                    ctx.startService(new Intent(ctx, MyService.class));
                                    
                                    
                                } else if (Integer.parseInt(res) == 0) {
                                    ctx.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ldr1.HideLoader();
                                            Toast.makeText(AppContextProvider.getContext(), "Settings not saved.\nUser not found.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    
                                }
                                
                            }
                            
                            
                        } catch (final IOException e) {
                            
                            if (sqlDB.inTransaction()) {
                                sqlDB.setTransactionSuccessful();
                                sqlDB.endTransaction();
                            }
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ldr1.HideLoader();
                                    Log.d("Prof", "Excep " + e.toString());
                                    Toast.makeText(AppContextProvider.getContext(), "I/O Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                    
                                    e.printStackTrace();
                                }
                            });
                        } catch (final JSONException e) {
                            
                            if (sqlDB.inTransaction()) {
                                sqlDB.setTransactionSuccessful();
                                sqlDB.endTransaction();
                            }
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ldr1.HideLoader();
                                    Log.d("Prof", "Excep JSOn " + e.toString());
                                    Toast.makeText(AppContextProvider.getContext(), "Json Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                    
                                    e.printStackTrace();
                                }
                            });
                        } catch (final Exception e) {
                            if (sqlDB.inTransaction()) {
                                sqlDB.setTransactionSuccessful();
                                sqlDB.endTransaction();
                            }
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ldr1.HideLoader();
                                    Log.d("Prof", "Excep " + e.toString());
                                    
                                    e.printStackTrace();
                                    Toast.makeText(AppContextProvider.getContext(), "Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    
                    @Override
                    public void failure(RetrofitError error) {
                        
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ldr1.HideLoader();
                                Toast.makeText(AppContextProvider.getContext(), "Kindly Check Your Internet Connection", Toast.LENGTH_LONG).show();
                            }
                        });
                        
                    }
                }
        );
        
    }
    
    private void LogoutFunction() {
        
        posDB.OpenDb();
        //posDB.mUserLogout();
        posDB.DeleteAllRecords();
        String res = posDB.getSettingsData();
        posDB.CloseDb();
        
        Log.d("Setings DB Data LOGOUT", res);
        
        ctx.stopService(new Intent(ctx, MyService.class));
        
        Intent i;
        i = new Intent(ctx, Login.class);
        ctx.startActivity(i);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.finish();
        
        ctx.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(AppContextProvider.getContext(), "Your Account has been Deactivated", Toast.LENGTH_LONG).show();
            }
        });
        
        
    }
    
    public boolean networkAvailable() {
        
        
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
    
    public void CustomDialogNoInternet() {
        /*
         * Custom Dialog box starts
         */
        
        AlertDialog alert = new AlertDialog.Builder(ctx).create();
        alert.setTitle("Sorry");
        alert.setMessage("Please Check Your Internet Connection");
        alert.setIcon(R.drawable.ic_drawer);
        alert.show();
        /*
         * Custom dialog box ends
         */
        
    }
    
    private String GetValues(String... vals) {
        
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < vals.length; i++) {
            
            result.append(vals[i]).append("@@");
            
            if (i == vals.length - 1) {
                result.append(vals[i]).append("##");
            }
            
        }
        return result.toString();
        
    }
    
    private String GetValues2(String... vals) {
        
        StringBuilder result = new StringBuilder();
        Log.e("OrderValuesSend3", String.valueOf(vals.length));
        for (String val : vals) {
            
            result.append(val).append("@@");

            /*if( i == vals.length - 1 ){
				result = result + vals[i] + "##";
            }*/
            
        }
        return result.toString();
        
    }
    
    public static class DownloadFile extends AsyncTask<String, Void, Void> {
        
        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "testthreepdf");
            folder.mkdir();
            
            File pdfFile = new File(folder, fileName);
            
            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }
}
