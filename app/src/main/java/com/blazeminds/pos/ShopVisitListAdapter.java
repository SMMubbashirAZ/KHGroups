package com.blazeminds.pos;

import android.app.Activity;
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

public class ShopVisitListAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    public ArrayList<String> list1;
    Activity activity;
    
    
    public ShopVisitListAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = activity;
        
        
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
        final ShopVisitListAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_4_shopvisit, null);
            holder = new ShopVisitListAdapter.ViewHolder();
            //  holder.txtCode = (TextView) convertView.findViewById(R.id.Code);
            //  holder.txtPic = (ImageView) convertView.findViewById(R.id.Pic);
            holder.dateTV = convertView.findViewById(R.id.dateTxt);
            holder.shopTV = convertView.findViewById(R.id.shopTxt);
            holder.reasonTV = convertView.findViewById(R.id.reasonTxt);
            //holder.notesTV = (TextView) convertView.findViewById(R.id.notesTxt);
            // holder.txtFourth = (TextView) convertView.findViewById(R.id.DBBrandNameValues);
            
            
            convertView.setTag(holder);
        } else {
            holder = (ShopVisitListAdapter.ViewHolder) convertView.getTag();
        }
        
        final HashMap map = list.get(i);
        
        
        try {
            
            
            holder.dateTV.setText(convertDate(map.get("sv_datetime").toString()));
            holder.shopTV.setText(map.get("sv_customerName").toString());
            holder.reasonTV.setText(map.get("sv_reasonName").toString());
            
            //holder.notesTV.setText(map.get("sv_remarks").toString());
            //  holder.txtFourth.setText(map.get(FOURTH_COLUMN).toString());
            
            
        } catch (Exception e) {
            System.out.println("Error IMG: " + e.toString());
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
        //TextView txtCode;
        //ImageView txtPic;
        TextView dateTV;
        TextView shopTV;
        TextView reasonTV;
        TextView notesTV;
        //TextView txtFourth;
        
    }
}
