package com.blazeminds.pos.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.MainActivity;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.adapter.CashMemoMainRecyclerviewAdapter;
import com.blazeminds.pos.fragments.SaleOrderFinal;
import com.blazeminds.pos.model.MainCartModel;
import com.blazeminds.pos.model.ProductModel;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.blazeminds.pos.Constant.round;
import static com.blazeminds.pos.MainActivity.FRAGMENT_TAG;
import static com.blazeminds.pos.MainActivity.trackCount;

public class CashSummaryActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    // My GPS states
    public static final int GPS_PROVIDER_DISABLED = 1;
    public static final int GPS_GETTING_COORDINATES = 2;
    public static final int GPS_GOT_COORDINATES = 3;
    //public static final int GPS_PROVIDER_UNAVIALABLE = 4;
    //public static final int GPS_PROVIDER_OUT_OF_SERVICE = 5;
    public static final int GPS_PAUSE_SCANNING = 6;
    public static final int GPS_PROVIDER_DOESNOT_EXIST = 4;
    private static double Latitude, Longitude;
    private final ArrayList<JsonBrandModel> cashMemoList = new ArrayList<>();
    UserSettings userSettings;
    PosDB db;
    TextView locStatusTxt;
    CashMemoMainRecyclerviewAdapter SummayAdapter;
    int totalAMOUNT, creditMinusOldRec;
    double TOTALAMOUNT, CREDITMINUSOLDRECEIVABLE, creditLimit, oldReceivable;
    ArrayList<MainCartModel> arrayList = new ArrayList<>();
    private String coordsToSend;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient mGoogleApiClient;
    private TextView tv_cash_summary_total_cost,tv_cash_summary_cust_name,tv_cash_summary_delivery_date;
    private ImageView iv_cash_summary_cart;
    private EditText NotesTxt;
    // Location manager
    private LocationManager manager;
    private boolean isOne = true;
    private final boolean formatError = true;
    private RecyclerView rv_main_summary;
    private String gAccuracy;
    // Location events (we use GPS only)
    private final LocationListener locListener = new LocationListener() {

        public void onLocationChanged(Location argLocation) {
            printLocation(argLocation, GPS_GOT_COORDINATES);
        }

        @Override
        public void onProviderDisabled(String arg0) {
            if (isOne) {
                final String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                final AlertDialog.Builder builder = new AlertDialog.Builder(CashSummaryActivity.this);

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
    private String SelectedCustomerId = "", SelectedTypeCustomerId = "";

    private static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        DateFormat df = DateFormat.getDateTimeInstance();

        //SelectedDate = dateFormat.format(new Date());

        return dateFormat.format(new Date());
        //return df.format(new Date());
    }

    private static String getDateTimeSHORT() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        DateFormat df = DateFormat.getDateTimeInstance();

        //SelectedDate = dateFormat.format(new Date());

        return dateFormat.format(new Date());
        //return df.format(new Date());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_summary);
        InitUi();
        iv_cash_summary_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AreYouSure();
            }
        });
        //
