package com.blazeminds.pos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Blazeminds on 4/12/2018.
 */

public class TravelExpenseAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    public TravelExpenseAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        
        final TravelExpenseAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_4_travel_exp, null);
            holder = new TravelExpenseAdapter.ViewHolder();
            
            holder.fromTownTV = convertView.findViewById(R.id.fromTownTV);
            holder.toTownTV = convertView.findViewById(R.id.toTownTV);
            /*holder.txtTotalAmount = (TextView) convertView.findViewById(R.id.TotalAmountTxt);*/
            holder.dateTV = convertView.findViewById(R.id.dateTV);
            holder.amountTV = convertView.findViewById(R.id.amountTV);
            
            
            convertView.setTag(holder);
            
            
        } else {
            holder = (TravelExpenseAdapter.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(i);
        
        try {
            
            holder.fromTownTV.setText(map.get("t_exp_from_town_name").toString());
            holder.toTownTV.setText(map.get("t_exp_to_town_name").toString());
            holder.dateTV.setText(convertDate(map.get("t_exp_datetime").toString()));
            
            /*holder.txtTotalAmount.setText(map.get(ORDER_AMOUNT_COLUMN).toString());*/
            
            holder.amountTV.setText(map.get("t_exp_amount").toString());
            
            
        } catch (NullPointerException e) {
            System.out.println("Error Travel Expense Adapter: " + e.toString());
        }
        return convertView;
    }
    
    public String convertDate(String dbDate) {
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
    
    private class ViewHolder {
        TextView fromTownTV;
        TextView toTownTV;
        /*TextView txtTotalAmount;*/
        TextView dateTV;
        TextView amountTV;
        
    }
    
}
