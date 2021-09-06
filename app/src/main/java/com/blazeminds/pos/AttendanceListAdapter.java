package com.blazeminds.pos;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
 * Created by Blazeminds on 3/9/2018.
 */

public class AttendanceListAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    public AttendanceListAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
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
        
        final AttendanceListAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_attendance, null);
            holder = new AttendanceListAdapter.ViewHolder();
            
            holder.id = convertView.findViewById(R.id.idTxt);
            holder.clockIn = convertView.findViewById(R.id.clockInTxt);
            /*holder.txtTotalAmount = (TextView) convertView.findViewById(R.id.TotalAmountTxt);*/
            holder.clockOut = convertView.findViewById(R.id.clockOutTxt);
            //holder.notesTxt = (TextView) convertView.findViewById(R.id.notesTxt);
            
            
            convertView.setTag(holder);
            
            
        } else {
            holder = (AttendanceListAdapter.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(i);
        
        try {
            
            holder.id.setText(convertDate(map.get("clock_in").toString()));
            
            if (!map.get("clock_in").toString().equalsIgnoreCase("0"))
                holder.clockIn.setText(convertDateTimeOnly(map.get("clock_in").toString()));
            else
                holder.clockIn.setText("N/A");
            /*holder.txtTotalAmount.setText(map.get(ORDER_AMOUNT_COLUMN).toString());*/
            if (!map.get("clock_out").toString().equalsIgnoreCase("0"))
                holder.clockOut.setText(convertDateTimeOnly(map.get("clock_out").toString()));
            else
                holder.clockOut.setText("N/A");
            //holder.notesTxt.setText(map.get("exp_longitude").toString());
            
            
        } catch (NullPointerException e) {
            e.getMessage();
            System.out.println("Error Attendance List Adapter: " + e.toString());
        }
        return convertView;
    }
    
    public String convertDateTimeOnly(String dbDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        //DateFormat dfProper = new SimpleDateFormat("dd-MMM-yy\nK:mm a");
        DateFormat dfProper = new SimpleDateFormat("hh:mm a");
        Date dt = null;
        try {
            dt = df.parse(dbDate);
        } catch (ParseException e) {
            Log.e("ParseDate()", e.getMessage());
        }
        
        return dfProper.format(dt);
    }
    
    public String convertDate(String dbDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        //DateFormat dfProper = new SimpleDateFormat("dd-MMM-yy\nK:mm a");
        DateFormat dfProper = new SimpleDateFormat("d MMM yyyy");
        Date dt = null;
        try {
            dt = df.parse(dbDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return dfProper.format(dt);
    }
    
    private class ViewHolder {
        TextView id;
        TextView clockIn;
        /*TextView txtTotalAmount;*/
        TextView clockOut;
        // TextView notesTxt;
        
    }
    
    
}
