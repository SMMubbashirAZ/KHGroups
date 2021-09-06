package com.blazeminds.pos;

/*
  Created by Saad Kalim on 06-Apr-15.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blazeminds.pos.Constant.ORDER_CUST_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_DATE_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_NEW_AMOUNT_COLUMN;


public class SalesOrderListViewHEADERAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    public SalesOrderListViewHEADERAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
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
        final ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item4salesorder_header, null);
            holder = new ViewHolder();
            
            holder.txtDate = convertView.findViewById(R.id.DateTxt);
            holder.txtCustomerName = convertView.findViewById(R.id.CustomerNameTxt);
            /*holder.txtTotalAmount = (TextView) convertView.findViewById(R.id.TotalAmountTxt);*/
            holder.txtTotalNewAmount = convertView.findViewById(R.id.TotalNewAmountTxt);
            
            
            convertView.setTag(holder);
            
            
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(position);
        
        try {
            
            holder.txtDate.setText(map.get(ORDER_DATE_COLUMN).toString());
            holder.txtCustomerName.setText(map.get(ORDER_CUST_COLUMN).toString());
            /*holder.txtTotalAmount.setText(map.get(ORDER_AMOUNT_COLUMN).toString());*/
            holder.txtTotalNewAmount.setText(map.get(ORDER_NEW_AMOUNT_COLUMN).toString());
            
            
        } catch (NullPointerException e) {
            System.out.println("Error Sales Order ListHEADERAdapter: " + e.toString());
        }
        return convertView;
    }
    
    private class ViewHolder {
        TextView txtDate;
        TextView txtCustomerName;
        /*TextView txtTotalAmount;*/
        TextView txtTotalNewAmount;
        
    }
    
}