package com.blazeminds.pos.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.pos.Constant;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.adapter.OrderBookingChildAdapter;
import com.blazeminds.pos.adapter.OrderBookingMainAdapter;
import com.blazeminds.pos.autocomplete_resource.MyObject;
import com.blazeminds.pos.model.BrandModel;
import com.blazeminds.pos.model.OnvalueChanged;
import com.blazeminds.pos.resources.UserSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OrderBookingActivity extends AppCompatActivity implements OnvalueChanged {

    private RecyclerView rv_main_order;
    private EditText edt_search;
    public ImageView iv_order_booking_cart;
    public TextView tv_order_booking_no_item,tv_order_booking_total_cost;
    public LinearLayout ll_order_booking_unit;
    PosDB db;
    private OrderBookingMainAdapter brandAdapter;
    ArrayList<BrandModel> brandDataList = new ArrayList<>();
    private  String CustId="",CustTypeId="";
    public static double total_price=0;
    private UserSettings userSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_booking);
        initView();
    }
    private void initView(){
        rv_main_order= findViewById(R.id.rv_order_booking_main);
        edt_search= findViewById(R.id.edt_search);
        iv_order_booking_cart= findViewById(R.id.iv_order_booking_cart);
        tv_order_booking_no_item= findViewById(R.id.tv_order_booking_no_item);
        tv_order_booking_total_cost= findViewById(R.id.tv_order_booking_total_cost);
        ll_order_booking_unit= findViewById(R.id.ll_order_booking_unit);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
        userSettings = UserSettings.getInstance(OrderBookingActivity.this);
        userSettings.set("start_time",getDateTime());
        Intent intent = getIntent();
        CustId= intent.getStringExtra("custId");
        db=PosDB.getInstance(OrderBookingActivity.this);
        db.OpenDb();
        CustTypeId= db.getCustomerTypeIDById(CustId);
        userSettings.set("custId",CustId);
        userSettings.set("custTypeId",CustTypeId);
        final ArrayList<HashMap<String, String>> brandsList = db.getAllBrandsForOrderBooking();

        brandDataList.clear();

        for (HashMap<String, String> map : brandsList) {
            brandDataList.add(new BrandModel(map.get("id"),map.get("bname"),false));

        }

        brandAdapter = new OrderBookingMainAdapter(brandDataList, this,db,CustId,CustTypeId,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv_main_order.setLayoutManager(mLayoutManager);
        rv_main_order.setAdapter(brandAdapter);
        rv_main_order.getRecycledViewPool().setMaxRecycledViews(0, 0);

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterBrands(editable.toString(), brandDataList);
            }
        });
        iv_order_booking_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (brandAdapter.getMainList().size() == 0) {
                    Toast.makeText(OrderBookingActivity.this, "Please Select some product to proceed", Toast.LENGTH_LONG).show();
                }else{
                    JSONObject cartObject = new JSONObject();
                    JSONArray mainArray = new JSONArray();
                    for (int i = 0; i < brandAdapter.getMainList().size(); i++) {
                        JSONObject jsonObject =new JSONObject();
                        try {
//                            ,tax_mrp,mrp_price,tradeOffer,schemeValue,schemeTxt,tax1,tax2,tax3,taxf1,taxf2,taxf3;
                            jsonObject.put("b_id",db.getBrandIDFromProductId(brandAdapter.getMainList().get(i).getProd_id()));
                            jsonObject.put("p_id",brandAdapter.getMainList().get(i).getProd_id());
                            jsonObject.put("p_name",brandAdapter.getMainList().get(i).getProd_name());
                            jsonObject.put("p_price",brandAdapter.getMainList().get(i).getProd_price());
                            jsonObject.put("p_qty",brandAdapter.getMainList().get(i).getProd_qty());
                            jsonObject.put("p_discount",brandAdapter.getMainList().get(i).getDiscount());
                            jsonObject.put("TradePrice",brandAdapter.getMainList().get(i).getTradePrice());
                            jsonObject.put("isTaxMrp",brandAdapter.getMainList().get(i).getIsTaxMrp());
                            jsonObject.put("is_taxable",brandAdapter.getMainList().get(i).getIs_taxable());
                            jsonObject.put("tax_mrp",brandAdapter.getMainList().get(i).getTax_mrp()==null?"0":brandAdapter.getMainList().get(i).getTax_mrp());
                            jsonObject.put("mrp_price",brandAdapter.getMainList().get(i).getMrp_price()==null?"0":brandAdapter.getMainList().get(i).getMrp_price());
                            jsonObject.put("tradeOffer",brandAdapter.getMainList().get(i).getTradeOffer());
                            jsonObject.put("schemeValue",brandAdapter.getMainList().get(i).getSchemeValue());
                            jsonObject.put("schemeTxt",brandAdapter.getMainList().get(i).getSchemeTxt());
                            jsonObject.put("tax1",brandAdapter.getMainList().get(i).getTax1());
                            jsonObject.put("tax2",brandAdapter.getMainList().get(i).getTax2());
                            jsonObject.put("tax3",brandAdapter.getMainList().get(i).getTax3());
                            jsonObject.put("taxf1",brandAdapter.getMainList().get(i).getTaxf1());
                            jsonObject.put("taxf2",brandAdapter.getMainList().get(i).getTaxf2());
                            jsonObject.put("taxf3",brandAdapter.getMainList().get(i).getTaxf3());


                            mainArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        cartObject.put("CartObj",mainArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    userSettings.set(Constant.CART_LIST,cartObject.toString());
                    userSettings.set(Constant.CART_LIST_PRICE,Double.parseDouble(tv_order_booking_total_cost.getText().toString()));
                    startActivity(new Intent(OrderBookingActivity.this,CashSummaryActivity.class));
                }

            }
        });
        //db.getSelectedCustomerSearchName(ShopIDForSO)

