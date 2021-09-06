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
 * Created by Blazeminds on 3/8/2018.
 */

public class MerchandizingListHeaderAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    public MerchandizingListHeaderAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
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
        
        final MerchandizingListHeaderAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_4_merchandizing_header, null);
            holder = new MerchandizingListHeaderAdapter.ViewHolder();
            
            holder.dateTV = convertView.findViewById(R.id.dateTxt);
            holder.shopTV = convertView.findViewById(R.id.shopTxt);
            /*holder.txtTotalAmount = (TextView) convertView.findViewById(R.id.TotalAmountTxt);*/
            holder.brandTV = convertView.findViewById(R.id.brandTxt);
            //holder.productTV = (TextView) convertView.findViewById(R.id.productTxt);
            
            
            convertView.setTag(holder);
            
            
        } else {
            holder = (MerchandizingListHeaderAdapter.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(i);
        
        try {
            
            holder.dateTV.setText(map.get("m_datetime").toString());
            holder.shopTV.setText(map.get("m_shopId").toString());
            /*holder.txtTotalAmount.setText(map.get(ORDER_AMOUNT_COLUMN).toString());*/
            holder.brandTV.setText(map.get("m_campaignId").toString());
            //holder.productTV.setText(map.get("m_productId").toString());
            
            
        } catch (NullPointerException e) {
            System.out.println("Error Payment Recieving ListHEADERAdapter: " + e.toString());
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
        TextView dateTV;
        TextView shopTV;
        /*TextView txtTotalAmount;*/
        TextView brandTV;
        TextView productTV;
        
    }
}
