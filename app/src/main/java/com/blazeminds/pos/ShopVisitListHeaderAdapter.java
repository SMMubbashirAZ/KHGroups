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
 * Created by Blazeminds on 3/8/2018.
 */

public class ShopVisitListHeaderAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    public ShopVisitListHeaderAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
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
        
        final ShopVisitListHeaderAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_4_shop_visit_header, null);
            holder = new ShopVisitListHeaderAdapter.ViewHolder();
            
            holder.datetimeTxt = convertView.findViewById(R.id.datetimeTxt);
            holder.shopTxt = convertView.findViewById(R.id.shopTxt);
            /*holder.txtTotalAmount = (TextView) convertView.findViewById(R.id.TotalAmountTxt);*/
            holder.reasonTxt = convertView.findViewById(R.id.reasonTxt);
            //holder.notesTxt = (TextView) convertView.findViewById(R.id.notesTxt);
            
            
            convertView.setTag(holder);
            
            
        } else {
            holder = (ShopVisitListHeaderAdapter.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(i);
        
        try {
            
            holder.datetimeTxt.setText(map.get("sv_datetime").toString());
            holder.shopTxt.setText(map.get("sv_shopName").toString());
            /*holder.txtTotalAmount.setText(map.get(ORDER_AMOUNT_COLUMN).toString());*/
            holder.reasonTxt.setText(map.get("sv_reasonName").toString());
            //holder.notesTxt.setText(map.get("sv_longitude").toString());
            
            
        } catch (NullPointerException e) {
            System.out.println("Error Payment Recieving ListHEADERAdapter: " + e.toString());
        }
        return convertView;
    }
    
    private class ViewHolder {
        TextView datetimeTxt;
        TextView shopTxt;
        /*TextView txtTotalAmount;*/
        TextView reasonTxt;
        // TextView notesTxt;
        
    }
}