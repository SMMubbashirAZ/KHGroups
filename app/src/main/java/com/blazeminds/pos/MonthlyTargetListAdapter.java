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

/**
 * Created by Blazeminds on 4/12/2018.
 */

public class MonthlyTargetListAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    private double target, sold, achieved;
    
    public MonthlyTargetListAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
    }
    
    @Override
    public int getCount() {
        return list.size();
    }
    
    @Override
    public Object getItem(int i) {
        return list.get(i);
    }
    
    @Override
    public long getItemId(int i) {
        return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // TODO Auto-generated method stub
        MonthlyTargetListAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_4_target, null);
            holder = new MonthlyTargetListAdapter.ViewHolder();
            holder.txtFirst = convertView.findViewById(R.id.first);
            holder.txtSecond = convertView.findViewById(R.id.second);
            holder.txtThird = convertView.findViewById(R.id.third);
            holder.txtFourth = convertView.findViewById(R.id.fourth);

/*
            txtCode = (TextView) convertView.findViewById(R.id.Code);
            txtFirst = (TextView) convertView.findViewById(R.id.DBCustName);
            txtSecond = (TextView) convertView.findViewById(R.id.DBComp);
*/
            
            
            convertView.setTag(holder);
        } else {
            holder = (MonthlyTargetListAdapter.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(position);
        
        try {
           /*
            holder.txtFirst.setText(map.get(FIRST_COLUMN).toString());
            holder.txtSecond.setText(map.get(SECOND_COLUMN).toString());
            //holder.txtSecond.setText(map.get("search").toString());
            if (map.get(THIRD_COLUMN) != null) {
                holder.txtThird.setText(map.get(THIRD_COLUMN).toString());
                sold = Double.parseDouble(map.get(THIRD_COLUMN).toString());
                target = Double.parseDouble(map.get(SECOND_COLUMN).toString());
                achieved = (sold / target) * 100;
                holder.txtFourth.setText(String.format("%.1f", achieved)  +" %");
            }
            else {
                achieved = 0;
                holder.txtThird.setText("0");
                holder.txtFourth.setText(String.format("%.1f", achieved)  +" %");

            }
            */
            
            holder.txtFirst.setText(map.get("item").toString());
            holder.txtSecond.setText(map.get("target").toString());
            holder.txtThird.setText(map.get("sold").toString());
            holder.txtFourth.setText(map.get("achieved").toString());
            
        } catch (NullPointerException e) {
            System.out.println("Error Monthly Target List Adapter: " + e.toString());
        } catch (NumberFormatException ex) {
            System.out.println("Error Monthly Target List Parsing: " + ex.toString());
            
        }
        return convertView;
        
    }
    
    private class ViewHolder {
        TextView txtFirst;
        TextView txtSecond;
        TextView txtThird;
        TextView txtFourth;
        
    }
}
