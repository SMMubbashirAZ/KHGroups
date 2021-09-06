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

public class AttendanceListHeaderAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    public AttendanceListHeaderAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
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
        
        final AttendanceListHeaderAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_attendance_header, null);
            holder = new AttendanceListHeaderAdapter.ViewHolder();
            
            holder.id = convertView.findViewById(R.id.idTxt);
            holder.clockIn = convertView.findViewById(R.id.clockInTxt);
            /*holder.txtTotalAmount = (TextView) convertView.findViewById(R.id.TotalAmountTxt);*/
            holder.clockOut = convertView.findViewById(R.id.clockOutTxt);
            //holder.notesTxt = (TextView) convertView.findViewById(R.id.notesTxt);
            
            
            convertView.setTag(holder);
            
            
        } else {
            holder = (AttendanceListHeaderAdapter.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(i);
        
        try {
            
            holder.id.setText(map.get("id").toString());
            holder.clockIn.setText(map.get("clock_in").toString());
            /*holder.txtTotalAmount.setText(map.get(ORDER_AMOUNT_COLUMN).toString());*/
            holder.clockOut.setText(map.get("clock_out").toString());
            //holder.notesTxt.setText(map.get("exp_longitude").toString());
            
            
        } catch (NullPointerException e) {
            System.out.println("Error Attendance List HEADER Adapter: " + e.toString());
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
        TextView id;
        TextView clockIn;
        /*TextView txtTotalAmount;*/
        TextView clockOut;
        // TextView notesTxt;
        
    }
}

