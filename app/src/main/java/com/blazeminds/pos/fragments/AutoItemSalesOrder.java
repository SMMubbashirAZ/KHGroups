package com.blazeminds.pos.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.BuildConfig;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.MainActivity;
import com.blazeminds.pos.MyService;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.SchemeDisplayListAdapter;
import com.blazeminds.pos.ShowSchemeListAdapter;
import com.blazeminds.pos.adapter.FilterWithSpaceAdapter;
import com.blazeminds.pos.autocomplete_resource.MyObject;
import com.blazeminds.pos.resources.Loader;
import com.blazeminds.pos.webservice_url.RetrofitWebService;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.blazeminds.pos.Constant.customToast;
import static com.blazeminds.pos.MainActivity.granted;
import static com.blazeminds.pos.MainActivity.requestPermission;


public class AutoItemSalesOrder extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    
    public final static String TAG = AutoItemSalesOrder.class.getSimpleName();
    // My GPS states
    public static final int GPS_PROVIDER_DISABLED = 1;
    public static final int GPS_GETTING_COORDINATES = 2;
    public static final int GPS_GOT_COORDINATES = 3;
    //public static final int GPS_PROVIDER_UNAVIALABLE = 4;
    //public static final int GPS_PROVIDER_OUT_OF_SERVICE = 5;
    public static final int GPS_PAUSE_SCANNING = 6;
    public static final int GPS_PROVIDER_DOESNOT_EXIST = 4;
    public static String SelectedCustomerId = "0";

    public static String DEFAULT_SO_ROUTE_ENABLE_DISABLE = "0"; // By Default Zero For Pre
    // Selected Routes
    public static PopupWindow popupWindow;
    private static String text = ".",text2=".";
    private static int counter = 0, counter2 = 0;
    private static double Latitude, Longitude;
    public FilterWithSpaceAdapter<String> myAdapterCustomer,myAdapterBrand;
    public String[] itemCustomer = new String[]{"Please search..."};
    public String[] itemBrand = new String[]{"Please search..."};
    public FilterWithSpaceAdapter<String> myAdapter2;
    public String[] item2 = new String[]{"Please search..."};
    Button Add, Submit, SchemeOffer, Search;
    TextView TotalQty, TotalAmount, OpeningBalTV, discountTV, discountTV2;
    String TotalAmountString, invoiceDiscountFinal, selectedDiscount, selectedDiscount2;
    double totalAmount, creditLimit, oldReceivable;
    double TOTALAMOUNT, CREDITMINUSOLDRECEIVABLE;
    int totalAMOUNT, creditMinusOldRec;
    EditText Notes;
    AutoCompleteTextView CustomerTxt,BrandTxt;
    ArrayList<String> brandsList = new ArrayList<>();
    String SelectedCustomerTypeId = "0";
    String SelectedProductId = "0";
    double respons = 0;
    TextView DBAmount;
    LinearLayout DynamicLayout;
    LinearLayout OrderChildLayout;
    int Incre = 0;
    AutoCompleteTextView pro,pro2;
    EditText qty;
    EditText schemeQty;
    Button minusBtn;
    Button showDetails;
    TextView proVal, startTime;
    View space;
    Spinner discountDrop, discountDrop2;
    //View spaceLast;
    TextView selDiscountDropTV, selDiscountDropTV2;
    Spinner bulkDiscountDrop;
    String selectedBulkDiscount;
    // New Feature Variables Start
    EditText tradeOfferEdtTxt, tradeOfferEdtTxt2;
    List<EditText> allTradeOfferList = new ArrayList<>();
    List<EditText> allTradeOfferList2 = new ArrayList<>();
    String mobEmpDiscountType = "", mobEmpSaleType = "";
    // New Feature Variables End
    List<AutoCompleteTextView> allPro = new ArrayList<>();
    List<EditText> allQty = new ArrayList<>();
    List<EditText> schemeQtyList = new ArrayList<>();
    List<TextView> allDBAmount = new ArrayList<>();
    List<Button> allMinusBtn = new ArrayList<>();
    List<TextView> ProVal = new ArrayList<>();
    ArrayList<HashMap<String, String>> result = new ArrayList<>();
    HashMap<String, String> map2 = new HashMap<>();
    double scheme = 0;
    Spinner saleTypeDrop;
    String selectedSaletype;
    PosDB db;
    TextView locStatusTxt;
    int position1 = -2;
    private double totalQTY = 0;
    private boolean selectCustomerItem;
    private boolean selectProductItem;
    LinearLayout brands_filter_layout;
    // Below editOrderList is using for saving discount data
    private ArrayList<TextView> discountTVList = new ArrayList<>(), discountTVList2 = new ArrayList<>();
    // Below editOrderList is using for display bulk discount data
    private ArrayList<Spinner> discountDropList = new ArrayList<>(), discountDropList2 = new ArrayList<>();
    private String coordsToSend;
    private ArrayList<HashMap<String, String>> schemeHolder, schemeDisplayHolder;
    private String gAccuracy;
    private LocationCallback mLocationCallBack = new LocationCallback() {
        
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
    // Location manager
    private LocationManager manager;
    private boolean isOne = true, formatError = true;
    private FragmentActivity fragmentActivity;
    private Switch route_switcher;
    private Spinner brand_type_spinner;
    private String selected_brand_id = "0";
    // Location events (we use GPS only)
    private LocationListener locListener = new LocationListener() {
        
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
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient mGoogleApiClient;
    private Dialog schemeDialog, showSchemeDialog;
    private ShowSchemeListAdapter showSchemeListAdapter;
    private SchemeDisplayListAdapter schemeDisplayListAdapter;
    private double t_o_v = 0, d_v_1 = 0, d_v_2 = 0, t_type = 0, t_mrp_type = 0, t_val = 0;
    
    public static AutoItemSalesOrder newInstance() {
        return new AutoItemSalesOrder();
    }
    
    public static Fragment newInstances() {
        
        return new AutoItemSalesOrder();
        
    }
    
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        isOne = true;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_sales_order_bulk, container, false);
        /*gps = new GPSTracker( getActivity() );*/
        
        SalesReturn.DEFAULT_SR_ROUTE_ENABLE_DISABLE = null;
        fragmentActivity = getActivity();
        counter = 0;
        counter2 = 0;
        
        LinearLayout lay = rootView.findViewById(R.id.RouteFrag);
        
        // load the animation
        Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
        
        lay.startAnimation(enter);
        
        
        db = PosDB.getInstance(getActivity());
        db.OpenDb();
        selectCustomerItem = false;
        selectProductItem = false;
        locStatusTxt = rootView.findViewById(R.id.locStatusTxt);
        
         brands_filter_layout = rootView.findViewById(R.id.brands_filter_layout);
        
     
        
        brand_type_spinner = rootView.findViewById(R.id.brand_type_spinner);
        
        schemeDialog = new Dialog(fragmentActivity);
        showSchemeDialog = new Dialog(fragmentActivity);
        PopulateAllBrands();
        
        manager = (LocationManager) fragmentActivity.getSystemService(Context.LOCATION_SERVICE);
        startLocation();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        
        route_switcher = rootView.findViewById(R.id.route_switcher);
    int    key = db.getAppSettingsValueByKey("en_non_route");
        if (key == 0) {
            route_switcher.setVisibility(View.GONE);
        }
        
        route_switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                
                if (isChecked) {
                    
                    DEFAULT_SO_ROUTE_ENABLE_DISABLE = "1";
                    itemCustomer = new String[]{"Please search..."};
                    
                } else {
                    itemCustomer = new String[]{"Please search..."};
                    DEFAULT_SO_ROUTE_ENABLE_DISABLE = "0";
                }
                
            }
        });
        
        
        startTime = rootView.findViewById(R.id.startTime);
        
        
        CustomerTxt = rootView.findViewById(R.id.SelectCustomer);
        BrandTxt=rootView.findViewById(R.id.SelectBrand);
        OpeningBalTV = rootView.findViewById(R.id.openingBalTxt);
        discountTV = rootView.findViewById(R.id.discountTxt);
        
        TotalQty = rootView.findViewById(R.id.TotQtyTxt);
        TotalAmount = rootView.findViewById(R.id.TotPriceTxt);
        Notes = rootView.findViewById(R.id.NotesTxt);
        bulkDiscountDrop = rootView.findViewById(R.id.bulkDiscountDrop);
        saleTypeDrop = rootView.findViewById(R.id.saleTypeDrop);
        
        CustomerTxt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        CustomerTxt.setRawInputType(InputType.TYPE_CLASS_TEXT);
        
        Notes.setImeOptions(EditorInfo.IME_ACTION_DONE);
        Notes.setRawInputType(InputType.TYPE_CLASS_TEXT);
        
        db.OpenDb();
        mobEmpDiscountType = db.getMobEmpDiscountType();
        mobEmpSaleType = db.getMobEmpSaleType();
        db.CloseDb();
        
        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
            bulkDiscountDrop.setVisibility(View.VISIBLE);
        } else {
            bulkDiscountDrop.setVisibility(View.GONE);
        }
        // set our adapter
        myAdapterCustomer = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, itemCustomer);
        myAdapterBrand = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, itemBrand);
        CustomerTxt.setAdapter(myAdapterCustomer);
        BrandTxt.setAdapter(myAdapterBrand);
        OrderChildLayout = rootView.findViewById(R.id.orderChildLayout);
        
        DynamicLayout = rootView.findViewById(R.id.DynamicLayout);
        
        Submit = rootView.findViewById(R.id.SubmitBtn);
        Search= rootView.findViewById(R.id.Search);
        brands_filter_layout.setVisibility(View.GONE);
        Submit.setVisibility(View.GONE);
        Search.setVisibility(View.GONE);
        SchemeOffer = rootView.findViewById(R.id.SchemeOffer);
        SchemeOffer.setVisibility(View.GONE);
        Add = rootView.findViewById(R.id.AddMoreBtn);
        Add.setVisibility(View.GONE);
        startTime.setText(getDateTime());
        
        
        startTime.setVisibility(View.GONE);
        
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (ProVal.get(Incre - 1).getText().toString().trim().isEmpty()) {
                    Toast.makeText(AppContextProvider.getContext(), "Please fill last item first", Toast.LENGTH_SHORT).show();
                } else if (formatError) {
                    if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                        CreateDynamicView2();
                    } else {
                        CreateDynamicView();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please insert proper format number", Toast.LENGTH_SHORT).show();
                }
                
                
            }
        });
        
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission(getActivity());
                if (!granted) {
                    return;
                }
                SalesOrderSubmit();
                counter = 0;
                counter2 = 0;
                
            }
        });
        
        
        SchemeOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                showScheme();
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
                myAdapterCustomer = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, itemCustomer);
                CustomerTxt.setAdapter(myAdapterCustomer);
                myAdapterCustomer.notifyDataSetChanged();
                
                
                if (s.toString().contains("'") || s.toString().contains("%") || s.toString().contains("&")) {
                    Toast.makeText(AppContextProvider.getContext(), " Symbols like \" ' \" \" % \" \" & \"\n not acceptable ", Toast.LENGTH_SHORT).show();
                    
                } else {
                    
                    itemCustomer = getCustomerNameFromDb(Constant.testInput(s.toString()), db);
                    
                    
                    db.OpenDb();
                    
                    SelectedCustomerId = (db.getCustomerID(Constant.testInput(s.toString())));
                    SelectedCustomerTypeId = db.getCustomerTypeID(Constant.testInput(s.toString()));
                    db.CloseDb();
                }
                
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                
                text = s.toString();
                Log.e("AfterTextChange", String.valueOf(text.length()));
                //if (!text.equals(""))
                   // pro.setError(null);
                
            }
        });
    
        BrandTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
            
            }
        
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            
            
                BrandTxt.setError(null);
            
                // update the adapater
                myAdapterBrand = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, itemBrand);
                BrandTxt.setAdapter(myAdapterBrand);
                myAdapterBrand.notifyDataSetChanged();
            
            
                if (s.toString().contains("'") || s.toString().contains("%") || s.toString().contains("&")) {
                    Toast.makeText(AppContextProvider.getContext(), " Symbols like \" ' \" \" % \" \" & \"\n not acceptable ", Toast.LENGTH_SHORT).show();
                
                } else {
                
                    itemBrand = getBrandNameFromDb(Constant.testInput(s.toString()), db);
                
                
                    db.OpenDb();
    
                    selected_brand_id = (db.getBrandID(Constant.testInput(s.toString())));
                 
                    db.CloseDb();
                }
            
            
            }
        
            @Override
            public void afterTextChanged(Editable s) {
            
//                text2 = s.toString();
//
//                if (!text2.equals(""))
//                    pro2.setError(null);
            
            }
        });
        
        CustomerTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                
                
                selectCustomerItem = true;
                CustomerTxt.setEnabled(false);
                CustomerTxt.setTextColor(Color.BLACK);
                double total = 0, sum, obal, amtReceived = 0, advancePayment = 0;
                db.OpenDb();
                String total2 = db.getSelectedCustomerOpeningBalanceTotal(SelectedCustomerId);
                String amountReceived = db.getSelectedCustomerAmountReceived(SelectedCustomerId);
                String ob = db.getSelectedCustomerOpeningBalance(SelectedCustomerId);
                String advancePymt = db.getSelectedCustomerAdvancePayment(SelectedCustomerId);
                //String creditAmount = db.getSelectedCustomerCreditAmountLimit(SelectedCustomerId);
                db.CloseDb();
                if (total2 != null && !total2.equals("")) {
                    total = Double.parseDouble(total2);
                }
                if (amountReceived != null && !amountReceived.equals("")) {
                    amtReceived = Double.parseDouble(amountReceived);
                }
                if (advancePymt != null && !advancePymt.equalsIgnoreCase("")) {
                    advancePayment = Double.parseDouble(advancePymt);
                }
				/*String creditAmount = db.getSelectedCustomerCreditAmountLimit(SelectedCustomerId);

				if (creditAmount != null && !creditAmount.equals("") && !creditAmount.equals("-1")){
					creditLimit = Double.parseDouble(creditAmount);
				}*/
                obal = Double.parseDouble(ob);
                sum = obal + (total - amtReceived) - advancePayment;
                //if (ob != null)
                OpeningBalTV.setText(String.format("%.2f", sum));
                
                oldReceivable = sum;
                counter++;
               
//       if(BuildConfig.FLAVOR.equalsIgnoreCase("saeed_ghani")  )
//       {
//         final  ProgressDialog dialog= new ProgressDialog(getActivity());
//           dialog.setMessage("Loading Items...");
//           dialog.show();
//       //    rootView.findViewById(R.id.progressBarSalesOrder).setVisibility(View.VISIBLE);
//           new Handler().postDelayed(new Runnable() {
//               @Override
//               public void run() {
//
//
//
//                           final  ArrayList<HashMap<String, String>> OrderTemplateList=   db.getOrderTemplate(SelectedCustomerId);
//                           if(OrderTemplateList.size()>0){
////
////
//new                               AsyncTask<Void, Void, Void>()
//                               {
//
//
//                                   @Override
//                                   protected void onPreExecute() {
//                                   super.onPreExecute();
//
//                                   //this method will be running on UI thread
//
//                               }
//                                   @Override
//                                   protected Void doInBackground(Void... params) {
//                                       getActivity()   .runOnUiThread(new Runnable() {
//                                                                          @Override
//                                                                          public void run() {
//                                   for(int templateCounter=0;templateCounter<OrderTemplateList.size();templateCounter++)
//                                       {
//                                           if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
//                                               //    CreateDynamicView2();
//                                           } else
//                                               {
//                                               if(templateCounter!=0){
//                                                   CreateDynamicView3();
//                                                 //  allMinusBtn.get(templateCounter).performClick();
//                                            //       Incre=0;
//                                               }
//                                              // CreateDynamicView3();
//
//                                       allPro.get(templateCounter).setText(OrderTemplateList.get(templateCounter).get("name"));
//                                       allPro.get(templateCounter).setEnabled(false);
//                                       allQty.get(templateCounter).setText(OrderTemplateList.get(templateCounter).get("quantity"));
//                                       allQty.get(templateCounter).setEnabled(false);
//    CustomerTxt.setEnabled(false);
//
//
//                                           }
//
//                                       }
////                                                                              rootView.findViewById(R.id.progressBarSalesOrder).setVisibility(View.GONE);
//                                                                          }   });
//
////                                   }
////                               });
//            //ends
//                                       if (dialog.isShowing()) {
//                                           dialog.dismiss();
//                                       }
//                                       return null;
//                                   }
//
//                                   @Override
//                                   protected void onPostExecute(Void result) {
//                                       super.onPostExecute(result);
//
//                                       //this method will be running on UI thread
//
//
//                                   }
//
//                               }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
//                               discountDropList.get(0).setEnabled(false);
//                               allMinusBtn.get(0).setEnabled(false);
//                               Add.setEnabled(false);
//
//
//
//                           }
//            else{ if (dialog.isShowing()) {
//                CustomerTxt.setEnabled(true);
//                               dialog.dismiss();
//                           }}
//
//
//               }},500);
//
//
//       }
                int key = db.getAppSettingsValueByKey("en_brands_filter");
                if (key == 0) {
                    brands_filter_layout.setVisibility(View.GONE);
                }
                else{
    
                    brands_filter_layout.setVisibility(View.VISIBLE);
                    ArrayList<HashMap<String, String>>   getBrandByStoreList = db.getBrandByStore(db.getCustomerID(CustomerTxt.getText().toString()));
    
                    brandsList.clear();
                    	brandsList.add("Select Brand");
                    for (int j = 0; j < getBrandByStoreList.size(); j++) {
        
                        brandsList  .add(db.getBrandName(getBrandByStoreList.get(j).get("brand_id")));
        
                    }
                    ArrayAdapter     brandsAdapter  = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_list_item_1, brandsList);
                    brandsAdapter.notifyDataSetChanged();
                    brand_type_spinner.setAdapter(brandsAdapter);
                }
   
           Search.setVisibility(View.VISIBLE);
            }
        });
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int    key = db.getAppSettingsValueByKey("en_brands_filter");
                if (key != 0) {


                if(!brand_type_spinner.getSelectedItem().toString().equals("Select Brand")) {
                    getProgress();
                }
                else {
                    customToast(getActivity(), "Please select brand.");
                }        }
                else{  getProgress();}
         //   Search.setEnabled(false);
            }
        });
        
        lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if (selectCustomerItem) {
                    
                    CustomerTxt.setEnabled(true);
                }
                if (selectProductItem) {
                    
                    pro.setEnabled(true);
                    
                }
            }
        });
        
        OrderChildLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if (selectCustomerItem) {
                    CustomerTxt.setEnabled(true);
                }
                if (selectProductItem) {
                    pro.setEnabled(true);
                }
            }
        });
        
        PopulateSaleTypeDropDown();
        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
            PopulateBulkDiscountDropDown();
        }
        