//        brandDataList.add(new BrandModel())

    }

    private static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        DateFormat df = DateFormat.getDateTimeInstance();

        //SelectedDate = dateFormat.format(new Date());

        return dateFormat.format(new Date());
        //return df.format(new Date());
    }
    private void filterBrands(String text, ArrayList<BrandModel> brandDataList) {
        //new array list that will hold the filtered data
        ArrayList<BrandModel> filterdNames = new ArrayList<>();
        //looping through existing elements
        for (BrandModel s : brandDataList) {
            //if the existing elements contains the search input
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        brandAdapter.filterList(filterdNames);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (OrderBookingChildAdapter.popupWindowOrderBooking != null) {
            if (OrderBookingChildAdapter.popupWindowOrderBooking.isShowing()) {
                OrderBookingChildAdapter.popupWindowOrderBooking.dismiss();
                OrderBookingChildAdapter.popupWindowOrderBooking = null;
            }
        }
        userSettings.set(Constant.CART_LIST,"");
        userSettings.set(Constant.CART_LIST_PRICE,"");

//        db.DeleteFromTempCart();
    }

    @Override
    public void OnChangeValue() {
        if (total_price == 0) {
            Log.d("TAG", "OnChangeValue: "+total_price);
            ll_order_booking_unit.setVisibility(View.GONE);
            tv_order_booking_no_item.setVisibility(View.VISIBLE);

        }else{
            Log.d("TAG", "OnChangeValue: "+total_price);
            tv_order_booking_total_cost.setText(String.valueOf(total_price));
            ll_order_booking_unit.setVisibility(View.VISIBLE);
            tv_order_booking_no_item.setVisibility(View.GONE);

        }

    }

    @Override
    public void setCartValue(double price) {
        ll_order_booking_unit.setVisibility(View.VISIBLE);
        tv_order_booking_no_item.setVisibility(View.GONE);
        tv_order_booking_total_cost.setText(String.valueOf(price));
    }

    @Override
    public double getCartValue() {
        return  Double.parseDouble( tv_order_booking_total_cost.getText().toString() );
    }
}