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
 * Created by Blazeminds on 1/11/2018.
 */

public class PaymentListHeaderAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    public PaymentListHeaderAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
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
        
        final PaymentListHeaderAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item4payment_recieving_header, null);
            holder = new PaymentListHeaderAdapter.ViewHolder();
            
            holder.orderIdTxt = convertView.findViewById(R.id.orderIdTxt);
            holder.amountTxt = convertView.findViewById(R.id.amountTxt);
            /*holder.txtTotalAmount = (TextView) convertView.findViewById(R.id.TotalAmountTxt);*/
            holder.recievingTxt = convertView.findViewById(R.id.recievingTxt);
            // holder.notesTxt = (TextView) convertView.findViewById(R.id.notesTxt);
            
            
            convertView.setTag(holder);
            
            
        } else {
            holder = (PaymentListHeaderAdapter.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(i);
        
        try {
            
            holder.orderIdTxt.setText(map.get("date").toString());
            holder.amountTxt.setText(map.get("custId").toString());
            /*holder.txtTotalAmount.setText(map.get(ORDER_AMOUNT_COLUMN).toString());*/
            holder.recievingTxt.setText(map.get("amount").toString());
            //holder.notesTxt.setText(map.get("notes").toString());
            
            
        } catch (NullPointerException e) {
            System.out.println("Error Payment Recieving ListHEADERAdapter: " + e.toString());
        }
        return convertView;
    }
    
    private class ViewHolder {
        TextView orderIdTxt;
        TextView amountTxt;
        /*TextView txtTotalAmount;*/
        TextView recievingTxt;
        TextView notesTxt;
        
    }
}