//        if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
//            CreateDynamicView2();
//        } else {
//            CreateDynamicView3();
//        }
//
        return rootView;
    }
    
    private void PopulateAllBrands() {
        
        
        final ArrayList<HashMap<String, String>> brandsList = db.getAllBrands();
        
        List<String> brandsName = new ArrayList<>();
        
        
        for (HashMap<String, String> map : brandsList) {
            
            brandsName.add(map.get("bname"));
            
        }
        
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(fragmentActivity,
                android.R.layout.simple_spinner_item, brandsName);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        brand_type_spinner.setAdapter(dataAdapter);
        
        brand_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                
                
                selected_brand_id = brandsList.get(position).get("id");
                
                
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            
            }
        });
    }
    
    private void CreateDynamicView() {
        
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((ViewGroup.MarginLayoutParams) (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)));
        // left 20, right 20 previous
        params.setMargins(10, 10, 10, 10);
        
        LinearLayout.LayoutParams paramsTable = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 10);
        paramsTable.setMargins(2, 2, 2, 2);
        
        TableRow trProduct = new TableRow(getActivity());
        
        trProduct.setLayoutParams(paramsTable);
        trProduct.setWeightSum(10);

        
        db.OpenDb();
        
        proVal = new TextView(getActivity());
        proVal.setId(Incre);
        ProVal.add(proVal);
        
        DBAmount = new TextView(getActivity());
        DBAmount.setId(Incre);
        
        
        pro = new AutoCompleteTextView(getActivity());
        pro.setId(Incre);
        //pro.setTag(1);
        pro.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        
        qty = new EditText(getActivity());
        qty.setId(Incre);
        qty.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        
        
        schemeQty = new EditText(getActivity());
        schemeQty.setId(Incre);
        schemeQty.setVisibility(View.GONE);
        
        
        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
//			tradeOfferEdtTxt.setVisibility(View.GONE);
            
            selDiscountDropTV = new TextView(getActivity());
            selDiscountDropTV.setId(Incre);
            selDiscountDropTV.setVisibility(View.GONE);
            
            discountDrop = new Spinner(getActivity());
            discountDrop.setId(Incre);
            discountDrop.setBackgroundResource(R.drawable.edittxt_bg);
            PopulateDiscountDropDown();
            
        } else {
//			discountDrop.setVisibility(View.GONE);
            
            tradeOfferEdtTxt = new EditText(getActivity());
            tradeOfferEdtTxt.setId(Incre);
            tradeOfferEdtTxt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            tradeOfferEdtTxt.setHint("T.O");
            tradeOfferEdtTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
            tradeOfferEdtTxt.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(4)});
            
            
        }
        
        discountTVList.add(selDiscountDropTV);
        
        minusBtn = new Button(getActivity());
        minusBtn.setId(Incre);
        //minusBtn.setText("Remove");
        //minusBtn.setBackgroundResource(R.drawable.edittxt_bg);
        minusBtn.setTextColor(Color.BLACK);
        minusBtn.setBackgroundResource(R.drawable.minus);
        
        showDetails = new Button(getActivity());
        showDetails.setId(Incre);
        //showDetails.setText("Details");
        showDetails.setBackgroundResource(R.mipmap.details);
        //showDetails.setTextColor(Color.BLACK);
        
        pro.setHint("Select Product");
        pro.setTextSize(15);
        //pro.setFocusable(false);
//		pro.setEms(10);
        
        // set our adapter
        myAdapter2 = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, item2);
        pro.setAdapter(myAdapter2);
        
        
        pro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
            
            }
            
            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                
                if (counter2 > 1) {
                    if (SelectedCustomerId.equals("0")) {
                        pro.setError("Please Valid Customer First");
                        return;
                    }
                }
                
                String s = CustomerTxt.getText().toString();
                
                if (counter != 0 && !s.equals("")) {
                    
                    int discount1 = -1;
                    
                    int maxDiscount = db.getMobUserMaxDiscount();
                    
                    
                    myAdapter2 = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, item2);
                    
                    pro.setAdapter(myAdapter2);
                    
                    myAdapter2.notifyDataSetChanged();
                    
                    // query the database based on the user input
                    
                    item2 = getInventoryNameFromDb(Constant.testInput(userInput.toString()), db);
                    
                    db.OpenDb();
                    
                    //discountDrop.setSelection(0, true);
                    proVal.setText(db.getInventoryID(Constant.testInput(pro.getText().toString())));
                    
                    qty.setText("");
                    
                    schemeQty.setText("0");
                    
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        selDiscountDropTV.setText("0");
                        
                    } else {
                        tradeOfferEdtTxt.setText("");
                        
                    }
                    
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        
                        for (int j = 0; j < ProVal.size(); j++) {
                            if (ProVal.size() == allQty.size()) {
                             
                                String[] val =
                                        db.getBrandAndProductTypeAndSubType(ProVal.get(j).getText().toString());
                                String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
                                result = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId,
                                        val[1], val[0], allQty.get(j).getText().toString(),
                                        ProVal.get(j).getText().toString(), val1[0], val1[1],
                                        val[2]);
                                
                                if (result.size() > 0) {
                                    for (int i = 0; i < result.size(); i++) {
                                        
                                        map2 = result.get(i);
                                        
                                        if (discountTVList.get(j).getText().toString().equalsIgnoreCase("0")) {
                                            discount1 = Integer.parseInt(map2.get("discount1")/*discountTVList.get(j).getText().toString()*/);
                                            
                                        } else {
                                        
                                        }
                                    }
                                    
                                    if (discount1 > maxDiscount) {
                                        discountDrop.setSelection(maxDiscount, true);
                                    } else {
                                        if (discount1 == 0) {
                                            discountDrop.setSelection(1, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                        } else if (discount1 == 1) {
                                            discountDrop.setSelection(2, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 2) {
                                            discountDrop.setSelection(3, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 3) {
                                            discountDrop.setSelection(4, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 4) {
                                            discountDrop.setSelection(5, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 5) {
                                            discountDrop.setSelection(6, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 6) {
                                            discountDrop.setSelection(7, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 7) {
                                            discountDrop.setSelection(8, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 8) {
                                            discountDrop.setSelection(9, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 9) {
                                            discountDrop.setSelection(10, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 10) {
                                            discountDrop.setSelection(11, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 11) {
                                            discountDrop.setSelection(12, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 12) {
                                            discountDrop.setSelection(13, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 13) {
                                            discountDrop.setSelection(14, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 14) {
                                            discountDrop.setSelection(15, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 15) {
                                            discountDrop.setSelection(16, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 16) {
                                            discountDrop.setSelection(17, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 17) {
                                            discountDrop.setSelection(18, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 18) {
                                            discountDrop.setSelection(19, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 19) {
                                            discountDrop.setSelection(20, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 20) {
                                            discountDrop.setSelection(21, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else {
                                            discountDrop.setSelection(0, true);
                                            
                                        }
                                    }
                                }
                                
                            }
                        }
                    }
                    
                    db.CloseDb();
                } else {
                    pro.setError(null);
                    counter++;
                }
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                
                if (counter2 > 1) {
                    String txt = CustomerTxt.getText().toString();
                    if (txt.equals("")) {
                        pro.setError("Please Select Customer First");
                    } else if (SelectedCustomerId.equals("0")) {
                        pro.setError("Please Valid Customer First");
                    }
                } else {
                    counter2++;
                    //pro.setError(null);
                }
                
            }
        });
        
        pro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                
                
                selectProductItem = true;
                pro.setEnabled(false);
                pro.setTextColor(Color.BLACK);
            }
        });
        
        DBAmount.setVisibility(View.GONE);
        allDBAmount.add(DBAmount);
        
        
        pro.setInputType(InputType.TYPE_CLASS_TEXT);
        pro.setBackgroundResource(R.drawable.edittxt_bg);
        pro.setSingleLine(false);
        pro.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        //pro.setLayoutParams(params);
        
        LinearLayout.LayoutParams paramsTableComponets = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 5f);
        //LinearLayout.LayoutParams paramsTableComponets = new TableRow.LayoutParams(0, 125, 0.50f);
        // left 10 , right 10 previous
        paramsTableComponets.setMargins(10, 2, 10, 2);
        
        pro.setLayoutParams(paramsTableComponets);
        
        trProduct.addView(pro);
        
        allPro.add(pro);
        
        space = new View(getActivity());
        
        LinearLayout.LayoutParams paramsSpace = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsTableComponets.setMargins(10, 2, 10, 2);
        space.setLayoutParams(paramsSpace);
        
        qty.setHint("Qty");
        qty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        qty.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(5)});
        
        
        LinearLayout.LayoutParams paramsQtyComponets = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.7f);
        
        paramsQtyComponets.setMargins(10, 2, 10, 2);
        
        qty.setLayoutParams(paramsQtyComponets);
        
        LinearLayout.LayoutParams paramsComponets = new TableRow.LayoutParams(/*ViewGroup.LayoutParams.WRAP_CONTENT*/0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.3f);
        
        paramsComponets.setMargins(10, 2, 10, 2);
        
        if (mobEmpDiscountType.equalsIgnoreCase("1"))
            discountDrop.setLayoutParams(paramsComponets);
        else
            tradeOfferEdtTxt.setLayoutParams(paramsComponets);
        
        qty.addTextChangedListener(new TextWatcher() {
            
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                
                
                double oldQTY = 0;
                double amountTxt = 0;
                double invoiceDiscount = 0, afterDivideDiscount = 0, afterSubtractDiscount = 0;
                String tradeOffer = "";
                
                if (s.length() > 0) {
                    if (s.charAt(0) == '.') {
                        qty.setError("Wrong Format");
                        
                        formatError = false;
                        return;
                    } else formatError = true;
                }
                
                try {
                    
                    
                    db.OpenDb();
                    for (int j = 0; j < allQty.size(); j++) {
                        
                    
                        String[] val =
                                db.getBrandAndProductTypeAndSubType(ProVal.get(j).getText().toString());
                        String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
                        result = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId,
                                val[1], val[0], allQty.get(j).getText().toString(),
                                ProVal.get(j).getText().toString(), val1[0], val1[1], val[2]);
                        
                        if (mobEmpDiscountType.equalsIgnoreCase("2")) {
                            if (allTradeOfferList.get(j).getText().toString().equalsIgnoreCase("")) {
                                tradeOffer = "0";
                            } else {
                                  tradeOffer = allTradeOfferList.get(j).getText().toString();if(Integer.valueOf(tradeOffer)>Integer.valueOf(result.get(0).get("tradePrice")))
                                    {
                                        allTradeOfferList.get(j).setText(result.get(0).get("tradePrice"));
                                        Toast.makeText(AppContextProvider.getContext(), "T.O cannot be more than product price.", Toast.LENGTH_LONG).show();
                                        return;
                                    }}
                        }
                        
                        if (result.size() > 0) {
                            for (int i = 0; i < result.size(); i++) {
                                map2 = result.get(i);
                                if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                                    respons =
                                            pricingCalculation(map2,
                                                    Double.parseDouble(allQty.get(j).getText().toString()),
                                                    map2.get("tradePrice"),/*map2.get("discount1")*/
                                                    discountTVList.get(j).getText().toString().equals("Disc") ? "0" : discountTVList.get(j).getText().toString(),
                                                    map2.get("discount2"), map2.get("tradeOffer"), map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                } else {
                                    respons = pricingCalculation(map2, Double.parseDouble(allQty.get(j).getText().toString()), map2.get("tradePrice"),/*map2.get("discount1")*/map2.get("discount1"), map2.get("discount2"), tradeOffer, map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                }
                                if (map2.get("multi").equals("0") && !map2.get("scheme").equalsIgnoreCase("") && Double.parseDouble(map2.get("scheme")) != 0) {
                                    if (!map2.get("schemeVal").equalsIgnoreCase("") && Double.parseDouble(map2.get("schemeVal")) != 0)
                                        scheme = (Math.floor(Double.parseDouble(allQty.get(j).getText().toString()) / Double.parseDouble(map2.get("scheme")))) * Double.parseDouble(map2.get("schemeVal"));
                                } else
                                    scheme = 0;
                                schemeQty.setText(String.format("%.0f", scheme));
                                
                                
                                oldQTY = oldQTY + Double.parseDouble(allQty.get(j).getText().toString()) + Double.parseDouble(schemeQtyList.get(j).getText().toString());
                                
                                totalQTY = oldQTY;
                                amountTxt = amountTxt + respons;
                                
                                
                            }
                            
                            Log.e("RESULT_SIZE", result.size() + "");
                        } else {
                            
                            oldQTY = oldQTY + Double.parseDouble(allQty.get(j).getText().toString()) + Double.parseDouble(schemeQtyList.get(j).getText().toString());
                            totalQTY = oldQTY;
                        }
                        
                        //amountTxt = amountTxt + (Double.parseDouble(allDBAmount.get(j).getText().toString()) * Double.parseDouble(allQty.get(j).getText().toString()));
                    }
                } catch (NumberFormatException e) {
                    oldQTY += 0;
                    amountTxt += 0;
                    Log.e("TestQty", e.getMessage());
                    
                }
                
                db.CloseDb();
                
                //TotalQty.setText( String.valueOf( "Total qty:\n"+ oldQTY+newValue ) );
                if (SelectedCustomerTypeId.equalsIgnoreCase("1")) {
                    invoiceDiscount = db.getDiscountForSaleOrder(amountTxt);
                    if (invoiceDiscount > 0) {
                        afterDivideDiscount = invoiceDiscount / 100;
                        afterSubtractDiscount = 1 - afterDivideDiscount;
                        amountTxt = amountTxt * afterSubtractDiscount;
                    }
                }
                TotalQty.setText("Total Quantity:\n" + String.format("%.2f", totalQTY));
                TotalAmount.setText("Total Amount:\n" + String.format("%.2f", amountTxt));
                
                discountTV.setText(String.format("%.2f", invoiceDiscount) + " %");
                
                TotalAmountString = String.valueOf(String.format("%.2f", amountTxt));
                
                invoiceDiscountFinal = String.valueOf(String.format("%.2f", invoiceDiscount));
                totalAmount = amountTxt;
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
            
            }
        });
        
        
        if (mobEmpDiscountType.equalsIgnoreCase("2")) {
            tradeOfferEdtTxt.addTextChangedListener(new TextWatcher() {
                
                
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
                }
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    
                    double oldQTY = 0;
                    double amountTxt = 0;
                    double invoiceDiscount = 0, afterDivideDiscount = 0, afterSubtractDiscount = 0;
                    String tradeOffer = "";
                    try {
                        
                        
                        db.OpenDb();
                        for (int j = 0; j < allQty.size(); j++) {
                            
                           
                            String[] val =
                                    db.getBrandAndProductTypeAndSubType(ProVal.get(j).getText().toString());
                            String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
                            result = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId,
                                    val[1], val[0], allQty.get(j).getText().toString(),
                                    ProVal.get(j).getText().toString(), val1[0], val1[1], val[2]);
                            
                            if (mobEmpDiscountType.equalsIgnoreCase("2")) {
                                if (allTradeOfferList.get(j).getText().toString().equalsIgnoreCase("")) {
                                    tradeOffer = "0";
                                } else {
                                      tradeOffer = allTradeOfferList.get(j).getText().toString();if(Integer.valueOf(tradeOffer)>Integer.valueOf(result.get(0).get("tradePrice")))
                                    {
                                        allTradeOfferList.get(j).setText(result.get(0).get("tradePrice"));
                                        Toast.makeText(AppContextProvider.getContext(), "T.O cannot be more than product price.", Toast.LENGTH_LONG).show();
                                        return;
                                    }}
                            }
                            
                            if (result.size() > 0) {
                                for (int i = 0; i < result.size(); i++) {
                                    map2 = result.get(i);
                                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                                        respons = pricingCalculation(map2, Double.parseDouble(allQty.get(j).getText().toString()), map2.get("tradePrice"),/*map2.get("discount1")*/discountTVList.get(j).getText().toString(), map2.get("discount2"), map2.get("tradeOffer"), map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                    } else {
                                        respons = pricingCalculation(map2, Double.parseDouble(allQty.get(j).getText().toString()), map2.get("tradePrice"),/*map2.get("discount1")*/ map2.get("discount1"), map2.get("discount2"), tradeOffer, map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                    }
                                    if (!map2.get("scheme").equalsIgnoreCase("") && Double.parseDouble(map2.get("scheme")) != 0) {
                                        if (!map2.get("schemeVal").equalsIgnoreCase("") && Double.parseDouble(map2.get("schemeVal")) != 0)
                                            scheme = (Math.floor(Double.parseDouble(allQty.get(j).getText().toString()) / Double.parseDouble(map2.get("scheme")))) * Double.parseDouble(map2.get("schemeVal"));
                                    } else
                                        scheme = 0;
                                    schemeQty.setText(String.format("%.0f", scheme));
                                    
                                    
                                    oldQTY = oldQTY + Double.parseDouble(allQty.get(j).getText().toString()) + Double.parseDouble(schemeQtyList.get(j).getText().toString());
                                    
                                    amountTxt = amountTxt + respons;
                                    
                                }
                            }
                            
                            //amountTxt = amountTxt + (Double.parseDouble(allDBAmount.get(j).getText().toString()) * Double.parseDouble(allQty.get(j).getText().toString()));
                        }
                    } catch (NumberFormatException e) {
                        oldQTY += 0;
                        amountTxt += 0;
                        
                        
                    }
                    
                    db.CloseDb();
                    
                    //TotalQty.setText( String.valueOf( "Total qty:\n"+ oldQTY+newValue ) );
                    if (SelectedCustomerTypeId.equalsIgnoreCase("1")) {
                        invoiceDiscount = db.getDiscountForSaleOrder(amountTxt);
                        if (invoiceDiscount > 0) {
                            afterDivideDiscount = invoiceDiscount / 100;
                            afterSubtractDiscount = 1 - afterDivideDiscount;
                            amountTxt = amountTxt * afterSubtractDiscount;
                        }
                    }
                    TotalQty.setText("Total Quantity:\n" + String.format("%.2f", oldQTY));
                    TotalAmount.setText("Total Amount:\n" + String.format("%.2f", amountTxt));
                    
                    discountTV.setText(String.format("%.2f", invoiceDiscount) + " %");
                    
                    TotalAmountString = String.valueOf(String.format("%.2f", amountTxt));
                    
                    invoiceDiscountFinal = String.valueOf(String.format("%.2f", invoiceDiscount));
                    totalAmount = amountTxt;
                    
                }
                
                @Override
                public void afterTextChanged(Editable s) {
                
                }
            });
        }
        
        LinearLayout.LayoutParams paramsMinusBtnComponets = new TableRow.LayoutParams(0, 50, 1f);
        
        paramsMinusBtnComponets.setMargins(10, 2, 10, 2);
        
        minusBtn.setLayoutParams(paramsMinusBtnComponets);
        
        showDetails.setLayoutParams(paramsMinusBtnComponets);
        
        
        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                final TableRow parent = (TableRow) v.getParent();
                Log.d("DYNID", String.valueOf(v.getId()));
                
                DeleteValues(v.getId());
                
                DynamicLayout.removeView(parent);
                
            }
        });
        
        showDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                final TableRow parent = (TableRow) v.getParent();
                View discount = null, tradeOffer = null;
                Spinner discount1 = null;
                String discountValue = "0";
                EditText tradeOfferEdtTxt;
                String tradeOfferValue = "0";
                View pos = parent.getChildAt(0);
                View qtyView = parent.getChildAt(1);
                if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                    discount = parent.getChildAt(2);
                    discount1 = (Spinner) discount;
                    discountValue = discount1.getSelectedItem().toString();
                    
                } else {
                    tradeOffer = parent.getChildAt(2);
                    tradeOfferEdtTxt = (EditText) tradeOffer;
                    tradeOfferValue = tradeOfferEdtTxt.getText().toString();
                }
                View schemeValView = parent.getChildAt(3);
                AutoCompleteTextView proTV = (AutoCompleteTextView) pos;
                EditText qtyEdtTxt = (EditText) qtyView;
                EditText schemeValHiddenEdtTxt = (EditText) schemeValView;
                String quantity = qtyEdtTxt.getText().toString();
                String schemeValHidden = schemeValHiddenEdtTxt.getText().toString();
                db.OpenDb();
                SelectedProductId = db.getInventoryID(proTV.getText().toString());
                db.CloseDb();
                if (tradeOfferValue.equalsIgnoreCase("")) {
                    tradeOfferValue = "0";
                }
                if (discountValue.equalsIgnoreCase("")) {
                    discountValue = "0";
                }
                initiatePopupWindow(v, SelectedCustomerTypeId, SelectedProductId, Incre, quantity, schemeValHidden, discountValue, tradeOfferValue);
                
            }
        });
        trProduct.addView(qty);
        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
            trProduct.addView(discountDrop);
            
        } else {
            trProduct.addView(tradeOfferEdtTxt);
            
        }
