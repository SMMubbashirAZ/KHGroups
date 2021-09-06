package com.blazeminds.pos;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Blazeminds on 1/11/2018.
 */

public class PaymentListAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    public ArrayList<String> list1;
    Activity activity;
    
    
    public PaymentListAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
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
        final PaymentListAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_payment_recieving_row, null);
            holder = new PaymentListAdapter.ViewHolder();
            //  holder.txtCode = (TextView) convertView.findViewById(R.id.Code);
            //  holder.txtPic = (ImageView) convertView.findViewById(R.id.Pic);
            holder.paymentRecRow = convertView.findViewById(R.id.paymentRecievedRow);
            holder.dateTV = convertView.findViewById(R.id.dateTV);
            holder.customerTV = convertView.findViewById(R.id.customerTV);
            holder.amountTV = convertView.findViewById(R.id.amountTV);
            //holder.notesTV = (TextView) convertView.findViewById(R.id.notesTV);
            // holder.txtFourth = (TextView) convertView.findViewById(R.id.DBBrandNameValues);
            
            
            convertView.setTag(holder);
        } else {
            holder = (PaymentListAdapter.ViewHolder) convertView.getTag();
        }
        
        final HashMap map = list.get(i);
        
        
        try {
            
            
            if (map.get("execute_complete").toString().equals("1")) {
                holder.paymentRecRow.setBackgroundColor(activity.getResources().getColor(R.color.green_back));
            } else {
                holder.paymentRecRow.setBackgroundColor(activity.getResources().getColor(R.color.mdtp_white));
            }
            
            String dbDate = map.get("datetime").toString();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            //DateFormat dfProper = new SimpleDateFormat("dd-MMM-yy\nK:mm a");
            DateFormat dfProper = new SimpleDateFormat("d MMM yyyy, hh:mm a");
            Date dt = null;
            try {
                dt = df.parse(dbDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.dateTV.setText(dfProper.format(dt));
            holder.customerTV.setText(map.get("customerName").toString());
            holder.amountTV.setText(map.get("amount").toString());
            //holder.notesTV.setText(map.get("details").toString());
            //  holder.txtFourth.setText(map.get(FOURTH_COLUMN).toString());
            
            
        } catch (Exception e) {
            System.out.println("Error IMG: " + e.toString());
        }
        
        
        return convertView;
    }
    
    private class ViewHolder {
        //TextView txtCode;
        //ImageView txtPic;
        TableRow paymentRecRow;
        TextView dateTV;
        TextView customerTV;
        TextView amountTV;
        TextView notesTV;
        //TextView txtFourth;
        
    }
}
