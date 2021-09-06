package com.blazeminds.pos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blazeminds.pos.Constant.FIRST_COLUMN;
import static com.blazeminds.pos.Constant.FOURTH_COLUMN;
import static com.blazeminds.pos.Constant.SECOND_COLUMN;
import static com.blazeminds.pos.Constant.THIRD_COLUMN;

/**
 * Created by Blazeminds on 11/3/2018.
 */

public class RoutePlanHeaderAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    public RoutePlanHeaderAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        
        // TODO Auto-generated method stub
        RoutePlanHeaderAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_4_route_plan_header, null);
            holder = new RoutePlanHeaderAdapter.ViewHolder();
            holder.shopNameTV = convertView.findViewById(R.id.shopNameTV);
            holder.personTV = convertView.findViewById(R.id.personTV);
            holder.routeTV = convertView.findViewById(R.id.routeTV);
            holder.viewTV = convertView.findViewById(R.id.viewTV);
            
            convertView.setTag(holder);
        } else {
            holder = (RoutePlanHeaderAdapter.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(position);
        
        try {
            //holder.txtCode.setText(map.get(CODE_COLUMN).toString());
            holder.shopNameTV.setText(map.get(FIRST_COLUMN).toString());
            holder.personTV.setText(map.get(SECOND_COLUMN).toString());
            holder.routeTV.setText(map.get(THIRD_COLUMN).toString());
            holder.viewTV.setText(map.get(FOURTH_COLUMN).toString());
            
        } catch (NullPointerException e) {
            System.out.println("Error Header Adapter Route Plan : " + e.toString());
        }
        return convertView;
    }
    
    private class ViewHolder {
        TextView shopNameTV;
        TextView personTV;
        TextView routeTV;
        TextView viewTV;
        
    }
    
}