//		trProduct.addView(selDiscountDropTV);
        trProduct.addView(schemeQty);
        trProduct.addView(showDetails);
        trProduct.addView(minusBtn);
        
        
        trProduct.setGravity(Gravity.CENTER_HORIZONTAL);
        
        //trPro2.addView(minusBtn);
        //trPro2.addView(showDetails);
        allQty.add(qty);
        schemeQtyList.add(schemeQty);
        allMinusBtn.add(minusBtn);
        
        discountDropList.add(discountDrop);
        allTradeOfferList.add(tradeOfferEdtTxt);
        allTradeOfferList2.add(tradeOfferEdtTxt2);
        db.CloseDb();
        
        DynamicLayout.addView(trProduct);
        DynamicLayout.addView(space);
        
        //DynamicLayout.addView(trPro2);
        //DynamicLayout.addView(space);
        
        Incre++;
        
        
    }
    
    private void CreateDynamicView3() {
        
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((ViewGroup.MarginLayoutParams) (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)));
        // left 20, right 20 previous
        params.setMargins(10, 10, 10, 10);
        
        LinearLayout.LayoutParams paramsTable = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 10);
        paramsTable.setMargins(2, 2, 2, 2);
        
        TableRow trProduct = new TableRow(getActivity());
        
        trProduct.setLayoutParams(paramsTable);
        trProduct.setWeightSum(7);
        
        
        db.OpenDb();
        
        proVal = new TextView(getActivity());
       
        proVal.setId(Incre);
        ProVal.add(proVal);
        
        DBAmount = new TextView(getActivity());
        DBAmount.setId(Incre);
        
        
        pro = new AutoCompleteTextView(getActivity());
        pro.setId(Incre);
        pro.setEnabled(false);
        //pro.setTag(1);
        pro.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        
        qty = new EditText(getActivity());
        qty.setId(Incre);
        qty.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        qty.setEnabled(false);
        
        schemeQty = new EditText(getActivity());
        schemeQty.setId(Incre);
        schemeQty.setVisibility(View.GONE);
        
        
        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
//			tradeOfferEdtTxt.setVisibility(View.GONE);
            
            selDiscountDropTV = new TextView(getActivity());
            selDiscountDropTV.setId(Incre);
            selDiscountDropTV.setVisibility(View.GONE);
            
            discountDrop = new Spinner(getActivity());
            discountDrop.setId(Incre);
            discountDrop.setBackgroundResource(R.drawable.edittxt_bg);
            PopulateDiscountDropDown();
            
        } else {
//			discountDrop.setVisibility(View.GONE);
            
            tradeOfferEdtTxt = new EditText(getActivity());
            tradeOfferEdtTxt.setId(Incre);
            tradeOfferEdtTxt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            tradeOfferEdtTxt.setHint("T.O");
            tradeOfferEdtTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
            tradeOfferEdtTxt.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(4)});
            
            
        }
        
        discountTVList.add(selDiscountDropTV);
        
        minusBtn = new Button(getActivity());
        minusBtn.setId(Incre);
        //minusBtn.setText("Remove");
        //minusBtn.setBackgroundResource(R.drawable.edittxt_bg);
        minusBtn.setTextColor(Color.BLACK);
        minusBtn.setBackgroundResource(R.drawable.minus);
        
        showDetails = new Button(getActivity());
        showDetails.setId(Incre);
        //showDetails.setText("Details");
        showDetails.setBackgroundResource(R.mipmap.details);
        //showDetails.setTextColor(Color.BLACK);
        
        pro.setHint("Select Product");
        pro.setTextSize(15);
        //pro.setFocusable(false);