//        userSettings.set("custId",CustId);
//        userSettings.set("custTypeId",CustTypeId);

        //if (SelectedCustomerTypeId.equalsIgnoreCase("1")) {
        //                    invoiceDiscount = db.getDiscountForSaleOrder(amountTxt);
        //                    invoiceDiscount = invoiceDiscount + Double.parseDouble(selectedBulkTradeOffer);
        //                    if (invoiceDiscount > 0) {
        //                        afterDivideDiscount = invoiceDiscount / 100;
        //                        afterSubtractDiscount = 1 - afterDivideDiscount;
        //                        amountTxt = amountTxt * afterSubtractDiscount;
        //                    }
        //                }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isOne = true;
    }

    private void newLogicMethodForSaleOrder() {

        double PA = 0, BALANCE = 0, PENDING = 0, AMOUNT_REC = 0, TOTAL2 = 0;
        String id, balance, total2;
        ArrayList<HashMap<String, String>> salesDetailsFromDB = new ArrayList<>();
        HashMap<String, String> map;
        //String pa = TotalAmountString;
        db.OpenDb();
        String advancePayment = db.getSelectedCustomerAdvancePayment(SelectedCustomerId);
        if (!advancePayment.equalsIgnoreCase("") && advancePayment != null)
            PA = /*Double.parseDouble(pa) +*/ Double.parseDouble(advancePayment);

        if (PA > 0) {

            String ob = db.getSelectedCustomerOpeningBalanceForSaleReturn(SelectedCustomerId);
            if (!ob.equalsIgnoreCase("") && ob != null)
                BALANCE = Double.parseDouble(ob);
            PENDING = BALANCE - PA;

            if (PENDING <= 0) {
                PENDING = 0;
            }
            PA = PA - BALANCE;
            if (PA <= 0) {
                PA = 0;
            }

            db.updateOpeningBalanceCustomer(SelectedCustomerId, String.valueOf(PENDING));

            salesDetailsFromDB = db.getSalesDetailsForSaleReturn(SelectedCustomerId);
            if (salesDetailsFromDB.size() > 0) {

                for (int i = 0; i < salesDetailsFromDB.size(); i++) {

                    map = salesDetailsFromDB.get(i);
                    id = map.get("id");
                    balance = map.get("pending_amount");
                    total2 = map.get("total2");
                    if (!balance.equalsIgnoreCase("") && balance != null)
                        BALANCE = Double.parseDouble(balance);
                    if (!total2.equalsIgnoreCase("") && total2 != null)
                        TOTAL2 = Double.parseDouble(total2);

                    if (BALANCE != 0) {
                        PENDING = BALANCE - PA;
                        if (PENDING <= 0) {
                            PENDING = 0;
                        }
                        PA = PA - BALANCE;
                        if (PA <= 0) {
                            PA = 0;
                        }
                        AMOUNT_REC = TOTAL2 - PENDING;
                        db.updateAmountRecievedSaleOrder(id, String.valueOf(AMOUNT_REC));
                    }
                }
            }

            if (PA >= 0) {
                db.updateAdvancePaymentCustomer(SelectedCustomerId, String.valueOf(PA));
            }
        }

        db.CloseDb();

    }

    private boolean creditLimitFunction() {

        boolean check;
        String creditAmount = db.getSelectedCustomerCreditAmountLimit(SelectedCustomerId);

        if (creditAmount != null && !creditAmount.equals("") && !creditAmount.equals("-1")) {
            creditLimit = Double.parseDouble(creditAmount);
            totalAMOUNT = Integer.parseInt(tv_cash_summary_total_cost.getText().toString());
            CREDITMINUSOLDRECEIVABLE = Math.abs(creditLimit - oldReceivable);
            creditMinusOldRec = (int) CREDITMINUSOLDRECEIVABLE;
            check = totalAMOUNT > CREDITMINUSOLDRECEIVABLE;
        } else {
            check = false;
        }
        return check;

    }

    private void InitUi() {
        tv_cash_summary_total_cost = findViewById(R.id.tv_cash_summary_total_cost);
        tv_cash_summary_cust_name = findViewById(R.id.tv_cash_summary_cust_name);
        tv_cash_summary_delivery_date = findViewById(R.id.tv_cash_summary_delivery_date);
        NotesTxt = findViewById(R.id.NotesTxt);
        locStatusTxt = findViewById(R.id.locStatusTxt);
        rv_main_summary = findViewById(R.id.rv_main_summary);
        iv_cash_summary_cart = findViewById(R.id.iv_cash_summary_cart);

        userSettings = UserSettings.getInstance(CashSummaryActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
        db = PosDB.getInstance(CashSummaryActivity.this);
        String jsonStr = userSettings.getString(Constant.CART_LIST);
        if (jsonStr != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray jsonArray = jsonObject.getJSONArray("CartObj");
                for (int i = 0; i < jsonArray.length(); i++) {
                    // TradePrice,isTaxMrp,is_taxable,tax_mrp,mrp_price,tradeOffer,schemeValue,schemeTxt,tax1,tax2,tax3,taxf1,taxf2,taxf3

                    JSONObject innerObject = jsonArray.getJSONObject(i);
                    JsonBrandModel jsonBrandModel = new JsonBrandModel();
                    jsonBrandModel.setB_id(innerObject.getString("b_id"));
                    jsonBrandModel.setP_id(innerObject.getString("p_id"));
                    jsonBrandModel.setP_name(innerObject.getString("p_name"));
                    jsonBrandModel.setP_price(innerObject.getString("p_price"));
                    jsonBrandModel.setP_qty(innerObject.getString("p_qty"));
                    jsonBrandModel.setP_discount(innerObject.getString("p_discount"));
                    jsonBrandModel.setTradePrice(innerObject.getString("TradePrice"));
                    jsonBrandModel.setIsTaxMrp(innerObject.getString("isTaxMrp"));
                    jsonBrandModel.setIs_taxable(innerObject.getString("is_taxable"));
                    jsonBrandModel.setTax_mrp(innerObject.getString("tax_mrp"));
                    jsonBrandModel.setMrp_price(innerObject.getString("mrp_price"));
                    jsonBrandModel.setTradeOffer(innerObject.getString("tradeOffer"));
                    jsonBrandModel.setSchemeValue(innerObject.getString("schemeValue"));
                    jsonBrandModel.setSchemeTxt(innerObject.getString("schemeTxt"));
                    jsonBrandModel.setTax1(innerObject.getString("tax1"));
                    jsonBrandModel.setTax2(innerObject.getString("tax2"));
                    jsonBrandModel.setTax3(innerObject.getString("tax3"));
                    jsonBrandModel.setTaxf1(innerObject.getString("taxf1"));
                    jsonBrandModel.setTaxf2(innerObject.getString("taxf2"));
                    jsonBrandModel.setTaxf3(innerObject.getString("taxf3"));
                    cashMemoList.add(jsonBrandModel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d("TAG", "InitUi: " + cashMemoList.toString());
        for (int i = 0; i < cashMemoList.size(); i++) {
            ArrayList<ProductModel> productArrayList = new ArrayList<>();
            MainCartModel mainCartModel = new MainCartModel();
            mainCartModel.setB_id(cashMemoList.get(i).getB_id());
//            productArrayList.clear();
            mainCartModel.setB_name(db.getBrandsName(cashMemoList.get(i).getB_id()));
            for (int j = 0; j < cashMemoList.size(); j++) {
                if (cashMemoList.get(i).getB_id().equals(cashMemoList.get(j).getB_id())) {
                    ProductModel model = new ProductModel();
                    model.setProd_qty(Integer.parseInt(cashMemoList.get(j).getP_qty()));
                    model.setProd_id(cashMemoList.get(j).getP_id());
                    model.setProd_name(cashMemoList.get(j).getP_name());
                    model.setProd_price(cashMemoList.get(j).getP_price());
                    model.setDiscount(cashMemoList.get(j).getP_discount());
                    model.setTradePrice(cashMemoList.get(j).getTradePrice());
                    model.setIsTaxMrp  (cashMemoList.get(j).getIsTaxMrp());
                    model.setIs_taxable(cashMemoList.get(j).getIs_taxable());
                    model.setTax_mrp   (cashMemoList.get(j).getTax_mrp());
                    model.setMrp_price (cashMemoList.get(j).getMrp_price());
                    model.setTradeOffer (cashMemoList.get(j).getTradeOffer());
                    model.setSchemeValue(cashMemoList.get(j).getSchemeValue());
                    model.setSchemeTxt(cashMemoList.get(j).getSchemeTxt());
                    model.setTax1(cashMemoList.get(j).getTax1());
                    model.setTax2(cashMemoList.get(j).getTax2());
                    model.setTax3(cashMemoList.get(j).getTax3());
                    model.setTaxf1(cashMemoList.get(j).getTaxf1());
                    model.setTaxf2(cashMemoList.get(j).getTaxf2());
                    model.setTaxf3(cashMemoList.get(j).getTaxf3());
                    productArrayList.add(model);
                }
            }
//            ProductModel model = new ProductModel();
//            model.setProd_qty(Integer.parseInt(cashMemoList.get(i).getP_qty()));
//            model.setProd_id(cashMemoList.get(i).getP_id());
//            model.setProd_name(cashMemoList.get(i).getP_name());
//            model.setProd_price(cashMemoList.get(i).getP_price());
//            model.setDiscount(cashMemoList.get(i).getP_discount());
//            productArrayList.add(model);
            mainCartModel.setProductList(productArrayList);
            arrayList.add(mainCartModel);
        }
//        Collection<MainCartModel> list= Arrays.asList(arrayList);
        for (int i = 0; i < arrayList.size(); i++) {
            for (int j = 0; j < arrayList.size(); j++) {
                if (arrayList.get(i).getB_id().equals(arrayList.get(j).getB_id())) {
                    if (i != j) {
                        arrayList.remove(j);
                    }
                }
            }
//            ProductModel model = new ProductModel();
//            model.setProd_qty(Integer.parseInt(cashMemoList.get(i).getP_qty()));
//            model.setProd_id(cashMemoList.get(i).getP_id());
//            model.setProd_name(cashMemoList.get(i).getP_name());
//            model.setProd_price(cashMemoList.get(i).getP_price());
//            model.setDiscount(cashMemoList.get(i).getP_discount());
//            productArrayList.add(model);

        }

        SelectedCustomerId = userSettings.getString("custId");
        SelectedTypeCustomerId = userSettings.getString("custTypeId");

        tv_cash_summary_cust_name.setText(db.getCustomerName(SelectedCustomerId));
        tv_cash_summary_total_cost.setText(userSettings.getDouble(Constant.CART_LIST_PRICE) != 0.0 ? "Rs." + userSettings.getDouble(Constant.CART_LIST_PRICE) : "Rs.0");
        SummayAdapter = new CashMemoMainRecyclerviewAdapter(arrayList, CashSummaryActivity.this,db,SelectedCustomerId,SelectedTypeCustomerId);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CashSummaryActivity.this);
        rv_main_summary.setLayoutManager(layoutManager);
//        rv_child.addItemDecoration(new DividerItemDecoration(rv_child.getContext(), DividerItemDecoration.VERTICAL));
        rv_main_summary.setAdapter(SummayAdapter);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startLocation();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(CashSummaryActivity.this);


    }

    public void AreYouSure() {

        final Dialog dialog = new Dialog(CashSummaryActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_are_you_sure);
        //dialog.getWindow().setBac(getResources().getColor(R.color.login_bg));
        // dialog.getWindow().


        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview

        //  dialog.setTitle(ItemName);
        final Button Yes = dialog.findViewById(R.id.YesBtn);
        Button No = dialog.findViewById(R.id.NoBtn);

        dialog.show();

        Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Yes.setClickable(false);

//                schemeHolder = new ArrayList<>(showSchemeListAdapter != null ? showSchemeListAdapter.getHoldList() : (new ArrayList<HashMap<String, String>>()));

                Log.d("Location_Sent", "Lati: " + Latitude + "\nLongi: " + Longitude);

                db.OpenDb();
                int orderId = db.getMaxOrderIdFromSaleOrder();
                String savedDistributorId = db.getSavedDistributorList();
//                int total = round(Double.parseDouble(tv_cash_summary_total_cost.getText().toString().replace("Rs.","")));
//                invoiceDiscountFinal       =String.valueOf(Double.parseDouble( invoiceDiscountFinal)+Double.parseDouble(selectedBulkTradeOffer));
                db.createSalesOrderEntry(String.valueOf(orderId + 1), SelectedCustomerId,
                        db.getMobEmpId(), GetValues(), Constant.testInput(NotesTxt.getText().toString()),
                        userSettings.getString("start_time"), getDateTime(), getDateTimeSHORT(), tv_cash_summary_total_cost.getText().toString().replace("Rs.",""),
                        tv_cash_summary_total_cost.getText().toString().replace("Rs.",""), "0", null, null, Latitude, Longitude,
                        null, null, null, null, false, "1",
                        savedDistributorId, 1, "0");
                //  db.updateCustomerLastUpdate(SelectedCustomerId, getDateTimeSHORT());
                db.CloseDb();
                insertOrderDetails();
                newLogicMethodForSaleOrder();

                dialog.dismiss();

                trackCount = 0;
                FRAGMENT_TAG = SaleOrderFinal.TAG;

                /*getActivity().finish();
                getActivity().startActivity(new Intent(getActivity(), MainActivity.class));*/

                new MainActivity().updateSync(CashSummaryActivity.this);
                startActivity(new Intent(CashSummaryActivity.this,MainActivity.class));
                finish();

            }
        });

        No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                if (schemeHolder.size() > 0)
//                    schemeDialog.show();
            }
        });


    }

    private void insertOrderDetails() {

        ArrayList<HashMap<String, String>> list;
        HashMap<String, String> map = new HashMap<>();
        double res = 0.0;
        String tradeOffer = "0";
        db.OpenDb();
        String tax1 = "0", tax2 = "0", tax3 = "0";
        /*  int orderId = db.getMaxOrderIdFromSaleOrder();*/
        int orderId = db.getMaxOrderIdFromSaleOrder();
        for (int i = 0; i < arrayList.size(); i++) {


            for (int j = 0; j < arrayList.get(i).getProductList().size(); j++) {
                db.createOrderDetails(
                        String.valueOf(orderId),
                        arrayList.get(i).getProductList().get(j).getProd_id(),
                        String.valueOf(arrayList.get(i).getProductList().get(j).getProd_qty()),
                        String.valueOf(arrayList.get(i).getProductList().get(j).getProd_qty()),
                        arrayList.get(i).getProductList().get(j).getProd_price(), /*"0"*/
                        "0",
                        arrayList.get(i).getProductList().get(j).getDiscount(),
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        String.valueOf(price(Double.parseDouble(arrayList.get(i).getProductList().get(j).getDiscount()),
                                String.valueOf(arrayList.get(i).getProductList().get(j).getProd_qty()),
                                Double.parseDouble(arrayList.get(i).getProductList().get(j).getProd_price()))),
                        "0",
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        "0");

            }


        }

        db.CloseDb();
    }
    private double price(double disc2,String quantity,double tp ){
        double discount_2=0,totAmount=0;
        // productList.get(pos).setProd_qty(Integer.parseInt(quantity));
        totAmount= Double.parseDouble(quantity) * tp;

        discount_2 = disc2 / 100;
        discount_2 = discount_2 * totAmount;
        totAmount = totAmount - discount_2;
        Log.d("TAG", "price: "+totAmount);
        return totAmount;


    }

    private String GetValues() {

        String result = "";

        for (int i = 0; i < arrayList.size(); i++) {


            for (int j = 0; j < arrayList.get(i).getProductList().size(); j++) {

//                boolean ProLogic = !((ProVal.get(i).getText().toString().isEmpty()) || (ProVal.get(i).getText().toString().equalsIgnoreCase("0")));

                result = result + arrayList.get(i).getProductList().get(j).getProd_id() + "!!";

                result = result + arrayList.get(i).getProductList().get(j).getProd_qty() + "!!";
                result = result + arrayList.get(i).getProductList().get(j).getProd_qty() + "!!";
                result = result + arrayList.get(i).getProductList().get(j).getProd_qty() + "!!!";
//                if (ProLogic) {
//
//
//                }
            }
        }
        return result;
    }

    public ArrayList<MainCartModel> mergeAmount(ArrayList<JsonBrandModel> JsonBrandModelList) {
        ArrayList<JsonBrandModel> newJsonBrandModelList = new ArrayList<>();
        ArrayList<MainCartModel> mainCartModel = new ArrayList<>();
        ArrayList<ProductModel> productModelList = new ArrayList<>();
        for (JsonBrandModel inv : JsonBrandModelList) {
            int isThere = 0;
            MainCartModel mainCartModel1 = new MainCartModel();
            mainCartModel1.setB_id(inv.getB_id());


            for (JsonBrandModel inv1 : newJsonBrandModelList) {
                if (inv1.getB_id().equals(inv.getB_id())) {
                    ProductModel productModel = new ProductModel();
                    productModel.setProd_qty(Integer.parseInt(inv1.getP_qty()));
                    productModel.setProd_id(inv1.getP_id());
                    productModel.setProd_name(inv1.getP_name());
                    productModel.setProd_price(inv1.getP_price());
                    productModel.setDiscount(inv1.getP_discount());

                    productModelList.add(productModel);
                    isThere++;
                }
            }
            if (isThere > 0) {
                newJsonBrandModelList.add(inv);
                mainCartModel1.setProductList(productModelList);
            } else {
                ProductModel productModel = new ProductModel();
                productModel.setProd_qty(Integer.parseInt(inv.getP_qty()));
                productModel.setProd_id(inv.getP_id());
                productModel.setProd_name(inv.getP_name());
                productModel.setProd_price(inv.getP_price());
                productModel.setDiscount(inv.getP_discount());
                ArrayList<ProductModel> singleProduct = new ArrayList<>();
                singleProduct.add(productModel);
                mainCartModel1.setProductList(singleProduct);
            }
            mainCartModel.add(mainCartModel1);
        }
        return mainCartModel;
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
        } catch (SecurityException ignore) {
        }
    }

    private void startLocation() {

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


        } catch (SecurityException ignore) {
        }
    }

    private synchronized void buildGoogleApiClient() {
//        if (getActivity() != null) {
        mGoogleApiClient = new GoogleApiClient.Builder(CashSummaryActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocation();
        stopLocationUpdates();

    }

    @Override
    public void onResume() {
        super.onResume();
        startLocation();

        if (mGoogleApiClient != null && mFusedLocationClient != null) {
            startLocationUpdates();
            Log.e("startLocationUpdates();", "startLocationUpdates()");
        } else {
            buildGoogleApiClient();
            Log.e("buildGoogleApiClient();", "buildGoogleApiClient()");
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /*
     Location Works Ends
     */
    @SuppressLint("RestrictedApi")
    private void startLocationUpdates() {
//        if (getActivity() != null) {
        // Create the location request to start receiving updates
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /* 10 secs */
        long UPDATE_INTERVAL = 5 * 1000;
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(CashSummaryActivity.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(CashSummaryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CashSummaryActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("TAG", "Missing Permission Location");
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
//        }
    }

    private void stopLocationUpdates() {

        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallBack);
        }
    }

    class JsonBrandModel {
        String b_id, p_id, p_name, p_price, p_qty, p_discount;

       String TradePrice,isTaxMrp,is_taxable,tax_mrp,mrp_price,tradeOffer,schemeValue,schemeTxt,tax1,tax2,tax3,taxf1,taxf2,taxf3;
        public JsonBrandModel() {
        }

        public String getTradePrice() {
            return TradePrice;
        }

        public void setTradePrice(String tradePrice) {
            TradePrice = tradePrice;
        }

        public String getIsTaxMrp() {
            return isTaxMrp;
        }

        public void setIsTaxMrp(String isTaxMrp) {
            this.isTaxMrp = isTaxMrp;
        }

        public String getIs_taxable() {
            return is_taxable;
        }

        public void setIs_taxable(String is_taxable) {
            this.is_taxable = is_taxable;
        }

        public String getTax_mrp() {
            return tax_mrp;
        }

        public void setTax_mrp(String tax_mrp) {
            this.tax_mrp = tax_mrp;
        }

        public String getMrp_price() {
            return mrp_price;
        }

        public void setMrp_price(String mrp_price) {
            this.mrp_price = mrp_price;
        }

        public String getTradeOffer() {
            return tradeOffer;
        }

        public void setTradeOffer(String tradeOffer) {
            this.tradeOffer = tradeOffer;
        }

        public String getSchemeValue() {
            return schemeValue;
        }

        public void setSchemeValue(String schemeValue) {
            this.schemeValue = schemeValue;
        }

        public String getSchemeTxt() {
            return schemeTxt;
        }

        public void setSchemeTxt(String schemeTxt) {
            this.schemeTxt = schemeTxt;
        }

        public String getTax1() {
            return tax1;
        }

        public void setTax1(String tax1) {
            this.tax1 = tax1;
        }

        public String getTax2() {
            return tax2;
        }

        public void setTax2(String tax2) {
            this.tax2 = tax2;
        }

        public String getTax3() {
            return tax3;
        }

        public void setTax3(String tax3) {
            this.tax3 = tax3;
        }

        public String getTaxf1() {
            return taxf1;
        }

        public void setTaxf1(String taxf1) {
            this.taxf1 = taxf1;
        }

        public String getTaxf2() {
            return taxf2;
        }

        public void setTaxf2(String taxf2) {
            this.taxf2 = taxf2;
        }

        public String getTaxf3() {
            return taxf3;
        }

        public void setTaxf3(String taxf3) {
            this.taxf3 = taxf3;
        }

        public String getB_id() {
            return b_id;
        }

        public void setB_id(String b_id) {
            this.b_id = b_id;
        }

        public String getP_id() {
            return p_id;
        }

        public void setP_id(String p_id) {
            this.p_id = p_id;
        }

        public String getP_name() {
            return p_name;
        }

        public void setP_name(String p_name) {
            this.p_name = p_name;
        }

        public String getP_price() {
            return p_price;
        }

        public void setP_price(String p_price) {
            this.p_price = p_price;
        }

        public String getP_qty() {
            return p_qty;
        }

        public void setP_qty(String p_qty) {
            this.p_qty = p_qty;
        }

        public String getP_discount() {
            return p_discount;
        }

        public void setP_discount(String p_discount) {
            this.p_discount = p_discount;
        }
    }


}