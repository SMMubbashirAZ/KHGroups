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
 * Created by Blazeminds on 4/17/2018.
 */

public class SupportHeaderAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    public SupportHeaderAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
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
        
        final SupportHeaderAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_4_support_header, null);
            holder = new SupportHeaderAdapter.ViewHolder();
            
            holder.datetimeTxt = convertView.findViewById(R.id.datetimeTxt);
            holder.titleTxt = convertView.findViewById(R.id.titleTxt);
            /*holder.txtTotalAmount = (TextView) convertView.findViewById(R.id.TotalAmountTxt);*/
            holder.statusTxt = convertView.findViewById(R.id.statusTxt);
            //holder.notesTxt = (TextView) convertView.findViewById(R.id.notesTxt);
            
            
            convertView.setTag(holder);
            
            
        } else {
            holder = (SupportHeaderAdapter.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(i);
        
        try {
            
            holder.datetimeTxt.setText(map.get("support_datetime").toString());
            holder.titleTxt.setText(map.get("support_title").toString());
            /*holder.txtTotalAmount.setText(map.get(ORDER_AMOUNT_COLUMN).toString());*/
            holder.statusTxt.setText(map.get("support_status").toString());
            //holder.notesTxt.setText(map.get("sv_longitude").toString());
            
            
        } catch (NullPointerException e) {
            System.out.println("Error Support HEADER Adapter: " + e.toString());
        }
        return convertView;
    }
    
    private class ViewHolder {
        TextView datetimeTxt;
        TextView titleTxt;
        /*TextView txtTotalAmount;*/
        TextView statusTxt;
        // TextView notesTxt;
        
    }
}