//		pro.setEms(10);
        
        // set our adapter
        myAdapter2 = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, item2);
        pro.setAdapter(myAdapter2);
        
        
        pro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
            
            }
            
            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                
                if (counter2 > 1) {
                    if (SelectedCustomerId.equals("0")) {
                        pro.setError("Please Valid Customer First");
                        return;
                    }
                }
                
                String s = CustomerTxt.getText().toString();
                
                if (counter != 0 && !s.equals("")) {
                    
                    int discount1 = -1;
                    
                    int maxDiscount = db.getMobUserMaxDiscount();
                    
                    
                    myAdapter2 = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, item2);
                    
                    pro.setAdapter(myAdapter2);
                    
                    myAdapter2.notifyDataSetChanged();
                    
                    // query the database based on the user input
                    
                    item2 = getInventoryNameFromDb(Constant.testInput(userInput.toString()), db);
                    
                    db.OpenDb();
                    
                    //discountDrop.setSelection(0, true);
                    proVal.setText(db.getInventoryID(Constant.testInput(pro.getText().toString())));
                    
                    qty.setText("");
                    
                    schemeQty.setText("0");
                    
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        selDiscountDropTV.setText("0");
                        
                    } else {
                        tradeOfferEdtTxt.setText("");
                        
                    }
                    
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        
                        for (int j = 0; j < ProVal.size(); j++) {
                            if (ProVal.size() == allQty.size()) {
                            
                                String[] val =
                                        db.getBrandAndProductTypeAndSubType(ProVal.get(j).getText().toString());
                                String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
                                result = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId,
                                        val[1], val[0], allQty.get(j).getText().toString(),
                                        ProVal.get(j).getText().toString(), val1[0], val1[1],
                                        val[2]);
                                
                                if (result.size() > 0) {
                                    for (int i = 0; i < result.size(); i++) {
                                        
                                        map2 = result.get(i);
                                        
                                        if (discountTVList.get(j).getText().toString().equalsIgnoreCase("0")) {
                                            discount1 = Integer.parseInt(map2.get("discount1")/*discountTVList.get(j).getText().toString()*/);
                                            
                                        } else {
                                        
                                        }
                                    }
                                    
                                    if (discount1 > maxDiscount) {
                                        discountDrop.setSelection(maxDiscount, true);
                                    } else {
                                        if (discount1 == 0) {
                                            discountDrop.setSelection(1, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                        } else if (discount1 == 1) {
                                            discountDrop.setSelection(2, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 2) {
                                            discountDrop.setSelection(3, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 3) {
                                            discountDrop.setSelection(4, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 4) {
                                            discountDrop.setSelection(5, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 5) {
                                            discountDrop.setSelection(6, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 6) {
                                            discountDrop.setSelection(7, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 7) {
                                            discountDrop.setSelection(8, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 8) {
                                            discountDrop.setSelection(9, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 9) {
                                            discountDrop.setSelection(10, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 10) {
                                            discountDrop.setSelection(11, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 11) {
                                            discountDrop.setSelection(12, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 12) {
                                            discountDrop.setSelection(13, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 13) {
                                            discountDrop.setSelection(14, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 14) {
                                            discountDrop.setSelection(15, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 15) {
                                            discountDrop.setSelection(16, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 16) {
                                            discountDrop.setSelection(17, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 17) {
                                            discountDrop.setSelection(18, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 18) {
                                            discountDrop.setSelection(19, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 19) {
                                            discountDrop.setSelection(20, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 20) {
                                            discountDrop.setSelection(21, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else {
                                            discountDrop.setSelection(0, true);
                                            
                                        }
                                    }
                                }
                                
                            }
                        }
                    }
                    
                    db.CloseDb();
                } else {
                    pro.setError(null);
                    counter++;
                }
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                
                if (counter2 > 1) {
                    String txt = CustomerTxt.getText().toString();
                    if (txt.equals("")) {
                        pro.setError("Please Select Customer First");
                    } else if (SelectedCustomerId.equals("0")) {
                        pro.setError("Please Valid Customer First");
                    }
                } else {
                    counter2++;
                    //pro.setError(null);
                }
                
            }
        });
    
       
        
        DBAmount.setVisibility(View.GONE);
        allDBAmount.add(DBAmount);
        
        
        pro.setInputType(InputType.TYPE_CLASS_TEXT);
        pro.setBackgroundResource(R.drawable.edittxt_bg);
        pro.setSingleLine(false);
        pro.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        //pro.setLayoutParams(params);
        
        LinearLayout.LayoutParams paramsTableComponets = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 5f);
        //LinearLayout.LayoutParams paramsTableComponets = new TableRow.LayoutParams(0, 125, 0.50f);
        // left 10 , right 10 previous
        paramsTableComponets.setMargins(10, 2, 10, 2);
        
        pro.setLayoutParams(paramsTableComponets);
        
        trProduct.addView(pro);
        
        allPro.add(pro);
        
        space = new View(getActivity());
        
        LinearLayout.LayoutParams paramsSpace = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsTableComponets.setMargins(10, 2, 10, 2);
        space.setLayoutParams(paramsSpace);
        
        qty.setHint("Qty");
        qty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        qty.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(5)});
        
        
        LinearLayout.LayoutParams paramsQtyComponets = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.7f);
        
        paramsQtyComponets.setMargins(10, 2, 10, 2);
        
        qty.setLayoutParams(paramsQtyComponets);
        
        LinearLayout.LayoutParams paramsComponets = new TableRow.LayoutParams(/*ViewGroup.LayoutParams.WRAP_CONTENT*/0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.3f);
        
        paramsComponets.setMargins(10, 2, 10, 2);
        
        if (mobEmpDiscountType.equalsIgnoreCase("1"))
            discountDrop.setLayoutParams(paramsComponets);
        else
            tradeOfferEdtTxt.setLayoutParams(paramsComponets);
        
        qty.addTextChangedListener(new TextWatcher() {
            
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                
                
                double oldQTY = 0;
                double amountTxt = 0;
                double invoiceDiscount = 0, afterDivideDiscount = 0, afterSubtractDiscount = 0;
                String tradeOffer = "";
                
                if (s.length() > 0) {
                    if (s.charAt(0) == '.') {
                        qty.setError("Wrong Format");
                        
                        formatError = false;
                        return;
                    } else formatError = true;
                }
                
                try {
                    
                    
                    db.OpenDb();
                    for (int j = 0; j < allQty.size(); j++) {
                        
                       
                        String[] val =
                                db.getBrandAndProductTypeAndSubType(ProVal.get(j).getText().toString());
                        String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
                        result = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId,
                                val[1], val[0], allQty.get(j).getText().toString(),
                                ProVal.get(j).getText().toString(), val1[0], val1[1], val[2]);
                        
                        if (mobEmpDiscountType.equalsIgnoreCase("2")) {
                            if (allTradeOfferList.get(j).getText().toString().equalsIgnoreCase("")) {
                                tradeOffer = "0";
                            } else {
                                  tradeOffer = allTradeOfferList.get(j).getText().toString();if(Integer.valueOf(tradeOffer)>Integer.valueOf(result.get(0).get("tradePrice")))
                                    {
                                        allTradeOfferList.get(j).setText(result.get(0).get("tradePrice"));
                                        Toast.makeText(AppContextProvider.getContext(), "T.O cannot be more than product price.", Toast.LENGTH_LONG).show();
                                        return;
                                    }}
                        }
                        
                        if (result.size() > 0) {
                            for (int i = 0; i < result.size(); i++) {
                                map2 = result.get(i);
                                if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                                    respons =
                                            pricingCalculation(map2,
                                                    Double.parseDouble(allQty.get(j).getText().toString()),
                                                    map2.get("tradePrice"),/*map2.get("discount1")*/
                                                    discountTVList.get(j).getText().toString().equals("Disc") ? "0" : discountTVList.get(j).getText().toString(),
                                                    map2.get("discount2"), map2.get("tradeOffer"), map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                } else {
                                    respons = pricingCalculation(map2, Double.parseDouble(allQty.get(j).getText().toString()), map2.get("tradePrice"),/*map2.get("discount1")*/map2.get("discount1"), map2.get("discount2"), tradeOffer, map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                }
                                if (map2.get("multi").equals("0") && !map2.get("scheme").equalsIgnoreCase("") && Double.parseDouble(map2.get("scheme")) != 0) {
                                    if (!map2.get("schemeVal").equalsIgnoreCase("") && Double.parseDouble(map2.get("schemeVal")) != 0)
                                        scheme = (Math.floor(Double.parseDouble(allQty.get(j).getText().toString()) / Double.parseDouble(map2.get("scheme")))) * Double.parseDouble(map2.get("schemeVal"));
                                } else
                                    scheme = 0;
                                schemeQty.setText(String.format("%.0f", scheme));
                                
                                
                                oldQTY = oldQTY + Double.parseDouble(allQty.get(j).getText().toString()) + Double.parseDouble(schemeQtyList.get(j).getText().toString());
                                
                                totalQTY = oldQTY;
                                amountTxt = amountTxt + respons;
                                
                                
                            }
                            
                            Log.e("RESULT_SIZE", result.size() + "");
                        } else {
                            
                            oldQTY = oldQTY + Double.parseDouble(allQty.get(j).getText().toString()) + Double.parseDouble(schemeQtyList.get(j).getText().toString());
                            totalQTY = oldQTY;
                        }
                        
                        //amountTxt = amountTxt + (Double.parseDouble(allDBAmount.get(j).getText().toString()) * Double.parseDouble(allQty.get(j).getText().toString()));
                    }
                } catch (NumberFormatException e) {
                    oldQTY += 0;
                    amountTxt += 0;
                    Log.e("TestQty", e.getMessage());
                    
                }
                
                db.CloseDb();
                
                //TotalQty.setText( String.valueOf( "Total qty:\n"+ oldQTY+newValue ) );
                if (SelectedCustomerTypeId.equalsIgnoreCase("1")) {
                    invoiceDiscount = db.getDiscountForSaleOrder(amountTxt);
                    if (invoiceDiscount > 0) {
                        afterDivideDiscount = invoiceDiscount / 100;
                        afterSubtractDiscount = 1 - afterDivideDiscount;
                        amountTxt = amountTxt * afterSubtractDiscount;
                    }
                }
                TotalQty.setText("Total Quantity:\n" + String.format("%.2f", totalQTY));
                TotalAmount.setText("Total Amount:\n" + String.format("%.2f", amountTxt));
                
                discountTV.setText(String.format("%.2f", invoiceDiscount) + " %");
                
                TotalAmountString = String.valueOf(String.format("%.2f", amountTxt));
                
                invoiceDiscountFinal = String.valueOf(String.format("%.2f", invoiceDiscount));
                totalAmount = amountTxt;
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
            
            }
        });
        
        
        if (mobEmpDiscountType.equalsIgnoreCase("2")) {
            tradeOfferEdtTxt.addTextChangedListener(new TextWatcher() {
                
                
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
                }
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    
                    double oldQTY = 0;
                    double amountTxt = 0;
                    double invoiceDiscount = 0, afterDivideDiscount = 0, afterSubtractDiscount = 0;
                    String tradeOffer = "";
                    try {
                        
                        
                        db.OpenDb();
                        for (int j = 0; j < allQty.size(); j++) {
                            
                          
                            String[] val =
                                    db.getBrandAndProductTypeAndSubType(ProVal.get(j).getText().toString());
                            String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
                            result = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId,
                                    val[1], val[0], allQty.get(j).getText().toString(),
                                    ProVal.get(j).getText().toString(), val1[0], val1[1], val[2]);
                            
                            if (mobEmpDiscountType.equalsIgnoreCase("2")) {
                                if (allTradeOfferList.get(j).getText().toString().equalsIgnoreCase("")) {
                                    tradeOffer = "0";
                                } else {
                                      tradeOffer = allTradeOfferList.get(j).getText().toString();if(Integer.valueOf(tradeOffer)>Integer.valueOf(result.get(0).get("tradePrice")))
                                    {
                                        allTradeOfferList.get(j).setText(result.get(0).get("tradePrice"));
                                        Toast.makeText(AppContextProvider.getContext(), "T.O cannot be more than product price.", Toast.LENGTH_LONG).show();
                                        return;
                                    }}
                            }
                            
                            if (result.size() > 0) {
                                for (int i = 0; i < result.size(); i++) {
                                    map2 = result.get(i);
                                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                                        respons = pricingCalculation(map2, Double.parseDouble(allQty.get(j).getText().toString()), map2.get("tradePrice"),/*map2.get("discount1")*/discountTVList.get(j).getText().toString(), map2.get("discount2"), map2.get("tradeOffer"), map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                    } else {
                                        respons = pricingCalculation(map2, Double.parseDouble(allQty.get(j).getText().toString()), map2.get("tradePrice"),/*map2.get("discount1")*/ map2.get("discount1"), map2.get("discount2"), tradeOffer, map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                    }
                                    if (!map2.get("scheme").equalsIgnoreCase("") && Double.parseDouble(map2.get("scheme")) != 0) {
                                        if (!map2.get("schemeVal").equalsIgnoreCase("") && Double.parseDouble(map2.get("schemeVal")) != 0)
                                            scheme = (Math.floor(Double.parseDouble(allQty.get(j).getText().toString()) / Double.parseDouble(map2.get("scheme")))) * Double.parseDouble(map2.get("schemeVal"));
                                    } else
                                        scheme = 0;
                                    schemeQty.setText(String.format("%.0f", scheme));
                                    
                                    
                                    oldQTY = oldQTY + Double.parseDouble(allQty.get(j).getText().toString()) + Double.parseDouble(schemeQtyList.get(j).getText().toString());
                                    
                                    amountTxt = amountTxt + respons;
                                    
                                }
                            }
                            
                            //amountTxt = amountTxt + (Double.parseDouble(allDBAmount.get(j).getText().toString()) * Double.parseDouble(allQty.get(j).getText().toString()));
                        }
                    } catch (NumberFormatException e) {
                        oldQTY += 0;
                        amountTxt += 0;
                        
                        
                    }
                    
                    db.CloseDb();
                    
                    //TotalQty.setText( String.valueOf( "Total qty:\n"+ oldQTY+newValue ) );
                    if (SelectedCustomerTypeId.equalsIgnoreCase("1")) {
                        invoiceDiscount = db.getDiscountForSaleOrder(amountTxt);
                        if (invoiceDiscount > 0) {
                            afterDivideDiscount = invoiceDiscount / 100;
                            afterSubtractDiscount = 1 - afterDivideDiscount;
                            amountTxt = amountTxt * afterSubtractDiscount;
                        }
                    }
                    TotalQty.setText("Total Quantity:\n" + String.format("%.2f", oldQTY));
                    TotalAmount.setText("Total Amount:\n" + String.format("%.2f", amountTxt));
                    
                    discountTV.setText(String.format("%.2f", invoiceDiscount) + " %");
                    
                    TotalAmountString = String.valueOf(String.format("%.2f", amountTxt));
                    
                    invoiceDiscountFinal = String.valueOf(String.format("%.2f", invoiceDiscount));
                    totalAmount = amountTxt;
                    
                }
                
                @Override
                public void afterTextChanged(Editable s) {
                
                }
            });
        }
        
        LinearLayout.LayoutParams paramsMinusBtnComponets = new TableRow.LayoutParams(0, 50, 1f);
        
        paramsMinusBtnComponets.setMargins(10, 2, 10, 2);
        
        minusBtn.setLayoutParams(paramsMinusBtnComponets);
        
        showDetails.setLayoutParams(paramsMinusBtnComponets);
        
        
       
        
       
        trProduct.addView(qty);
      
//		trProduct.addView(selDiscountDropTV);
       
        
        
        trProduct.setGravity(Gravity.CENTER_HORIZONTAL);
        
        //trPro2.addView(minusBtn);
        //trPro2.addView(showDetails);
        allQty.add(qty);
        schemeQtyList.add(schemeQty);
        allMinusBtn.add(minusBtn);
        
        discountDropList.add(discountDrop);
        allTradeOfferList.add(tradeOfferEdtTxt);
        allTradeOfferList2.add(tradeOfferEdtTxt2);
        db.CloseDb();
        
        DynamicLayout.addView(trProduct);
        DynamicLayout.addView(space);
        
        //DynamicLayout.addView(trPro2);
        //DynamicLayout.addView(space);
        
        Incre++;
        
        
    }
    
    private void CreateDynamicView2() {
        
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((ViewGroup.MarginLayoutParams) (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)));
        // left 20, right 20 previous
        params.setMargins(10, 10, 10, 10);
        
        LinearLayout.LayoutParams paramsTable = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 10);
        paramsTable.setMargins(2, 2, 2, 2);
        
        TableRow trProduct = new TableRow(getActivity());
        
        trProduct.setLayoutParams(paramsTable);
        trProduct.setWeightSum(10);
        
        
        db.OpenDb();
        
        proVal = new TextView(getActivity());
        proVal.setId(Incre);
        ProVal.add(proVal);
        
        DBAmount = new TextView(getActivity());
        DBAmount.setId(Incre);
        
        
        pro = new AutoCompleteTextView(getActivity());
        pro.setId(Incre);
        //pro.setTag(1);
        pro.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        
        qty = new EditText(getActivity());
        qty.setId(Incre);
        qty.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        
        
        schemeQty = new EditText(getActivity());
        schemeQty.setId(Incre);
        schemeQty.setVisibility(View.GONE);
        
        
        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
//			tradeOfferEdtTxt.setVisibility(View.GONE);
            
            
            selDiscountDropTV = new TextView(getActivity());
            selDiscountDropTV.setId(Incre);
            selDiscountDropTV.setVisibility(View.GONE);
            
            discountDrop = new Spinner(getActivity());
            discountDrop.setId(Incre);
            discountDrop.setBackgroundResource(R.drawable.edittxt_bg);
            PopulateDiscountDropDown();
            
            selDiscountDropTV2 = new TextView(getActivity());
            selDiscountDropTV2.setId(Incre);
            selDiscountDropTV2.setVisibility(View.GONE);
            
            discountDrop2 = new Spinner(getActivity());
            discountDrop2.setId(Incre);
            discountDrop2.setBackgroundResource(R.drawable.edittxt_bg);
            PopulateDiscountDropDown2();
            
        } else {
//			discountDrop.setVisibility(View.GONE);
            
            tradeOfferEdtTxt = new EditText(getActivity());
            tradeOfferEdtTxt.setId(Incre);
            tradeOfferEdtTxt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            tradeOfferEdtTxt.setHint("T.O");
            tradeOfferEdtTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
            tradeOfferEdtTxt.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(4)});
            
            
            tradeOfferEdtTxt2 = new EditText(getActivity());
            tradeOfferEdtTxt2.setId(Incre);
            tradeOfferEdtTxt2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            tradeOfferEdtTxt2.setHint("T.O");
            tradeOfferEdtTxt2.setInputType(InputType.TYPE_CLASS_NUMBER);
            tradeOfferEdtTxt2.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(4)});
            
            
        }
        
        discountTVList.add(selDiscountDropTV);
        discountTVList2.add(selDiscountDropTV2);
        
        minusBtn = new Button(getActivity());
        minusBtn.setId(Incre);
        //minusBtn.setText("Remove");
        //minusBtn.setBackgroundResource(R.drawable.edittxt_bg);
        minusBtn.setTextColor(Color.BLACK);
        minusBtn.setBackgroundResource(R.drawable.minus);
        
        showDetails = new Button(getActivity());
        showDetails.setId(Incre);
        //showDetails.setText("Details");
        showDetails.setBackgroundResource(R.mipmap.details);
        //showDetails.setTextColor(Color.BLACK);
        
        pro.setHint("Select Product");
        pro.setTextSize(15);
        //pro.setFocusable(false);
//		pro.setEms(10);
        
        // set our adapter
        myAdapter2 = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, item2);
        pro.setAdapter(myAdapter2);
        
        
        pro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
            
            }
            
            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                if (counter2 > 1) {
                    if (SelectedCustomerId.equals("0")) {
                        pro.setError("Please Valid Customer First");
                        return;
                    }
                }
                
                String s = CustomerTxt.getText().toString();
                
                if (counter != 0 && !s.equals("")) {
                    
                    int discount1 = -1;
                    int discount2 = -1;
                    
                    int maxDiscount = db.getMobUserMaxDiscount();
                    
                    
                    myAdapter2 = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, item2);
                    
                    pro.setAdapter(myAdapter2);
                    
                    myAdapter2.notifyDataSetChanged();
                    
                    // query the database based on the user input
                    
                    item2 = getInventoryNameFromDb(Constant.testInput(userInput.toString()), db);
                    
                    db.OpenDb();
                    
                    //discountDrop.setSelection(0, true);
                    proVal.setText(db.getInventoryID(Constant.testInput(pro.getText().toString())));
                    
                    qty.setText("");
                    
                    schemeQty.setText("0");
                    
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        selDiscountDropTV.setText("0");
                        selDiscountDropTV2.setText("0");
                        
                    } else {
                        tradeOfferEdtTxt.setText("");
                        
                    }
                    
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        
                        for (int j = 0; j < ProVal.size(); j++) {
                            if (ProVal.size() == allQty.size()) {
                              
                                String[] val =
                                        db.getBrandAndProductTypeAndSubType(ProVal.get(j).getText().toString());
                                String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
                                
                                result = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId,
                                        val[1], val[0], allQty.get(j).getText().toString(),
                                        ProVal.get(j).getText().toString(), val1[0], val1[1], val[2]);
                                
                                if (result.size() > 0) {
                                    
                                    for (int i = 0; i < result.size(); i++) {
                                        
                                        map2 = result.get(i);
                                        
                                        if (discountTVList.get(j).getText().toString().equalsIgnoreCase("0")) {
                                            
                                            discount1 = Integer.parseInt(map2.get("discount1")/*discountTVList.get(j).getText().toString()*/);
                                            
                                        } else {
                                        
                                        }
                                        
                                        
                                        if (discountTVList2.get(j).getText().toString().equalsIgnoreCase("0")) {
                                            
                                            discount2 = Integer.parseInt(map2.get("discount2")
                                                    /*discountTVList.get(j).getText().toString()*/);
                                            
                                        } else {
                                        
                                        }
                                    }
                                    
                                    if (discount1 > maxDiscount) {
                                        discountDrop.setSelection(maxDiscount, true);
                                    } else {
                                        if (discount1 == 0) {
                                            discountDrop.setSelection(1, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                        } else if (discount1 == 1) {
                                            discountDrop.setSelection(2, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 2) {
                                            discountDrop.setSelection(3, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 3) {
                                            discountDrop.setSelection(4, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 4) {
                                            discountDrop.setSelection(5, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 5) {
                                            discountDrop.setSelection(6, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 6) {
                                            discountDrop.setSelection(7, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 7) {
                                            discountDrop.setSelection(8, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 8) {
                                            discountDrop.setSelection(9, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 9) {
                                            discountDrop.setSelection(10, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 10) {
                                            discountDrop.setSelection(11, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 11) {
                                            discountDrop.setSelection(12, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 12) {
                                            discountDrop.setSelection(13, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 13) {
                                            discountDrop.setSelection(14, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 14) {
                                            discountDrop.setSelection(15, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 15) {
                                            discountDrop.setSelection(16, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 16) {
                                            discountDrop.setSelection(17, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 17) {
                                            discountDrop.setSelection(18, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 18) {
                                            discountDrop.setSelection(19, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 19) {
                                            discountDrop.setSelection(20, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else if (discount1 == 20) {
                                            discountDrop.setSelection(21, true);
                                            discountTVList.get(j).setText(discount1 + "");
                                            
                                        } else {
                                            discountDrop.setSelection(0, true);
                                            
                                        }
                                    }
                                    
                                    
                                    if (discount2 > maxDiscount) {
                                        discountDrop2.setSelection(maxDiscount, true);
                                    } else {
                                        if (discount2 == 0) {
                                            discountDrop2.setSelection(1, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                        } else if (discount1 == 1) {
                                            discountDrop2.setSelection(2, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 2) {
                                            discountDrop2.setSelection(3, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 3) {
                                            discountDrop2.setSelection(4, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 4) {
                                            discountDrop2.setSelection(5, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 5) {
                                            discountDrop2.setSelection(6, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 6) {
                                            discountDrop2.setSelection(7, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 7) {
                                            discountDrop2.setSelection(8, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 8) {
                                            discountDrop2.setSelection(9, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 9) {
                                            discountDrop2.setSelection(10, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 10) {
                                            discountDrop2.setSelection(11, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 11) {
                                            discountDrop2.setSelection(12, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 12) {
                                            discountDrop2.setSelection(13, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 13) {
                                            discountDrop2.setSelection(14, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 14) {
                                            discountDrop2.setSelection(15, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 15) {
                                            discountDrop2.setSelection(16, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 16) {
                                            discountDrop2.setSelection(17, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 17) {
                                            discountDrop2.setSelection(18, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 18) {
                                            discountDrop2.setSelection(19, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 19) {
                                            discountDrop2.setSelection(20, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else if (discount2 == 20) {
                                            discountDrop2.setSelection(21, true);
                                            discountTVList2.get(j).setText(discount2 + "");
                                            
                                        } else {
                                            discountDrop2.setSelection(0, true);
                                            
                                        }
                                    }
                                }
                                
                            }
                        }
                    }
                    
                    db.CloseDb();
                } else {
                    pro.setError(null);
                    counter++;
                }
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                
                if (counter2 > 1) {
                    String txt = CustomerTxt.getText().toString();
                    if (txt.equals("")) {
                        pro.setError("Please Select Customer First");
                    } else if (SelectedCustomerId.equals("0")) {
                        pro.setError("Please Valid Customer First");
                        return;
                    }
                    
                } else {
                    counter2++;
                    //pro.setError(null);
                }
                
            }
        });
        
        pro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                
                
                selectProductItem = true;
                pro.setEnabled(false);
                pro.setTextColor(Color.BLACK);
            }
        });
        
        DBAmount.setVisibility(View.GONE);
        allDBAmount.add(DBAmount);
        
        
        pro.setInputType(InputType.TYPE_CLASS_TEXT);
        pro.setBackgroundResource(R.drawable.edittxt_bg);
        pro.setSingleLine(false);
        pro.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        //pro.setLayoutParams(params);
        
        LinearLayout.LayoutParams paramsTableComponets = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4f);
        //LinearLayout.LayoutParams paramsTableComponets = new TableRow.LayoutParams(0, 125, 0.50f);
        // left 10 , right 10 previous
        paramsTableComponets.setMargins(0, 2, 10, 2);
        
        pro.setLayoutParams(paramsTableComponets);
        
        trProduct.addView(pro);
        
        allPro.add(pro);
        
        space = new View(getActivity());
        
        LinearLayout.LayoutParams paramsSpace = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsTableComponets.setMargins(10, 2, 10, 2);
        space.setLayoutParams(paramsSpace);
        
        qty.setHint("Qty");
        qty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        qty.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(5)});
        
        LinearLayout.LayoutParams paramsQtyComponets = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.3f);
        
        paramsQtyComponets.setMargins(10, 2, 10, 2);
        
        qty.setLayoutParams(paramsQtyComponets);
        
        LinearLayout.LayoutParams paramsComponets = new TableRow.LayoutParams(/*ViewGroup.LayoutParams.WRAP_CONTENT*/0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.3f);
        
        paramsComponets.setMargins(10, 2, 10, 2);
        
        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
            discountDrop.setLayoutParams(paramsComponets);
            discountDrop2.setLayoutParams(paramsComponets);
        } else {
            tradeOfferEdtTxt.setLayoutParams(paramsComponets);
            tradeOfferEdtTxt2.setLayoutParams(paramsComponets);
        }
        qty.addTextChangedListener(new TextWatcher() {
            
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                
                double oldQTY = 0;
                double amountTxt = 0;
                double invoiceDiscount = 0, afterDivideDiscount = 0, afterSubtractDiscount = 0;
                String tradeOffer = "";
                
                if (s.length() > 0) {
                    if (s.charAt(0) == '.') {
                        qty.setError("Wrong Format");
                        
                        formatError = false;
                        return;
                    } else formatError = true;
                }
                
                try {
                    
                    
                    db.OpenDb();
                    for (int j = 0; j < allQty.size(); j++) {
                        
                      
                        String[] val =
                                db.getBrandAndProductTypeAndSubType(ProVal.get(j).getText().toString());
                        String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
                        
                        result = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId,
                                val[1], val[0], allQty.get(j).getText().toString(),
                                ProVal.get(j).getText().toString(), val1[0], val1[1], val[2]);
                        
                        if (mobEmpDiscountType.equalsIgnoreCase("2")) {
                            if (allTradeOfferList.get(j).getText().toString().equalsIgnoreCase("")) {
                                tradeOffer = "0";
                            } else {
                                  tradeOffer = allTradeOfferList.get(j).getText().toString();if(Integer.valueOf(tradeOffer)>Integer.valueOf(result.get(0).get("tradePrice")))
                                    {
                                        allTradeOfferList.get(j).setText(result.get(0).get("tradePrice"));
                                        Toast.makeText(AppContextProvider.getContext(), "T.O cannot be more than product price.", Toast.LENGTH_LONG).show();
                                        return;
                                    }}
                        }
                        
                        if (result.size() > 0) {
                            for (int i = 0; i < result.size(); i++) {
                                map2 = result.get(i);
                                if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                                    respons = pricingCalculation(map2, Double.parseDouble(allQty.get(j).getText().toString()), map2.get("tradePrice"),/*map2.get("discount1")*/discountTVList.get(j).getText().toString(), discountTVList2.get(j).getText().toString(), map2.get("tradeOffer"), map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                } else {
                                    respons = pricingCalculation(map2, Double.parseDouble(allQty.get(j).getText().toString()), map2.get("tradePrice"),/*map2.get("discount1")*/map2.get("discount1"), map2.get("discount2"), tradeOffer, map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                }
                                if (map2.get("multi").equals("0") && !map2.get("scheme").equalsIgnoreCase("") && Double.parseDouble(map2.get("scheme")) != 0) {
                                    if (!map2.get("schemeVal").equalsIgnoreCase("") && Double.parseDouble(map2.get("schemeVal")) != 0)
                                        scheme = (Math.floor(Double.parseDouble(allQty.get(j).getText().toString()) / Double.parseDouble(map2.get("scheme")))) * Double.parseDouble(map2.get("schemeVal"));
                                } else
                                    scheme = 0;
                                schemeQty.setText(String.format("%.0f", scheme));
                                
                                
                                oldQTY = oldQTY + Double.parseDouble(allQty.get(j).getText().toString()) + Double.parseDouble(schemeQtyList.get(j).getText().toString());
                                
                                totalQTY = oldQTY;
                                amountTxt = amountTxt + respons;
                                
                            }
                        } else {
                            
                            oldQTY = oldQTY + Double.parseDouble(allQty.get(j).getText().toString()) + Double.parseDouble(schemeQtyList.get(j).getText().toString());
                            totalQTY = oldQTY;
                        }
                        
                        //amountTxt = amountTxt + (Double.parseDouble(allDBAmount.get(j).getText().toString()) * Double.parseDouble(allQty.get(j).getText().toString()));
                    }
                } catch (NumberFormatException e) {
                    oldQTY += 0;
                    amountTxt += 0;
                    Log.e("TestQty", e.getMessage());
                    
                }
                
                db.CloseDb();
                
                //TotalQty.setText( String.valueOf( "Total qty:\n"+ oldQTY+newValue ) );
                if (SelectedCustomerTypeId.equalsIgnoreCase("1")) {
                    invoiceDiscount = db.getDiscountForSaleOrder(amountTxt);
                    if (invoiceDiscount > 0) {
                        afterDivideDiscount = invoiceDiscount / 100;
                        afterSubtractDiscount = 1 - afterDivideDiscount;
                        amountTxt = amountTxt * afterSubtractDiscount;
                    }
                }
                TotalQty.setText("Total Quantity:\n" + String.format("%.2f", totalQTY));
                TotalAmount.setText("Total Amount:\n" + String.format("%.2f", amountTxt));
                
                discountTV.setText(String.format("%.2f", invoiceDiscount) + " %");
                
                TotalAmountString = String.valueOf(String.format("%.2f", amountTxt));
                
                invoiceDiscountFinal = String.valueOf(String.format("%.2f", invoiceDiscount));
                totalAmount = amountTxt;
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
            
            }
        });
        
        
        if (mobEmpDiscountType.equalsIgnoreCase("2")) {
            tradeOfferEdtTxt.addTextChangedListener(new TextWatcher() {
                
                
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
                }
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    
                    double oldQTY = 0;
                    double amountTxt = 0;
                    double invoiceDiscount = 0, afterDivideDiscount = 0, afterSubtractDiscount = 0;
                    String tradeOffer = "";
                    try {
                        
                        
                        db.OpenDb();
                        for (int j = 0; j < allQty.size(); j++) {
                            
                         
                            String[] val =
                                    db.getBrandAndProductTypeAndSubType(ProVal.get(j).getText().toString());
                            String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
                            
                            result = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId,
                                    val[1], val[0], allQty.get(j).getText().toString(),
                                    ProVal.get(j).getText().toString(), val1[0], val1[1], val[2]);
                            
                            if (mobEmpDiscountType.equalsIgnoreCase("2")) {
                                if (allTradeOfferList.get(j).getText().toString().equalsIgnoreCase("")) {
                                    tradeOffer = "0";
                                } else {
                                      tradeOffer = allTradeOfferList.get(j).getText().toString();if(Integer.valueOf(tradeOffer)>Integer.valueOf(result.get(0).get("tradePrice")))
                                    {
                                        allTradeOfferList.get(j).setText(result.get(0).get("tradePrice"));
                                        Toast.makeText(AppContextProvider.getContext(), "T.O cannot be more than product price.", Toast.LENGTH_LONG).show();
                                        return;
                                    }}
                            }
                            
                            if (result.size() > 0) {
                                for (int i = 0; i < result.size(); i++) {
                                    map2 = result.get(i);
                                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                                        respons = pricingCalculation(map2, Double.parseDouble(allQty.get(j).getText().toString()), map2.get("tradePrice"),/*map2.get("discount1")*/discountTVList.get(j).getText().toString(), map2.get("discount2"), map2.get("tradeOffer"), map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                    } else {
                                        respons = pricingCalculation(map2, Double.parseDouble(allQty.get(j).getText().toString()), map2.get("tradePrice"),/*map2.get("discount1")*/ map2.get("discount1"), map2.get("discount2"), tradeOffer, map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                    }
                                    if (!map2.get("scheme").equalsIgnoreCase("") && Double.parseDouble(map2.get("scheme")) != 0) {
                                        if (!map2.get("schemeVal").equalsIgnoreCase("") && Double.parseDouble(map2.get("schemeVal")) != 0)
                                            scheme = (Math.floor(Double.parseDouble(allQty.get(j).getText().toString()) / Double.parseDouble(map2.get("scheme")))) * Double.parseDouble(map2.get("schemeVal"));
                                    } else
                                        scheme = 0;
                                    schemeQty.setText(String.format("%.0f", scheme));
                                    
                                    
                                    oldQTY = oldQTY + Double.parseDouble(allQty.get(j).getText().toString()) + Double.parseDouble(schemeQtyList.get(j).getText().toString());
                                    
                                    amountTxt = amountTxt + respons;
                                    
                                }
                            }
                            
                            //amountTxt = amountTxt + (Double.parseDouble(allDBAmount.get(j).getText().toString()) * Double.parseDouble(allQty.get(j).getText().toString()));
                        }
                    } catch (NumberFormatException e) {
                        oldQTY += 0;
                        amountTxt += 0;
                        
                        
                    }
                    
                    db.CloseDb();
                    
                    //TotalQty.setText( String.valueOf( "Total qty:\n"+ oldQTY+newValue ) );
                    if (SelectedCustomerTypeId.equalsIgnoreCase("1")) {
                        invoiceDiscount = db.getDiscountForSaleOrder(amountTxt);
                        if (invoiceDiscount > 0) {
                            afterDivideDiscount = invoiceDiscount / 100;
                            afterSubtractDiscount = 1 - afterDivideDiscount;
                            amountTxt = amountTxt * afterSubtractDiscount;
                        }
                    }
                    TotalQty.setText("Total Quantity:\n" + String.format("%.2f", oldQTY));
                    TotalAmount.setText("Total Amount:\n" + String.format("%.2f", amountTxt));
                    
                    discountTV.setText(String.format("%.2f", invoiceDiscount) + " %");
                    
                    TotalAmountString = String.valueOf(String.format("%.2f", amountTxt));
                    
                    invoiceDiscountFinal = String.valueOf(String.format("%.2f", invoiceDiscount));
                    totalAmount = amountTxt;
                    
                }
                
                @Override
                public void afterTextChanged(Editable s) {
                
                }
            });
            
        }
        
        LinearLayout.LayoutParams paramsMinusBtnComponets = new TableRow.LayoutParams(0, 50, 1f);
        
        paramsMinusBtnComponets.setMargins(10, 2, 10, 2);
        
        minusBtn.setLayoutParams(paramsMinusBtnComponets);
        
        showDetails.setLayoutParams(paramsMinusBtnComponets);
        
        
        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                final TableRow parent = (TableRow) v.getParent();
                Log.d("DYNID", String.valueOf(v.getId()));
                
                DeleteValues(v.getId());
                
                DynamicLayout.removeView(parent);
                
            }
        });
        
        showDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                final TableRow parent = (TableRow) v.getParent();
                View discount = null, tradeOffer = null, disc2 = null;
                Spinner discount1 = null;
                String discountValue = "0";
                Spinner discount2 = null;
                String discountValue2 = "0";
                EditText tradeOfferEdtTxt;
                String tradeOfferValue = "0";
                View pos = parent.getChildAt(0);
                View qtyView = parent.getChildAt(1);
                if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                    discount = parent.getChildAt(2);
                    disc2 = parent.getChildAt(3);
                    discount1 = (Spinner) discount;
                    discountValue = discount1.getSelectedItem().toString();
                    discount2 = (Spinner) disc2;
                    discountValue2 = discount2.getSelectedItem().toString();
                    
                } else {
                    tradeOffer = parent.getChildAt(2);
                    tradeOfferEdtTxt = (EditText) tradeOffer;
                    tradeOfferValue = tradeOfferEdtTxt.getText().toString();
                }
                View schemeValView = parent.getChildAt(4);
                AutoCompleteTextView proTV = (AutoCompleteTextView) pos;
                EditText qtyEdtTxt = (EditText) qtyView;
                EditText schemeValHiddenEdtTxt = (EditText) schemeValView;
                String quantity = qtyEdtTxt.getText().toString();
                String schemeValHidden = schemeValHiddenEdtTxt.getText().toString();
                db.OpenDb();
                SelectedProductId = db.getInventoryID(proTV.getText().toString());
                db.CloseDb();
                if (tradeOfferValue.equalsIgnoreCase("")) {
                    tradeOfferValue = "0";
                }
                if (discountValue.equalsIgnoreCase("")) {
                    discountValue = "0";
                }
                if (discountValue2.equalsIgnoreCase("")) {
                    discountValue2 = "0";
                }
                initiatePopupWindow(v, SelectedCustomerTypeId, SelectedProductId, Incre, quantity
                        , schemeValHidden, discountValue, discountValue2, tradeOfferValue);
                
            }
        });
        trProduct.addView(qty);
        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
            trProduct.addView(discountDrop);
            trProduct.addView(discountDrop2);
        } else {
            trProduct.addView(tradeOfferEdtTxt);
            trProduct.addView(tradeOfferEdtTxt2);
        }
//		trProduct.addView(selDiscountDropTV);
        trProduct.addView(schemeQty);
        trProduct.addView(showDetails);
        trProduct.addView(minusBtn);
        
        
        trProduct.setGravity(Gravity.CENTER_HORIZONTAL);
        
        //trPro2.addView(minusBtn);
        //trPro2.addView(showDetails);
        allQty.add(qty);
        schemeQtyList.add(schemeQty);
        allMinusBtn.add(minusBtn);
        
        discountDropList.add(discountDrop);
        discountDropList2.add(discountDrop2);
        allTradeOfferList.add(tradeOfferEdtTxt);
        allTradeOfferList2.add(tradeOfferEdtTxt2);
        db.CloseDb();
        
        DynamicLayout.addView(trProduct);
        DynamicLayout.addView(space);
        
        //DynamicLayout.addView(trPro2);
        //DynamicLayout.addView(space);
        
        Incre++;
        
        
    }
    
    private void PopulateSaleTypeDropDown() {
        
        final ArrayList<String> List = new ArrayList<>();
        final ArrayList<String> ListID = new ArrayList<>();
        
        
        if (mobEmpSaleType.equalsIgnoreCase("1")) {
            List.add(0, "Cash");
            List.add(1, "Credit");
            
            ListID.add(0, "1");
            ListID.add(1, "2");
        } else if (mobEmpSaleType.equalsIgnoreCase("2")) {
            List.add(0, "Credit");
            List.add(1, "Cash");
            
            ListID.add(0, "2");
            ListID.add(1, "1");
            
        }
        
        
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(fragmentActivity, android.R.layout.simple_spinner_item, List);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        saleTypeDrop.setAdapter(dataAdapter);
        
        saleTypeDrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String label = parent.getItemAtPosition(position).toString();
                
                // Showing selected spinner item
                selectedSaletype = ListID.get(position);
                Log.d("SelectedSaleType", selectedSaletype);
                
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            
            }
        });
        
        
    }
    
    private void PopulateDiscountDropDown() {
        
        final ArrayList<String> List = new ArrayList<>();
        
        List.add(0, "Disc");
        
        db.OpenDb();
        int minDiscount = db.getMobUserMinDiscount();
        int maxDiscount = db.getMobUserMaxDiscount();
        db.CloseDb();
        int kl = 1;
        for (int i = minDiscount; i <= maxDiscount; i++) {
            
            List.add(kl, String.valueOf(i));
            kl++;
        }
        
        
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(fragmentActivity, R.layout.spinner_item, List);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        discountDrop.setAdapter(dataAdapter);
        
        discountDrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                
                
                selectedDiscount = List.get(position);
                if (!selectedDiscount.equalsIgnoreCase("Disc")) {
                    
                    if (checkDiscountId(adapterView.getId())) {
                        
                        discountTVList.get(adapterView.getId()).setText(selectedDiscount);
                        
                        
                        double oldQTY = 0;
                        double amountTxt = 0;
                        double invoiceDiscount = 0, afterDivideDiscount = 0, afterSubtractDiscount = 0;
                        try {
                            
                            db.OpenDb();
                            for (int j = 0; j < allQty.size(); j++) {
                                
                            
                                String[] val =
                                        db.getBrandAndProductTypeAndSubType(ProVal.get(j).getText().toString());
                                
                                String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
                                
                                result = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId,
                                        val[1], val[0], allQty.get(j).getText().toString(),
                                        ProVal.get(j).getText().toString(), val1[0], val1[1], val[2]);
                                
                                
                                if (result.size() > 0) {
                                    for (int i = 0; i < result.size(); i++) {
                                        map2 = result.get(i);
                                        if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                                            respons = pricingCalculation(map2, Double.parseDouble(allQty.get(j).getText().toString()), map2.get("tradePrice"),/*map2.get("discount1")*/discountTVList.get(j).getText().toString(), discountTVList2.get(j).getText().toString(), map2.get("tradeOffer"), map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                        } else {
                                            respons =
                                                    pricingCalculation(map2, Double.parseDouble(allQty.get(j).getText().toString()), map2.get("tradePrice"),/*map2.get("discount1")*/discountTVList.get(j).getText().toString(), map2.get("discount2"), map2.get("tradeOffer"), map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                        }
                                        if (!map2.get("scheme").equalsIgnoreCase("") && Double.parseDouble(map2.get("scheme")) != 0) {
                                            if (!map2.get("schemeVal").equalsIgnoreCase("") && Double.parseDouble(map2.get("schemeVal")) != 0)
                                                scheme = (Math.floor(Double.parseDouble(allQty.get(j).getText().toString()) / Double.parseDouble(map2.get("scheme")))) * Double.parseDouble(map2.get("schemeVal"));
                                        } else
                                            scheme = 0;
                                        schemeQty.setText(String.format("%.0f", scheme));
                                        
                                        
                                        oldQTY = oldQTY + Double.parseDouble(allQty.get(j).getText().toString()) + Double.parseDouble(schemeQtyList.get(j).getText().toString());
                                        
                                        amountTxt = amountTxt + respons;
                                        
                                        
                                    }
                                }
                            }
                        } catch (NumberFormatException e) {
                            oldQTY += 0;
                            amountTxt += 0;
                            
                            
                        }
                        
                        db.CloseDb();
                        
                        if (SelectedCustomerTypeId.equalsIgnoreCase("1")) {
                            invoiceDiscount = db.getDiscountForSaleOrder(amountTxt);
                            if (invoiceDiscount > 0) {
                                afterDivideDiscount = invoiceDiscount / 100;
                                afterSubtractDiscount = 1 - afterDivideDiscount;
                                amountTxt = amountTxt * afterSubtractDiscount;
                            }
                        }
                        TotalQty.setText("Total Quantity:\n" + String.format("%.2f", oldQTY));
                        TotalAmount.setText("Total Amount:\n" + String.format("%.2f", amountTxt));
                        
                        discountTV.setText(String.format("%.2f", invoiceDiscount) + " %");
                        
                        TotalAmountString = String.valueOf(String.format("%.2f", amountTxt));
                        
                        invoiceDiscountFinal = String.valueOf(String.format("%.2f", invoiceDiscount));
                        totalAmount = amountTxt;
                        ////////
                    }
                } else {
//					selDiscountDropTV.setText("0");
                    selectedDiscount = "0";
                }
                
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            
            }
        });
        
        
    }
    
    private void PopulateDiscountDropDown2() {
        
        final ArrayList<String> List = new ArrayList<>();
        
        List.add(0, "Disc2");
        
        
        int minDiscount = 0;
        int maxDiscount = 10;
        
        int kl = 1;
        for (int i = minDiscount; i <= maxDiscount; i++) {
            //int j = 1;
            List.add(kl, String.valueOf(i));
            kl++;
        }
        
        
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(fragmentActivity, R.layout.spinner_item, List);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        discountDrop2.setAdapter(dataAdapter);
        
        discountDrop2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
				
               /* if (position == 0) {
					try {
                        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                        
                    } catch (NullPointerException e) {
                        Log.e("TrackFragment", e.getMessage());
                    }
                }*/
                selectedDiscount2 = List.get(position);
                if (!selectedDiscount2.equalsIgnoreCase("Disc2")) {
                    
                    if (checkDiscountId2(adapterView.getId())) {
                        
                        //selDiscountDropTV.setText(selectedDiscount);
                        //Log.d("selectedDiscount--", selectedDiscount + "--" + Incre);
                        discountTVList2.get(adapterView.getId()).setText(selectedDiscount2);
                        
                        
                        double oldQTY = 0;
                        double amountTxt = 0;
                        double invoiceDiscount = 0, afterDivideDiscount = 0, afterSubtractDiscount = 0;
                        try {
                            
                            
                            db.OpenDb();
                            for (int j = 0; j < allQty.size(); j++) {
                                
                   
                                String[] val =
                                        db.getBrandAndProductTypeAndSubType(ProVal.get(j).getText().toString());
                                String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
                                
                                result = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId,
                                        val[1], val[0], allQty.get(j).getText().toString(),
                                        ProVal.get(j).getText().toString(), val1[0], val1[1], val[2]);
                                
                                if (result.size() > 0) {
                                    for (int i = 0; i < result.size(); i++) {
                                        map2 = result.get(i);
                                        respons = pricingCalculation(map2, Double.parseDouble(allQty.get(j).getText().toString()), map2.get("tradePrice"),/*map2.get("discount1")*/
                                                discountTVList.get(j).getText().toString(), discountTVList2.get(j).getText().toString(), map2.get("tradeOffer"), map2.get("scheme"), map2.get("tax1"), map2.get("tax2"), map2.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                                        if (!map2.get("scheme").equalsIgnoreCase("") && Double.parseDouble(map2.get("scheme")) != 0) {
                                            if (!map2.get("schemeVal").equalsIgnoreCase("") && Double.parseDouble(map2.get("schemeVal")) != 0)
                                                scheme = (Math.floor(Double.parseDouble(allQty.get(j).getText().toString()) / Double.parseDouble(map2.get("scheme")))) * Double.parseDouble(map2.get("schemeVal"));
                                        } else
                                            scheme = 0;
                                        schemeQty.setText(String.format("%.0f", scheme));
                                        
                                        
                                        oldQTY = oldQTY + Double.parseDouble(allQty.get(j).getText().toString()) + Double.parseDouble(schemeQtyList.get(j).getText().toString());
                                        
                                        amountTxt = amountTxt + respons;
                                        
                                        
                                    }
                                }
                            }
                        } catch (NumberFormatException e) {
                            oldQTY += 0;
                            amountTxt += 0;
                            
                            
                        }
                        
                        db.CloseDb();
                        
                        if (SelectedCustomerTypeId.equalsIgnoreCase("1")) {
                            invoiceDiscount = db.getDiscountForSaleOrder(amountTxt);
                            if (invoiceDiscount > 0) {
                                afterDivideDiscount = invoiceDiscount / 100;
                                afterSubtractDiscount = 1 - afterDivideDiscount;
                                amountTxt = amountTxt * afterSubtractDiscount;
                            }
                        }
                        TotalQty.setText("Total Quantity:\n" + String.format("%.2f", oldQTY));
                        TotalAmount.setText("Total Amount:\n" + String.format("%.2f", amountTxt));
                        
                        discountTV.setText(String.format("%.2f", invoiceDiscount) + " %");
                        
                        TotalAmountString = String.valueOf(String.format("%.2f", amountTxt));
                        
                        invoiceDiscountFinal = String.valueOf(String.format("%.2f", invoiceDiscount));
                        totalAmount = amountTxt;
                        ////////
                    }
                } else {
//					selDiscountDropTV.setText("0");
                    selectedDiscount2 = "0";
                }
                
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            
            }
        });
        
        
    }
    
    private boolean checkDiscountId(int id) {
        
        boolean flag = false;
        if (discountTVList.get(id).getId() == id) {
            flag = true;
        }
        
        return flag;
    }
    
    private boolean checkDiscountId2(int id) {
        
        boolean flag = false;
        if (discountTVList2.get(id).getId() == id) {
            flag = true;
        }
        
        return flag;
    }
    
    private void PopulateBulkDiscountDropDown() {
        
        final ArrayList<String> List = new ArrayList<>();
        
        
        List.add(0, "Bulk Disc");
        
        db.OpenDb();
        int minDiscount = db.getMobUserMinDiscount();
        int maxDiscount = db.getMobUserMaxDiscount();
        db.CloseDb();
        int j = 1;
        for (int i = minDiscount; i <= maxDiscount; i++) {
            //int j = 1;
            List.add(j, String.valueOf(i));
            j++;
        }
        
        
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(fragmentActivity, R.layout.spinner_item, List);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bulkDiscountDrop.setAdapter(dataAdapter);
        
        bulkDiscountDrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
              
              
              
              
              /*  if (position == 0) {
                    try {
                        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                        
                    } catch (NullPointerException e) {
                        Log.e("TrackFragment", e.getMessage());
                    }
                }*/
                selectedBulkDiscount = List.get(position);
                if (!selectedBulkDiscount.equalsIgnoreCase("Bulk Disc")) {
                    
                    for (int a = 0; a < allQty.size(); a++) {

                        /*switch (selectedBulkDiscount) {
                            case "0":
                                discountDropList.get(a).setSelection(1, true);
                                break;
                            case "1":
                                discountDropList.get(a).setSelection(2, true);
                                break;
                            case "2":
                                discountDropList.get(a).setSelection(3, true);
                                break;
                            case "3":
                                discountDropList.get(a).setSelection(4, true);
                                break;
                            case "4":
                                discountDropList.get(a).setSelection(5, true);
                                break;
                            case "5":
                                discountDropList.get(a).setSelection(6, true);
                                break;
                            case "6":
                                discountDropList.get(a).setSelection(7, true);
                                break;
                            case "7":
                                discountDropList.get(a).setSelection(8, true);
                                break;
                            case "8":
                                discountDropList.get(a).setSelection(9, true);
                                break;
                            case "9":
                                discountDropList.get(a).setSelection(10, true);
                                break;
                            case "10":
                                discountDropList.get(a).setSelection(11, true);
                                break;
                            case "11":
                                discountDropList.get(a).setSelection(12, true);
                                break;
                            case "12":
                                discountDropList.get(a).setSelection(13, true);
                                break;
                            case "13":
                                discountDropList.get(a).setSelection(14, true);
                                break;
                            case "14":
                                discountDropList.get(a).setSelection(15, true);
                                break;
                            case "15":
                                discountDropList.get(a).setSelection(16, true);
                                break;*/
                        
                        discountDropList.get(a).setSelection(Integer.parseInt(selectedBulkDiscount) + 1, true);
                        
                    }
                    
                }
                
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            
            }
        });
        
        
    }
    
    
    private void initiatePopupWindow(View v, final String customerTypeId, final String productId,
                                     int incre, String quantity, String schemeValHidden,
                                     String discountValue, String discountValue2,
                                     String tradeOfferValue) {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            
            View popUpView = getLayoutInflater().inflate(R.layout.custom_details_popup, null);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            fragmentActivity.getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displaymetrics);
            int widht = displaymetrics.widthPixels;
            int height = displaymetrics.heightPixels;
            
            if (discountValue.equals("Disc")) {
                discountValue = "0";
            }
            if (discountValue2.equals("Disc2")) {
                discountValue2 = "0";
            }
            
            popupWindow = new PopupWindow(popUpView, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            final TextView tradePrice = popUpView.findViewById(R.id.tradePriceTextView);
            final TableRow discount1Row = popUpView.findViewById(R.id.discount1Row);
            final TextView discount1 = popUpView.findViewById(R.id.Discount1TextView);
            final TextView discount2 = popUpView.findViewById(R.id.Discount2TextView);
            final TextView tradeOffer = popUpView.findViewById(R.id.tradeOfferTextView);
            final TextView scheme = popUpView.findViewById(R.id.schemeTextView);
            final TextView tax1 = popUpView.findViewById(R.id.Tax1TextView);
            final TextView tax2 = popUpView.findViewById(R.id.tax2TextView);
            final TextView tax3 = popUpView.findViewById(R.id.tax3TextView);
            
            
            final TextView subTotal = popUpView.findViewById(R.id.subTotalTextView);
            
            popupWindow.setBackgroundDrawable(new ColorDrawable());
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            
            double Quantity = Double.parseDouble(quantity.equals("") ? "0" : quantity);
            
            //discount1Row.setVisibility(View.GONE);
            
            db.OpenDb();
            
            
            String[] val =
                    db.getBrandAndProductTypeAndSubType(productId);
            
            String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
            ArrayList<HashMap<String, String>> data = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId,
                    val[1], val[0], quantity,
                    productId, val1[0], val1[1], val[2]);
            
            double schemeValue = 0;
            double result = 0;
            //double schemeValue = 0 , totalpieces2, totAmount, actualTotalAmount, tradeOfferValue, discount_1,discount_2, totalTax;
            if (data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    HashMap<String, String> map = data.get(i);
                    tradePrice.setText(map.get("tradePrice"));
                    discount1.setText(discountValue);
                    discount2.setText(discountValue2);
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        tradeOffer.setText(map.get("tradeOffer"));
                    } else {
                        tradeOffer.setText(tradeOfferValue);
                    }
                    //scheme.setText(schemeValHidden + " ( "+map.get("scheme") + " / " + map.get("schemeVal") +" )");
                    
                    schemeValue = (Math.floor(Double.parseDouble(quantity) / Double.parseDouble(map.get(
                            "scheme")))) * Double.parseDouble(map.get("schemeVal"));
                    if (map.get("schemeProduct").equalsIgnoreCase("0")) {
                        scheme.setText((schemeValue + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / Same"));
                    } else {
                        scheme.setText((schemeValue + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / " + db.getSelectedInventoryName(map.get("schemeProduct"))));
                    }
                    
                    
                    if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 0) {
                        
                        tax1.setText(map.get("tax1"));
                        tax2.setText(map.get("tax2"));
                        tax3.setText(map.get("tax3"));
                    } else if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 1) {
                        tax1.setText(map.get("tax_filer_1"));
                        tax2.setText(map.get("tax_filer_2"));
                        tax3.setText(map.get("tax_filer_3"));
                    }
                    
                    
                    if (mobEmpDiscountType.equalsIgnoreCase("1"))
                        result = pricingCalculation(map, Quantity, map.get("tradePrice"),/* map.get
                        ("discount1")*/discountValue, discountValue2, map.get("tradeOffer"), map.get("scheme"), map.get("tax1"), map.get("tax2"), map.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                    else
                        result = pricingCalculation(map, Quantity, map.get("tradePrice"),/* map.get("discount1")*/map.get("discount1"), map.get("discount2"), tradeOfferValue, map.get("scheme"), map.get("tax1"), map.get("tax2"), map.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                    
                    //double res = 10 * Double.parseDouble(map.get("tradePrice")) * Double.parseDouble(map.get("discount1")) * Double.parseDouble(map.get("discount2")) * Double.parseDouble(map.get("tradeOffer")) * Double.parseDouble( map.get("scheme")) * Double.parseDouble(map.get("tax1")) * Double.parseDouble(map.get("tax2"))* Double.parseDouble(map.get("tax3"));
                    subTotal.setText((String.format("%.2f", result)));
                    //break;
                }
            }
            
            
            db.CloseDb();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private void initiatePopupWindow(View v, final String customerTypeId, final String productId, int incre, String quantity, String schemeValHidden, String discountValue, String tradeOfferValue) {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            
            View popUpView = getLayoutInflater().inflate(R.layout.custom_details_popup, null);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            fragmentActivity.getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displaymetrics);
            int widht = displaymetrics.widthPixels;
            int height = displaymetrics.heightPixels;
            
            popupWindow = new PopupWindow(popUpView, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            final TextView tradePrice = popUpView.findViewById(R.id.tradePriceTextView);
            final TableRow discount1Row = popUpView.findViewById(R.id.discount1Row);
            final TextView discount1 = popUpView.findViewById(R.id.Discount1TextView);
            final TextView discount2 = popUpView.findViewById(R.id.Discount2TextView);
            final TextView tradeOffer = popUpView.findViewById(R.id.tradeOfferTextView);
            final TextView scheme = popUpView.findViewById(R.id.schemeTextView);
            final TextView tax1 = popUpView.findViewById(R.id.Tax1TextView);
            final TextView tax2 = popUpView.findViewById(R.id.tax2TextView);
            final TextView tax3 = popUpView.findViewById(R.id.tax3TextView);
            
            
            final TextView subTotal = popUpView.findViewById(R.id.subTotalTextView);
            
            popupWindow.setBackgroundDrawable(new ColorDrawable());
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            
            double Quantity = Double.parseDouble(quantity.equals("") ? "0" : quantity);
            
            //discount1Row.setVisibility(View.GONE);
            
            db.OpenDb();
            
            
            if (discountValue.equals("Disc")) {
                discountValue = "0";
            }
            
            
            String[] val =
                    db.getBrandAndProductTypeAndSubType(productId);
            
            String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
            
            
            double schemeValue = 0;
            ArrayList<HashMap<String, String>> data = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId,
                    val[1], val[0], quantity,
                    productId, val1[0], val1[1], val[2]);
            double result = 0;
            //double schemeValue = 0 , totalpieces2, totAmount, actualTotalAmount, tradeOfferValue, discount_1,discount_2, totalTax;
            if (data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    HashMap<String, String> map = data.get(i);
                    tradePrice.setText(map.get("tradePrice"));
                    discount1.setText(discountValue);
                    discount2.setText(map.get("discount2"));
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        tradeOffer.setText(map.get("tradeOffer"));
                    } else {
                        tradeOffer.setText(tradeOfferValue);
                    }
                    //scheme.setText(schemeValHidden + " ( "+map.get("scheme") + " / " + map.get("schemeVal") +" )");
                    
                    schemeValue = (Math.floor(Double.parseDouble(quantity) / Double.parseDouble(map.get(
                            "scheme")))) * Double.parseDouble(map.get("schemeVal"));
                    if (map.get("schemeProduct").equalsIgnoreCase("0")) {
                        scheme.setText((schemeValue + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / Same"));
                    } else {
                        scheme.setText((schemeValue + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / " + db.getSelectedInventoryName(map.get("schemeProduct"))));
                    }
                    
                    if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 0) {
                        
                        tax1.setText(map.get("tax1"));
                        tax2.setText(map.get("tax2"));
                        tax3.setText(map.get("tax3"));
                    } else if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 1) {
                        tax1.setText(map.get("tax_filer_1"));
                        tax2.setText(map.get("tax_filer_2"));
                        tax3.setText(map.get("tax_filer_3"));
                    }
                    
                    
                    if (mobEmpDiscountType.equalsIgnoreCase("1"))
                        result = pricingCalculation(map, Quantity, map.get("tradePrice"),/* map.get("discount1")*/discountValue, map.get("discount2"), map.get("tradeOffer"), map.get("scheme"), map.get("tax1"), map.get("tax2"), map.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                    else
                        result = pricingCalculation(map, Quantity, map.get("tradePrice"),/* map.get("discount1")*/map.get("discount1"), map.get("discount2"), tradeOfferValue, map.get("scheme"), map.get("tax1"), map.get("tax2"), map.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                    
                    //double res = 10 * Double.parseDouble(map.get("tradePrice")) * Double.parseDouble(map.get("discount1")) * Double.parseDouble(map.get("discount2")) * Double.parseDouble(map.get("tradeOffer")) * Double.parseDouble( map.get("scheme")) * Double.parseDouble(map.get("tax1")) * Double.parseDouble(map.get("tax2"))* Double.parseDouble(map.get("tax3"));
                    subTotal.setText((String.format("%.2f", result)));
                    //break;
                }
            }
            
            
            db.CloseDb();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private double pricingCalculation(HashMap<String, String> dataMap,
                                      double Quantity,
                                      String tradePrice,
                                      String discount1,
                                      String discount2, String tradeOffer, String scheme,
                                      String tax1, String tax2, String tax3, String taxf1,
                                      String taxf2, String taxf3) {
        
        double tp, disc1, disc2, to, schem, tax_1, tax_2, tax_3, tax_f_1, tax_f_2, tax_f_3;
        double schemeValue = 0, totalpieces2, totAmount, actualTotalAmount, tradeOfferValue,
                discount_1, discount_11, discount_2, discount_22, totalTax, afterAmmount,
                afterAmmountMrp, afterAmmountMrp1;
        tp = Double.parseDouble(tradePrice);
        disc1 = Double.parseDouble(discount1);
        disc2 = Double.parseDouble(discount2);
        if (tradeOffer.equalsIgnoreCase("")) {
            to = 0;
        } else
            to = Double.parseDouble(tradeOffer);
        schem = Double.parseDouble(scheme);
        int isTaxMrp = Integer.parseInt(dataMap.get("is_taxable"));
        int tax_mrp = Integer.parseInt(dataMap.get("tax_mrp"));
        tax_1 = Double.parseDouble(tax1);
        tax_2 = Double.parseDouble(tax2);
        tax_3 = Double.parseDouble(tax3);
        
        tax_f_1 = Double.parseDouble(taxf1);
        tax_f_2 = Double.parseDouble(taxf2);
        tax_f_3 = Double.parseDouble(taxf3);
        
        if (schem != 0) {
            schemeValue = Quantity / schem;
        }
        
        totalpieces2 = Quantity + schemeValue;
        
        
        totAmount = Quantity * tp;
        
        actualTotalAmount = totalpieces2 * tp;
        tradeOfferValue = to * Quantity;
        
        t_o_v = tradeOfferValue;
        
        totAmount = totAmount - tradeOfferValue;
        afterAmmount = totAmount;
        
        discount_1 = disc1 / 100;
        discount_1 = discount_1 * totAmount;
        totAmount = totAmount - discount_1;
        
        d_v_1 = discount_1;
        
        
        discount_2 = disc2 / 100;
        discount_2 = discount_2 * totAmount;
        totAmount = totAmount - discount_2;
        
        d_v_2 = discount_2;
        
        
        t_type = isTaxMrp;
        t_mrp_type = tax_mrp;
        
        
        if (isTaxMrp == 2) {
            
            Log.e("pricingCalculation", "isTaxMrp " + isTaxMrp);
            
            if (tax_mrp == 0) {
                
                if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 0) {
                    
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    actualTotalAmount = actualTotalAmount * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;
                    
                } else if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 1) {
                    
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    actualTotalAmount = actualTotalAmount * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;
                    
                }
                
                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            } else if (tax_mrp == 1) {
                
                if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 0) {
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    actualTotalAmount = (Quantity * Integer.parseInt(dataMap.get("mrp_price"))) * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;
                } else if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 1) {
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    actualTotalAmount = (Quantity * Integer.parseInt(dataMap.get("mrp_price"))) * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;
                }
                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            }
            
            
        } else if (isTaxMrp == 3) {
            Log.e("pricingCalculation", "isTaxMrp " + isTaxMrp);
            if (tax_mrp == 0) {
                
                
                if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 0) {
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    t_val = (totalTax * totAmount);
                    totAmount = totAmount + (totalTax * totAmount);
                } else if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 1) {
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    t_val = (totalTax * totAmount);
                    totAmount = totAmount + (totalTax * totAmount);
                }
                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            } else if (tax_mrp == 1) {
                
                
                afterAmmountMrp = Quantity * (Integer.parseInt(dataMap.get("mrp_price")) - to);
                
                discount_11 = disc1 / 100;
                discount_11 = discount_11 * afterAmmountMrp;
                afterAmmountMrp1 = afterAmmountMrp - discount_11;
                
                discount_22 = disc2 / 100;
                discount_22 = discount_22 * afterAmmountMrp1;
                afterAmmountMrp1 = afterAmmountMrp1 - discount_22;
                
         /*       actualTotalAmount = (afterAmmountMrp) - discount_1;
    
                actualTotalAmount = actualTotalAmount - discount_2;*/
                
                if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 0) {
                    
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    
                    t_val = (totalTax * afterAmmountMrp1);
                    totAmount = totAmount + ((totalTax * afterAmmountMrp1));
                    
                } else if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 1) {
                    
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    
                    t_val = (totalTax * afterAmmountMrp1);
                    totAmount = totAmount + ((totalTax * afterAmmountMrp1));
                    
                }
                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            }
            
            
        }
        Log.e("pricingCalculation", totAmount + "");
        return totAmount;
        
    }
    
    // this function is used in CustomAutoCompleteTextChangedListener.java
    public String[] getInventoryNameFromDb(String searchTerm, PosDB db) {
        
        // add items on the array dynamically
        db.OpenDb();
        List<MyObject> products;
        
        if (selected_brand_id.equals("0"))
            products = db.GetInventoryAutoCompleteData(searchTerm);
        else products = db.GetInventoryAutoCompleteData(searchTerm, selected_brand_id);
        
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
    
    public String[] getBrandNameFromDb(CharSequence searchTerm, PosDB db) {
        
        // add items on the array dynamically
        db.OpenDb();
        List<MyObject> products = db.GetBrandAutoCompleteData(searchTerm);
        
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
    
    private boolean creditLimitFunction() {
        
        boolean check;
        String creditAmount = db.getSelectedCustomerCreditAmountLimit(SelectedCustomerId);
        
        if (creditAmount != null && !creditAmount.equals("") && !creditAmount.equals("-1")) {
            creditLimit = Double.parseDouble(creditAmount);
            totalAMOUNT = (int) totalAmount;
            CREDITMINUSOLDRECEIVABLE = Math.abs(creditLimit - oldReceivable);
            creditMinusOldRec = (int) CREDITMINUSOLDRECEIVABLE;
            check = totalAMOUNT > CREDITMINUSOLDRECEIVABLE;
        } else {
            check = false;
        }
        return check;
        
    }
    
    private void SalesOrderSubmit() {
        
        
        //CREDITMINUSOLDRECEIVABLE = Double.compare(creditLimit, oldReceivable);
        
        
        if (CheckProduct()) {
            
            if (CheckProductDupplicate()) {
                
                if (CheckQuantity()) {
                    
                    if (CustomerTxt.getText().toString().trim().isEmpty() || SelectedCustomerId.equalsIgnoreCase("0") || SelectedCustomerId.trim().isEmpty()
                            || GetValues().isEmpty() || GetValues().trim().equalsIgnoreCase("0!!0!!0!!0!!!")
                            || locStatusTxt.getText().toString().equalsIgnoreCase("GPS Disabled")
                            || creditLimitFunction()
                    ) {
                        
                        if (CustomerTxt.getText().toString().trim().isEmpty()) {
                            
                            CustomerTxt.setError("Customer Required");
                            
                            
                        }
                        
                        if (SelectedCustomerId.equalsIgnoreCase("0")) {
                            
                            CustomerTxt.setError("Customer Invalid");
                            
                            
                        }
                        
                        if (SelectedCustomerId.trim().isEmpty()) {
                            
                            CustomerTxt.setError("Customer Invalid");
                            
                        }
                        
                        if (GetValues().isEmpty() || GetValues().trim().equalsIgnoreCase("0!!0!!0!!0!!!")) {
                            
                            
                            fragmentActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    
                                    Toast.makeText(AppContextProvider.getContext(), "No Order Found", Toast.LENGTH_SHORT).show();
                                    
                                }
                            });
                            
                            
                        }
                        
                        if (locStatusTxt.getText().toString().equalsIgnoreCase("GPS Disabled")) {
                            Toast.makeText(AppContextProvider.getContext(), "Enable your GPS first", Toast.LENGTH_SHORT).show();
                            
                        }
                        
                        if (creditLimitFunction()) {
                            
                            Toast.makeText(AppContextProvider.getContext(), "Credit Limit Exceeds Over " + Math.abs(creditLimit - oldReceivable), Toast.LENGTH_LONG).show();
                            
                        }
                        
                        
                    } else {
                        
                        schemeHolder = new ArrayList<>();
                        
                        ArrayList<HashMap<String, String>> hold_b_p_type = new ArrayList<>();
                        String[] b_p_type;
                        for (int i = 0; i < Incre; i++) {
                            
                            b_p_type = db.getBrandAndProductTypeAndSubType(ProVal.get(i).getText().toString());
                            
                            HashMap<String, String> mapHold = new HashMap<>();
                            
                            mapHold.put("b_id", b_p_type[1]);
                            mapHold.put("p_type_id", b_p_type[0]);
                            mapHold.put("value", "0");
                            hold_b_p_type.add(mapHold);
                        }
                        
                        
                        double holder = 0;
                        for (int i = 0; i < Incre; i++) {
                            ArrayList<HashMap<String, String>> listCheck;
                            String[] val =
                                    db.getBrandAndProductTypeAndSubType(ProVal.get(i).getText().toString());
                            
                            String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
                            
                            listCheck = db.getPricingDataforscheme(SelectedCustomerTypeId, SelectedCustomerId,
                                    val[1], val[0], allQty.get(i).getText().toString(),
                                    ProVal.get(i).getText().toString(), val1[0], val1[1], val[2]);
                            double scheme_hold_count = 0;
                            if (listCheck != null) {
                                if (listCheck.size() > 0) {
                                    for (int j = 0; j < listCheck.size(); j++) {
                                        if (listCheck.get(j).get("multi").equals("1") && !listCheck.get(j).get(
                                                "scheme").equals("0") && !listCheck.get(j).get("schemeVal").equals("0")) {
                                            
                                            hold_b_p_type.get(i).put("value",
                                                    allQty.get(i).getText().toString());
                                            
                                            for (HashMap<String, String> hashMap : hold_b_p_type) {
                                                
                                                if (hashMap.get("b_id").equals(val[1]) && hashMap.get("p_type_id").equals(val[0])) {
                                                    
                                                    scheme_hold_count =
                                                            scheme_hold_count + Double.parseDouble(hashMap.get("value"));
                                                    
                                                    
                                                }
                                                
                                            }
                                            holder = ((Math.floor(scheme_hold_count / Double.parseDouble(listCheck.get(j).get("scheme")))) * Double.parseDouble(listCheck.get(j).get("schemeVal")));
                                            Log.e("HOLder", holder + "");
                                            if (holder != 0)
                                                if ((holder % Double.parseDouble(listCheck.get(j).get(
                                                        "schemeVal")) == 0) && (hold_b_p_type.get(i).get("b_id").equals(val[1]) && hold_b_p_type.get(i).get("p_type_id").equals(val[0]))) {
                                                    
                                                    scheme_hold_count = (scheme_hold_count %
                                                            Double.parseDouble(listCheck.get(j).get(
                                                                    "scheme")));
                                                    if (scheme_hold_count < 0)
                                                        scheme_hold_count = 0;
                                                    for(int ii=0;ii<hold_b_p_type.size();ii++){
                                                        hold_b_p_type.get(ii).put("value", String.valueOf(scheme_hold_count));
    
                                                    }
                                               //     hold_b_p_type.get(i).put("value", String.valueOf(scheme_hold_count));
                                                    HashMap<String, String> listCheck2 = listCheck.get(j);
                                                    
                                                    listCheck2.put("b_id", val[1]);
                                                    listCheck2.put("p_type_id", val[0]);
                                                    listCheck2.put("p_id", "0");
                                                    listCheck2.put("l1_l1", "false");
                                                    
                                                    for (int k = 0; k < ((int) holder); k++) {
                                                        
                                                        HashMap<String, String> map =
                                                                new HashMap<>(listCheck2);
                                                        if (k == 0) {
                                                            map.put("visibility", "true");
                                                        } else {
                                                            map.put("visibility", "false");
                                                        }
                                                        
                                                        if ((k + 1) == ((int) holder)) {
                                                            map.put("divider", "true");
                                                        } else {
                                                            map.put("divider", "false");
                                                        }
                                                        
                                                        map.put("group_id", String.valueOf(i));
                                                        map.put("item_search",db.getSelectedItemName(ProVal.get(i).getText().toString()));
                                                        map.put("item_search_id",ProVal.get(i).getText().toString());
                                                        map.put("scheme_item_search_SelectedCustomerTypeId",SelectedCustomerTypeId);
                                                        map.put("scheme_item_search_SelectedCustomerId",SelectedCustomerId);
                                                        map.put("scheme_item_search_val_1",val[1]);
                                                        map.put("scheme_item_search_val_0",val[0]);
                                                        map.put("scheme_item_search_allQty",allQty.get(i).getText().toString());
                                                        map.put("scheme_item_search_ProVal", ProVal.get(i).getText().toString());
                                                        map.put("scheme_item_search_val1_0", val1[0]);
                                                        map.put("scheme_item_search_val1_1", val1[1]);
                                                        map.put("scheme_item_search_val_2", val[2]);

                                                        schemeHolder.add(map);
                                                        
                                                        
                                                    }
                                                }
                                            
                                        }
                                        
                                        
                                    }
                                }
                            }
                        }
                        
                        
                        if (schemeHolder.size() > 0) {
                            
                            showScheme(schemeHolder);
                            
                        } else {
                            AreYouSure();
                        }
                        
                    }
                }
            }
        }
        
    }
    
    private void showScheme() {
        schemeDisplayHolder = new ArrayList<>();
        
        for (int i = 0; i < Incre; i++) {
            ArrayList<HashMap<String, String>> listCheck;
            String[] val =
                    db.getBrandAndProductTypeAndSubType(ProVal.get(i).getText().toString());
            
            String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
            
            listCheck = db.getPricingDataforscheme(SelectedCustomerTypeId, SelectedCustomerId,
                    val[1], val[0], allQty.get(i).getText().toString(),
                    ProVal.get(i).getText().toString(), val1[0], val1[1], val[2]);
            
            if (listCheck != null) {
                if (listCheck.size() > 0) {
                    for (int j = 0; j < listCheck.size(); j++) {
                        if (listCheck.get(j).get("multi").equals("1") && !listCheck.get(j).get(
                                "scheme").equals("0") && !listCheck.get(j).get("schemeVal").equals("0")) {
                            
                            HashMap<String, String> listCheck2 = listCheck.get(j);
                            
                            listCheck2.put("b_id", val[1]);
                            listCheck2.put("p_type_id", val[0]);
                            
                            listCheck2.put("l1_l1", "false");
                            
                            schemeDisplayHolder.add(listCheck2);
                            
                            
                        }
                    }
                    
                }
                
            }
            
        }
        
        
        if (schemeDisplayHolder.size() > 0) {
            
            showSchemeDisplay(schemeDisplayHolder);
            
        } else {
            
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            
            builder.setTitle("Alert");
            builder.setMessage("No scheme offer available yet");
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    
                    dialog.dismiss();
                }
            });
            builder.setCancelable(true);
            
            builder.setIcon(R.drawable.error_not_found);
            
            builder.show();
            
        }
    }
    
    private boolean CheckProduct() {
        
        boolean chk = false;
        
        for (int i = 0; i < Incre; i++) {
            
            if (ProVal.get(i).getText().toString().isEmpty()) {
                
                final int finalI = i;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            allPro.get(finalI).setError("Product not found");
                        }
                    });
                }
                
                chk = false;
                break;
            } else {
                chk = true;
            }
            
        }
        
        return chk;
    }
    
    private boolean CheckQuantity() {
        
        boolean chk = true;
        
        for (int i = 0; i < Incre; i++) {
            final int finalI = i;
            
            if (!allQty.get(i).getText().toString().isEmpty()) {
                if (allQty.get(i).getText().toString().charAt(0) == '.') {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                allQty.get(finalI).setError("Number Format Error");
                            }
                        });
                        
                    }
                    
                    
                    chk = false;
                    break;
                } else if (allQty.get(i).getText().toString().isEmpty()) {
                    
                    
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                allQty.get(finalI).setError("Qty Required");
                            }
                        });
                        
                    }
                    
                    chk = false;
                    break;
                }
            } else {
                allQty.get(finalI).setError("Qty Required");
                chk = false;
                break;
            }
            
        }
        
        
        return chk;
    }
    
    private boolean CheckProductDupplicate() {
        
        boolean chk = false;
        
        for (int i = 0; i < Incre; i++) {
            
            for (int j = 0; j < Incre; j++) {
                
                if (i != j && ProVal.get(i).getText().toString().equals(ProVal.get(j).getText().toString())) {
                    
                    final int finalI = j;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                allPro.get(finalI).setError("Product Already Selected");
                            }
                        });
                    }
                    chk = false;
                    break;
                } else {
                    chk = true;
                }
            }
            
        }
        
        return chk;
    }
    
    private String GetValues() {
        
        String result = "";
        
        for (int i = 0; i < Incre; i++) {
            
            
            boolean ProLogic = !((ProVal.get(i).getText().toString().isEmpty()) || (ProVal.get(i).getText().toString().equalsIgnoreCase("0")));
            
            if (ProLogic) {
                
                result = result + ProVal.get(i).getText().toString() + "!!";
                
                result = result + allQty.get(i).getText().toString() + "!!";
                result = result + allQty.get(i).getText().toString() + "!!";
                result = result + allQty.get(i).getText().toString() + "!!!";
                
            }
        }
        return result;
    }
    
    private void insertOrderDetails() {
        
        ArrayList<HashMap<String, String>> list;
        HashMap<String, String> map = new HashMap<>();
        double res = 0.0, quantity = 0;
        String tradeOffer = "0";
        db.OpenDb();
        String tax1 = "0", tax2 = "0", tax3 = "0";
        /*  int orderId = db.getMaxOrderIdFromSaleOrder();*/
        int orderId = db.getMaxOrderIdFromSaleOrder();
        for (int i = 0; i < Incre; i++) {
            
 
            String[] val = db.getBrandAndProductTypeAndSubType(ProVal.get(i).getText().toString());
            String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId);
            list = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId,
                    val[1], val[0], allQty.get(i).getText().toString(),
                    ProVal.get(i).getText().toString(), val1[0], val1[1], val[2]);
            
            
            if (list.size() > 0) {
                for (int j = 0; j < list.size(); j++) {
                    map = list.get(j);
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        
                        
                        if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                            
                            res =
                                    pricingCalculation(map,
                                            Double.parseDouble(allQty.get(i).getText().toString()),
                                            map.get("tradePrice"),/*map.get("discount1")*/
                                            discountTVList.get(i).getText().toString(),
                                            discountTVList2.get(i).getText().toString(),
                                            map.get("tradeOffer"),
                                            map.get("scheme"),
                                            map.get("tax1"),
                                            map.get("tax2"),
                                            map.get("tax3"),
                                            map.get("tax_filer_1"),
                                            map.get("tax_filer_2"),
                                            map.get("tax_filer_3"));
                        } else {
                            res = pricingCalculation(map, Double.parseDouble(allQty.get(i).getText().toString()), map.get("tradePrice"),/*map.get("discount1")*/discountTVList.get(i).getText().toString(), map.get("discount2"), map.get("tradeOffer"), map.get("scheme"), map.get("tax1"), map.get("tax2"), map.get("tax3"), map.get("tax_filer_1"), map.get("tax_filer_2"), map.get("tax_filer_3"));
                        }
                    } else {
                        if (allTradeOfferList.get(i).getText().toString().isEmpty()) {
                            tradeOffer = "0";
                        } else {
                            tradeOffer = allTradeOfferList.get(i).getText().toString();
                        }
                        res = pricingCalculation(map, Double.parseDouble(allQty.get(i).getText().toString()), map.get("tradePrice"),/*map.get("discount1")*/map.get("discount1"), map.get("discount2"), tradeOffer, map.get("scheme"), map.get("tax1"), map.get("tax2"), map.get("tax3"), map.get("tax_filer_1"), map.get("tax_filer_2"), map.get("tax_filer_3"));
                    }
                }
                quantity = Double.parseDouble(allQty.get(i).getText().toString()) + Double.parseDouble(schemeQtyList.get(i).getText().toString());
                if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                    if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                        if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 0) {
                            
                            tax1 = map.get("tax1");
                            tax2 = map.get("tax2");
                            tax3 = map.get("tax3");
                            
                            
                        } else if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 1) {
                            tax1 = map.get("tax_filer_1");
                            tax2 = map.get("tax_filer_2");
                            tax3 = map.get("tax_filer_3");
                            
                        }
                        if (!ProVal.get(i).getText().toString().equals("0"))
                            db.createOrderDetails(String.valueOf(orderId),
                                    ProVal.get(i).getText().toString(), String.format("%.2f",
                                            quantity), String.format("%.2f", quantity), map.get(
                                            "tradePrice"),/*map.get("discount1")
                                     */discountTVList.get(i).getText().toString(),
                                    discountTVList2.get(i).getText().toString(),
                                    map.get("tradeOffer"), map.get("scheme"),
                                    schemeQtyList.get(i).getText().toString(), map.get("schemeVal"),
                                    map.get("schemeProduct"),
                                    tax1,
                                    tax2,
                                    tax3,
                                    String.format("%.2f", res),
                                    "0", t_o_v, d_v_1, d_v_2, t_type, t_mrp_type, t_val, map.get("mrp_price"));
                    } else {
                        if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 0) {
                            
                            tax1 = map.get("tax1");
                            tax2 = map.get("tax2");
                            tax3 = map.get("tax3");
                            
                            
                        } else if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 1) {
                            tax1 = map.get("tax_filer_1");
                            tax2 = map.get("tax_filer_2");
                            tax3 = map.get("tax_filer_3");
                            
                        }
                        db.createOrderDetails(String.valueOf(orderId),
                                ProVal.get(i).getText().toString(), String.format("%.2f", quantity), String.format("%.2f", quantity), map.get(
                                        "tradePrice"),/*map.get("discount1")
                                 */discountTVList.get(i).getText().toString(), map.get("discount2"), map.get("tradeOffer"), map.get("scheme"), schemeQtyList.get(i).getText().toString(), map.get("schemeVal"), map.get("schemeProduct"), tax1, tax2, tax3, String.format("%.2f", res), "0", t_o_v, d_v_1, d_v_2, t_type, t_mrp_type, t_val, map.get("mrp_price"));
                    }
                } else {
                    if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 0) {
                        
                        tax1 = map.get("tax1");
                        tax2 = map.get("tax2");
                        tax3 = map.get("tax3");
                        
                        
                    } else if (db.CheckFilerNonFilerCust(SelectedCustomerId) == 1) {
                        tax1 = map.get("tax_filer_1");
                        tax2 = map.get("tax_filer_2");
                        tax3 = map.get("tax_filer_3");
                        
                    }
                    if (!ProVal.get(i).getText().toString().equals("0"))
                        db.createOrderDetails(String.valueOf(orderId), ProVal.get(i).getText().toString(), String.format("%.2f", quantity), String.format("%.2f", quantity), map.get("tradePrice"),/*map.get("discount1")*/map.get("discount1"), map.get("discount2"), /*map.get("tradeOffer")*/tradeOffer, map.get("scheme"), schemeQtyList.get(i).getText().toString(), map.get("schemeVal"), map.get("schemeProduct"), tax1, tax2, tax3, String.format("%.2f", res), "0", t_o_v, d_v_1, d_v_2, t_type, t_mrp_type, t_val, map.get("mrp_price"));
                }
            } else {
                
                quantity = Double.parseDouble(allQty.get(i).getText().toString()) + Double.parseDouble(schemeQtyList.get(i).getText().toString());
                String disc = "0", disc2 = "0", t_o = "0";
                if (mobEmpDiscountType.equals("1")) {
                    disc = discountTVList.get(i).getText().toString();
                    if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                        disc2 = discountTVList2.get(i).getText().toString();
                    }
                } else t_o = allTradeOfferList.get(i).getText().toString();
                if (!ProVal.get(i).getText().toString().equals("0"))
                    db.createOrderDetails(
                            String.valueOf(orderId),
                            ProVal.get(i).getText().toString(),
                            String.format("%.2f", quantity),
                            String.format("%.2f", quantity),
                            "0", /*"0"*/
                            disc,
                            disc2,
                            t_o,
                            "0",
                            schemeQtyList.get(i).getText().toString(),
                            "0",
                            "0",
                            "0",
                            "0",
                            "0",
                            "0",
                            "0",
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            "0");
            }
            
            t_o_v = 0;
            d_v_1 = 0;
            d_v_2 = 0;
            t_type = 0;
            t_mrp_type = 0;
            t_val = 0;
            
        }
        Log.e("schemeHolder", schemeHolder.size() + "");
        for (int i = 0; i < schemeHolder.size(); i++) {
            
            
            db.createOrderDetails(
                    String.valueOf(orderId),
                    schemeHolder.get(i).get("p_id"),
                    "1",
                    "1",
                    "0", /*"0"*/
                    "0",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0",
                    "1",
                    t_o_v,
                    d_v_1,
                    d_v_2,
                    t_type,
                    t_mrp_type,
                    t_val,
                    "0");
        }
        
        db.CloseDb();
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
    
    private void DeleteValues(int id) {
        
        int p = ProVal.get(id).getId();
        if (ProVal.get(id).getId() == id) {
            ProVal.get(id).setText("0");
        }
        
        if (allQty.get(id).getId() == id) {
            allQty.get(id).setText("0");
        }
        
        if (schemeQtyList.get(id).getId() == id) {
            schemeQtyList.get(id).setText("0");
        }
        
        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
            if (discountTVList.get(id).getId() == id) {
                discountTVList.get(id).setText("0");
                
            }
            if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                if (discountTVList2.get(id).getId() == id) {
                    discountTVList2.get(id).setText("0");
                }
            }
            
        } else {
            if (allTradeOfferList.get(id).getId() == id) {
                allTradeOfferList.get(id).setText("0");
            }
        }
        
    }
    
    
    private void showSchemeDisplay(ArrayList<HashMap<String, String>> hashMaps) {
        
        
        showSchemeDialog.setTitle("Scheme Offer Details");
        showSchemeDialog.setContentView(R.layout.display_scheme);
        
        final ListView listView = showSchemeDialog.findViewById(R.id.listview);
        
        Button back;
        
        
        back = showSchemeDialog.findViewById(R.id.hide);
        
        schemeDisplayListAdapter = new SchemeDisplayListAdapter(getActivity(),
                hashMaps);
        
        listView.setAdapter(schemeDisplayListAdapter);
        
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                showSchemeDialog.cancel();
                
            }
        });
        
        
        showSchemeDialog.show();
        Window window = showSchemeDialog.getWindow();
        
        
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }
    
    private void showScheme(ArrayList<HashMap<String, String>> hashMaps) {
        
        
        schemeDialog.setTitle("Scheme Offer");
        schemeDialog.setContentView(R.layout.show_scheme_popup_layout);
        
        final ListView listView = schemeDialog.findViewById(R.id.scheme_list);
        
        Button next, back;
        
        next = schemeDialog.findViewById(R.id.next_scheme);
        back = schemeDialog.findViewById(R.id.back_scheme);
        
        showSchemeListAdapter = new ShowSchemeListAdapter(getActivity(),
                hashMaps);

        listView.setAdapter(showSchemeListAdapter);
        
        
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                
                position1 = showSchemeListAdapter.checkScheme();
                if (position1 == -1) {
                    
                    AreYouSure();
                    schemeDialog.dismiss();
                } else {
                    listView.smoothScrollToPosition(position1);
                    
                    
                }
            }
        });
        
        
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                schemeDialog.cancel();
                
            }
        });
        
        
        schemeDialog.show();
        Window window = schemeDialog.getWindow();
        
        
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }
    
    public void AreYouSure() {
        
        final Dialog dialog = new Dialog(fragmentActivity);
        
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
                
                schemeHolder = new ArrayList<>(showSchemeListAdapter != null ? showSchemeListAdapter.getHoldList() : (new ArrayList<HashMap<String, String>>()));
                
                Log.d("Location_Sent", "Lati: " + Latitude + "\nLongi: " + Longitude);
                
                db.OpenDb();
                int orderId = db.getMaxOrderIdFromSaleOrder();
                String savedDistributorId = db.getSavedDistributorList();
                db.createSalesOrderEntry(String.valueOf(orderId + 1), SelectedCustomerId, db.getMobEmpId(), GetValues(), Constant.testInput(Notes.getText().toString()), startTime.getText().toString(), getDateTime(), getDateTimeSHORT(), TotalAmountString, TotalAmountString, invoiceDiscountFinal, null, null, Latitude, Longitude, null, null, null, null, false, selectedSaletype, savedDistributorId, 1,"");
                db.updateCustomerLastUpdate(SelectedCustomerId, getDateTimeSHORT());
                db.CloseDb();
                insertOrderDetails();
                newLogicMethodForSaleOrder();
                
                dialog.dismiss();

                /*getActivity().finish();
                getActivity().startActivity(new Intent(getActivity(), MainActivity.class));*/
                fragmentActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, SaleOrderFinal.newInstance(), SaleOrderFinal.TAG).commit();
                
                new MainActivity().updateSync(fragmentActivity);
                
            }
        });
        
        No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (schemeHolder.size() > 0)
                    schemeDialog.show();
            }
        });
        
        
    }
    
    /**
     * Location Works Starts
     **/
    
    
    @Override
    public void onPause() {
        super.onPause();
        stopLocation();
        stopLocationUpdates();
        
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
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
        if (getActivity() != null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
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
        if (getActivity() != null) {
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
    private void getProgress() {
        
    
        
        
        getProgress(
                db.getMobEmpId(),
        
                SelectedCustomerId,
                db.getBrandID(brand_type_spinner.getSelectedItem().toString()));
        
        
    }
    
    private void getProgress(String emp_id,
                             String store, String brand) {
        
        final Loader loader = new Loader();
        loader.showDialog(getActivity());
        RestAdapter adapter = new RestAdapter.Builder()
                                      .setEndpoint(BuildConfig.BASE_URL)
                                      .build();
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        api.order_req(emp_id, store, brand, new Callback<Response>() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void success(Response response, Response response2) {
                
                
                
                
                
                BufferedReader reader;
                
                //An string to store output from the server
                String output=null;
                
                
                //Initializing buffered reader
                try {
                    reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    
                    while(output==null)
                    {
                        output = reader.readLine();
                    }
                   JSONObject   json = new JSONObject(output);
                    
                    JSONArray values_stock=   json.getJSONArray("values_stock");
                    int txtviewCount=0;
                    for(int i=0;i<values_stock.length();i++)
                    {
						
//String qty="";
                        String qty2="";
                    //    String moq="";
                    //    moq=values_stock.getJSONObject(i).getString("moq");
                  //      qty=values_stock.getJSONObject(i).getString("balanceqty");
                        qty2=values_stock.getJSONObject(i).getString("qty2");
             
//                                       if( isNumeric(moq) && (Integer.parseInt(moq)>Integer.parseInt(qty))){
//                                           CreateDynamicView3();
//                                           allPro.get(txtviewCount).setText(values_stock.getJSONObject(i).getString("sku") + " ( " + values_stock.getJSONObject(i).getString("name")+ " )");
//                                           allPro.get(txtviewCount).setEnabled(false);
//                                       allQty.get(txtviewCount).setText(String.valueOf((Integer.parseInt(moq)-Integer.parseInt(qty))));
//                                           allQty.get(txtviewCount).setEnabled(false);
//                                           txtviewCount++;}
//                                       else if(isNumeric(qty) && Integer.parseInt(qty)>0)
//                                           {
                                               CreateDynamicView3();
                                               allPro.get(txtviewCount).setText(values_stock.getJSONObject(i).getString("sku") + " ( " + values_stock.getJSONObject(i).getString("name")+ " )");
                                               allPro.get(txtviewCount).setEnabled(false);
                                               allQty.get(txtviewCount).setText(qty2);
                                               allQty.get(txtviewCount).setEnabled(false);
                                               txtviewCount++;
                                         //  }
                                    

                 
                    }
                    brand_type_spinner.setEnabled(false);
                    Submit.setVisibility(View.VISIBLE);
                    Submit.setEnabled(true);
    Search.setVisibility(View.GONE);
                    Search.setEnabled(false);
                    loader.HideLoader();
					CustomerTxt.setEnabled(false);
                } catch (JSONException e) {
                    loader.HideLoader();
                    e.printStackTrace();
                } catch (IOException e) {
                    loader.HideLoader();
                    e.printStackTrace();
                }
                catch (Exception e) {
                    loader.HideLoader();
                    e.printStackTrace();
                }
            }
            
            
            @Override
            public void failure(RetrofitError error ) {
                loader.HideLoader();
                Toast.makeText(getActivity(), "Your internet connection is not working properly.",
                        Toast.LENGTH_LONG).show();
                Log.e("GetError", error.getMessage());
            }
        });
        
        
    }
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
