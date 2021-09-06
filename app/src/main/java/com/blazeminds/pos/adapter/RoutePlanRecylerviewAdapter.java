package com.blazeminds.pos.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.BuildConfig;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.RoutePlanMapActivity;
import com.blazeminds.pos.activities.OrderBookingActivity;
import com.blazeminds.pos.fragments.SaleOrderFinal;
import com.blazeminds.pos.fragments.SaleReturnFinal;
import com.blazeminds.pos.fragments.ShopsVisit;
import com.blazeminds.pos.resources.UserSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.blazeminds.pos.Constant.customToast;
import static com.blazeminds.pos.MainActivity.FRAGMENT_TAG;
import static com.blazeminds.pos.MainActivity.trackCount;

public class RoutePlanRecylerviewAdapter extends RecyclerView.Adapter<RoutePlanRecylerviewAdapter.ViewHolder> {


    private String  empTimeIn;
    private int enableAttendanceMust,enableShopReturn,enablePlaceOrder,enableShopVisit;
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    PosDB db;
    UserSettings userSettings;

    public RoutePlanRecylerviewAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
        db = PosDB.getInstance(activity);
        userSettings = UserSettings.getInstance(activity);
        db.OpenDb();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_shop_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        final HashMap<String,String> map = list.get(position);
        try {

            //    if (map.get("last_update") != null) {
            //       if (map.get("last_update").equals(getDateTimeSHORT())) {
            if (db.getTodaysSalesOrderByShop(map.get("id")) || db.getTodaysShopVisitByShop(map.get("id"))) {
                holder.main_card.setCardBackgroundColor(activity.getResources().getColor(R.color.green_back));
            } else {
                holder.main_card.setCardBackgroundColor(activity.getResources().getColor(R.color.mdtp_white));
            }
            //    }
            db.OpenDb();
//            enableAttendanceMust = db.getAppSettingsValueByKey("attendance_must");
//            enableShopReturn = db.getAppSettingsValueByKey("en_sale_return");
//            enablePlaceOrder = db.getAppSettingsValueByKey("en_sale_order");
//            enableShopVisit = db.getAppSettingsValueByKey("en_shop_visit");
            if (db.getAppSettingsValueByKey("en_sale_return") == 0) {
                holder.shop_return.setVisibility(View.GONE);
            }else{
                holder.shop_return.setVisibility(View.VISIBLE);
            }
            if (db.getAppSettingsValueByKey("en_sale_order") == 0) {
                holder.place_order.setVisibility(View.GONE);
            }else{
                holder.place_order.setVisibility(View.VISIBLE);

            }
            if (db.getAppSettingsValueByKey("en_shop_visit") == 0) {
                holder.shop_visit.setVisibility(View.GONE);
            }else{
                holder.shop_visit.setVisibility(View.VISIBLE);

            }
            db.CloseDb();


            holder.shop_name.setText(map.get("shop_name").toString());
            holder.person_name.setText(map.get("name").toString());
            holder.route.setText(map.get("route_name").toString());

            //holder.viewBtn.setText(map.get(FOURTH_COLUMN).toString());
            holder.shop_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Toast.makeText(activity, map.get("latitude").toString() +" , " + map.get("longitude").toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, RoutePlanMapActivity.class);
                    intent.putExtra("latitude", map.get("latitude").toString());
                    intent.putExtra("longitude", map.get("longitude").toString());
                    activity.startActivity(intent);
                }
            });

            holder.shop_visit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.OpenDb();
                    enableAttendanceMust = db.getAppSettingsValueByKey("attendance_must");
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    if (enableAttendanceMust == 1){

                        if (empTimeIn.equalsIgnoreCase("1")){

                            ((FragmentActivity)activity).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame,
                                            ShopsVisit.newInstanceSwipe(map.get("id")),
                                            ShopsVisit.TAG).addToBackStack("RoutePlan").commit();
                            FRAGMENT_TAG = ShopsVisit.TAG;
                            trackCount = 0;
                        }  else {
                            //Toast.makeText(context, "Can't Create Sale Return, Time In first to Create Sale Return", Toast.LENGTH_LONG).show();
                            customToast(AppContextProvider.getContext(), "Can't Create Shop Visit, Time In first to Create Shop Visit");
                        }
                    }else{
                        ((FragmentActivity)activity).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame,
                                        ShopsVisit.newInstanceSwipe(map.get("id")),
                                        ShopsVisit.TAG).addToBackStack("RoutePlan").commit();
                        FRAGMENT_TAG = ShopsVisit.TAG;
                        trackCount = 0;
                    }
                }
            });
            holder.place_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.OpenDb();
                    enableAttendanceMust = db.getAppSettingsValueByKey("attendance_must");
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    userSettings.set(Constant.CART_LIST_PRICE,"");
                    userSettings.set(Constant.CART_LIST,"");
                    userSettings.set("start_time","");

                    userSettings.set("custId","");
                    userSettings.set("custTypeId","");
                    if (OrderBookingChildAdapter.selectedProductList != null) {
                        OrderBookingChildAdapter.selectedProductList.clear();
                    }
                    Intent intent= new Intent(activity, OrderBookingActivity.class);
                    intent.putExtra("custId",map.get("id"));
                    if (enableAttendanceMust == 1){
                        if (empTimeIn.equalsIgnoreCase("1")){
                            if (BuildConfig.FLAVOR.equals("khalil_group")) {
                                activity.startActivity(intent);
                            }else{

                                ((FragmentActivity)activity).getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.content_frame,
                                                SaleOrderFinal.newInstanceSwipe(map.get("id")),
                                                SaleOrderFinal.TAG).addToBackStack("RoutePlan").commit();
                                FRAGMENT_TAG = SaleOrderFinal.TAG;
                                trackCount = 0;
                            }

                        } else {
                            //Toast.makeText(context, "Can't Create Sale Order, Time In first to Create Order", Toast.LENGTH_LONG).show();
                            customToast(AppContextProvider.getContext(), "Can't Create Sale Order, Time In first to Create Order");
                        }
                    }else{
                        if (BuildConfig.FLAVOR.equals("khalil_group")) {
                            activity.startActivity(intent);
                        }else {
                            ((FragmentActivity)activity).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame,
                                            SaleOrderFinal.newInstanceSwipe(map.get("id")),
                                            SaleOrderFinal.TAG).addToBackStack("RoutePlan").commit();
                            FRAGMENT_TAG = SaleOrderFinal.TAG;
                            trackCount = 0;
                        }
