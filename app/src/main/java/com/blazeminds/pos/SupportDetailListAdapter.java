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
 * Created by Blazeminds on 4/18/2018.
 */

public class SupportDetailListAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    
    public SupportDetailListAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
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
        final SupportDetailListAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_4_support_detail, null);
            holder = new SupportDetailListAdapter.ViewHolder();
            holder.dateTV = convertView.findViewById(R.id.dateTxt);
            holder.messageTV = convertView.findViewById(R.id.messageTxt);
            holder.adminTV = convertView.findViewById(R.id.adminTxt);
            
            
            convertView.setTag(holder);
        } else {
            holder = (SupportDetailListAdapter.ViewHolder) convertView.getTag();
        }
        
        final HashMap map = list.get(i);
        
        
        try {
            
            holder.dateTV.setText(convertDate(map.get("support_detail_datetime").toString()));
            holder.messageTV.setText(map.get("support_detail_message").toString());
            if (map.get("support_detail_person").toString().equals(map.get("user_id").toString())) {
                holder.adminTV.setText(map.get("user").toString());
            } else {
                holder.adminTV.setText("Admin");
                
            }
            
        } catch (Exception e) {
            System.out.println("Error Support Detail List Adapter: " + e.toString());
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
        TextView messageTV;
        TextView adminTV;
        TextView notesTV;
        
    }
}