package com.blazeminds.pos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blazeminds.pos.adapter.MyListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Blazeminds on 11/3/2018.
 */

public class RoutePlanAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    MyListener ml;
    PosDB db ;
    public RoutePlanAdapter(Context activity, ArrayList<HashMap<String, String>> list,MyListener ml) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
        this.ml = ml;
        db = PosDB.getInstance(activity);
        db.OpenDb();
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }
    
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        
        // TODO Auto-generated method stub
        RoutePlanAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_4_route_plan, null);
            holder = new RoutePlanAdapter.ViewHolder();
            
            holder.parentLayout = convertView.findViewById(R.id.parentLayout);
            holder.shopNameTV = convertView.findViewById(R.id.shopNameTV);
            holder.personTV = convertView.findViewById(R.id.personTV);
            holder.routeTV = convertView.findViewById(R.id.routeTV);
            holder.viewBtn = convertView.findViewById(R.id.viewBtn);
            
            convertView.setTag(holder);
        } else {
            holder = (RoutePlanAdapter.ViewHolder) convertView.getTag();
        }
        
        final HashMap<String,String> map = list.get(position);
        
        try {
            
        //    if (map.get("last_update") != null) {
         //       if (map.get("last_update").equals(getDateTimeSHORT())) {
                    if (db.getTodaysSalesOrderByShop(map.get("id")) || db.getTodaysShopVisitByShop(map.get("id"))) {
                    holder.parentLayout.setBackgroundColor(activity.getResources().getColor(R.color.green_back));
                } else {
                    holder.parentLayout.setBackgroundColor(activity.getResources().getColor(R.color.mdtp_white));
                }
        //    }
            
            holder.shopNameTV.setText(map.get("shop_name").toString());
            holder.personTV.setText(map.get("name").toString());
            holder.routeTV.setText(map.get("route_name").toString());
            
            //holder.viewBtn.setText(map.get(FOURTH_COLUMN).toString());
            holder.viewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    
                    //Toast.makeText(activity, map.get("latitude").toString() +" , " + map.get("longitude").toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, RoutePlanMapActivity.class);
                    intent.putExtra("latitude", map.get("latitude").toString());
                    intent.putExtra("longitude", map.get("longitude").toString());
                    activity.startActivity(intent);
                }
            });
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
    
                    ml.TriggerOnItemClick(position);
                }
            });
        } catch (NullPointerException e) {
            System.out.println("Error Adapter Route Plan : " + e.toString());
        }
        return convertView;
    }
    
    private String getDateTimeSHORT() {
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        DateFormat df = DateFormat.getDateTimeInstance();
        
        //SelectedDate = dateFormat.format(new Date());
        
        return dateFormat.format(new Date());
        //return df.format(new Date());
    }
    
    private class ViewHolder {
        TextView shopNameTV;
        TextView personTV;
        TextView routeTV;
        Button viewBtn;
        LinearLayout parentLayout;
        
    }
    
}