//                            activity.startActivity(intent);

                    }

                }
            });
            holder.shop_return.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.OpenDb();
                    enableAttendanceMust = db.getAppSettingsValueByKey("attendance_must");
                    empTimeIn = db.getMobEmpTimeIn();
                    db.CloseDb();
                    if (enableAttendanceMust == 1){
                        if (empTimeIn.equalsIgnoreCase("1")){
                            ((FragmentActivity)activity).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame,
                                            SaleReturnFinal.newInstanceSwipe(map.get("id")),
                                            SaleReturnFinal.TAG).addToBackStack("RoutePlan").commit();
                            FRAGMENT_TAG = SaleReturnFinal.TAG;
                            trackCount = 0;
                        } else {
                            //Toast.makeText(context, "Can't Create Sale Order, Time In first to Create Order", Toast.LENGTH_LONG).show();
                            customToast(AppContextProvider.getContext(), "Can't Create Sale Order, Time In first to Create Order");
                        }
                    }else{
                        ((FragmentActivity)activity).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame,
                                        SaleReturnFinal.newInstanceSwipe(map.get("id")),
                                        SaleReturnFinal.TAG).addToBackStack("RoutePlan").commit();
                        FRAGMENT_TAG = SaleReturnFinal.TAG;
                        trackCount = 0;
                    }

                }
            });
//            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    ml.TriggerOnItemClick(position);
//                }
//            });
        } catch (NullPointerException e) {
            System.out.println("Error Adapter Route Plan : " + e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {



        ImageView shop_visit,place_order,shop_view,shop_return;
        TextView shop_name,person_name,route;
        LinearLayout parentLayout;
        CardView main_card;
        ViewHolder(View itemView) {
            super(itemView);
            shop_name = itemView.findViewById(R.id.route_shop_name_tv);
            person_name = itemView.findViewById(R.id.route_person_name_tv);
            route = itemView.findViewById(R.id.route_tv);
            shop_visit = itemView.findViewById(R.id.route_shop_visit_iv);
            place_order = itemView.findViewById(R.id.route_shop_place_order_iv);
            shop_return = itemView.findViewById(R.id.route_shop_return_iv);
            shop_view = itemView.findViewById(R.id.route_shop_view_iv);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            main_card = itemView.findViewById(R.id.my_route_cv);


        }
    }
}
